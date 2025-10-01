-- =============================================
-- Data initialization script for Spring Boot
-- This file will be automatically executed when the application starts
-- =============================================

-- Insert roles (only if not exists)
INSERT INTO roles (role_name, description) 
SELECT 'CUSTOMER', 'Khách hàng'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'CUSTOMER');

INSERT INTO roles (role_name, description) 
SELECT 'ADMIN', 'Quản trị viên'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'ADMIN');

INSERT INTO roles (role_name, description) 
SELECT 'CUSTOMER_SERVICE_AGENT', 'Nhân viên chăm sóc khách hàng'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'CUSTOMER_SERVICE_AGENT');

INSERT INTO roles (role_name, description) 
SELECT 'TECHNICAL_SUPPORT', 'Hỗ trợ kỹ thuật'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'TECHNICAL_SUPPORT');

INSERT INTO roles (role_name, description) 
SELECT 'FINANCIAL_SUPPORT', 'Hỗ trợ tài chính'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'FINANCIAL_SUPPORT');

-- Insert sample customers (only if not exists)
INSERT INTO customers (name, email, username, password, phone, is_active, register_date, role_id) 
SELECT 'Nguyễn Văn An', 'an.nguyen@email.com', 'annguyen', 'password123', '0123456789', 1, GETDATE(), 
       (SELECT role_id FROM roles WHERE role_name = 'CUSTOMER')
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE email = 'an.nguyen@email.com');

INSERT INTO customers (name, email, username, password, phone, is_active, register_date, role_id) 
SELECT 'Trần Thị Bình', 'binh.tran@email.com', 'binhtran', 'password456', '0987654321', 1, GETDATE(),
       (SELECT role_id FROM roles WHERE role_name = 'CUSTOMER')
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE email = 'binh.tran@email.com');

INSERT INTO customers (name, email, username, password, phone, is_active, register_date, role_id) 
SELECT 'Lê Văn Cường', 'cuong.le@email.com', 'cuongle', 'password789', '0369258147', 1, GETDATE(),
       (SELECT role_id FROM roles WHERE role_name = 'CUSTOMER')
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE email = 'cuong.le@email.com');

-- Insert sample staff (only if not exists)
INSERT INTO staff (name, email, username, password, phone, is_active, register_date, role_id) 
SELECT 'Nguyễn Văn Admin', 'admin@company.com', 'admin', 'admin123', '0123456789', 1, GETDATE(),
       (SELECT role_id FROM roles WHERE role_name = 'ADMIN')
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE email = 'admin@company.com');

INSERT INTO staff (name, email, username, password, phone, is_active, register_date, role_id) 
SELECT 'Trần Thị CS', 'cs@company.com', 'customer_service', 'cs123', '0987654321', 1, GETDATE(),
       (SELECT role_id FROM roles WHERE role_name = 'CUSTOMER_SERVICE_AGENT')
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE email = 'cs@company.com');

INSERT INTO staff (name, email, username, password, phone, is_active, register_date, role_id) 
SELECT 'Lê Văn Tech', 'tech@company.com', 'technical_support', 'tech123', '0369258147', 1, GETDATE(),
       (SELECT role_id FROM roles WHERE role_name = 'TECHNICAL_SUPPORT')
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE email = 'tech@company.com');

INSERT INTO staff (name, email, username, password, phone, is_active, register_date, role_id) 
SELECT 'Phạm Thị Finance', 'finance@company.com', 'financial_support', 'finance123', '0147258369', 1, GETDATE(),
       (SELECT role_id FROM roles WHERE role_name = 'FINANCIAL_SUPPORT')
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE email = 'finance@company.com');

-- Insert sample shifts (only if not exists)
INSERT INTO shifts (name, start_time, end_time) 
SELECT 'Ca sáng', '08:00:00', '16:00:00'
WHERE NOT EXISTS (SELECT 1 FROM shifts WHERE name = 'Ca sáng');

