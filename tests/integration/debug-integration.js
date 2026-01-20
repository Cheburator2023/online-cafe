import http from 'k6/http';
import { check, group } from 'k6';
import { Counter } from 'k6/metrics';

export const options = {
    vus: 1,
    iterations: 1,
    duration: '30s'
};

const BASE_URL = 'http://localhost:8080';

export default function () {
    group('Тест 1: Проверка здоровья Eureka', () => {
        const res = http.get('http://localhost:8761/eureka/apps');
        console.log(`Eureka status: ${res.status}`);
        console.log(`Eureka response: ${res.body.substring(0, 500)}...`);

        check(res, {
            'Eureka is accessible': (r) => r.status === 200
        });
    });

    group('Тест 2: Проверка регистрации сервисов в Eureka', () => {
        const services = ['API-GATEWAY', 'USER-SERVICE', 'MENU-SERVICE'];

        services.forEach(service => {
            const res = http.get(`http://localhost:8761/eureka/apps/${service}`);
            console.log(`${service} registration status: ${res.status}`);

            if (res.status === 200) {
                const body = res.body;
                if (body.includes('instanceId')) {
                    console.log(`✅ ${service} is registered in Eureka`);
                    // Извлекаем hostname и порт
                    const hostnameMatch = body.match(/<hostName>([^<]+)<\/hostName>/);
                    const portMatch = body.match(/<port[^>]*>([^<]+)<\/port>/);
                    if (hostnameMatch && portMatch) {
                        console.log(`   Hostname: ${hostnameMatch[1]}, Port: ${portMatch[1]}`);
                    }
                } else {
                    console.log(`❌ ${service} found in Eureka but no instance info`);
                }
            } else {
                console.log(`❌ ${service} not found in Eureka`);
            }
        });
    });

    group('Тест 3: Прямой запрос к user-service', () => {
        const userData = {
            name: 'Direct Test User',
            email: `direct_${Date.now()}@test.com`
        };

        const res = http.post(
            'http://localhost:8000/api/v1/users',
            JSON.stringify(userData),
            {
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            }
        );

        console.log(`Direct user-service status: ${res.status}`);
        console.log(`Direct user-service response: ${res.body}`);

        check(res, {
            'Direct POST user returns 201': (r) => r.status === 201
        });
    });

    group('Тест 4: Запрос к user-service через API Gateway', () => {
        const userData = {
            name: 'Gateway Test User',
            email: `gateway_${Date.now()}@test.com`
        };

        const res = http.post(
            `${BASE_URL}/api/user/api/v1/users`,
            JSON.stringify(userData),
            {
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                timeout: '30s'
            }
        );

        console.log(`API Gateway status: ${res.status}`);
        console.log(`API Gateway response: ${res.body}`);
        console.log(`API Gateway headers: ${JSON.stringify(res.headers)}`);

        check(res, {
            'Gateway POST user returns 201': (r) => r.status === 201,
            'Gateway response has user ID': (r) => r.json() && r.json().data && r.json().data.id
        });
    });

    group('Тест 5: Проверка circuit breaker', () => {
        const res = http.get(`${BASE_URL}/actuator/health`);

        if (res.status === 200) {
            const healthData = res.json();
            console.log('Circuit breaker status:', JSON.stringify(healthData.components.circuitBreakers, null, 2));
        }
    });
}