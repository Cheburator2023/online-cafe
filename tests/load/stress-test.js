import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';

// Метрики
const responseTimeTrend = new Trend('response_time');
const errorRate = new Rate('error_rate');

// Конфигурация стресс-теста
export const options = {
    stages: [
        { duration: '1m', target: 10 },    // Низкая нагрузка
        { duration: '2m', target: 50 },    // Средняя нагрузка
        { duration: '3m', target: 100 },   // Высокая нагрузка
        { duration: '2m', target: 150 },   // Пиковая нагрузка
        { duration: '2m', target: 200 },   // Стрессовая нагрузка
        { duration: '2m', target: 0 },     // Восстановление
    ],
    thresholds: {
        http_req_duration: ['p(95)<3000', 'p(99)<5000'],
        http_req_failed: ['rate<0.1'],
        error_rate: ['rate<0.2']
    }
};

const BASE_URL = 'http://localhost:8080';
const ENDPOINTS = [
    '/api/menu',
    '/api/user/api/v1/users',
    '/api/orders',
    '/api/payments',
    '/actuator/health'
];

export default function () {
    // Случайный выбор endpoint для тестирования
    const endpoint = ENDPOINTS[Math.floor(Math.random() * ENDPOINTS.length)];
    const url = `${BASE_URL}${endpoint}`;

    let res;
    const startTime = Date.now();

    switch (endpoint) {
        case '/api/user/api/v1/users':
            // POST для создания пользователя
            const userData = {
                name: `Stress Test User ${__VU}-${__ITER}`,
                email: `stress_${Date.now()}@test.com`
            };
            res = http.post(url, JSON.stringify(userData), {
                headers: { 'Content-Type': 'application/json' }
            });
            break;

        case '/api/orders':
            // GET для получения заказов
            res = http.get(url);
            break;

        case '/api/payments':
            // GET для получения платежей
            res = http.get(url);
            break;

        default:
            // GET для остальных endpoints
            res = http.get(url);
    }

    const responseTime = Date.now() - startTime;
    responseTimeTrend.add(responseTime);

    const isSuccess = check(res, {
        'status is 2xx or 3xx': (r) => r.status >= 200 && r.status < 400
    });

    if (!isSuccess) {
        errorRate.add(1);
    }

    sleep(Math.random() * 3 + 1);
}