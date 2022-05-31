package com.keke.cloud.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "回收文件列表Vo")
public class RecoveryFileListVo {
    @ApiModelProperty(value = "回收文件id", example = "1")
    private Long recoveryFileId;
    @ApiModelProperty(value = "id", example = "1")
    private Long userFileId;
    @ApiModelProperty(value = "userId", example = "1")
    private Long userId;
    @ApiModelProperty(value = "fileId", example = "1")
    private Long fileId;
    @ApiModelProperty(value = "文件名", example = "图片")
    private String fileName;
    @ApiModelProperty(value = "文件路径", example = "upload/bddd/caaa")
    private String filePath;
    @ApiModelProperty(value = "文件扩展名", example = "zip")
    private String extendName;
    @ApiModelProperty(value = "是否是目录，1-是，0-否", example = "1")
    private Integer isDir;
    @ApiModelProperty(value = "上传时间", example = "2020-10-10 12:21:22")
    private String uploadTime;
    @ApiModelProperty(value = "删除标志", example = "1")
    private Integer deleteFlag;
    @ApiModelProperty(value = "删除时间", example = "2020-10-10 12:21:22")
    private String deleteTime;
    @ApiModelProperty(value = "删除批次号", example = "1111-222-22")
    private String deleteBatchNum;
}
