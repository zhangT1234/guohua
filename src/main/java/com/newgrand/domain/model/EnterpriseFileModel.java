package com.newgrand.domain.model;

import lombok.Data;

import java.util.List;

@Data
public class EnterpriseFileModel {

    //银行账号
    private String bankAccount;
    //开户行
    private String openBank;

    private FileModel bankFileInfo;
    private FileModel fileInfo;
    private List<FileModel> suppliperFileList;
    private List<FileModel> otherFileList;

}
