graph TB
Client[Web/Mobile Client] --> Gateway[API Gateway]

    Gateway --> User[User Service]
    Gateway --> Menu[Menu Service] 
    Gateway --> Order[Order Service]
    Gateway --> Payment[Payment Service]
    
    User --> UserDB[(User DB)]
    Menu --> MenuDB[(Menu DB)]
    Order --> OrderDB[(Order DB)]
    Payment --> PaymentDB[(Payment DB)]
    
    subgraph "Service Discovery"
        Eureka[Eureka Server]
    end
    
    User --> Eureka
    Menu --> Eureka
    Order --> Eureka
    Payment --> Eureka
    Gateway --> Eureka
    
    subgraph "Message Broker"
        RabbitMQ[RabbitMQ]
    end
    
    Order --> RabbitMQ
    Payment --> RabbitMQ
    
    subgraph "Monitoring"
        Prometheus[Prometheus]
        Grafana[Grafana]
    end
    
    Prometheus --> User
    Prometheus --> Menu
    Prometheus --> Order
    Prometheus --> Payment
    Prometheus --> Gateway
    Grafana --> Prometheus
    
    style Client fill:#e1f5fe
    style Gateway fill:#f3e5f5
    style User fill:#e8f5e8
    style Menu fill:#fff3e0
    style Order fill:#fce4ec
    style Payment fill:#e0f2f1
    style Eureka fill:#fff8e1
    style RabbitMQ fill:#fbe9e7