CREATE TABLE menu_items (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            description TEXT NOT NULL,
                            price DECIMAL(10,2) NOT NULL CHECK (price > 0),
                            category VARCHAR(50) NOT NULL,
                            available BOOLEAN NOT NULL DEFAULT TRUE,
                            created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT uk_menu_item_name UNIQUE (name)
);

CREATE INDEX idx_menu_items_category ON menu_items(category);
CREATE INDEX idx_menu_items_available ON menu_items(available);
CREATE INDEX idx_menu_items_created_at ON menu_items(created_at DESC);