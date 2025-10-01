-- =============================================
-- Customer Service Database Setup Script
-- SQL Server Database Creation Script
-- =============================================

-- Tạo database CustomerService
USE master;
GO

-- Kiểm tra và tạo database nếu chưa tồn tại
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'customerService_db')
BEGIN
    CREATE DATABASE customerService_db;
    PRINT 'Database customerService_db created successfully';
END
ELSE
BEGIN
    PRINT 'Database customerService_db already exists';
END
GO

-- Sử dụng database customerService_db
USE customerService_db;
GO

-- =============================================
-- Tạo các bảng theo thứ tự phụ thuộc
-- =============================================

-- 1. Tạo bảng roles (bảng cơ sở, không phụ thuộc)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='roles' AND xtype='U')
BEGIN
    CREATE TABLE roles (
        role_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        role_name NVARCHAR(50) NOT NULL UNIQUE,
        description NVARCHAR(255)
    );
    PRINT 'Table roles created successfully';
END
GO

-- 2. Tạo bảng customers (phụ thuộc vào roles)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='customers' AND xtype='U')
BEGIN
    CREATE TABLE customers (
        customer_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        email NVARCHAR(100) NOT NULL UNIQUE,
        username NVARCHAR(50) NOT NULL UNIQUE,
        password NVARCHAR(255) NOT NULL,
        phone NVARCHAR(20),
        is_active BIT NOT NULL DEFAULT 1,
        register_date DATETIME2 NOT NULL DEFAULT GETDATE(),
        role_id BIGINT NOT NULL,
        FOREIGN KEY (role_id) REFERENCES roles(role_id)
    );
    PRINT 'Table customers created successfully';
END
GO

-- 3. Tạo bảng staff (phụ thuộc vào roles)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='staff' AND xtype='U')
BEGIN
    CREATE TABLE staff (
        staff_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        email NVARCHAR(100) NOT NULL UNIQUE,
        username NVARCHAR(50) NOT NULL UNIQUE,
        password NVARCHAR(255) NOT NULL,
        phone NVARCHAR(20),
        is_active BIT NOT NULL DEFAULT 1,
        register_date DATETIME2 NOT NULL DEFAULT GETDATE(),
        role_id BIGINT NOT NULL,
        FOREIGN KEY (role_id) REFERENCES roles(role_id)
    );
    PRINT 'Table staff created successfully';
END
GO

-- 4. Tạo bảng shifts (bảng độc lập)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='shifts' AND xtype='U')
BEGIN
    CREATE TABLE shifts (
        shift_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        start_time TIME NOT NULL,
        end_time TIME NOT NULL
    );
    PRINT 'Table shifts created successfully';
END
GO

-- 5. Tạo bảng staff_shift_assign (phụ thuộc vào staff và shifts)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='staff_shift_assign' AND xtype='U')
BEGIN
    CREATE TABLE staff_shift_assign (
        staff_shift_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        staff_id BIGINT NOT NULL,
        shift_id BIGINT NOT NULL,
        date DATE NOT NULL,
        FOREIGN KEY (staff_id) REFERENCES staff(staff_id),
        FOREIGN KEY (shift_id) REFERENCES shifts(shift_id)
    );
    PRINT 'Table staff_shift_assign created successfully';
END
GO

-- 6. Tạo bảng tickets (phụ thuộc vào customers và orders)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='tickets' AND xtype='U')
BEGIN
    CREATE TABLE tickets (
        ticket_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        customer_id BIGINT NOT NULL,
        order_id BIGINT,
        subject NVARCHAR(255) NOT NULL,
        description TEXT,
        priority NVARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
        status NVARCHAR(20) NOT NULL DEFAULT 'OPEN',
        created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        closed_at DATETIME2,
        FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
        FOREIGN KEY (order_id) REFERENCES orders(order_id)
    );
    PRINT 'Table tickets created successfully';
END
GO

-- 7. Tạo bảng ticket_assign (phụ thuộc vào tickets và staff)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='ticket_assign' AND xtype='U')
BEGIN
    CREATE TABLE ticket_assign (
        ticket_assignment_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        ticket_id BIGINT NOT NULL,
        assigned_to BIGINT NOT NULL,
        assigned_by BIGINT NOT NULL,
        assigned_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        role_needed NVARCHAR(50) NOT NULL,
        FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id),
        FOREIGN KEY (assigned_to) REFERENCES staff(staff_id),
        FOREIGN KEY (assigned_by) REFERENCES staff(staff_id)
    );
    PRINT 'Table ticket_assign created successfully';
