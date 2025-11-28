package com.newgrand.domain.dbo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("uip_query")
@ApiModel(description = "配置表单")
public class UipQuery implements Serializable {

    @TableId
    @ApiModelProperty("主键")
    private String phid;

    @ApiModelProperty("父级主键")
    private String pphid;

    @ApiModelProperty("标识")
    private String bill_no;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("属性 form:表头 table:标题 string:字符串")
    private String formtype;

    @ApiModelProperty("别名")
    private String asname;

    @ApiModelProperty("表名")
    private String tablename;

    @ApiModelProperty("单点登录地址")
    private String ssourl;

    @ApiModelProperty("取数语句")
    private String sqlstr;

    @ApiModelProperty("更新时间")
    private Date ng_update_dt;

    @ApiModelProperty("新增时间")
    private Date ng_insert_dt;

}
