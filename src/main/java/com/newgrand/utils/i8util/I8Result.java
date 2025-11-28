package com.newgrand.utils.i8util;

import com.newgrand.domain.model.I8ReturnModel;

import java.io.Serializable;

/**
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/8/11 16:43
 */
public class I8Result<T> extends I8ReturnModel implements Serializable {
    private T result;

    public I8Result() {
    }

    public T getResult() {
        return this.result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
