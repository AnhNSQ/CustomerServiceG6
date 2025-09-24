-- Tạo database CustomerService
USE master;
GO

-- Kiểm tra và tạo database nếu chưa tồn tại
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'customerService_db')
BEGIN
    CREATE DATABASE customerService_db;
END
GO

-- Sử dụng database customerService_db
USE customerService_db;
GO

-- Tạo bảng roles trước
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='roles' AND xtype='U')
BEGIN
    CREATE TABLE roles (
        role_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        role_name NVARCHAR(50) NOT NULL UNIQUE,
        description NVARCHAR(255)
    );
    
    -- Insert các role cơ bản
    INSERT INTO roles (role_name, description) VALUES 
    ('CUSTOMER', 'Khách hàng'),
    ('ADMIN', 'Quản trị viên'),
    ('CUSTOMER_SERVICE_AGENT', 'Nhân viên chăm sóc khách hàng'),
    ('TECHNICAL_SUPPORT', 'Hỗ trợ kỹ thuật'),
    ('FINANCIAL_SUPPORT', 'Hỗ trợ tài chính');
END
GO

-- Tạo bảng customers với foreign key đến roles
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
        role_id BIGINT NOT NULL DEFAULT 1,
        FOREIGN KEY (role_id) REFERENCES roles(role_id)
    );
END
GO

-- Insert 3 tài khoản customer mẫu
-- Xóa dữ liệu cũ nếu có
DELETE FROM customers WHERE customer_id IN (1, 2, 3);

-- Reset identity để bắt đầu từ 1
DBCC CHECKIDENT ('customers', RESEED, 0);

-- Insert 3 customers mẫu với role_id = 1 (CUSTOMER)
INSERT INTO customers (name, email, username, password, phone, is_active, register_date, role_id) VALUES 
('Nguyễn Văn An', 'an.nguyen@email.com', 'annguyen', 'password123', '0123456789', 1, GETDATE(), 1),
('Trần Thị Bình', 'binh.tran@email.com', 'binhtran', 'password456', '0987654321', 1, GETDATE(), 1),
('Lê Văn Cường', 'cuong.le@email.com', 'cuongle', 'password789', '0369258147', 1, GETDATE(), 1);

-- Kiểm tra dữ liệu đã insert
SELECT 
    c.customer_id,
    c.name,
    c.email,
    c.username,
    c.phone,
    c.is_active,
    c.register_date,
    r.role_name
FROM customers c
LEFT JOIN roles r ON c.role_id = r.role_id
ORDER BY c.customer_id;

-- Tạo bảng staff
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
END
GO

-- Tạo bảng tickets
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='tickets' AND xtype='U')
BEGIN
    CREATE TABLE tickets (
        ticket_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        customer_id BIGINT NOT NULL,
        subject NVARCHAR(255) NOT NULL,
        description TEXT,
        priority NVARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
        status NVARCHAR(20) NOT NULL DEFAULT 'OPEN',
        created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        closed_at DATETIME2,
        FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
    );
END
GO

-- Tạo bảng ticket_assign
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
END
GO

-- Tạo bảng evaluations
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
END
GO

-- Tạo bảng shifts
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='shifts' AND xtype='U')
BEGIN
    CREATE TABLE shifts (
        shift_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        start_time TIME NOT NULL,
        end_time TIME NOT NULL
    );
END
GO

-- Tạo bảng staff_shift_assign
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
END
GO

-- Tạo bảng vendors
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='vendors' AND xtype='U')
BEGIN
    CREATE TABLE vendors (
        vendor_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        contact_info TEXT
    );
END
GO

-- Tạo bảng products
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
END
GO

-- Tạo bảng shipping_methods
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='shipping_methods' AND xtype='U')
BEGIN
    CREATE TABLE shipping_methods (
        shipping_method_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        cost_estimate DECIMAL(10,2) NOT NULL,
        estimated_time NVARCHAR(100)
    );
END
GO

-- Tạo bảng orders
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
END
GO

-- Tạo bảng order_details
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
END
GO

-- Tạo bảng invoices
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
END
GO

-- Tạo bảng payments
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
END
GO

-- Insert dữ liệu mẫu cho staff
INSERT INTO staff (name, email, username, password, phone, is_active, register_date, role_id) VALUES 
('Nguyễn Văn Admin', 'admin@company.com', 'admin', 'admin123', '0123456789', 1, GETDATE(), 2),
('Trần Thị CS', 'cs@company.com', 'customer_service', 'cs123', '0987654321', 1, GETDATE(), 3),
('Lê Văn Tech', 'tech@company.com', 'technical_support', 'tech123', '0369258147', 1, GETDATE(), 4),
('Phạm Thị Finance', 'finance@company.com', 'financial_support', 'finance123', '0147258369', 1, GETDATE(), 5);

-- Insert dữ liệu mẫu cho shifts
INSERT INTO shifts (name, start_time, end_time) VALUES 
('Ca sáng', '08:00:00', '16:00:00'),
('Ca chiều', '16:00:00', '00:00:00'),
('Ca đêm', '00:00:00', '08:00:00');

-- Insert dữ liệu mẫu cho vendors
INSERT INTO vendors (name, contact_info) VALUES 
('Công ty ABC', 'Địa chỉ: 123 Đường ABC, Quận 1, TP.HCM. ĐT: 0123456789'),
('Công ty XYZ', 'Địa chỉ: 456 Đường XYZ, Quận 2, TP.HCM. ĐT: 0987654321'),
('Công ty DEF', 'Địa chỉ: 789 Đường DEF, Quận 3, TP.HCM. ĐT: 0369258147');

-- Insert dữ liệu mẫu cho products
INSERT INTO products (vendor_id, name, description, price, quantity, status) VALUES 
(1, 'Laptop Dell XPS 13', 'Laptop cao cấp với màn hình 13 inch', 25000000, 10, 'ACTIVE'),
(1, 'Mouse Logitech MX Master', 'Chuột không dây cao cấp', 2500000, 50, 'ACTIVE'),
(2, 'Keyboard Mechanical', 'Bàn phím cơ với switch Cherry MX', 1500000, 30, 'ACTIVE'),
(2, 'Monitor 27 inch 4K', 'Màn hình 4K 27 inch', 8000000, 15, 'ACTIVE'),
(3, 'Webcam HD', 'Webcam HD 1080p', 1200000, 25, 'ACTIVE');

-- Insert dữ liệu mẫu cho shipping_methods
INSERT INTO shipping_methods (name, cost_estimate, estimated_time) VALUES 
('Giao hàng tiêu chuẩn', 30000, '3-5 ngày làm việc'),
('Giao hàng nhanh', 50000, '1-2 ngày làm việc'),
('Giao hàng siêu tốc', 100000, 'Trong ngày');

PRINT 'Database setup completed successfully!';
PRINT '3 sample customers have been created:';
PRINT '1. an.nguyen@email.com / annguyen / password123';
PRINT '2. binh.tran@email.com / binhtran / password456';
PRINT '3. cuong.le@email.com / cuongle / password789';
PRINT '';
PRINT '4 sample staff have been created:';
PRINT '1. admin@company.com / admin / admin123';
PRINT '2. cs@company.com / customer_service / cs123';
PRINT '3. tech@company.com / technical_support / tech123';
PRINT '4. finance@company.com / financial_support / finance123';
PRINT '';
PRINT 'Sample data for shifts, vendors, products, and shipping methods have been created.';
