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
 * @Data: 2022/9/5 15:17
 * @Description: TODO
 */
@Data
@TableName("hr_epm_base")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HrEpmBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("phid")
    private String phid;

    @TableField("systype")
    private String systype;

    @TableField("birthday")
    private String birthday;

    @TableField("must_phid")
    private String mustPhid;

}
