package com.keke.cloud.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.keke.cloud.common.domain.UploadFile;
import com.keke.cloud.common.upload.Uploader;
import com.keke.cloud.common.upload.factory.ChunkUploaderFactory;
import com.keke.cloud.common.upload.product.ChunkUploader;
import com.keke.cloud.common.util.DateUtil;
import com.keke.cloud.common.util.FileUtil;
import com.keke.cloud.common.util.OssUtil;
import com.keke.cloud.web.domain.FileBean;
import com.keke.cloud.web.domain.UserFile;
import com.keke.cloud.web.dto.UploadFileDTO;
import com.keke.cloud.web.mapper.FileMapper;
import com.keke.cloud.web.mapper.UserFileMapper;
import com.keke.cloud.web.service.IFiletransferService;
import com.keke.cloud.web.vo.UploadFileVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


@Service
@Slf4j
public class FiletransferService implements IFiletransferService {

    @Resource
    FileMapper fileMapper;
    @Resource
    UserFileMapper userFileMapper;

    @Autowired
    private OssUtil OssUtil;




    //分片保存
    @Override
    public void uploadChunk(HttpServletRequest request, UploadFileDTO UploadFileDto, Long userId) throws IOException {

        //oss
        if(Objects.equals(new Integer(1), UploadFileDto.getIsOss())){

            StandardMultipartHttpServletRequest srequest = (StandardMultipartHttpServletRequest) request;
            boolean isMultipart = ServletFileUpload.isMultipartContent(srequest);
            if(!isMultipart){
                return;
            }
//            LambdaQueryWrapper<UserFile> qw = new LambdaQueryWrapper<UserFile>();
//            qw.eq(UserFile::getFileName, UploadFileDto.getFilename()).orderByDesc(UserFile::getUploadTime);
//            qw.last("limit 1");
//            UserFile userFile = userFileMapper.selectOne(qw);
//            if(Objects.isNull(userFile)){
//                return;
//            }
            Iterator<String> iter =  srequest.getFileNames();
            while (iter.hasNext()) {
                MultipartFile multipartfile = srequest.getFile(iter.next());
                OssUtil.uploadChunk(UploadFileDto.getFilename(), UploadFileDto.getChunkNumber(), multipartfile);
            }

            //oss 合并操作
            if(UploadFileDto.getChunkNumber() == UploadFileDto.getTotalChunks()){
                String fileUrl = OssUtil.completeFile(UploadFileDto.getFilename());
                FileBean fileBean = new FileBean();
                BeanUtil.copyProperties(UploadFileDto, fileBean);
                fileBean.setFileUrl(fileUrl);
                fileBean.setFileSize(UploadFileDto.getTotalSize());

                fileBean.setTimeStampName(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
                fileBean.setIsOss(UploadFileDto.getIsOss());
                fileBean.setPointCount(1);
                fileMapper.insert(fileBean);
                UserFile userFile = new UserFile();
                userFile.setFileId(fileBean.getFileId());
                userFile.setExtendName(FileUtil.getFileType(UploadFileDto.getFilename()));
                userFile.setFileName(UploadFileDto.getFilename().substring(0, UploadFileDto.getFilename().lastIndexOf(".")));
                userFile.setFilePath(UploadFileDto.getFilePath());
                userFile.setDeleteFlag(0);
                userFile.setUserId(userId);
                userFile.setIsDir(0);
                userFile.setUploadTime(DateUtil.getCurrentTime());
                userFileMapper.insert(userFile);
            }

           //local
        }else{
            Uploader uploader;
            UploadFile uploadFile = new UploadFile();
            uploadFile.setFileName(UploadFileDto.getFilename());
            uploadFile.setChunkNumber(UploadFileDto.getChunkNumber());
            uploadFile.setChunkSize(UploadFileDto.getChunkSize());
            uploadFile.setTotalChunks(UploadFileDto.getTotalChunks());
            uploadFile.setIdentifier(UploadFileDto.getIdentifier());
            uploadFile.setTotalSize(UploadFileDto.getTotalSize());
            uploadFile.setCurrentChunkSize(UploadFileDto.getCurrentChunkSize());
            //本地存储
            uploader = new ChunkUploaderFactory().getUploader(uploadFile);
            //合并后修改映射
            List<UploadFile> uploadFileList = uploader.uploadChunk(request);
            for (int i = 0; i < uploadFileList.size(); i++){
                uploadFile = uploadFileList.get(i);
                FileBean fileBean = new FileBean();
                BeanUtil.copyProperties(UploadFileDto, fileBean);
                fileBean.setTimeStampName(uploadFile.getTimeStampName());
                if (uploadFile.getSuccess() == 1){
                    fileBean.setFileUrl(uploadFile.getUrl());
                    fileBean.setFileSize(uploadFile.getFileSize());
                    //fileBean.setUploadTime(DateUtil.getCurrentTime());
                    fileBean.setIsOss(uploadFile.getIsOss());

                    fileBean.setPointCount(1);
                    fileMapper.insert(fileBean);
                    UserFile userFile = new UserFile();
                    userFile.setFileId(fileBean.getFileId());
                    userFile.setExtendName(uploadFile.getFileType());
                    userFile.setFileName(uploadFile.getFileName());
                    userFile.setFilePath(UploadFileDto.getFilePath());
                    userFile.setDeleteFlag(0);
                    userFile.setUserId(userId);
                    userFile.setIsDir(0);
                    userFile.setUploadTime(DateUtil.getCurrentTime());
                    userFileMapper.insert(userFile);
                }
            }
        }

    }

    @Override
    public List<Integer> getIntegerList(String md5) {
        ChunkUploader uploader = new ChunkUploader();
        List<Integer> list= uploader.getChunks(md5);
        log.info(md5+",已存在的分片："+list);
        return list;
    }
}
