package com.keke.cloud.web.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.keke.cloud.common.config.UploadConfig;
import com.keke.cloud.common.domain.RestResult;
import com.keke.cloud.common.operation.FileOperation;
import com.keke.cloud.common.util.DateUtil;
import com.keke.cloud.common.util.FileUtil;
import com.keke.cloud.common.util.PathUtil;
import com.keke.cloud.web.domain.*;
import com.keke.cloud.web.dto.*;
import com.keke.cloud.web.service.IFileService;
import com.keke.cloud.web.service.IRecoveryFileService;
import com.keke.cloud.web.service.IUserFileService;
import com.keke.cloud.web.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.keke.cloud.common.util.FileUtil.getFileExtendsByType;

/**
 * @auther flk
 * @create 2021/2/24
 */
@Api(value = "file", tags = "该接口为文件接口，主要用来做一些文件的基本操作，如创建目录，删除，移动，复制等。")
@RestController
@Slf4j
@RequestMapping("/file")
public class FileController {

    @Resource
    IFileService fileService;
    @Resource
    IUserService userService;
    @Resource
    IUserFileService userFileService;
    @Resource
    IRecoveryFileService recoveryFileService;

    public static Executor executor = Executors.newFixedThreadPool(20);

    public static int COMPLETE_COUNT = 0;

    public static long treeid = 0;


    @ApiOperation(value = "创建文件", notes = "目录(文件夹)的创建", tags = {"file"})
    @RequestMapping(value = "/createfile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> createFile(@RequestBody CreateFileDTO createFileDto, @RequestHeader("token") String token) {
        RestResult<String> restResult = new RestResult<>();
        if (!operationCheck(token).isSuccess()) {
            return operationCheck(token);
        }

        UserBean sessionUserBean = userService.getUserBeanByToken(token);

        List<UserFile> userFiles = userFileService.selectUserFileByNameAndPath(createFileDto.getFileName(), createFileDto.getFilePath(), sessionUserBean.getUserId());
        if (userFiles != null && !userFiles.isEmpty()) {
            restResult.setErrorMessage("同名文件已存在");
            restResult.setSuccess(false);
            return restResult;
        }

        UserFile userFile = new UserFile();
        userFile.setUserId(sessionUserBean.getUserId());
        userFile.setFileName(createFileDto.getFileName());
        userFile.setFilePath(createFileDto.getFilePath());
        userFile.setDeleteFlag(0);
        userFile.setIsDir(1);
        userFile.setUploadTime(DateUtil.getCurrentTime());

        userFileService.save(userFile);

        restResult.setSuccess(true);
        return restResult;
    }

