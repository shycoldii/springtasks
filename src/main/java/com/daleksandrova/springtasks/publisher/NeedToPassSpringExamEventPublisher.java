package com.daleksandrova.springtasks.publisher;

import com.daleksandrova.springtasks.event.NeedToPassSpringExamEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Создатель события "Нужно сдать экзамен по Spring".
 *
 * @author Darya Alexandrova
 * @since 2023.11.15
 */
@Component
public class NeedToPassSpringExamEventPublisher {

    /**
     * Создатель событий приложения.
     */
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Конструктор.
     *
     * @param applicationEventPublisher создатель событий приложения
     */
    public NeedToPassSpringExamEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Публикует событие "Нужно сдать экзамен по Spring".
     *
     * @param message сообщение
     */
    public void publishNeedToPassSpringExamEvent(final String message) {
        NeedToPassSpringExamEvent needToPassSpringExamEvent = new NeedToPassSpringExamEvent(this, message);
        applicationEventPublisher.publishEvent(needToPassSpringExamEvent);
    }
}