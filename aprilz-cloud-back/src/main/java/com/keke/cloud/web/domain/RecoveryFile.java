package com.keke.cloud.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "recoveryfile")
@Entity
@TableName("recoveryfile")
public class RecoveryFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition="bigint(20) comment '回收文件id'")
    private Long recoveryFileId;
    @Column(columnDefinition = "bigint(20) comment '用户文件id'")
    private Long userFileId;
    @Column(columnDefinition="varchar(25) comment '删除时间'")
    private String deleteTime;
    @Column(columnDefinition = "varchar(50) comment '删除批次号'")
    private String deleteBatchNum;
}