    @ApiOperation(value = "文件重命名", notes = "文件重命名", tags = {"file"})
    @RequestMapping(value = "/renamefile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> renameFile(@RequestBody RenameFileDTO renameFileDto, @RequestHeader("token") String token) {
        RestResult<String> restResult = new RestResult<>();
        if (!operationCheck(token).isSuccess()) {
            return operationCheck(token);
        }
        UserBean sessionUserBean = userService.getUserBeanByToken(token);

        List<UserFile> userFiles = userFileService.selectUserFileByNameAndPath(renameFileDto.getFileName(), renameFileDto.getFilePath(), sessionUserBean.getUserId());
        if (userFiles != null && !userFiles.isEmpty()) {
            restResult.setErrorMessage("同名文件已存在");
            restResult.setSuccess(false);
            return restResult;
        }
        if (1 == renameFileDto.getIsDir()) {
            LambdaUpdateWrapper<UserFile> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.set(UserFile::getFileName, renameFileDto.getFileName())
                    .set(UserFile::getUploadTime, DateUtil.getCurrentTime())
                    .eq(UserFile::getUserFileId, renameFileDto.getUserFileId());
            userFileService.update(lambdaUpdateWrapper);
            userFileService.replaceUserFilePath(renameFileDto.getFilePath() + renameFileDto.getFileName() + "/",
                    renameFileDto.getFilePath() + renameFileDto.getOldFileName() + "/", sessionUserBean.getUserId());
        } else {
            if (renameFileDto.getIsOss() == 1) {
                LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UserFile::getUserFileId, renameFileDto.getUserFileId());
                UserFile userFile = userFileService.getOne(lambdaQueryWrapper);

                FileBean file = fileService.getById(userFile.getFileId());
                String fileUrl = file.getFileUrl();
                String newFileUrl = fileUrl.replace(userFile.getFileName(), renameFileDto.getFileName());
                LambdaUpdateWrapper<FileBean> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper
                        .set(FileBean::getFileUrl, newFileUrl)
                        .eq(FileBean::getFileId, file.getFileId());
                fileService.update(lambdaUpdateWrapper);

                LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                userFileLambdaUpdateWrapper
                        .set(UserFile::getFileName, renameFileDto.getFileName())
                        .set(UserFile::getUploadTime, DateUtil.getCurrentTime())
                        .eq(UserFile::getUserFileId, renameFileDto.getUserFileId());
                userFileService.update(userFileLambdaUpdateWrapper);
            } else {
                LambdaUpdateWrapper<UserFile> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.set(UserFile::getFileName, renameFileDto.getFileName())
                        .set(UserFile::getUploadTime, DateUtil.getCurrentTime())
                        .eq(UserFile::getUserFileId, renameFileDto.getUserFileId());
                userFileService.update(lambdaUpdateWrapper);
            }

        }


