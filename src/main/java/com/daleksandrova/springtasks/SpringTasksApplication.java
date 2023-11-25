package com.daleksandrova.springtasks;

import com.daleksandrova.springtasks.task1.listener.NeedToRestEventExternalListener;
import com.daleksandrova.springtasks.task1.listener.NeedToRestEventMyListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Главный класс приложения.
 *
 * @author Darya Alexandrova
 * @since 2023.11.15
 */
@SpringBootApplication
@EnableJpaAuditing
public class SpringTasksApplication {

    /**
     * Переопределяем бин здесь явно, а не в имени аннотации Component,
     * иначе упадем с ConflictingBeanDefinitionException при парсинге.
     *
     * @return needToRestEventMyListener
     */
    @Bean
    public NeedToRestEventExternalListener needToRestEventExternalListener() {
        return new NeedToRestEventMyListener();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringTasksApplication.class);
    }
}
