package com.newgrand.domain.dbo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: zhanglixin
 * @Data: 2022/11/16 12:42
 * @Description: TODO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("uip_common")
public class UIPCommon implements Serializable {

    @TableId
    private String phid;
}