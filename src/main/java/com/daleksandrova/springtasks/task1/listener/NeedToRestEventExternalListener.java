package com.daleksandrova.springtasks.task1.listener;

import com.daleksandrova.springtasks.task1.event.NeedToRestEvent;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Слушатель события "Нужно отдохнуть". Приходит к нам извне, формально, readonly.
 *
 * @author Darya Alexandrova
 * @since 2023.11.15
 */
@Component
@Getter
public class NeedToRestEventExternalListener {

    /**
     * Часы отдыха.
     */
    protected int hoursOfRest = 0;

    /**
     * Логгер.
     */
    private static final Logger LOG = LoggerFactory.getLogger(NeedToRestEventExternalListener.class);

    /**
     * Слушатель события "Нужно отдохнуть".
     *
     * @param needToRestEvent событие
     */
    @EventListener
    public void onNeedToRestEvent(NeedToRestEvent needToRestEvent) {
        LOG.info("EventListener NeedToRestEventExternalListener onNeedToRestEvent is working...");
        LOG.info(this.getClass().getName());
        hoursOfRest += 10;
    }

    /**
     * Слушатель события "Нужно отдохнуть", который не будем переопределять.
     *
     * @param needToRestEvent событие
     */
    @EventListener
    public void onNeedToRestEventWithoutOverriding(NeedToRestEvent needToRestEvent) {
        LOG.info("EventListener NeedToRestEventExternalListener onNeedToRestEventWithoutOverriding is working...");
        LOG.info(this.getClass().getName());
        hoursOfRest += 1;
    }
}
