package com.daleksandrova.springtasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

    public static void main(String[] args) {
        SpringApplication.run(SpringTasksApplication.class);
    }
}
