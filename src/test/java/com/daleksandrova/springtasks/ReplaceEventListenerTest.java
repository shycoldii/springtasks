package com.daleksandrova.springtasks;

import com.daleksandrova.springtasks.event.NeedToPassSpringExamEvent;
import com.daleksandrova.springtasks.listener.NeedToPassSpringExamEventExternalListener;
import com.daleksandrova.springtasks.listener.NeedToPassSpringExamEventMyListener;
import com.daleksandrova.springtasks.publisher.NeedToPassSpringExamEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SmartApplicationListener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Тест для проверки отключения(замены) eventListener-а.
 *
 * @author Darya Alexandrova
 * @since 2023.11.15
 */
@SpringBootTest
public class ReplaceEventListenerTest {

    /**
     * Имя "внешнего" слушателя, которого хотим отключить.
     * Для сложных ситуаций можно было воспользоваться регулярными выражениями, но тут не вижу такой необходимости.
     */
    private final String NAME_OF_EXTERNAL_LISTENER = "onNeedToPassSpringExamEventForExclude(";

    /**
     * Создатель события.
     */
    @Autowired
    private NeedToPassSpringExamEventPublisher needToPassSpringExamEventPublisher;

    /**
     * Управляющий слушателями событий.
     */
    @Autowired
    private ApplicationEventMulticaster applicationEventMulticaster;

    /**
     * Спай-бин для проверок внешнего слушателя.
     */
    @SpyBean
    private NeedToPassSpringExamEventExternalListener needToPassSpringExamEventExternalListener;


    /**
     * Спай-бин для проверок нашего слушателя.
     */
    @SpyBean
    private NeedToPassSpringExamEventMyListener needToPassSpringExamEventMyListener;

    /**
     * Тест, проверяющий самый прямой кейс: без исключений/замен все слушатели работают.
     */
    @Test
    void testEventListeners() {
        // публикуем событие "Нужно сдать экзамен по Spring"
        needToPassSpringExamEventPublisher.publishNeedToPassSpringExamEvent("Really need!");

        // проверяем, что метод внешнего слушателя БЫЛ был вызван
        verify(needToPassSpringExamEventExternalListener, times(1)).onNeedToPassSpringExamEventForExclude(any(NeedToPassSpringExamEvent.class));

        // проверяем, что метод нашего слушателя БЫЛ вызван
        verify(needToPassSpringExamEventMyListener, times(1)).onNeedToPassSpringExamEvent(any(NeedToPassSpringExamEvent.class));
    }

    /**
     * Тест, проверяющий исключение слушателя из "внешней" библиотеки средствами Spring. Самый хороший и честный способ.
     * Для усложнения задачи исключать будем не целый класс, а именно метод-слушатель. При этом только определенный.
     */
    @Test
    void testExcludeEventListenerWithApplicationEventMulticaster() {
        // используем встроенный механизм Spring для исключения события
        // по предикату (к сожалению, по-другому не получится, так как исключать нужно не бин)
        applicationEventMulticaster.removeApplicationListeners(l -> l instanceof SmartApplicationListener &&
                ((SmartApplicationListener) l).getListenerId().contains(NAME_OF_EXTERNAL_LISTENER));

        // публикуем событие "Нужно сдать экзамен по Spring"
        needToPassSpringExamEventPublisher.publishNeedToPassSpringExamEvent("Really need!");

        // проверяем, что метод внешнего слушателя НЕ был вызван
        verify(needToPassSpringExamEventExternalListener, times(0)).onNeedToPassSpringExamEventForExclude(any(NeedToPassSpringExamEvent.class));

        // проверяем, что метод нашего слушателя БЫЛ вызван
        verify(needToPassSpringExamEventMyListener, times(1)).onNeedToPassSpringExamEvent(any(NeedToPassSpringExamEvent.class));

        // честная замена произошла :)
    }

    /**
     * Тест, проверяющий исключение слушателя из "внешней" библиотеки средствами AOP.
     * Способ работает, но это "костыль".
     * По факту, для контейнера слушатель существует в самом полном объеме,
     * просто мы перехватываем для своей бизнес-логики его вызов.
     */
    @Test
    void testExcludeEventListenerWithAspect() {
        // публикуем событие "Нужно сдать экзамен по Spring"
        needToPassSpringExamEventPublisher.publishNeedToPassSpringExamEvent("Really need!");

        // проверяем, что метод внешнего слушателя НЕ был вызван
        verify(needToPassSpringExamEventExternalListener, times(0)).onNeedToPassSpringExamEventForAspect(any(NeedToPassSpringExamEvent.class));

        // проверяем, что метод нашего слушателя БЫЛ вызван
        verify(needToPassSpringExamEventMyListener, times(1)).onNeedToPassSpringExamEventForAspect(any(NeedToPassSpringExamEvent.class));

        // нечестная замена произошла :)
    }
}