INSERT INTO shifts (name, start_time, end_time) 
SELECT 'Ca chiều', '16:00:00', '00:00:00'
WHERE NOT EXISTS (SELECT 1 FROM shifts WHERE name = 'Ca chiều');

INSERT INTO shifts (name, start_time, end_time) 
SELECT 'Ca đêm', '00:00:00', '08:00:00'
WHERE NOT EXISTS (SELECT 1 FROM shifts WHERE name = 'Ca đêm');

-- Insert sample vendors (only if not exists)
INSERT INTO vendors (name, contact_info) 
SELECT 'Công ty ABC', 'Địa chỉ: 123 Đường ABC, Quận 1, TP.HCM. ĐT: 0123456789'
WHERE NOT EXISTS (SELECT 1 FROM vendors WHERE name = 'Công ty ABC');

INSERT INTO vendors (name, contact_info) 
SELECT 'Công ty XYZ', 'Địa chỉ: 456 Đường XYZ, Quận 2, TP.HCM. ĐT: 0987654321'
WHERE NOT EXISTS (SELECT 1 FROM vendors WHERE name = 'Công ty XYZ');

INSERT INTO vendors (name, contact_info) 
SELECT 'Công ty DEF', 'Địa chỉ: 789 Đường DEF, Quận 3, TP.HCM. ĐT: 0369258147'
WHERE NOT EXISTS (SELECT 1 FROM vendors WHERE name = 'Công ty DEF');

-- Insert sample products (only if not exists)
INSERT INTO products (vendor_id, name, description, price, quantity, status) 
SELECT (SELECT vendor_id FROM vendors WHERE name = 'Công ty ABC'), 
       'Laptop Dell XPS 13', 'Laptop cao cấp với màn hình 13 inch', 25000000, 10, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Laptop Dell XPS 13');

INSERT INTO products (vendor_id, name, description, price, quantity, status) 
SELECT (SELECT vendor_id FROM vendors WHERE name = 'Công ty ABC'), 
       'Mouse Logitech MX Master', 'Chuột không dây cao cấp', 2500000, 50, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Mouse Logitech MX Master');

INSERT INTO products (vendor_id, name, description, price, quantity, status) 
SELECT (SELECT vendor_id FROM vendors WHERE name = 'Công ty XYZ'), 
       'Keyboard Mechanical', 'Bàn phím cơ với switch Cherry MX', 1500000, 30, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Keyboard Mechanical');

INSERT INTO products (vendor_id, name, description, price, quantity, status) 
SELECT (SELECT vendor_id FROM vendors WHERE name = 'Công ty XYZ'), 
       'Monitor 27 inch 4K', 'Màn hình 4K 27 inch', 8000000, 15, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Monitor 27 inch 4K');

INSERT INTO products (vendor_id, name, description, price, quantity, status) 
SELECT (SELECT vendor_id FROM vendors WHERE name = 'Công ty DEF'), 
       'Webcam HD', 'Webcam HD 1080p', 1200000, 25, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Webcam HD');

-- Insert sample shipping methods (only if not exists)
INSERT INTO shipping_methods (name, cost_estimate, estimated_time) 
SELECT 'Giao hàng tiêu chuẩn', 30000, '3-5 ngày làm việc'
WHERE NOT EXISTS (SELECT 1 FROM shipping_methods WHERE name = 'Giao hàng tiêu chuẩn');

INSERT INTO shipping_methods (name, cost_estimate, estimated_time) 
SELECT 'Giao hàng nhanh', 50000, '1-2 ngày làm việc'
WHERE NOT EXISTS (SELECT 1 FROM shipping_methods WHERE name = 'Giao hàng nhanh');

INSERT INTO shipping_methods (name, cost_estimate, estimated_time) 
SELECT 'Giao hàng siêu tốc', 100000, 'Trong ngày'
WHERE NOT EXISTS (SELECT 1 FROM shipping_methods WHERE name = 'Giao hàng siêu tốc');

--ticket data

