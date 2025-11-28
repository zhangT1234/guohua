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
@TableName("pcm3_cnt_d")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pcm3CntD implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("phid")
    private String phid;

    @TableField("item_no")
    private String itemNo;

    @TableField("pphid")
    private String pphid;

}
