package com.newgrand.domain.dbo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("act_ng_log")
public class ActNgLog implements Serializable {
    @TableId
    private String phid;

    private String type;

    private String user_id;

    private java.util.Date crate_time;

    private String proc_int_id;

    private String info;

}
