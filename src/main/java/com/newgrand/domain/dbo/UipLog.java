package com.newgrand.domain.dbo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @创建人：ZhaoFengjie
 * @修改人：ZhaoFengjie
 * @创建时间：13:56 2022/11/10
 * @修改时间:：13:56 2022/11/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("uip_log")
@ApiModel(description = "日志")
public class UipLog {

    @TableId
    @ApiModelProperty("主键")
    private String phid;

    @ApiModelProperty("级别 info,debug,warn,error")
    private String level;

    @ApiModelProperty("标识")
    private String sign;

    @ApiModelProperty("描述")
    private String describe;

    @ApiModelProperty("详情")
    private String info;

    @ApiModelProperty("关联表单名称")
    private String relation_table;

    @ApiModelProperty("关联表单Id")
    private String relation_id;

    @ApiModelProperty("更新时间")
    private Date ng_update_dt;

    @ApiModelProperty("新增时间")
    private Date ng_insert_dt;
}
