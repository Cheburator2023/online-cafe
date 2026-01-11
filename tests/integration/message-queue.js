import http from 'k6/http';
import { check, group, sleep } from 'k6';
import exec from 'k6/execution';

export const options = {
    vus: 5,
    iterations: 10,
    duration: '1m'
};

const BASE_URL = 'http://localhost:8080';
const RABBITMQ_MGMT = 'http://localhost:15672';

export default function () {
    group('Тестирование асинхронной обработки через RabbitMQ', () => {
        // Шаг 1: Создание пользователя
        const userData = {
            name: `MQ Test User ${exec.vu.idInTest}-${exec.vu.iterationInInstance}`,
            email: `mq_${Date.now()}_${exec.vu.idInTest}@test.com`
        };

        const userRes = http.post(
            `${BASE_URL}/api/user/api/v1/users`,
            JSON.stringify(userData),
            { headers: { 'Content-Type': 'application/json' } }
        );

        check(userRes, {
            'user created': (r) => r.status === 201
        });

        const userId = userRes.json().data.id;

        // Шаг 2: Получение меню
        const menuRes = http.get(`${BASE_URL}/api/menu`);
        const menuItem = menuRes.json().data[0];

        // Шаг 3: Создание заказа (генерирует событие в RabbitMQ)
        const orderData = {
            userId: userId,
            items: [
                {
                    menuItemId: menuItem.id,
                    quantity: 1
                }
            ]
        };

        const orderRes = http.post(
            `${BASE_URL}/api/orders`,
            JSON.stringify(orderData),
            { headers: { 'Content-Type': 'application/json' } }
        );

        check(orderRes, {
            'order created': (r) => r.status === 200
        });

        const orderId = orderRes.json().data.id;

        // Шаг 4: Пауза для обработки события в RabbitMQ
        sleep(3);

        // Шаг 5: Проверка, что платеж был автоматически создан
        const paymentsRes = http.get(`${BASE_URL}/api/payments/order/${orderId}`);

        check(paymentsRes, {
            'payment auto-created via RabbitMQ': (r) =>
                r.status === 200 &&
                r.json().data &&
                r.json().data.length > 0 &&
                r.json().data[0].status === 'PENDING'
        });

        // Шаг 6: Проверка статуса RabbitMQ через management API
        const rabbitRes = http.get(`${RABBITMQ_MGMT}/api/queues`, {
            auth: 'guest:guest'
        });

        check(rabbitRes, {
            'RabbitMQ is accessible': (r) => r.status === 200,
            'queues exist': (r) => r.json() && r.json().length > 0
        });
    });
}