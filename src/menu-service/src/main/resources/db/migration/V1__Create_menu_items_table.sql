CREATE TABLE menu_items (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            description TEXT NOT NULL,
                            price DECIMAL(10,2) NOT NULL,
                            category VARCHAR(50) NOT NULL,
                            available BOOLEAN DEFAULT TRUE,
                            created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);