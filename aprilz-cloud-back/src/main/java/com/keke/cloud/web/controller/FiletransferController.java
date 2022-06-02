package com.keke.cloud.web.controller;

import cn.hutool.core.collection.CollUtil;
import com.keke.cloud.common.domain.RestResult;
import com.keke.cloud.common.util.DateUtil;
import com.keke.cloud.common.util.FileUtil;
import com.keke.cloud.common.util.OssUtil;
import com.keke.cloud.common.util.PathUtil;
import com.keke.cloud.web.domain.FileBean;
import com.keke.cloud.web.domain.UserBean;
import com.keke.cloud.web.domain.UserFile;
import com.keke.cloud.web.dto.UploadFileDTO;
import com.keke.cloud.web.service.IFileService;
import com.keke.cloud.web.service.IFiletransferService;
import com.keke.cloud.web.service.IUserFileService;
import com.keke.cloud.web.service.IUserService;
import com.keke.cloud.web.vo.UploadFileVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Api(value = "filetransfer", tags = "该接口为文件传输接口，主要用来做文件的上传和下载")
@RestController
@Slf4j
@RequestMapping("/filetransfer")
public class FiletransferController {

    @Resource
    IFiletransferService filetransferService;

    @Autowired
    private OssUtil OssUtil;

    @Resource
    FileController fileController;

    @Resource
    IFileService fileService;
    @Resource
    IUserService userService;
    @Resource
    IUserFileService userFileService;

    @ApiOperation(value = "极速上传", notes = "校验文件MD5判断文件是否存在，如果存在直接上传成功并返回skipUpload=true，如果不存在返回skipUpload=false需要再次调用该接口的POST方法", tags = {"filetransfer"})
    @RequestMapping(value = "/uploadfile", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<UploadFileVo> uploadFileSpeed(HttpServletRequest request, UploadFileDTO uploadFileDto, @RequestHeader("token") String token) {
        RestResult<UploadFileVo> restResult = new RestResult<UploadFileVo>();
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (sessionUserBean == null){
            restResult.setSuccess(false);
            restResult.setErrorMessage("未登录");
            return restResult;
        }
        RestResult<String> operationCheckResult = fileController.operationCheck(token);
        if (!operationCheckResult.isSuccess()){
            restResult.setSuccess(false);
            restResult.setErrorMessage("没权限，请联系管理员！");
            return restResult;
        }
        UploadFileVo uploadFileVo = new UploadFileVo();
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("identifier", uploadFileDto.getIdentifier());
        synchronized (FiletransferController.class) {
            List<FileBean> list = fileService.listByMap(param);
            String fileName = uploadFileDto.getFilename();
            if (CollUtil.isNotEmpty(list)) {
                FileBean file = list.get(0);

                UserFile userFile = new UserFile();
                userFile.setFileId(file.getFileId());
                userFile.setUserId(sessionUserBean.getUserId());
                userFile.setFilePath(uploadFileDto.getFilePath());
                userFile.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
                userFile.setExtendName(FileUtil.getFileType(fileName));
                userFile.setDeleteFlag(0);
                userFile.setIsDir(0);
                userFile.setUploadTime(DateUtil.getCurrentTime());
                userFileService.save(userFile);
                fileService.increaseFilePointCount(file.getFileId());
                uploadFileVo.setSkipUpload(true);
            } else {
                //不存在则上传oss，获取oss uploadId放置redis
                if(Objects.equals(new Integer(1), uploadFileDto.getIsOss())){
                   OssUtil.bigFileInitUpload(fileName);
                }

                List<Integer> integerList = filetransferService.getIntegerList(uploadFileDto.getIdentifier());
                uploadFileVo.setUploaded(integerList);
                uploadFileVo.setSkipUpload(false);
            }
        }

        restResult.setData(uploadFileVo);
        restResult.setSuccess(true);
        return restResult;
    }

    /**
     * 上传文件
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "上传文件", notes = "真正的上次文件接口", tags = {"filetransfer"})
    @RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<UploadFileVo> uploadFile(HttpServletRequest request,  UploadFileDTO uploadFileDto, @RequestHeader("token") String token) throws IOException {
        RestResult<UploadFileVo> restResult = new RestResult<>();
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (sessionUserBean == null){
            restResult.setSuccess(false);
            restResult.setErrorMessage("未登录");
            return restResult;
        }
        //判断是否登录 是否有权限
        RestResult<String> operationCheckResult = fileController.operationCheck(token);
        if (!operationCheckResult.isSuccess()){
            restResult.setSuccess(false);
            restResult.setErrorMessage("没权限，请联系管理员！");
            return restResult;
        }

        //准备根据uploadFileDto的isOss，区分使用oss或者本地

        filetransferService.uploadChunk(request, uploadFileDto, sessionUserBean.getUserId());
        UploadFileVo uploadFileVo = new UploadFileVo();
        restResult.setData(uploadFileVo);
        log.info("########上传文件执行！"+"，返回数据+"+restResult);
        return restResult;
    }
}