        restResult.setSuccess(true);
        return restResult;
    }


    @ApiOperation(value = "获取文件列表", notes = "用来做前台列表展示", tags = {"file"})
    @RequestMapping(value = "/getfilelist", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<List<Map<String, Object>>> getFileList(FileListDTO fileListDto, @RequestHeader("token") String token) {
        RestResult<List<Map<String, Object>>> restResult = new RestResult<>();
        if (token.equals("undefined") || StringUtils.isEmpty(token)) {
            restResult.setSuccess(false);
            restResult.setErrorMessage("用户暂未登录");
        } else {
            UserFile userFile = new UserFile();
            UserBean sessionUserBean = userService.getUserBeanByToken(token);
            if (userFile == null) {
                restResult.setSuccess(false);
                return restResult;
            }
            userFile.setUserId(sessionUserBean.getUserId());

            List<Map<String, Object>> fileList = null;
            userFile.setFilePath(PathUtil.urlDecode(fileListDto.getFilePath()));
            if (fileListDto.getCurrentPage() == null || fileListDto.getPageCount() == null) {
                fileList = userFileService.userFileList(userFile, 0L, 10L);
            } else {
                Long beginCount = (fileListDto.getCurrentPage() - 1) * fileListDto.getPageCount();
                fileList = userFileService.userFileList(userFile, beginCount, fileListDto.getPageCount()); //fileService.selectFileListByPath(fileBean);
            }

            fileList.stream().forEach(file-> {
                if(Objects.nonNull(file.get("fileUrl")))
                file.put("fileUrl",file.get("fileUrl").toString().replace(UploadConfig.filePath,""));
            });

            LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userFileLambdaQueryWrapper.eq(UserFile::getUserId, userFile.getUserId())
                    .eq(UserFile::getFilePath, userFile.getFilePath())
                    .eq(UserFile::getDeleteFlag, 0);
            int total = userFileService.count(userFileLambdaQueryWrapper);

            restResult.setTotal(total);
            restResult.setData(fileList);
            restResult.setSuccess(true);
        }
        return restResult;
    }

    @ApiOperation(value = "批量删除文件", notes = "批量删除文件", tags = {"file"})
    @RequestMapping(value = "/batchdeletefile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> deleteImageByIds(@RequestBody BatchDeleteFileDTO batchDeleteFileDto, @RequestHeader("token") String token) {
        RestResult<String> result = new RestResult<String>();
        if (!operationCheck(token).isSuccess()) {
            return operationCheck(token);
        }
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        List<UserFile> userFiles = JSON.parseArray(batchDeleteFileDto.getFiles(), UserFile.class);

        for (UserFile userFile : userFiles) {
            String uuid = UUID.randomUUID().toString();
            userFile.setDeleteBatchNum(uuid);
            userFileService.deleteUserFile(userFile, sessionUserBean);

            RecoveryFile recoveryFile = new RecoveryFile();
            recoveryFile.setUserFileId(userFile.getUserFileId());
            recoveryFile.setDeleteTime(DateUtil.getCurrentTime());
            recoveryFile.setDeleteBatchNum(uuid);
            recoveryFileService.save(recoveryFile);
        }

        result.setData("批量删除文件成功");
        result.setSuccess(true);
        return result;
    }

    @ApiOperation(value = "删除文件", notes = "可以删除文件或者目录", tags = {"file"})
    @RequestMapping(value = "/deletefile", method = RequestMethod.POST)
    @ResponseBody
    public String deleteFile(@RequestBody DeleteFileDTO deleteFileDto, @RequestHeader("token") String token) {
        RestResult<String> result = new RestResult<String>();
        if (!operationCheck(token).isSuccess()) {
            return JSON.toJSONString(operationCheck(token));
        }

        String uuid = UUID.randomUUID().toString();
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        UserFile userFile = new UserFile();
        userFile.setUserFileId(deleteFileDto.getUserFileId());
        userFile.setDeleteBatchNum(uuid);
        BeanUtil.copyProperties(deleteFileDto, userFile);
        userFileService.deleteUserFile(userFile, sessionUserBean);


        RecoveryFile recoveryFile = new RecoveryFile();
        recoveryFile.setUserFileId(deleteFileDto.getUserFileId());
        recoveryFile.setDeleteTime(DateUtil.getCurrentTime());
        recoveryFile.setDeleteBatchNum(uuid);
        recoveryFileService.save(recoveryFile);
        result.setSuccess(true);
        String resultJson = JSON.toJSONString(result);
        return resultJson;
    }

    @ApiOperation(value = "解压文件", notes = "压缩功能", tags = {"file"})
    @RequestMapping(value = "/unzipfile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> unzipFile(@RequestBody UnzipFileDTO unzipFileDto, @RequestHeader("token") String token) {
        RestResult<String> result = new RestResult<String>();
        if (!operationCheck(token).isSuccess()) {
            return operationCheck(token);
        }
        //压缩文件路径
        String zipFileUrl =  unzipFileDto.getFileUrl();
        File file = FileOperation.newFile(zipFileUrl);
        //获取父路径，解压后文件存储到同一目录下
        String unzipUrl = file.getParent();
        String[] arr = unzipFileDto.getFileUrl().split("\\.");
        if (arr.length <= 1) {
            result.setErrorMessage("文件名格式错误！");
            result.setSuccess(false);
            return result;
        }
        //解压后文件列表
        List<String> fileEntryNameList = new ArrayList<>();
        if ("zip".equals(arr[1])) {
            //zip文件解压
            fileEntryNameList = FileOperation.unzip(file, unzipUrl);
        } else if ("rar".equals(arr[1])) {
            try {
                //rar文件解压
                fileEntryNameList = FileOperation.unrar(file, unzipUrl);
            } catch (Exception e) {
                //抛出失败异常
                e.printStackTrace();
                result.setErrorMessage("rar解压失败！");
                result.setSuccess(false);
                return result;
            }
        } else {
            result.setErrorMessage("不支持的文件格式！");
            result.setSuccess(false);
            return result;
        }
        //获取用户信息
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        //fix  解压导致页面刷新不出解压文件
        //逐一把解压后的文件信息存储到数据库
//        for (int i = 0; i < fileEntryNameList.size(); i++) {
//            String entryName = fileEntryNameList.get(i);
//            log.info("文件名：" + entryName);
//            //并发处理文件信息
//            executor.execute(() -> {
//                String totalFileUrl = unzipUrl + entryName;
//                File currentFile = FileOperation.newFile(totalFileUrl);
//                FileBean tempFileBean = new FileBean();
//                UserFile userFile = new UserFile();
//                userFile.setUploadTime(DateUtil.getCurrentTime());
//                userFile.setUserId(sessionUserBean.getUserId());
//                userFile.setFilePath(FileUtil.pathSplitFormat(unzipFileDto.getFilePath() + entryName.replace(currentFile.getName(), "")).replace("\\", "/"));
//                if (currentFile.isDirectory()) {
//                    //文件夹
//                    userFile.setIsDir(1);
//                    userFile.setFileName(currentFile.getName());
//                    tempFileBean.setTimeStampName(currentFile.getName());
//                } else {
//                    //文件
//                    userFile.setIsDir(0);
//                    userFile.setExtendName(FileUtil.getFileType(totalFileUrl));
//                    userFile.setFileName(FileUtil.getFileNameNotExtend(currentFile.getName()));
//                    tempFileBean.setFileSize(currentFile.length());
//                    tempFileBean.setTimeStampName(FileUtil.getFileNameNotExtend(currentFile.getName()));
//                    tempFileBean.setFileUrl(File.separator + (currentFile.getPath()).replace(PathUtil.getStaticPath(), ""));
//                    tempFileBean.setPointCount(1);
//                    fileService.save(tempFileBean);
//                }
//                //持有者id
//                userFile.setFileId(tempFileBean.getFileId());
//                userFile.setDeleteFlag(0);
//                userFileService.save(userFile);
//            });
//
//        }

        fileEntryNameList.parallelStream().forEach(entryName -> {
            log.info("文件名：" + entryName);
            String totalFileUrl = unzipUrl + entryName;
            File currentFile = FileOperation.newFile(totalFileUrl);
            FileBean tempFileBean = new FileBean();
            UserFile userFile = new UserFile();
            userFile.setUploadTime(DateUtil.getCurrentTime());
            userFile.setUserId(sessionUserBean.getUserId());
            userFile.setFilePath(FileUtil.pathSplitFormat(unzipFileDto.getFilePath() + entryName.replace(currentFile.getName(), "")).replace("\\", "/"));
            if (currentFile.isDirectory()) {
                //文件夹
                userFile.setIsDir(1);
                userFile.setFileName(currentFile.getName());
                tempFileBean.setTimeStampName(currentFile.getName());
            } else {
                //文件
                userFile.setIsDir(0);
                userFile.setExtendName(FileUtil.getFileType(totalFileUrl));
                userFile.setFileName(FileUtil.getFileNameNotExtend(currentFile.getName()));
                tempFileBean.setFileSize(currentFile.length());
                tempFileBean.setTimeStampName(FileUtil.getFileNameNotExtend(currentFile.getName()));
                tempFileBean.setFileUrl(File.separator + currentFile.getPath());
                tempFileBean.setPointCount(1);
                fileService.save(tempFileBean);
            }
            //持有者id
            userFile.setFileId(tempFileBean.getFileId());
            userFile.setDeleteFlag(0);
            userFileService.save(userFile);
        });


        result.setSuccess(true);
        return result;
    }


    @ApiOperation(value = "文件移动", notes = "可以移动文件或者目录", tags = {"file"})
    @RequestMapping(value = "/movefile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> moveFile(@RequestBody MoveFileDTO moveFileDto, @RequestHeader("token") String token) {
        RestResult<String> result = new RestResult<String>();
        if (!operationCheck(token).isSuccess()) {
            return operationCheck(token);
        }
        String oldfilePath = moveFileDto.getOldFilePath();
        String newfilePath = moveFileDto.getFilePath();
        String fileName = moveFileDto.getFileName();
        String extendName = moveFileDto.getExtendName();

        userFileService.updateFilepathByFilepath(oldfilePath, newfilePath, fileName, extendName);
        result.setSuccess(true);
        return result;
    }

    @ApiOperation(value = "批量移动文件", notes = "可以同时选择移动多个文件或者目录", tags = {"file"})
    @RequestMapping(value = "/batchmovefile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> batchMoveFile(@RequestBody BatchMoveFileDTO batchMoveFileDto, @RequestHeader("token") String token) {

        RestResult<String> result = new RestResult<String>();
        if (!operationCheck(token).isSuccess()) {
            return operationCheck(token);
        }

        String files = batchMoveFileDto.getFiles();
        String newfilePath = batchMoveFileDto.getFilePath();

        List<UserFile> fileList = JSON.parseArray(files, UserFile.class);

        for (UserFile userFile : fileList) {
            userFileService.updateFilepathByFilepath(userFile.getFilePath(), newfilePath, userFile.getFileName(), userFile.getExtendName());
        }

        result.setData("批量移动文件成功");
        result.setSuccess(true);
        return result;
    }

    public RestResult<String> operationCheck(String token) {
        RestResult<String> result = new RestResult<String>();
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (sessionUserBean == null) {
            result.setSuccess(false);
            result.setErrorMessage("未登录");
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    @ApiOperation(value = "通过文件类型选择文件", notes = "该接口可以实现文件格式分类查看", tags = {"file"})
    @RequestMapping(value = "/selectfilebyfiletype", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<List<Map<String, Object>>> selectFileByFileType(int fileType, Long currentPage, Long pageCount, @RequestHeader("token") String token) {
        RestResult<List<Map<String, Object>>> result = new RestResult<List<Map<String, Object>>>();
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        long userId = sessionUserBean.getUserId();
        List<Map<String, Object>> fileList = new ArrayList<>();
        Long beginCount = 0L;
        if (pageCount == null || currentPage == null) {
            beginCount = 0L;
            pageCount = 10L;
        } else {
            beginCount = (currentPage - 1) * pageCount;
        }

        Long total = 0L;
        if (fileType == FileUtil.OTHER_TYPE) {

            List<String> arrList = new ArrayList<>();
            arrList.addAll(Arrays.asList(FileUtil.DOC_FILE));
            arrList.addAll(Arrays.asList(FileUtil.IMG_FILE));
            arrList.addAll(Arrays.asList(FileUtil.VIDEO_FILE));
            arrList.addAll(Arrays.asList(FileUtil.MUSIC_FILE));
            fileList = userFileService.selectFileNotInExtendNames(arrList, beginCount, pageCount, userId);
            total = userFileService.selectCountNotInExtendNames(arrList, beginCount, pageCount, userId);
        } else {
            fileList = userFileService.selectFileByExtendName(getFileExtendsByType(fileType), beginCount, pageCount, userId);
            total = userFileService.selectCountByExtendName(getFileExtendsByType(fileType), beginCount, pageCount, userId);
        }
        result.setData(fileList);
        result.setTotal(total.intValue());
        result.setSuccess(true);
        return result;
    }

    @ApiOperation(value = "获取文件树", notes = "文件移动的时候需要用到该接口，用来展示目录树，展示机制为饱汉模式", tags = {"file"})
    @RequestMapping(value = "/getfiletree", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<TreeNode> getFileTree(@RequestHeader("token") String token) {
        RestResult<TreeNode> result = new RestResult<TreeNode>();
        UserFile userFile = new UserFile();
        UserBean sessionUserBean = userService.getUserBeanByToken(token);

        userFile.setUserId(sessionUserBean.getUserId());


        List<UserFile> filePathList = userFileService.selectFilePathTreeByUserId(sessionUserBean.getUserId());
        TreeNode resultTreeNode = new TreeNode();
        resultTreeNode.setLabel("/");

        for (int i = 0; i < filePathList.size(); i++) {
            String filePath = filePathList.get(i).getFilePath() + filePathList.get(i).getFileName() + "/";

            Queue<String> queue = new LinkedList<>();

            String[] strArr = filePath.split("/");
            for (int j = 0; j < strArr.length; j++) {
                if (!"".equals(strArr[j]) && strArr[j] != null) {
                    queue.add(strArr[j]);
                }

            }
            if (queue.size() == 0) {
                continue;
            }
            resultTreeNode = insertTreeNode(resultTreeNode, "/", queue);


        }
        result.setSuccess(true);
        result.setData(resultTreeNode);
        return result;
    }

    public TreeNode insertTreeNode(TreeNode treeNode, String filePath, Queue<String> nodeNameQueue) {

        List<TreeNode> childrenTreeNodes = treeNode.getChildren();
        String currentNodeName = nodeNameQueue.peek();
        if (currentNodeName == null) {
            return treeNode;
        }

        Map<String, String> map = new HashMap<>();
        filePath = filePath + currentNodeName + "/";
        map.put("filePath", filePath);

        if (!isExistPath(childrenTreeNodes, currentNodeName)) {  //1、判断有没有该子节点，如果没有则插入
            //插入
            TreeNode resultTreeNode = new TreeNode();


            resultTreeNode.setAttributes(map);
            resultTreeNode.setLabel(nodeNameQueue.poll());
            resultTreeNode.setId(treeid++);

            childrenTreeNodes.add(resultTreeNode);

        } else {  //2、如果有，则跳过
            nodeNameQueue.poll();
        }

        if (nodeNameQueue.size() != 0) {
            for (int i = 0; i < childrenTreeNodes.size(); i++) {

                TreeNode childrenTreeNode = childrenTreeNodes.get(i);
                if (currentNodeName.equals(childrenTreeNode.getLabel())) {
                    childrenTreeNode = insertTreeNode(childrenTreeNode, filePath, nodeNameQueue);
                    childrenTreeNodes.remove(i);
                    childrenTreeNodes.add(childrenTreeNode);
                    treeNode.setChildren(childrenTreeNodes);
                }

            }
        } else {
            treeNode.setChildren(childrenTreeNodes);
        }

        return treeNode;

    }

    public boolean isExistPath(List<TreeNode> childrenTreeNodes, String path) {
        boolean isExistPath = false;

        try {
            for (int i = 0; i < childrenTreeNodes.size(); i++) {
                if (path.equals(childrenTreeNodes.get(i).getLabel())) {
                    isExistPath = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return isExistPath;
    }

    @ApiOperation(value = "文件搜索", notes = "文件搜索", tags = {"file"})
    @GetMapping(value = "/search")
    @ResponseBody
    public RestResult<List<Map<String, Object>>> searchFile(SearchFileDTO searchFileDTO, @RequestHeader("token") String token) {
        log.info("searchFileDTO :" + searchFileDTO);
        RestResult<List<Map<String, Object>>> restResult = new RestResult<>();
        if (token.equals("undefined") || StringUtils.isEmpty(token)) {
            restResult.setSuccess(false);
            restResult.setErrorMessage("用户暂未登录");
        } else {
            UserFile userFile = new UserFile();
            UserBean sessionUserBean = userService.getUserBeanByToken(token);
            if (userFile == null) {
                restResult.setSuccess(false);
                return restResult;
            }
            userFile.setUserId(sessionUserBean.getUserId());
            userFile.setFileName(searchFileDTO.getFileName());
            List<Map<String, Object>> fileList = null;
            //userFile.setFilePath(PathUtil.urlDecode(searchFileDTO.getFilePath()));
            if (searchFileDTO.getCurrentPage() == null || searchFileDTO.getPageCount() == null) {
                fileList = userFileService.userFileList(userFile, 0L, 10L);
            } else {
                Long beginCount = (searchFileDTO.getCurrentPage() - 1) * searchFileDTO.getPageCount();

                fileList = userFileService.searchFileList(userFile, beginCount, searchFileDTO.getPageCount()); //fileService.selectFileListByPath(fileBean);

            }

            LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userFileLambdaQueryWrapper.eq(UserFile::getUserId, userFile.getUserId())
                    .eq(UserFile::getDeleteFlag, 0);
            int total = userFileService.count(userFileLambdaQueryWrapper);
            restResult.setTotal(total);
            restResult.setData(fileList);
            restResult.setSuccess(true);
        }
        return restResult;
    }

}
