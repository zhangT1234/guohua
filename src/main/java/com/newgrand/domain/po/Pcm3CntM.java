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
@TableName("pcm3_cnt_m")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pcm3CntM implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("phid")
    private String phid;

    @TableField("bill_no")
    private String billNo;

    @TableField("title")
    private String title;

    @TableField("cnt_type")
    private String cntType;

}
