package com.keke.cloud.common.upload.product;


import com.keke.cloud.common.domain.NotSameFileExpection;
import com.keke.cloud.common.domain.UploadFile;
import com.keke.cloud.common.operation.ImageOperation;
import com.keke.cloud.common.upload.Uploader;
import com.keke.cloud.common.util.FileUtil;
import com.keke.cloud.common.util.PathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
@Slf4j
public class ChunkUploader extends Uploader {
    private static final Logger logger = LoggerFactory.getLogger(ChunkUploader.class);
    private UploadFile uploadFile;

    public ChunkUploader() {

    }
    //获取上传分片
    public List<Integer> getChunks(String md5){
        String path =  getSaveFilePath() + FILE_SEPARATOR + md5;
        File file = new File(path);
        log.info("已上传的分片："+file.list());
        List<Integer> menuIdList = new ArrayList<Integer>();
        if (file.exists()){
            List<String> list = Arrays.asList(file.list());
            CollectionUtils.collect(list, new Transformer() {
                @Override
                public Object transform(Object o) {
                    return Integer.valueOf(o.toString());
                }
            }, menuIdList);
        }

        return menuIdList;
    }

    public ChunkUploader(UploadFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    @Override
    public List<UploadFile> upload(HttpServletRequest httpServletRequest) {
        List<UploadFile> saveUploadFileList = new ArrayList<UploadFile>();
        this.request = (StandardMultipartHttpServletRequest) httpServletRequest;
        boolean isMultipart = ServletFileUpload.isMultipartContent(this.request);
        if (!isMultipart) {
            UploadFile uploadFile = new UploadFile();
            uploadFile.setSuccess(0);
            uploadFile.setMessage("未包含文件上传域");
            saveUploadFileList.add(uploadFile);
            return saveUploadFileList;
        }
        DiskFileItemFactory dff = new DiskFileItemFactory();//1、创建工厂
        String savePath = getSaveFilePath();
        dff.setRepository(new File(savePath));

        try {
            ServletFileUpload sfu = new ServletFileUpload(dff);//2、创建文件上传解析器
            sfu.setSizeMax(this.maxSize * 1024L);
            sfu.setHeaderEncoding("utf-8");//3、解决文件名的中文乱码
            Iterator<String> iter = this.request.getFileNames();
            while (iter.hasNext()) {
                saveUploadFileList = doUpload(savePath, iter);
            }
        } catch (IOException e) {
            UploadFile uploadFile = new UploadFile();
            uploadFile.setSuccess(1);
            uploadFile.setMessage("未知错误");
            saveUploadFileList.add(uploadFile);
            e.printStackTrace();
        } catch (NotSameFileExpection notSameFileExpection) {
            notSameFileExpection.printStackTrace();
        }
        return saveUploadFileList;
    }

    @Override
    public List<UploadFile> uploadChunk(HttpServletRequest httpServletRequest) {
        List<UploadFile> saveUploadFileList = new ArrayList<UploadFile>();
        this.request = (StandardMultipartHttpServletRequest) httpServletRequest;
        boolean isMultipart = ServletFileUpload.isMultipartContent(this.request);
        if (!isMultipart) {
            UploadFile uploadFile = new UploadFile();
            uploadFile.setSuccess(0);
            uploadFile.setMessage("未包含文件上传域");
            saveUploadFileList.add(uploadFile);
            return saveUploadFileList;
        }
        DiskFileItemFactory dff = new DiskFileItemFactory();//1、创建工厂
        String savePath = getSaveFilePath();
        dff.setRepository(new File(savePath));

        try {
            ServletFileUpload sfu = new ServletFileUpload(dff);//2、创建文件上传解析器
            sfu.setSizeMax(this.maxSize * 1024L);
            sfu.setHeaderEncoding("utf-8");//3、解决文件名的中文乱码
            Iterator<String> iter = this.request.getFileNames();
            while (iter.hasNext()) {
                saveUploadFileList = doUploadChunk(savePath, iter);
            }
        } catch (IOException e) {
            UploadFile uploadFile = new UploadFile();
            uploadFile.setSuccess(1);
            uploadFile.setMessage("未知错误");
            saveUploadFileList.add(uploadFile);
            e.printStackTrace();
        } catch (NotSameFileExpection notSameFileExpection) {
            notSameFileExpection.printStackTrace();
        }
        return saveUploadFileList;
    }

    //边上传边合并
    private List<UploadFile> doUpload(String savePath, Iterator<String> iter) throws IOException, NotSameFileExpection {
        List<UploadFile> saveUploadFileList = new ArrayList<UploadFile>();
        MultipartFile multipartfile = this.request.getFile(iter.next());

        String timeStampName = uploadFile.getIdentifier();
        String originalName = multipartfile.getOriginalFilename();

        String fileName = getFileName(originalName);
        String fileType = FileUtil.getFileType(originalName);
        uploadFile.setFileName(fileName);
        uploadFile.setFileType(fileType);
        uploadFile.setTimeStampName(timeStampName);

        String saveFilePath = savePath + FILE_SEPARATOR + timeStampName + "." + fileType;
        String tempFilePath = savePath + FILE_SEPARATOR + timeStampName + "." + fileType + "_tmp";
        String minFilePath = savePath + FILE_SEPARATOR + timeStampName + "_min" + "." + fileType;
        String confFilePath = savePath + FILE_SEPARATOR + timeStampName + "." + "conf";
        File file = new File( saveFilePath);
        File tempFile = new File( tempFilePath);
        File minFile = new File( minFilePath);
        File confFile = new File( confFilePath);
        uploadFile.setIsOss(0);
        uploadFile.setUrl(saveFilePath);

        if (StringUtils.isEmpty(uploadFile.getTaskId())) {// == null || "".equals(uploadFile.getTaskId())) {
            uploadFile.setTaskId(UUID.randomUUID().toString());
        }

        //第一步 打开将要写入的文件
        RandomAccessFile raf = new RandomAccessFile(tempFile, "rw");
        //第二步 打开通道
        FileChannel fileChannel = raf.getChannel();
        //第三步 计算偏移量
        long position = (uploadFile.getChunkNumber() - 1) * uploadFile.getChunkSize();
        //第四步 获取分片数据
        byte[] fileData = multipartfile.getBytes();
        //第五步 写入数据
        fileChannel.position(position);
        fileChannel.write(ByteBuffer.wrap(fileData));
        fileChannel.force(true);
        fileChannel.close();
        raf.close();
        //判断是否完成文件的传输并进行校验与重命名，保存分片
        boolean isComplete = checkUploadStatus(uploadFile, confFile);
        if (isComplete) {
            FileInputStream fileInputStream = new FileInputStream(tempFile.getPath());
            String md5 = DigestUtils.md5Hex(fileInputStream);
            fileInputStream.close();
            if (StringUtils.isNotBlank(md5) && !md5.equals(uploadFile.getIdentifier())) {
                throw new NotSameFileExpection();
            }
            tempFile.renameTo(file);
            if (FileUtil.isImageFile(uploadFile.getFileType())){
                ImageOperation.thumbnailsImage(file, minFile, 300);
            }
            uploadFile.setSuccess(1);
            uploadFile.setMessage("上传成功");
        } else {
            uploadFile.setSuccess(0);
            uploadFile.setMessage("未完成");
        }
        uploadFile.setFileSize(uploadFile.getTotalSize());
        saveUploadFileList.add(uploadFile);

        return saveUploadFileList;
    }
    //分片上传 合并操作
    private List<UploadFile> doUploadChunk(String savePath, Iterator<String> iter) throws IOException, NotSameFileExpection {
        //获取request中的MultipartFile对象
        MultipartFile multipartfile = this.request.getFile(iter.next());
        List<UploadFile> saveUploadFileList = new ArrayList<UploadFile>();
        //文件id
        String timeStampName = uploadFile.getIdentifier();
        //原文件名
        String originalName = multipartfile.getOriginalFilename();
        //原文件名（无后缀）
        String fileName = getFileName(originalName);
        //文件类型
        String fileType = FileUtil.getFileType(originalName);
        uploadFile.setFileName(fileName);
        uploadFile.setFileType(fileType);
        uploadFile.setTimeStampName(timeStampName);
        //D:\IdeaProjects\bishe\aprilz-cloud-back\target\classes\static \keke_files\yyyyMMdd\id.type
        //文件存储绝对路径
        String saveFilePath = savePath + FILE_SEPARATOR + timeStampName + "." + fileType;
        //D:\IdeaProjects\bishe\aprilz-cloud-back\target\classes\static \keke_files\yyyyMMdd\id
        //文件存储绝对路径（无后缀）
        String saveChunkPath =  savePath + FILE_SEPARATOR + timeStampName;
        //D:\IdeaProjects\bishe\aprilz-cloud-back\target\classes\static \keke_files\yyyyMMdd\id\x
        //文件流
        File file = createFile( mkdir(saveChunkPath)+ FILE_SEPARATOR + uploadFile.getChunkNumber());
        multipartfile.transferTo(file);
        uploadFile.setIsOss(0);
        //判断是否所有分片都上传了
        if(file.getParentFile().list().length==uploadFile.getTotalChunks()){
            log.info("开始文件合并");
            uploadFile.setUrl(saveFilePath);
            //合并文件
            File real = createFile( mkdir( savePath )+ FILE_SEPARATOR + timeStampName + "." + fileType);
            //临时文件
            File temp = null;
            if (uploadFile.getTotalChunks()==1){
                //未进行分片,单文件合并
                temp = new File(mkdir(saveChunkPath)+ FILE_SEPARATOR +"1");
                if (temp.exists()){
                    fileChannelCopy(temp,real);
                }
            }else{
                //多分片，按顺序合并
                for (int i =1;i<=uploadFile.getTotalChunks();i++){
                    temp = new File(mkdir(saveChunkPath)+ FILE_SEPARATOR + i);
                    if (temp.exists()){
                        fileChannelCopy(temp,real);
                    }
                }
            }
            //校验
            FileInputStream fileInputStream = new FileInputStream(real.getPath());
            //计算合并后文件md5
            String md5 = DigestUtils.md5Hex(fileInputStream);
            fileInputStream.close();
            //校验
            if (StringUtils.isNotBlank(md5) && !md5.equals(uploadFile.getIdentifier())) {
                throw new NotSameFileExpection();
            }
            //如果是图片 进行图片压缩 用于前端略览
            if (FileUtil.isImageFile(uploadFile.getFileType())){
                File minFile = createFile( mkdir( savePath )+ FILE_SEPARATOR + timeStampName +"_min"+ "." + fileType);
                ImageOperation.thumbnailsImage(file, minFile, 300);
            }
            log.info("合并完成");
            uploadFile.setSuccess(1);
            uploadFile.setMessage("上传成功");
        }else{
            uploadFile.setSuccess(0);
            uploadFile.setMessage("未完成");
        }
        uploadFile.setFileSize(uploadFile.getTotalSize());
        saveUploadFileList.add(uploadFile);

        return saveUploadFileList;
    }
}
