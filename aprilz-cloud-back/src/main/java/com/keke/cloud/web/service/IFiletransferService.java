package com.keke.cloud.web.service;


import com.keke.cloud.web.dto.UploadFileDTO;
import com.keke.cloud.web.vo.UploadFileVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IFiletransferService {
    /**
     * 上传文件
     * @param request 请求
     * @param UploadFileDto 文件信息
     */
//    void uploadFile(HttpServletRequest request, UploadFileDTO UploadFileDto, Long userId);
    /*
    * 分片上传
    * */
    void uploadChunk(HttpServletRequest request, UploadFileDTO UploadFileDto, Long userId);
    //获取已上传分片
    List<Integer> getIntegerList (String md5);
}
