package com.daleksandrova.springtasks.task1.listener;

import com.daleksandrova.springtasks.task1.event.NeedToRestEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Слушатель события "Нужно отдохнуть". Написан в этом проекте, его можно кастомизировать.
 *
 * @author Darya Alexandrova
 * @since 2023.11.15
 */
public class NeedToRestEventMyListener extends NeedToRestEventExternalListener {

    /**
     * Логгер.
     */
    private static final Logger LOG = LoggerFactory.getLogger(NeedToRestEventMyListener.class);

    /**
     * Слушатель события "Нужно отдохнуть".
     *
     * @param needToRestEvent событие
     */
    //@EventListener
    public void onNeedToRestEvent(NeedToRestEvent needToRestEvent) {
        LOG.info("EventListener NeedToRestEventMyListener onNeedToRestEvent is working...");
        hoursOfRest += 5;
    }
}
