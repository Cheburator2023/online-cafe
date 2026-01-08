-- Добавляем новые колонки в таблицу orders
ALTER TABLE orders
    ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE,
ADD COLUMN version BIGINT DEFAULT 0;

-- Создаем индексы для оптимизации
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_orders_status ON orders(status);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_menu_item_id ON order_items(menu_item_id);