import http from 'k6/http';
import { check, group } from 'k6';
import { Counter } from 'k6/metrics';

const successfulTests = new Counter('successful_tests');
const failedTests = new Counter('failed_tests');

export const options = {
    vus: 1,
    iterations: 1,
    duration: '30s'
};

const BASE_URL = 'http://localhost:8080';

export default function () {
    let testContext = {
        userId: null,
        menuItemId: null,
        orderId: null,
        paymentId: null
    };

    group('Тест 1: Проверка здоровья сервисов', () => {
        const services = [
            'discovery-service:8761',
            'user-service:8000',
            'menu-service:8001',
            'order-service:8002',
            'payment-service:8003',
            'api-gateway:8080'
        ];

        services.forEach(service => {
            const [name, port] = service.split(':');
            const res = http.get(`http://localhost:${port}/actuator/health`);

            const passed = check(res, {
                [`${name} is healthy`]: (r) => r.status === 200 && r.json().status === 'UP'
            });

            if (passed) successfulTests.add(1);
            else failedTests.add(1);
        });
    });

    group('Тест 2: Работа с меню', () => {
        // Получение всех пунктов меню
        const getRes = http.get(`${BASE_URL}/api/menu`);

        check(getRes, {
            'GET menu returns 200': (r) => r.status === 200,
            'response has data array': (r) => r.json() && Array.isArray(r.json().data)
        });

        if (getRes.json() && getRes.json().data && getRes.json().data.length > 0) {
            testContext.menuItemId = getRes.json().data[0].id;
            successfulTests.add(1);
        }
    });

    group('Тест 3: Создание пользователя', () => {
        const userData = {
            name: 'Integration Test User',
            email: `integration_${Date.now()}@test.com`
        };

        // Исправленный путь к API пользователей
        const res = http.post(
            `${BASE_URL}/api/user/api/v1/users`,
            JSON.stringify(userData),
            {
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            }
        );

        const passed = check(res, {
            'POST user returns 201': (r) => r.status === 201,
            'user has ID': (r) => r.json() && r.json().data && r.json().data.id
        });

        if (passed) {
            testContext.userId = res.json().data.id;
            successfulTests.add(1);
        } else {
            console.log(`User creation failed with status ${res.status}: ${res.body}`);
            failedTests.add(1);
        }
    });

    group('Тест 4: Создание заказа', () => {
        if (!testContext.userId || !testContext.menuItemId) {
            console.log('Skipping order test - missing prerequisites');
            return;
        }

        const orderData = {
            userId: testContext.userId,
            items: [
                {
                    menuItemId: testContext.menuItemId,
                    quantity: 2
                }
            ],
            specialInstructions: 'Integration test order'
        };

        const res = http.post(
            `${BASE_URL}/api/order`,
            JSON.stringify(orderData),
            {
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            }
        );

        const passed = check(res, {
            'POST order returns 200': (r) => r.status === 200,
            'order has ID': (r) => r.json() && r.json().data && r.json().data.id,
            'order has correct total': (r) => r.json() && r.json().data && r.json().data.totalAmount > 0
        });

        if (passed) {
            testContext.orderId = res.json().data.id;
            successfulTests.add(1);
        } else {
            console.log(`Order creation failed with status ${res.status}: ${res.body}`);
            failedTests.add(1);
        }
    });

    group('Тест 5: Обработка платежа', () => {
        if (!testContext.orderId || !testContext.userId) {
            console.log('Skipping payment test - missing prerequisites');
            return;
        }

        const paymentData = {
            orderId: testContext.orderId,
            userId: testContext.userId,
            amount: 25.50,
            paymentMethod: 'CREDIT_CARD'
        };

        const res = http.post(
            `${BASE_URL}/api/payment`,
            JSON.stringify(paymentData),
            {
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            }
        );

        const passed = check(res, {
            'POST payment returns 200': (r) => r.status === 200,
            'payment has ID': (r) => r.json() && r.json().data && r.json().data.id,
            'payment has status': (r) => r.json() && r.json().data && r.json().data.status
        });

        if (passed) {
            testContext.paymentId = res.json().data.id;
            successfulTests.add(1);
        } else {
            console.log(`Payment creation failed with status ${res.status}: ${res.body}`);
            failedTests.add(1);
        }
    });

    group('Тест 6: Проверка целостности данных', () => {
        // Проверка, что все данные корректно сохранены
        const checks = [];

        if (testContext.userId) {
            const userRes = http.get(`${BASE_URL}/api/user/api/v1/users/${testContext.userId}`);
            checks.push(check(userRes, {
                'user exists': (r) => r.status === 200
            }));
        }

        if (testContext.orderId) {
            const orderRes = http.get(`${BASE_URL}/api/order/${testContext.orderId}`);
            checks.push(check(orderRes, {
                'order exists': (r) => r.status === 200,
                'order has correct user': (r) => r.json() && r.json().data.userId === testContext.userId
            }));
        }

        if (testContext.paymentId) {
            const paymentRes = http.get(`${BASE_URL}/api/payment/${testContext.paymentId}`);
            checks.push(check(paymentRes, {
                'payment exists': (r) => r.status === 200,
                'payment has correct order': (r) => r.json() && r.json().data.orderId === testContext.orderId
            }));
        }

        if (checks.every(c => c)) {
            successfulTests.add(1);
        }
    });

    group('Тест 7: Проверка отказоустойчивости', () => {
        // Тестирование circuit breaker и fallback
        const invalidUrls = [
            `${BASE_URL}/api/nonexistent`,
            `${BASE_URL}/api/user/invalid-endpoint`,
            `${BASE_URL}/api/menu/999999` // Несуществующий ID
        ];

        invalidUrls.forEach(url => {
            const res = http.get(url);
            check(res, {
                'returns proper error status': (r) => r.status === 404 || r.status === 503 || r.status === 500
            });
        });

        successfulTests.add(1);
    });
}