import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 1,
    iterations: 1,
    duration: '10s'
};

const BASE_URL = 'http://localhost:8080';

export default function () {
    // Тест 1: Проверка здоровья API Gateway
    let res = http.get(`${BASE_URL}/actuator/health`);
    check(res, {
        'API Gateway is healthy': (r) => r.status === 200 && r.json().status === 'UP'
    });

    // Тест 2: Получение меню
    res = http.get(`${BASE_URL}/api/menu`);
    check(res, {
        'Menu returns 200': (r) => r.status === 200,
        'Menu has items': (r) => r.json() && r.json().data && r.json().data.length > 0
    });

    // Тест 3: Проверка Eureka
    res = http.get('http://localhost:8761/eureka/apps');
    check(res, {
        'Eureka is accessible': (r) => r.status === 200
    });

    // Тест 4: Проверка Swagger UI
    res = http.get(`${BASE_URL}/swagger-ui.html`);
    check(res, {
        'Swagger UI is accessible': (r) => r.status === 200
    });

    console.log('✅ Все базовые тесты пройдены успешно');
}