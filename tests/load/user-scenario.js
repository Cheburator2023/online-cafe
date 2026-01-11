import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Trend, Rate, Counter } from 'k6/metrics';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

// Кастомные метрики
const userCreationTrend = new Trend('user_creation_time');
const menuFetchTrend = new Trend('menu_fetch_time');
const orderCreationTrend = new Trend('order_creation_time');
const paymentProcessingTrend = new Trend('payment_processing_time');
const errorRate = new Rate('errors');
const successCounter = new Counter('successful_transactions');

// Конфигурация
export const options = {
    stages: [
        { duration: '2m', target: 50 },  // Постепенный рост до 50 пользователей
        { duration: '5m', target: 50 },  // Стабильная нагрузка
        { duration: '2m', target: 100 }, // Увеличение до 100 пользователей
        { duration: '5m', target: 100 }, // Пиковая нагрузка
        { duration: '2m', target: 0 },   // Постепенное снижение
    ],
    thresholds: {
        http_req_duration: ['p(95)<2000'], // 95% запросов должны быть быстрее 2 секунд
        http_req_failed: ['rate<0.05'],    // Менее 5% ошибок
        errors: ['rate<0.1'],              // Менее 10% кастомных ошибок
    },
    ext: {
        loadimpact: {
            projectID: 12345,
            name: 'Online Cafe Load Test'
        }
    }
};

// Базовые URL
const BASE_URL = 'http://localhost:8080';
const API_PATHS = {
    user: '/api/user',
    menu: '/api/menu',
    order: '/api/orders',
    payment: '/api/payments'
};

// Генератор данных
function generateUserData(vuId) {
    return {
        name: `Test User ${vuId}-${Date.now()}`,
        email: `user${vuId}_${Date.now()}@test.com`
    };
}

function generateOrderData(userId, menuItemId) {
    return {
        userId: userId,
        items: [
            {
                menuItemId: menuItemId,
                quantity: Math.floor(Math.random() * 3) + 1
            }
        ],
        specialInstructions: `Special instructions from VU ${__VU}`
    };
}

function generatePaymentData(orderId, userId, amount) {
    return {
        orderId: orderId,
        userId: userId,
        amount: amount,
        paymentMethod: Math.random() > 0.5 ? 'CREDIT_CARD' : 'PAYPAL'
    };
}

// Основной сценарий
export default function () {
    let userId, orderId, paymentId;
    let menuItems = [];
    let selectedMenuItem = null;

    group('01. Получение меню', () => {
        const startTime = Date.now();
        const res = http.get(`${BASE_URL}${API_PATHS.menu}`);
        menuFetchTrend.add(Date.now() - startTime);

        check(res, {
            'status is 200': (r) => r.status === 200,
            'response has menu items': (r) => r.json() && r.json().data && r.json().data.length > 0
        }) || errorRate.add(1);

        if (res.status === 200 && res.json().data) {
            menuItems = res.json().data;
            selectedMenuItem = menuItems[Math.floor(Math.random() * menuItems.length)];
            successCounter.add(1);
        }
    });

    sleep(Math.random() * 2);

    group('02. Создание пользователя', () => {
        const userData = generateUserData(__VU);
        const startTime = Date.now();
        const res = http.post(
            `${BASE_URL}${API_PATHS.user}/api/v1/users`,
            JSON.stringify(userData),
            { headers: { 'Content-Type': 'application/json' } }
        );
        userCreationTrend.add(Date.now() - startTime);

        check(res, {
            'status is 201': (r) => r.status === 201,
            'user created successfully': (r) => r.json() && r.json().data && r.json().data.id
        }) || errorRate.add(1);

        if (res.status === 201) {
            userId = res.json().data.id;
            successCounter.add(1);
        }
    });

    sleep(Math.random() * 1);

    if (userId && selectedMenuItem) {
        group('03. Создание заказа', () => {
            const orderData = generateOrderData(userId, selectedMenuItem.id);
            const startTime = Date.now();
            const res = http.post(
                `${BASE_URL}${API_PATHS.order}`,
                JSON.stringify(orderData),
                { headers: { 'Content-Type': 'application/json' } }
            );
            orderCreationTrend.add(Date.now() - startTime);

            check(res, {
                'status is 200': (r) => r.status === 200,
                'order created successfully': (r) => r.json() && r.json().data && r.json().data.id
            }) || errorRate.add(1);

            if (res.status === 200) {
                orderId = res.json().data.id;
                successCounter.add(1);
            }
        });

        sleep(Math.random() * 2);

        if (orderId) {
            group('04. Обработка платежа', () => {
                const paymentData = generatePaymentData(
                    orderId,
                    userId,
                    selectedMenuItem.price * orderData.items[0].quantity
                );
                const startTime = Date.now();
                const res = http.post(
                    `${BASE_URL}${API_PATHS.payment}`,
                    JSON.stringify(paymentData),
                    { headers: { 'Content-Type': 'application/json' } }
                );
                paymentProcessingTrend.add(Date.now() - startTime);

                check(res, {
                    'status is 200': (r) => r.status === 200,
                    'payment processed successfully': (r) => r.json() && r.json().data && r.json().data.id
                }) || errorRate.add(1);

                if (res.status === 200) {
                    paymentId = res.json().data.id;
                    successCounter.add(1);
                }
            });
        }
    }

    sleep(Math.random() * 1);
}

// Генерация отчета
export function handleSummary(data) {
    return {
        "load-test-summary.html": htmlReport(data),
        "stdout": textSummary(data, { indent: " ", enableColors: true })
    };
}