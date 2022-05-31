package com.keke.cloud.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "userfile", uniqueConstraints = {
        @UniqueConstraint(name = "fileindex", columnNames = {"file_name", "file_path", "extend_name"})})
@Entity
@TableName("userfile")
public class UserFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition = "bigint(20) comment '用户文件id'")
    private Long userFileId;

    @Column(columnDefinition = "bigint(20) comment '用户id'")
    private Long userId;

    @Column(columnDefinition="bigint(20) comment '文件id'")
    private Long fileId;

    @Column(name = "file_name",columnDefinition="varchar(100) comment '文件名'")
    private String fileName;

    @Column(name = "file_path",columnDefinition="varchar(500) comment '文件路径'")
    private String filePath;

    @Column(name = "extend_name",columnDefinition="varchar(100) comment '扩展名'")
    private String extendName;

    @Column(columnDefinition="int(1) comment '是否是目录 0-否, 1-是'")
    private Integer isDir;

    @Column(columnDefinition="varchar(25) comment '上传时间'")
    private String uploadTime;

    @Column(columnDefinition="int(11) comment '文件删除标志 0/null-正常, 1-删除'")
    private Integer deleteFlag;

    @Column(columnDefinition="varchar(25) comment '删除时间'")
    private String deleteTime;

    @Column(columnDefinition = "varchar(50) comment '删除批次号'")
    private String deleteBatchNum;

}
