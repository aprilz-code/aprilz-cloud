package com.keke.cloud.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.keke.cloud.web.domain.RecoveryFile;
import com.keke.cloud.web.domain.UserFile;
import com.keke.cloud.web.vo.RecoveryFileListVo;

import java.util.List;

public interface IRecoveryFileService extends IService<RecoveryFile> {
    public void deleteRecoveryFile(UserFile userFile);
    List<RecoveryFileListVo> selectRecoveryFileList(Long userId);
}