END
GO

-- 8. Tạo bảng evaluations (phụ thuộc vào tickets và customers)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='evaluations' AND xtype='U')
BEGIN
    CREATE TABLE evaluations (
        evaluation_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        ticket_id BIGINT NOT NULL UNIQUE,
        customer_id BIGINT NOT NULL,
        score INT NOT NULL,
        comment TEXT,
        created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id),
        FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
    );
    PRINT 'Table evaluations created successfully';
END
GO

-- 9. Tạo bảng vendors (bảng độc lập)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='vendors' AND xtype='U')
BEGIN
    CREATE TABLE vendors (
        vendor_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        contact_info TEXT
    );
    PRINT 'Table vendors created successfully';
END
GO

-- 10. Tạo bảng products (phụ thuộc vào vendors)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='products' AND xtype='U')
BEGIN
    CREATE TABLE products (
        product_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        vendor_id BIGINT NOT NULL,
        name NVARCHAR(255) NOT NULL,
        description TEXT,
        price DECIMAL(10,2) NOT NULL,
        quantity INT NOT NULL,
        status NVARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
        FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id)
    );
    PRINT 'Table products created successfully';
END
GO

-- 11. Tạo bảng shipping_methods (bảng độc lập)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='shipping_methods' AND xtype='U')
BEGIN
    CREATE TABLE shipping_methods (
        shipping_method_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        cost_estimate DECIMAL(10,2) NOT NULL,
        estimated_time NVARCHAR(100)
    );
    PRINT 'Table shipping_methods created successfully';
END
GO

-- 12. Tạo bảng orders (phụ thuộc vào customers và shipping_methods)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='orders' AND xtype='U')
BEGIN
    CREATE TABLE orders (
        order_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        customer_id BIGINT NOT NULL,
        order_date DATETIME2 NOT NULL DEFAULT GETDATE(),
        total_amount DECIMAL(10,2) NOT NULL,
        shipping_method_id BIGINT NOT NULL,
        shipping_status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',
        order_status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',
        shipping_address TEXT,
        FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
        FOREIGN KEY (shipping_method_id) REFERENCES shipping_methods(shipping_method_id)
    );
    PRINT 'Table orders created successfully';
END
GO

-- 13. Tạo bảng order_details (phụ thuộc vào orders và products)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='order_details' AND xtype='U')
BEGIN
    CREATE TABLE order_details (
        order_detail_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        order_id BIGINT NOT NULL,
        product_id BIGINT NOT NULL,
        unit_price DECIMAL(10,2) NOT NULL,
        quantity INT NOT NULL,
        sub_total DECIMAL(10,2) NOT NULL,
        FOREIGN KEY (order_id) REFERENCES orders(order_id),
        FOREIGN KEY (product_id) REFERENCES products(product_id)
    );
    PRINT 'Table order_details created successfully';
END
GO

-- 14. Tạo bảng invoices (phụ thuộc vào orders và staff)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='invoices' AND xtype='U')
BEGIN
    CREATE TABLE invoices (
        invoice_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        order_id BIGINT NOT NULL UNIQUE,
        invoice_date DATETIME2 NOT NULL DEFAULT GETDATE(),
        total_amount DECIMAL(10,2) NOT NULL,
        tax_amount DECIMAL(10,2) NOT NULL,
        invoice_number NVARCHAR(50) NOT NULL UNIQUE,
        issued_by BIGINT NOT NULL,
        status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',
        FOREIGN KEY (order_id) REFERENCES orders(order_id),
        FOREIGN KEY (issued_by) REFERENCES staff(staff_id)
    );
    PRINT 'Table invoices created successfully';
END
GO

-- 15. Tạo bảng payments (phụ thuộc vào invoices)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='payments' AND xtype='U')
BEGIN
    CREATE TABLE payments (
        payment_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        invoice_id BIGINT NOT NULL,
        payment_date DATETIME2 NOT NULL DEFAULT GETDATE(),
        method NVARCHAR(50) NOT NULL,
        status NVARCHAR(20) NOT NULL DEFAULT 'SUCCEEDED',
        FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id)
    );
    PRINT 'Table payments created successfully';
