package com.keke.cloud.web.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keke.cloud.web.domain.FileBean;

import java.util.List;

public interface FileMapper extends BaseMapper<FileBean> {


    void batchInsertFile(List<FileBean> fileBeanList);
//    void updateFile(FileBean fileBean);






}
