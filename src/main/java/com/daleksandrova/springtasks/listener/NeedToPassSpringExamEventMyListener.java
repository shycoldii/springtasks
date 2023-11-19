package com.daleksandrova.springtasks.listener;

import com.daleksandrova.springtasks.event.NeedToPassSpringExamEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Слушатель события "Нужно сдать экзамен по Spring". Написан в этом проекте, его можно кастомизировать.
 *
 * @author Darya Alexandrova
 * @since 2023.11.15
 */
@Component
public class NeedToPassSpringExamEventMyListener {

    /**
     * Логгер.
     */
    private static final Logger LOG = LoggerFactory.getLogger(NeedToPassSpringExamEventMyListener.class);

    /**
     * Слушатель события "Нужно сдать экзамен по Spring".
     *
     * @param needToPassSpringExamEvent событие
     */
    @EventListener
    public void onNeedToPassSpringExamEvent(NeedToPassSpringExamEvent needToPassSpringExamEvent) {
        LOG.info("EventListener NeedToPassSpringExamEventMyListener onNeedToPassSpringExamEvent is working...");
    }

    /**
     * Слушатель события "Нужно сдать экзамен по Spring".
     *
     * @param needToPassSpringExamEvent событие
     */
    @EventListener
    public void onNeedToPassSpringExamEventForAspect(NeedToPassSpringExamEvent needToPassSpringExamEvent) {
        LOG.info("EventListener NeedToPassSpringExamEventMyListener onNeedToPassSpringExamEventForAspect is working...");
    }
}
