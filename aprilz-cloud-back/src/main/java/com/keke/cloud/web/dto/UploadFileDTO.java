package com.keke.cloud.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "上传文件DTO")
public class UploadFileDTO {

    @ApiModelProperty(value = "文件路径")
    private String filePath;

    /**
     * 上传时间
     */
    @ApiModelProperty(value = "上传时间")
    private String uploadTime;

    /**
     * 扩展名
     */
    @ApiModelProperty(value = "扩展名")
    private String extendName;


    @ApiModelProperty(value = "文件名")
    private String filename;

    /**
     * 文件大小
     */
    @ApiModelProperty(value = "文件大小")
    private Long fileSize;

    @ApiModelProperty(value = "切片数量")
    private int chunkNumber;

    @ApiModelProperty(value = "切片大小")
    private long chunkSize;

    @ApiModelProperty(value = "所有切片")
    private int totalChunks;
    @ApiModelProperty(value = "总大小")
    private long totalSize;
    @ApiModelProperty(value = "当前切片大小")
    private long currentChunkSize;
    @ApiModelProperty(value = "md5码")
    private String identifier;

    @ApiModelProperty(value = "isOss")
    private Integer isOss;

}
