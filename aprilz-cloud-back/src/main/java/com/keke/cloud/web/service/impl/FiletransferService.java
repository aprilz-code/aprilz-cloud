package com.keke.cloud.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.keke.cloud.common.domain.UploadFile;
import com.keke.cloud.common.upload.Uploader;
import com.keke.cloud.common.upload.factory.ChunkUploaderFactory;
import com.keke.cloud.common.upload.product.ChunkUploader;
import com.keke.cloud.common.util.DateUtil;
import com.keke.cloud.web.domain.FileBean;
import com.keke.cloud.web.domain.UserFile;
import com.keke.cloud.web.dto.UploadFileDTO;
import com.keke.cloud.web.mapper.FileMapper;
import com.keke.cloud.web.mapper.UserFileMapper;
import com.keke.cloud.web.service.IFiletransferService;
import com.keke.cloud.web.vo.UploadFileVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.LinkedList;
import java.util.List;


@Service
@Slf4j
public class FiletransferService implements IFiletransferService {

    @Resource
    FileMapper fileMapper;
    @Resource
    UserFileMapper userFileMapper;


//    //边上传边合并
//    @Override
//    public void uploadFile(HttpServletRequest request, UploadFileDTO UploadFileDto, Long userId) {
//        Uploader uploader;
//        UploadFile uploadFile = new UploadFile();
//        uploadFile.setChunkNumber(UploadFileDto.getChunkNumber());
//        uploadFile.setChunkSize(UploadFileDto.getChunkSize());
//        uploadFile.setTotalChunks(UploadFileDto.getTotalChunks());
//        uploadFile.setIdentifier(UploadFileDto.getIdentifier());
//        uploadFile.setTotalSize(UploadFileDto.getTotalSize());
//        uploadFile.setCurrentChunkSize(UploadFileDto.getCurrentChunkSize());
//       //本地存储
//        uploader = new ChunkUploaderFactory().getUploader(uploadFile);
//        List<UploadFile> uploadFileList = uploader.upload(request);
//        for (int i = 0; i < uploadFileList.size(); i++){
//            uploadFile = uploadFileList.get(i);
//            FileBean fileBean = new FileBean();
//            BeanUtil.copyProperties(UploadFileDto, fileBean);
//            fileBean.setTimeStampName(uploadFile.getTimeStampName());
//            if (uploadFile.getSuccess() == 1){
//                fileBean.setFileUrl(uploadFile.getUrl());
//                fileBean.setFileSize(uploadFile.getFileSize());
//                //fileBean.setUploadTime(DateUtil.getCurrentTime());
//                fileBean.setIsOss(uploadFile.getIsOss());
//
//                fileBean.setPointCount(1);
//                fileMapper.insert(fileBean);
//                UserFile userFile = new UserFile();
//                userFile.setFileId(fileBean.getFileId());
//                userFile.setExtendName(uploadFile.getFileType());
//                userFile.setFileName(uploadFile.getFileName());
//                userFile.setFilePath(UploadFileDto.getFilePath());
//                userFile.setDeleteFlag(0);
//                userFile.setUserId(userId);
//                userFile.setIsDir(0);
//                userFile.setUploadTime(DateUtil.getCurrentTime());
//                userFileMapper.insert(userFile);
//            }
//        }
//    }
    //分片保存
    @Override
    public void uploadChunk(HttpServletRequest request, UploadFileDTO UploadFileDto, Long userId) {
        List<UploadFileVo> uploadFileVoList = new LinkedList<>();
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

    @Override
    public List<Integer> getIntegerList(String md5) {
        ChunkUploader uploader = new ChunkUploader();
        List<Integer> list= uploader.getChunks(md5);
        log.info(md5+",已存在的分片："+list);
        return list;
    }
}
