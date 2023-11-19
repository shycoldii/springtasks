package com.daleksandrova.springtasks.task1.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Событие "Нужно сдать экзамен по Spring".
 *
 * @author Darya Alexandrova
 * @since 2023.11.16
 */
@Getter
public class NeedToPassSpringExamEvent extends ApplicationEvent {

    /**
     * Сообщение события.
     */
    private final String message;

    /**
     * Конструктор.
     *
     * @param source  источник события
     * @param message сообщение
     */
    public NeedToPassSpringExamEvent(Object source, String message) {
        super(source);
        this.message = message;
    }
}