package com.daleksandrova.springtasks.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Утилита для инъкций зависимостей.
 *
 * @author Darya Alexandrova
 * @since 2023.11.18
 */
@Component
public class AutowireHelper implements ApplicationContextAware {

    /**
     * Логгер.
     */
    private static final Logger LOG = LoggerFactory.getLogger(AutowireHelper.class.getName());

    /**
     * Контекст.
     */
    @Nullable
    private static ApplicationContext applicationContext;

    /**
     * Дефолтный конструктор.
     */
    private AutowireHelper() {
    }

    /**
     * Инъекция зависимостей в класс.
     *
     * @param objectToAutowire       класс, которому нужна инъекция
     * @param beansToAutowireInClass объекты, которые нужно внедрить
     */
    public static void autowire(Object objectToAutowire, Object... beansToAutowireInClass) {
        for (Object bean : beansToAutowireInClass) {
            if (bean == null) {
                (Objects.requireNonNull(applicationContext)).getAutowireCapableBeanFactory().autowireBean(objectToAutowire);
                return;
            }
        }

    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        if (AutowireHelper.applicationContext == null) {
            AutowireHelper.applicationContext = applicationContext;
        } else {
            LOG.warn("Setting application context failed: application context is already set in AutowireHelper");
        }

    }
}