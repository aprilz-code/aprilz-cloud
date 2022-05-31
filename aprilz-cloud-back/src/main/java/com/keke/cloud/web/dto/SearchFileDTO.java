package com.keke.cloud.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@ApiModel(value = "查询文件列表DTO")
public class SearchFileDTO {
    @ApiModelProperty(value = "查询文件名称")
    private String fileName;
    @ApiModelProperty(value = "当前页码")
    private Long currentPage;
    @ApiModelProperty(value = "一页显示数量")
    private Long pageCount;
    @ApiModelProperty(value = "排序")
    private String order;
    private String direction;
}
