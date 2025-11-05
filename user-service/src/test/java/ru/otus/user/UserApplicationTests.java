package ru.otus.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class UserApplicationTests {

    @Test
    void contextLoads(ApplicationContext context) {
        // Проверяем что контекст Spring загружается
        assertNotNull(context);
    }

    @Test
    void mainMethodStartsApplication() {
        // Проверяем что приложение запускается
        UserApplication.main(new String[]{});
    }
}