END
GO

-- =============================================
-- Insert dữ liệu mẫu
-- =============================================

-- Insert roles
IF NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'CUSTOMER')
BEGIN
    INSERT INTO roles (role_name, description) VALUES 
    ('CUSTOMER', 'Khách hàng'),
    ('ADMIN', 'Quản trị viên'),
    ('CUSTOMER_SERVICE_AGENT', 'Nhân viên chăm sóc khách hàng'),
    ('TECHNICAL_SUPPORT', 'Hỗ trợ kỹ thuật'),
    ('FINANCIAL_SUPPORT', 'Hỗ trợ tài chính');
    PRINT 'Sample roles inserted successfully';
END
GO

-- Insert sample customers
IF NOT EXISTS (SELECT 1 FROM customers WHERE email = 'an.nguyen@email.com')
BEGIN
    INSERT INTO customers (name, email, username, password, phone, is_active, register_date, role_id) VALUES 
    ('Nguyễn Văn An', 'an.nguyen@email.com', 'annguyen', 'password123', '0123456789', 1, GETDATE(), 1),
    ('Trần Thị Bình', 'binh.tran@email.com', 'binhtran', 'password456', '0987654321', 1, GETDATE(), 1),
    ('Lê Văn Cường', 'cuong.le@email.com', 'cuongle', 'password789', '0369258147', 1, GETDATE(), 1);
    PRINT 'Sample customers inserted successfully';
END
GO

-- Insert sample staff
IF NOT EXISTS (SELECT 1 FROM staff WHERE email = 'admin@company.com')
BEGIN
    INSERT INTO staff (name, email, username, password, phone, is_active, register_date, role_id) VALUES 
    ('Nguyễn Văn Admin', 'admin@company.com', 'admin', 'admin123', '0123456789', 1, GETDATE(), 2),
    ('Trần Thị CS', 'cs@company.com', 'customer_service', 'cs123', '0987654321', 1, GETDATE(), 3),
    ('Lê Văn Tech', 'tech@company.com', 'technical_support', 'tech123', '0369258147', 1, GETDATE(), 4),
    ('Phạm Thị Finance', 'finance@company.com', 'financial_support', 'finance123', '0147258369', 1, GETDATE(), 5);
    PRINT 'Sample staff inserted successfully';
END
GO

-- Insert sample shifts
IF NOT EXISTS (SELECT 1 FROM shifts WHERE name = 'Ca sáng')
BEGIN
    INSERT INTO shifts (name, start_time, end_time) VALUES 
    ('Ca sáng', '08:00:00', '16:00:00'),
    ('Ca chiều', '16:00:00', '00:00:00'),
    ('Ca đêm', '00:00:00', '08:00:00');
    PRINT 'Sample shifts inserted successfully';
END
GO

-- Insert sample vendors
IF NOT EXISTS (SELECT 1 FROM vendors WHERE name = 'Công ty ABC')
BEGIN
    INSERT INTO vendors (name, contact_info) VALUES 
    ('Công ty ABC', 'Địa chỉ: 123 Đường ABC, Quận 1, TP.HCM. ĐT: 0123456789'),
    ('Công ty XYZ', 'Địa chỉ: 456 Đường XYZ, Quận 2, TP.HCM. ĐT: 0987654321'),
    ('Công ty DEF', 'Địa chỉ: 789 Đường DEF, Quận 3, TP.HCM. ĐT: 0369258147');
    PRINT 'Sample vendors inserted successfully';
END
GO

-- Insert sample products
IF NOT EXISTS (SELECT 1 FROM products WHERE name = 'Laptop Dell XPS 13')
BEGIN
    INSERT INTO products (vendor_id, name, description, price, quantity, status) VALUES 
    (1, 'Laptop Dell XPS 13', 'Laptop cao cấp với màn hình 13 inch', 25000000, 10, 'ACTIVE'),
    (1, 'Mouse Logitech MX Master', 'Chuột không dây cao cấp', 2500000, 50, 'ACTIVE'),
    (2, 'Keyboard Mechanical', 'Bàn phím cơ với switch Cherry MX', 1500000, 30, 'ACTIVE'),
    (2, 'Monitor 27 inch 4K', 'Màn hình 4K 27 inch', 8000000, 15, 'ACTIVE'),
    (3, 'Webcam HD', 'Webcam HD 1080p', 1200000, 25, 'ACTIVE');
    PRINT 'Sample products inserted successfully';
