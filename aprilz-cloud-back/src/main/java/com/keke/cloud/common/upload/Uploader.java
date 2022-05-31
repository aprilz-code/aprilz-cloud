package com.keke.cloud.common.upload;

import cn.hutool.log.Log;
import com.keke.cloud.common.config.UploadConfig;
import com.keke.cloud.common.domain.UploadFile;
import com.keke.cloud.common.util.PathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.channels.FileChannel;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @auther flk
 * @create 2021/2/24
 */
@Slf4j
public abstract class Uploader {

    public static final String FILE_SEPARATOR = File.separator;
    // 文件大小限制，单位KB
    public static final int maxSize = 10000000;

    protected StandardMultipartHttpServletRequest request = null;

    public abstract List<UploadFile> upload(HttpServletRequest request);
    //存储分片
    public abstract List<UploadFile> uploadChunk(HttpServletRequest request);

    /**
     * 根据字符串创建本地目录 并按照日期建立子目录返回
     *
     * @param
     * @return
     */
    protected String getSaveFilePath() {
        String path = UploadConfig.filePath;
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
        path = path + FILE_SEPARATOR + formater.format(new Date());
        File dir = new File( path);
        if (!dir.exists()) {
            try {
                boolean isSuccessMakeDir = dir.mkdirs();
                if (!isSuccessMakeDir) {
                    log.error("目录创建失败:" +  path);
                }
            } catch (Exception e) {
                log.error("目录创建失败" +  path);
                return "";
            }
        }
        log.info("文件存储地址： "+dir.getPath());
        return path;
    }
    /**
     * 根据字符串创建本地目录 并按照日期建立子目录返回
     *
     * @param
     * @return
     */
    protected String getLocalPath() {
        String path = UploadConfig.filePath;
        File dir = new File( path);
        if (!dir.exists()) {
            try {
                boolean isSuccessMakeDir = dir.mkdirs();
                if (!isSuccessMakeDir) {
                    log.error("目录创建失败:" +  path);
                }
            } catch (Exception e) {
                log.error("目录创建失败" +  path);
                return "";
            }
        }
        return path;
    }
    /**
     * 依据原始文件名生成新文件名
     *
     * @return
     */
    protected String getTimeStampName() {
        try {
            SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
            return "" + number.nextInt(10000)
                    + System.currentTimeMillis();
        } catch (NoSuchAlgorithmException e) {
            log.error("生成安全随机数失败");
        }
        return ""
                + System.currentTimeMillis();

    }

    public synchronized boolean checkUploadStatus(UploadFile param, File confFile) throws IOException {
        //File confFile = new File(savePath, timeStampName + ".conf");
        RandomAccessFile confAccessFile = new RandomAccessFile(confFile, "rw");
        //设置文件长度
        confAccessFile.setLength(param.getTotalChunks());
        //设置起始偏移量
        confAccessFile.seek(param.getChunkNumber() - 1);
        //将指定的一个字节写入文件中 127，
        confAccessFile.write(Byte.MAX_VALUE);
        byte[] completeStatusList = FileUtils.readFileToByteArray(confFile);
        confAccessFile.close();//不关闭会造成无法占用
        //创建conf文件文件长度为总分片数，每上传一个分块即向conf文件中写入一个127，那么没上传的位置就是默认的0,已上传的就是127
        for (int i = 0; i < completeStatusList.length; i++) {
            if (completeStatusList[i] != Byte.MAX_VALUE) {
                return false;
            }
        }
        confFile.delete();
        return true;
    }

    protected String getFileName(String fileName){
        if (!fileName.contains(".")) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf("."));
    }
    protected File createFile(String path) throws IOException{
        File file=new File(path);
        if(!file.exists()){
            file.createNewFile();
        }
        return file;
    }
    public String mkdir(String path) throws IOException{
        File file=new File(path);
        if(!file.exists()){
            file.mkdir();
        }
        return path;
    }
    //文件合并具体操作
    public void fileChannelCopy(File source, File target)  throws IOException{
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(source);
            fo = new FileOutputStream(target,true);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } finally {
            fi.close();
            in.close();
            fo.close();
            out.close();
        }
    }
}
