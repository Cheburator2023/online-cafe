-- Индексы для оптимизации запросов
CREATE INDEX idx_payments_user_id ON payments(user_id);
CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_created_at ON payments(created_at DESC);

-- Составной индекс для частых запросов
CREATE INDEX idx_payments_user_status ON payments(user_id, status);

-- Ограничения для целостности данных
ALTER TABLE payments ADD CONSTRAINT chk_amount_positive CHECK (amount > 0);
ALTER TABLE payments ADD CONSTRAINT chk_status_valid CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'));