package com.newgrand.utils.exception;


import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.utils.i8util.I8ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public I8ReturnModel handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                             HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        ////dbLog.error("HttpRequestMethodNotSupportedException", "请求方式不支持", "请求地址'" + requestURI + "',不支持'" + e.getMethod() + "'请求");
        return I8ResultUtil.error(e.getMessage());
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public I8ReturnModel handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        ////dbLog.error("RuntimeException", "请求地址'" + requestURI + "',发生未知异常.", e.getMessage());
        return I8ResultUtil.error(e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public I8ReturnModel handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        //dbLog.error("Exception", "请求地址'" + requestURI + "',发生系统异常", e.getMessage());
        return I8ResultUtil.error(e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public I8ReturnModel handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        //dbLog.error("BindException", "自定义验证异常", message);
        return I8ResultUtil.error(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public I8ReturnModel handleValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        ////dbLog.error("MethodArgumentNotValidException", "请求参数校验异常", e.getBindingResult().getFieldError().getDefaultMessage());
        return I8ResultUtil.error("保存失败：" + e.getBindingResult().getFieldError().getDefaultMessage());
    }
}
