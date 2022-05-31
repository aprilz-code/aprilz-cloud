package com.keke.cloud.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.keke.cloud.web.domain.UserBean;
import com.keke.cloud.web.domain.UserFile;

import java.util.List;
import java.util.Map;

public interface IUserFileService extends IService<UserFile> {
    //根据文件名和路径查询
    List<UserFile> selectUserFileByNameAndPath(String fileName, String filePath, Long userId);
    void replaceUserFilePath(String filePath, String oldFilePath, Long userId);
    List<Map<String, Object>> userFileList(UserFile userFile, Long beginCount, Long pageCount);
    List<Map<String, Object>> searchFileList(UserFile userFile, Long beginCount, Long pageCount);
    void updateFilepathByFilepath(String oldfilePath, String newfilePath, String fileName, String extendName);

//    List<Map<String, Object>> selectFileByExtendName(List<String> fileNameList, long userId);
//    List<Map<String, Object>> selectFileNotInExtendNames(List<String> fileNameList, long userId);
    //根据文件类型查询
    List<Map<String, Object>> selectFileByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, long userId);
    Long selectCountByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, long userId);
    List<Map<String, Object>> selectFileNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, long userId);
    Long selectCountNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, long userId);
    List<UserFile> selectFileTreeListLikeFilePath(String filePath);
    List<UserFile> selectFilePathTreeByUserId(Long userId);
    void deleteUserFile(UserFile userFile, UserBean sessionUserBean);

}
