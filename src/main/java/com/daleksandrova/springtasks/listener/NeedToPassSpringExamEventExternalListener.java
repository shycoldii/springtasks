package com.daleksandrova.springtasks.listener;

import com.daleksandrova.springtasks.event.NeedToPassSpringExamEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Слушатель события "Нужно сдать экзамен по Spring". Приходит к нам извне, формально, readonly.
 *
 * @author Darya Alexandrova
 * @since 2023.11.15
 */
@Component
public class NeedToPassSpringExamEventExternalListener {

    /**
     * Логгер.
     */
    private static final Logger LOG = LoggerFactory.getLogger(NeedToPassSpringExamEventExternalListener.class);

    /**
     * Слушатель события "Нужно сдать экзамен по Spring".
     *
     * @param needToPassSpringExamEvent событие
     */
    @EventListener
    public void onNeedToPassSpringExamEventForExclude(NeedToPassSpringExamEvent needToPassSpringExamEvent) {
        LOG.info("EventListener NeedToPassSpringExamEventExternalListener onNeedToPassSpringExamEventForExclude is working...");
    }

    /**
     * Слушатель события "Нужно сдать экзамен по Spring".
     *
     * @param needToPassSpringExamEvent событие
     */
    @EventListener
    public void onNeedToPassSpringExamEventForAspect(NeedToPassSpringExamEvent needToPassSpringExamEvent) {
        LOG.info("EventListener NeedToPassSpringExamEventExternalListener onNeedToPassSpringExamEventForAspect is working...");
    }
}