END
GO

-- Insert sample shipping methods
IF NOT EXISTS (SELECT 1 FROM shipping_methods WHERE name = 'Giao hàng tiêu chuẩn')
BEGIN
    INSERT INTO shipping_methods (name, cost_estimate, estimated_time) VALUES 
    ('Giao hàng tiêu chuẩn', 30000, '3-5 ngày làm việc'),
    ('Giao hàng nhanh', 50000, '1-2 ngày làm việc'),
    ('Giao hàng siêu tốc', 100000, 'Trong ngày');
    PRINT 'Sample shipping methods inserted successfully';
END
GO

-- =============================================
-- Tạo các Index để tối ưu hiệu suất
-- =============================================

-- Index cho bảng customers
CREATE NONCLUSTERED INDEX IX_customers_email ON customers(email);
CREATE NONCLUSTERED INDEX IX_customers_username ON customers(username);
CREATE NONCLUSTERED INDEX IX_customers_role_id ON customers(role_id);

-- Index cho bảng staff
CREATE NONCLUSTERED INDEX IX_staff_email ON staff(email);
CREATE NONCLUSTERED INDEX IX_staff_username ON staff(username);
CREATE NONCLUSTERED INDEX IX_staff_role_id ON staff(role_id);

-- Index cho bảng tickets
CREATE NONCLUSTERED INDEX IX_tickets_customer_id ON tickets(customer_id);
CREATE NONCLUSTERED INDEX IX_tickets_order_id ON tickets(order_id);
CREATE NONCLUSTERED INDEX IX_tickets_status ON tickets(status);
CREATE NONCLUSTERED INDEX IX_tickets_priority ON tickets(priority);
CREATE NONCLUSTERED INDEX IX_tickets_created_at ON tickets(created_at);

-- Index cho bảng orders
CREATE NONCLUSTERED INDEX IX_orders_customer_id ON orders(customer_id);
CREATE NONCLUSTERED INDEX IX_orders_order_date ON orders(order_date);
CREATE NONCLUSTERED INDEX IX_orders_order_status ON orders(order_status);

-- Index cho bảng products
CREATE NONCLUSTERED INDEX IX_products_vendor_id ON products(vendor_id);
CREATE NONCLUSTERED INDEX IX_products_status ON products(status);

-- Index cho bảng invoices
CREATE NONCLUSTERED INDEX IX_invoices_order_id ON invoices(order_id);
CREATE NONCLUSTERED INDEX IX_invoices_invoice_number ON invoices(invoice_number);
CREATE NONCLUSTERED INDEX IX_invoices_issued_by ON invoices(issued_by);

PRINT 'All indexes created successfully';

-- =============================================
-- Kiểm tra dữ liệu đã tạo
-- =============================================

PRINT 'Database setup completed successfully!';
PRINT '=============================================';
PRINT 'Sample data summary:';
PRINT '=============================================';

-- Hiển thị số lượng records trong mỗi bảng
SELECT 'roles' as table_name, COUNT(*) as record_count FROM roles
UNION ALL
SELECT 'customers', COUNT(*) FROM customers
UNION ALL
SELECT 'staff', COUNT(*) FROM staff
UNION ALL
SELECT 'shifts', COUNT(*) FROM shifts
UNION ALL
SELECT 'vendors', COUNT(*) FROM vendors
UNION ALL
SELECT 'products', COUNT(*) FROM products
UNION ALL
SELECT 'shipping_methods', COUNT(*) FROM shipping_methods;

PRINT '=============================================';
PRINT 'Sample login credentials:';
PRINT '=============================================';
PRINT 'Customers:';
PRINT '1. an.nguyen@email.com / annguyen / password123';
PRINT '2. binh.tran@email.com / binhtran / password456';
PRINT '3. cuong.le@email.com / cuongle / password789';
PRINT '';
PRINT 'Staff:';
PRINT '1. admin@company.com / admin / admin123';
PRINT '2. cs@company.com / customer_service / cs123';
PRINT '3. tech@company.com / technical_support / tech123';
PRINT '4. finance@company.com / financial_support / finance123';
PRINT '=============================================';
