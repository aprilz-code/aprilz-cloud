package com.keke.cloud.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keke.cloud.common.operation.FileOperation;
import com.keke.cloud.common.util.FileUtil;
import com.keke.cloud.common.util.PathUtil;
import com.keke.cloud.web.domain.FileBean;
import com.keke.cloud.web.mapper.FileMapper;
import com.keke.cloud.web.mapper.UserFileMapper;
import com.keke.cloud.web.service.IFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class FileService extends ServiceImpl<FileMapper, FileBean> implements IFileService {

    @Resource
    FileMapper fileMapper;
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    FiletransferService filetransferService;

    @Override
    public void increaseFilePointCount(Long fileId) {
        FileBean fileBean = fileMapper.selectById(fileId);
        fileBean.setPointCount(fileBean.getPointCount()+1);
        fileMapper.updateById(fileBean);
    }

    @Override
    public void decreaseFilePointCount(Long fileId) {
        FileBean fileBean = fileMapper.selectById(fileId);
        fileBean.setPointCount(fileBean.getPointCount()-1);
        fileMapper.updateById(fileBean);
    }

    @Override
    public void deleteLocalFile(FileBean fileBean) {
        log.info("删除本地文件：" + JSON.toJSONString(fileBean));
        //删除服务器文件
        if (fileBean.getFileUrl() != null && fileBean.getFileUrl().indexOf("upload") != -1){
                FileOperation.deleteFile( fileBean.getFileUrl());
                if (FileUtil.isImageFile(FileUtil.getFileType(fileBean.getFileUrl()))) {
                    FileOperation.deleteFile( fileBean.getFileUrl().replace(fileBean.getTimeStampName(), fileBean.getTimeStampName() + "_min"));
            }
        }
    }






}
