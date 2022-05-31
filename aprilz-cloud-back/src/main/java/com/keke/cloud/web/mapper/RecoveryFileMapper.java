package com.keke.cloud.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keke.cloud.web.domain.RecoveryFile;
import com.keke.cloud.web.vo.RecoveryFileListVo;

import java.util.List;


public interface RecoveryFileMapper extends BaseMapper<RecoveryFile> {
    List<RecoveryFileListVo> selectRecoveryFileList(long userId);
}
