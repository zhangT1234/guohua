package com.newgrand.aop;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.newgrand.utils.filter.NoRepeatSubmit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;


/**
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/5/19 13:34
 */
@Aspect
@Configuration
public class LockMethodInterceptor {

    private static final Cache<String, Object> CACHES = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(5, TimeUnit.SECONDS).build();

    @Around(value = "execution(public * *(..)) && @annotation(com.newgrand.aop.NoRepeatSubmit)")
    public Object interceptor(ProceedingJoinPoint pjp) throws Exception {
        //get Method by aop
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        //generate a key by Method
        String key = generateKey(pjp, method);

        if (!StringUtils.isEmpty(key)) {
            if (CACHES.getIfPresent(key) != null) {
                //if key exists in caches
                throw new RuntimeException("重复提交");
            } else {
                //put key into caches when first time commit
                CACHES.put(key, key);
            }
        }

        //normal business
        try {
            Object[] args = pjp.getArgs();
            return pjp.proceed(args);
        } catch (Throwable throwable) {
            throw new Exception("服务器内部错误");
        }
    }

    /**
     * generate a key by Method sign
     * key : className + functionName + args
     */
    private String generateKey(ProceedingJoinPoint pjp, Method method) {
        KeyGenerator keyGenerator = new SimpleKeyGenerator();
        NoRepeatSubmit localLock = method.getAnnotation(NoRepeatSubmit.class);
        return localLock.key() + keyGenerator.generate(pjp.getTarget(), method, pjp.getArgs());
    }
}