-- Customer 0 tickets
INSERT INTO tickets (customer_id, subject, description, status, priority, created_at)
SELECT 0, 'Login issue', 'User cannot log into account.', 'OPEN', 'HIGH', GETDATE()
    WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE subject = 'Login issue' AND customer_id = 0);

INSERT INTO tickets (customer_id, subject, description, status, priority, created_at)
SELECT 0, 'Password reset', 'User requests a password reset link.', 'IN_PROGRESS', 'MEDIUM', GETDATE()
    WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE subject = 'Password reset' AND customer_id = 0);

INSERT INTO tickets (customer_id, subject, description, status, priority, created_at)
SELECT 0, 'App crash', 'Mobile app crashes when opening profile.', 'RESOLVED', 'HIGH', GETDATE()
    WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE subject = 'App crash' AND customer_id = 0);

INSERT INTO tickets (customer_id, subject, description, status, priority, created_at)
SELECT 0, 'Feature request', 'User requests dark mode support.', 'CLOSED', 'LOW', GETDATE()
    WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE subject = 'Feature request' AND customer_id = 0);

-- Customer 1 tickets
INSERT INTO tickets (customer_id, subject, description, status, priority, created_at)
SELECT 1, 'Payment failed', 'Customer cannot complete payment.', 'OPEN', 'HIGH', GETDATE()
    WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE subject = 'Payment failed' AND customer_id = 1);

INSERT INTO tickets (customer_id, subject, description, status, priority, created_at)
SELECT 1, 'Slow website', 'Website takes too long to load.', 'IN_PROGRESS', 'MEDIUM', GETDATE()
    WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE subject = 'Slow website' AND customer_id = 1);

INSERT INTO tickets (customer_id, subject, description, status, priority, created_at)
SELECT 1, 'Refund request', 'Customer requests refund for recent purchase.', 'RESOLVED', 'HIGH', GETDATE()
    WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE subject = 'Refund request' AND customer_id = 1);

INSERT INTO tickets (customer_id, subject, description, status, priority, created_at)
SELECT 1, 'Account locked', 'Customer account locked after failed attempts.', 'CLOSED', 'MEDIUM', GETDATE()
    WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE subject = 'Account locked' AND customer_id = 1);

INSERT INTO tickets (customer_id, subject, description, status, priority, created_at)
SELECT 1, 'Invoice error', 'Invoice shows wrong billing amount.', 'ASSIGNED', 'HIGH', GETDATE()
    WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE subject = 'Invoice error' AND customer_id = 1);

-- Customer 2 tickets
INSERT INTO tickets (customer_id, subject, description, status, priority, created_at)
SELECT 2, 'Wrong item delivered', 'Delivered product does not match order.', 'OPEN', 'HIGH', GETDATE()
    WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE subject = 'Wrong item delivered' AND customer_id = 2);

INSERT INTO tickets (customer_id, subject, description, status, priority, created_at)
SELECT 2, 'Change shipping address', 'Customer wants to update delivery address.', 'ASSIGNED', 'LOW', GETDATE()
    WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE subject = 'Change shipping address' AND customer_id = 2);

INSERT INTO tickets (customer_id, subject, description, status, priority, created_at)
SELECT 2, 'Warranty claim', 'Customer requests warranty support.', 'RESOLVED', 'MEDIUM', GETDATE()
    WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE subject = 'Warranty claim' AND customer_id = 2);

INSERT INTO tickets (customer_id, subject, description, status, priority, created_at)
SELECT 2, 'Cancel order', 'Customer wants to cancel recent order.', 'CLOSED', 'HIGH', GETDATE()
    WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE subject = 'Cancel order' AND customer_id = 2);

INSERT INTO tickets (customer_id, subject, description, status, priority, created_at)
SELECT 2, 'Delivery delay', 'Order delivery taking longer than expected.', 'IN_PROGRESS', 'MEDIUM', GETDATE()
    WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE subject = 'Delivery delay' AND customer_id = 2);