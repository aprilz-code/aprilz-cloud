package com.keke.cloud.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keke.cloud.web.domain.FileBean;
import com.keke.cloud.web.domain.RecoveryFile;
import com.keke.cloud.web.domain.UserFile;
import com.keke.cloud.web.mapper.FileMapper;
import com.keke.cloud.web.mapper.RecoveryFileMapper;
import com.keke.cloud.web.mapper.UserFileMapper;
import com.keke.cloud.web.service.IRecoveryFileService;
import com.keke.cloud.web.vo.RecoveryFileListVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class RecoveryFileService  extends ServiceImpl<RecoveryFileMapper, RecoveryFile> implements IRecoveryFileService {
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    FileMapper fileMapper;
    @Resource
    RecoveryFileMapper recoveryFileMapper;

    public static Executor executor = Executors.newFixedThreadPool(20);

    @Override
    public void deleteRecoveryFile(UserFile userFile) {
        if (userFile.getIsDir() == 1) {
            updateFilePointCountByBatchNum(userFile.getDeleteBatchNum());

        }else{

            UserFile userFileTemp = userFileMapper.selectById(userFile.getUserFileId());
            FileBean fileBean = fileMapper.selectById(userFileTemp.getFileId());

            LambdaUpdateWrapper<FileBean> fileBeanLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            fileBeanLambdaUpdateWrapper.set(FileBean::getPointCount, fileBean.getPointCount() -1)
                    .eq(FileBean::getFileId, fileBean.getFileId());

            fileMapper.update(null, fileBeanLambdaUpdateWrapper);
        }
        LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userFileLambdaQueryWrapper.eq(UserFile::getDeleteBatchNum, userFile.getDeleteBatchNum());
        userFileMapper.delete(userFileLambdaQueryWrapper);



    }


    private void updateFilePointCountByBatchNum(String deleteBatchNum) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getDeleteBatchNum, deleteBatchNum);
        List<UserFile> fileList = userFileMapper.selectList(lambdaQueryWrapper);

        new Thread(()->{
            for (int i = 0; i < fileList.size(); i++){
                UserFile userFileTemp = fileList.get(i);
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (userFileTemp.getIsDir() != 1){
                            FileBean fileBean = fileMapper.selectById(userFileTemp.getFileId());
                            if (fileBean.getPointCount() != null) {

                                LambdaUpdateWrapper<FileBean> fileBeanLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                                fileBeanLambdaUpdateWrapper.set(FileBean::getPointCount, fileBean.getPointCount() -1)
                                        .eq(FileBean::getFileId, fileBean.getFileId());
                                fileMapper.update(null, fileBeanLambdaUpdateWrapper);

                            }
                        }
                    }
                });

            }
        }).start();
    }

    public List<RecoveryFileListVo> selectRecoveryFileList(Long userId) {
        return recoveryFileMapper.selectRecoveryFileList(userId);
    }
}
