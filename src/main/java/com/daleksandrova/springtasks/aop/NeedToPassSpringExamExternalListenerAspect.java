package com.daleksandrova.springtasks.aop;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Аспект для перехвата вызова метода внешнего слушателя события NeedToPassSpringExamEvent.
 *
 * @author Darya Alexandrova
 * @since 2023.11.16
 */
@Aspect
@Component
public class NeedToPassSpringExamExternalListenerAspect {

    /**
     * Логгер.
     */
    private static final Logger LOG = LoggerFactory.getLogger(NeedToPassSpringExamExternalListenerAspect.class);

    /**
     * Around advice вокруг метода внешнего листенера. Цель: отключить логику этого листенера.
     */
    @Around("execution(* com.daleksandrova.springtasks.listener.NeedToPassSpringExamEventExternalListener.onNeedToPassSpringExamEventForAspect(..))")
    public void aroundNeedToPassSpringExamExternalListener() {
        LOG.info("Aspect is working...");
        // ничего не делаем
    }
}
