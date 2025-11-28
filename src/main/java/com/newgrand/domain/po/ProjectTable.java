package com.newgrand.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@TableName("project_table")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTable implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("phid")
    private String phid;

    @TableField("bill_no")
    private String billNo;

}
