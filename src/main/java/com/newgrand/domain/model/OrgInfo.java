package com.newgrand.domain.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: zhanglixin
 * @Data: 2022/9/12 14:43
 * @Description: TODO
 */
@Setter
@Getter
public class OrgInfo {
    public String ocode;

    public String oname;

    public String orgtype;

    public String parentocode;

    public String isactive;

    public String ifcorp;

    public String codevalue;

    public String bopomofo;

    public String unisocialcredit;

    //组织性质 默认为空
    public String orgnatura;
}
