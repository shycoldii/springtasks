package com.daleksandrova.springtasks.task1.publisher;

import com.daleksandrova.springtasks.task1.event.NeedToRestEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Создатель события "Нужно отдохнуть".
 *
 * @author Darya Alexandrova
 * @since 2023.11.15
 */
@Component
public class NeedToRestEventPublisher {

    /**
     * Создатель событий приложения.
     */
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Конструктор.
     *
     * @param applicationEventPublisher создатель событий приложения
     */
    public NeedToRestEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Публикует событие "Нужно отдохнуть".
     *
     * @param message сообщение
     */
    public void publishNeedToRestEvent(final String message) {
        NeedToRestEvent needToRestEvent = new NeedToRestEvent(this, message);
        applicationEventPublisher.publishEvent(needToRestEvent);
    }
}