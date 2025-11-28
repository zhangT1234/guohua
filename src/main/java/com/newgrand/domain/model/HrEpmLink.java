package com.newgrand.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: zhanglixin
 * @Data: 2022/9/5 15:20
 * @Description: TODO
 */
@Data
@TableName("hr_epm_link")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HrEpmLink implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("phid")
    private String phid;

    @TableField("systype")
    private String systype;

    @TableField("mobile1")
    private String mobile1;

    @TableField("must_phid")
    private String mustPhid;

    @TableField("email")
    private String email;
}
