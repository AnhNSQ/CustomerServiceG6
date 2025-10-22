-- Sample data for Customer Service System
-- Using MSSQL syntax with nvarchar for all text fields

-- Insert Roles (if not exists) - Updated according to ERD
IF NOT EXISTS (SELECT 1 FROM roles WHERE role_name = N'CUSTOMER')
INSERT INTO roles (role_name, description) VALUES (N'CUSTOMER', N'Khách hàng');

IF NOT EXISTS (SELECT 1 FROM roles WHERE role_name = N'ADMIN')
INSERT INTO roles (role_name, description) VALUES (N'ADMIN', N'Quản trị viên');

IF NOT EXISTS (SELECT 1 FROM roles WHERE role_name = N'LEAD')
INSERT INTO roles (role_name, description) VALUES (N'LEAD', N'Trưởng nhóm');

IF NOT EXISTS (SELECT 1 FROM roles WHERE role_name = N'STAFF')
INSERT INTO roles (role_name, description) VALUES (N'STAFF', N'Nhân viên');

-- Insert Staff Departments (if not exists)
IF NOT EXISTS (SELECT 1 FROM staff_departments WHERE name = N'finance')
INSERT INTO staff_departments (name) VALUES (N'finance');

IF NOT EXISTS (SELECT 1 FROM staff_departments WHERE name = N'tech')
INSERT INTO staff_departments (name) VALUES (N'tech');

-- Insert Categories (if not exists)
IF NOT EXISTS (SELECT 1 FROM categories WHERE name = N'Gaming Gear')
INSERT INTO categories (name, description, is_active) VALUES (N'Gaming Gear', N'Thiết bị chơi game chuyên nghiệp', 1);

IF NOT EXISTS (SELECT 1 FROM categories WHERE name = N'Console')
INSERT INTO categories (name, description, is_active) VALUES (N'Console', N'Máy chơi game thế hệ mới', 1);

IF NOT EXISTS (SELECT 1 FROM categories WHERE name = N'Monitor')
INSERT INTO categories (name, description, is_active) VALUES (N'Monitor', N'Màn hình gaming chất lượng cao', 1);

IF NOT EXISTS (SELECT 1 FROM categories WHERE name = N'Laptop')
INSERT INTO categories (name, description, is_active) VALUES (N'Laptop', N'Laptop gaming và công việc', 1);

IF NOT EXISTS (SELECT 1 FROM categories WHERE name = N'Smartphone')
INSERT INTO categories (name, description, is_active) VALUES (N'Smartphone', N'Điện thoại thông minh', 1);

IF NOT EXISTS (SELECT 1 FROM categories WHERE name = N'Accessories')
INSERT INTO categories (name, description, is_active) VALUES (N'Accessories', N'Phụ kiện công nghệ', 1);

-- Insert Vendors (if not exists)
IF NOT EXISTS (SELECT 1 FROM vendors WHERE name = N'Công ty ABC')
INSERT INTO vendors (name, contact_info) VALUES (N'Công ty ABC', N'Địa chỉ: 123 Đường ABC, Quận 1, TP.HCM\nĐiện thoại: 0123456789\nEmail: contact@abc.com');

IF NOT EXISTS (SELECT 1 FROM vendors WHERE name = N'Công ty XYZ')
INSERT INTO vendors (name, contact_info) VALUES (N'Công ty XYZ', N'Địa chỉ: 456 Đường XYZ, Quận 2, TP.HCM\nĐiện thoại: 0987654321\nEmail: info@xyz.com');

IF NOT EXISTS (SELECT 1 FROM vendors WHERE name = N'Công ty DEF')
INSERT INTO vendors (name, contact_info) VALUES (N'Công ty DEF', N'Địa chỉ: 789 Đường DEF, Quận 3, TP.HCM\nĐiện thoại: 0369258147\nEmail: sales@def.com');


-- Insert Shifts (if not exists)
IF NOT EXISTS (SELECT 1 FROM shifts WHERE name = N'Ca sáng')
INSERT INTO shifts (name, start_time, end_time) VALUES (N'Ca sáng', '08:00:00', '16:00:00');

IF NOT EXISTS (SELECT 1 FROM shifts WHERE name = N'Ca chiều')
INSERT INTO shifts (name, start_time, end_time) VALUES (N'Ca chiều', '16:00:00', '00:00:00');

IF NOT EXISTS (SELECT 1 FROM shifts WHERE name = N'Ca đêm')
INSERT INTO shifts (name, start_time, end_time) VALUES (N'Ca đêm', '00:00:00', '08:00:00');

-- Insert Customers (if not exists)
IF NOT EXISTS (SELECT 1 FROM customers WHERE email = N'an.nguyen@email.com')
INSERT INTO customers (name, email, username, password, phone, is_active, register_date, role_id)
SELECT N'Nguyễn Văn An', N'an.nguyen@email.com', N'nguyenvanan', N'$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', N'0123456789', 1, GETDATE(), role_id
FROM roles WHERE role_name = N'CUSTOMER';

IF NOT EXISTS (SELECT 1 FROM customers WHERE email = N'binh.tran@email.com')
INSERT INTO customers (name, email, username, password, phone, is_active, register_date, role_id)
SELECT N'Trần Thị Bình', N'binh.tran@email.com', N'tranthibinh', N'$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', N'0987654321', 1, GETDATE(), role_id
FROM roles WHERE role_name = N'CUSTOMER';

IF NOT EXISTS (SELECT 1 FROM customers WHERE email = N'cuong.le@email.com')
INSERT INTO customers (name, email, username, password, phone, is_active, register_date, role_id)
SELECT N'Lê Văn Cường', N'cuong.le@email.com', N'levancuong', N'$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', N'0369258147', 1, GETDATE(), role_id
FROM roles WHERE role_name = N'CUSTOMER';

IF NOT EXISTS (SELECT 1 FROM customers WHERE email = N'dung.pham@email.com')
INSERT INTO customers (name, email, username, password, phone, is_active, register_date, role_id)
SELECT N'Phạm Thị Dung', N'dung.pham@email.com', N'phamthidung', N'$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', N'0147258369', 1, GETDATE(), role_id
FROM roles WHERE role_name = N'CUSTOMER';

-- Insert Staff (if not exists) - Updated according to ERD
IF NOT EXISTS (SELECT 1 FROM staff WHERE email = N'admin@company.com')
INSERT INTO staff (name, email, username, password, phone, is_active, register_date, role_id, staff_department_id)
SELECT N'Admin Nguyễn', N'admin@company.com', N'admin', N'$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'0123456780', 1, GETDATE(), r.role_id, sd.staff_department_id
FROM roles r, staff_departments sd
WHERE r.role_name = N'ADMIN' AND sd.name = N'finance';

IF NOT EXISTS (SELECT 1 FROM staff WHERE email = N'lead1@company.com')
INSERT INTO staff (name, email, username, password, phone, is_active, register_date, role_id, staff_department_id)
SELECT N'Lead Tài chính', N'lead1@company.com', N'lead1', N'$2a$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm', N'0123456781', 1, GETDATE(), r.role_id, sd.staff_department_id
FROM roles r, staff_departments sd
WHERE r.role_name = N'LEAD' AND sd.name = N'finance';

IF NOT EXISTS (SELECT 1 FROM staff WHERE email = N'lead2@company.com')
INSERT INTO staff (name, email, username, password, phone, is_active, register_date, role_id, staff_department_id)
SELECT N'Lead Kỹ thuật', N'lead2@company.com', N'lead2', N'$2a$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm', N'0123456782', 1, GETDATE(), r.role_id, sd.staff_department_id
FROM roles r, staff_departments sd
WHERE r.role_name = N'LEAD' AND sd.name = N'tech';

IF NOT EXISTS (SELECT 1 FROM staff WHERE email = N'finance1@company.com')
INSERT INTO staff (name, email, username, password, phone, is_active, register_date, role_id, staff_department_id)
SELECT N'Nhân viên Tài chính A', N'finance1@company.com', N'finance1', N'$2a$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm', N'0123456783', 1, GETDATE(), r.role_id, sd.staff_department_id
FROM roles r, staff_departments sd
WHERE r.role_name = N'STAFF' AND sd.name = N'finance';

IF NOT EXISTS (SELECT 1 FROM staff WHERE email = N'finance2@company.com')
INSERT INTO staff (name, email, username, password, phone, is_active, register_date, role_id, staff_department_id)
SELECT N'Nhân viên Tài chính B', N'finance2@company.com', N'finance2', N'$2a$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm', N'0123456784', 1, GETDATE(), r.role_id, sd.staff_department_id
FROM roles r, staff_departments sd
WHERE r.role_name = N'STAFF' AND sd.name = N'finance';

IF NOT EXISTS (SELECT 1 FROM staff WHERE email = N'tech1@company.com')
INSERT INTO staff (name, email, username, password, phone, is_active, register_date, role_id, staff_department_id)
SELECT N'Nhân viên Kỹ thuật A', N'tech1@company.com', N'tech1', N'$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'0123456785', 1, GETDATE(), r.role_id, sd.staff_department_id
FROM roles r, staff_departments sd
WHERE r.role_name = N'STAFF' AND sd.name = N'tech';

IF NOT EXISTS (SELECT 1 FROM staff WHERE email = N'tech2@company.com')
INSERT INTO staff (name, email, username, password, phone, is_active, register_date, role_id, staff_department_id)
SELECT N'Nhân viên Kỹ thuật B', N'tech2@company.com', N'tech2', N'$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'0123456786', 1, GETDATE(), r.role_id, sd.staff_department_id
FROM roles r, staff_departments sd
WHERE r.role_name = N'STAFF' AND sd.name = N'tech';

-- Insert Products (if not exists)
IF NOT EXISTS (SELECT 1 FROM products WHERE name = N'Laptop Dell Inspiron 15')
INSERT INTO products (vendor_id, category_id, name, description, price, quantity, status)
SELECT v.vendor_id, c.category_id, N'Laptop Dell Inspiron 15', N'Laptop Dell Inspiron 15 inch, RAM 8GB, SSD 256GB, Intel Core i5', 15000000.00, 50, N'ACTIVE'
FROM vendors v, categories c WHERE v.name = N'Công ty ABC' AND c.name = N'Laptop';

IF NOT EXISTS (SELECT 1 FROM products WHERE name = N'Laptop HP Pavilion 14')
INSERT INTO products (vendor_id, category_id, name, description, price, quantity, status)
SELECT v.vendor_id, c.category_id, N'Laptop HP Pavilion 14', N'Laptop HP Pavilion 14 inch, RAM 8GB, SSD 512GB, AMD Ryzen 5', 12000000.00, 30, N'ACTIVE'
FROM vendors v, categories c WHERE v.name = N'Công ty ABC' AND c.name = N'Laptop';

IF NOT EXISTS (SELECT 1 FROM products WHERE name = N'Điện thoại Samsung Galaxy S23')
INSERT INTO products (vendor_id, category_id, name, description, price, quantity, status)
SELECT v.vendor_id, c.category_id, N'Điện thoại Samsung Galaxy S23', N'Điện thoại Samsung Galaxy S23, 128GB, Camera 50MP', 20000000.00, 100, N'ACTIVE'
FROM vendors v, categories c WHERE v.name = N'Công ty XYZ' AND c.name = N'Smartphone';

IF NOT EXISTS (SELECT 1 FROM products WHERE name = N'Điện thoại iPhone 14')
INSERT INTO products (vendor_id, category_id, name, description, price, quantity, status)
SELECT v.vendor_id, c.category_id, N'Điện thoại iPhone 14', N'Điện thoại iPhone 14, 128GB, Camera 12MP', 25000000.00, 80, N'ACTIVE'
FROM vendors v, categories c WHERE v.name = N'Công ty XYZ' AND c.name = N'Smartphone';

IF NOT EXISTS (SELECT 1 FROM products WHERE name = N'Bàn phím cơ Logitech')
INSERT INTO products (vendor_id, category_id, name, description, price, quantity, status)
SELECT v.vendor_id, c.category_id, N'Bàn phím cơ Logitech', N'Bàn phím cơ Logitech G Pro X, switch Blue, RGB', 2500000.00, 200, N'ACTIVE'
FROM vendors v, categories c WHERE v.name = N'Công ty DEF' AND c.name = N'Gaming Gear';

IF NOT EXISTS (SELECT 1 FROM products WHERE name = N'Chuột gaming Razer')
INSERT INTO products (vendor_id, category_id, name, description, price, quantity, status)
SELECT v.vendor_id, c.category_id, N'Chuột gaming Razer', N'Chuột gaming Razer DeathAdder V2, 20000 DPI', 1500000.00, 150, N'ACTIVE'
FROM vendors v, categories c WHERE v.name = N'Công ty DEF' AND c.name = N'Gaming Gear';

-- Insert Orders (if not exists)
IF NOT EXISTS (SELECT 1 FROM orders WHERE customer_id = (SELECT customer_id FROM customers WHERE email = N'an.nguyen@email.com'))
INSERT INTO orders (customer_id, order_date, total_amount, shipping_method, cost_estimate, estimated_time, shipping_status, order_status, shipping_address)
SELECT c.customer_id, GETDATE(), 17500000.00, N'Giao hàng tiêu chuẩn', 30000.00, N'3-5 ngày làm việc', N'PENDING', N'PENDING', N'123 Đường ABC, Phường 1, Quận 1, TP.HCM'
FROM customers c
WHERE c.email = N'an.nguyen@email.com';

IF NOT EXISTS (SELECT 1 FROM orders WHERE customer_id = (SELECT customer_id FROM customers WHERE email = N'binh.tran@email.com'))
INSERT INTO orders (customer_id, order_date, total_amount, shipping_method, cost_estimate, estimated_time, shipping_status, order_status, shipping_address)
SELECT c.customer_id, GETDATE(), 26500000.00, N'Giao hàng nhanh', 50000.00, N'1-2 ngày làm việc', N'PENDING', N'PENDING', N'456 Đường XYZ, Phường 2, Quận 2, TP.HCM'
FROM customers c
WHERE c.email = N'binh.tran@email.com';

IF NOT EXISTS (SELECT 1 FROM orders WHERE customer_id = (SELECT customer_id FROM customers WHERE email = N'cuong.le@email.com'))
INSERT INTO orders (customer_id, order_date, total_amount, shipping_method, cost_estimate, estimated_time, shipping_status, order_status, shipping_address)
SELECT c.customer_id, GETDATE(), 4000000.00, N'Giao hàng tiêu chuẩn', 30000.00, N'3-5 ngày làm việc', N'SHIPPED', N'PAID', N'789 Đường DEF, Phường 3, Quận 3, TP.HCM'
FROM customers c
WHERE c.email = N'cuong.le@email.com';

-- Insert Order Details (if not exists)
IF NOT EXISTS (SELECT 1 FROM order_details WHERE order_id = (SELECT order_id FROM orders WHERE customer_id = (SELECT customer_id FROM customers WHERE email = N'an.nguyen@email.com')) AND product_id = (SELECT product_id FROM products WHERE name = N'Laptop Dell Inspiron 15'))
INSERT INTO order_details (order_id, product_id, unit_price, quantity, sub_total)
SELECT o.order_id, p.product_id, 15000000.00, 1, 15000000.00
FROM orders o, products p, customers c
WHERE c.email = N'an.nguyen@email.com' AND o.customer_id = c.customer_id AND p.name = N'Laptop Dell Inspiron 15';

IF NOT EXISTS (SELECT 1 FROM order_details WHERE order_id = (SELECT order_id FROM orders WHERE customer_id = (SELECT customer_id FROM customers WHERE email = N'an.nguyen@email.com')) AND product_id = (SELECT product_id FROM products WHERE name = N'Bàn phím cơ Logitech'))
INSERT INTO order_details (order_id, product_id, unit_price, quantity, sub_total)
SELECT o.order_id, p.product_id, 2500000.00, 1, 2500000.00
FROM orders o, products p, customers c
WHERE c.email = N'an.nguyen@email.com' AND o.customer_id = c.customer_id AND p.name = N'Bàn phím cơ Logitech';

IF NOT EXISTS (SELECT 1 FROM order_details WHERE order_id = (SELECT order_id FROM orders WHERE customer_id = (SELECT customer_id FROM customers WHERE email = N'binh.tran@email.com')) AND product_id = (SELECT product_id FROM products WHERE name = N'Điện thoại Samsung Galaxy S23'))
INSERT INTO order_details (order_id, product_id, unit_price, quantity, sub_total)
SELECT o.order_id, p.product_id, 20000000.00, 1, 20000000.00
FROM orders o, products p, customers c
WHERE c.email = N'binh.tran@email.com' AND o.customer_id = c.customer_id AND p.name = N'Điện thoại Samsung Galaxy S23';

IF NOT EXISTS (SELECT 1 FROM order_details WHERE order_id = (SELECT order_id FROM orders WHERE customer_id = (SELECT customer_id FROM customers WHERE email = N'binh.tran@email.com')) AND product_id = (SELECT product_id FROM products WHERE name = N'Điện thoại iPhone 14'))
INSERT INTO order_details (order_id, product_id, unit_price, quantity, sub_total)
SELECT o.order_id, p.product_id, 25000000.00, 1, 25000000.00
FROM orders o, products p, customers c
WHERE c.email = N'binh.tran@email.com' AND o.customer_id = c.customer_id AND p.name = N'Điện thoại iPhone 14';

IF NOT EXISTS (SELECT 1 FROM order_details WHERE order_id = (SELECT order_id FROM orders WHERE customer_id = (SELECT customer_id FROM customers WHERE email = N'cuong.le@email.com')) AND product_id = (SELECT product_id FROM products WHERE name = N'Bàn phím cơ Logitech'))
INSERT INTO order_details (order_id, product_id, unit_price, quantity, sub_total)
SELECT o.order_id, p.product_id, 2500000.00, 1, 2500000.00
FROM orders o, products p, customers c
WHERE c.email = N'cuong.le@email.com' AND o.customer_id = c.customer_id AND p.name = N'Bàn phím cơ Logitech';

IF NOT EXISTS (SELECT 1 FROM order_details WHERE order_id = (SELECT order_id FROM orders WHERE customer_id = (SELECT customer_id FROM customers WHERE email = N'cuong.le@email.com')) AND product_id = (SELECT product_id FROM products WHERE name = N'Chuột gaming Razer'))
INSERT INTO order_details (order_id, product_id, unit_price, quantity, sub_total)
SELECT o.order_id, p.product_id, 1500000.00, 1, 1500000.00
FROM orders o, products p, customers c
WHERE c.email = N'cuong.le@email.com' AND o.customer_id = c.customer_id AND p.name = N'Chuột gaming Razer';

-- Insert Invoices (if not exists)
IF NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_number = N'INV-001-2024')
INSERT INTO invoices (order_id, invoice_date, total_amount, tax_amount, invoice_number, issued_by, payment_method, status)
SELECT o.order_id, GETDATE(), 17500000.00, 1750000.00, N'INV-001-2024', s.staff_id, N'Chuyển khoản ngân hàng', N'PENDING'
FROM orders o, staff s, customers c
WHERE c.email = N'an.nguyen@email.com' AND o.customer_id = c.customer_id AND s.email = N'finance1@company.com';

IF NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_number = N'INV-002-2024')
INSERT INTO invoices (order_id, invoice_date, total_amount, tax_amount, invoice_number, issued_by, payment_method, status)
SELECT o.order_id, GETDATE(), 26500000.00, 2650000.00, N'INV-002-2024', s.staff_id, N'Thẻ tín dụng', N'PENDING'
FROM orders o, staff s, customers c
WHERE c.email = N'binh.tran@email.com' AND o.customer_id = c.customer_id AND s.email = N'finance1@company.com';

IF NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_number = N'INV-003-2024')
INSERT INTO invoices (order_id, invoice_date, total_amount, tax_amount, invoice_number, issued_by, payment_method, status)
SELECT o.order_id, GETDATE(), 4000000.00, 400000.00, N'INV-003-2024', s.staff_id, N'Ví điện tử', N'PAID'
FROM orders o, staff s, customers c
WHERE c.email = N'cuong.le@email.com' AND o.customer_id = c.customer_id AND s.email = N'finance2@company.com';


-- Insert Tickets (if not exists)
IF NOT EXISTS (SELECT 1 FROM tickets WHERE customer_id = (SELECT customer_id FROM customers WHERE email = N'an.nguyen@email.com'))
INSERT INTO tickets (customer_id, order_id, staff_department_id, subject, description, priority, status, created_at, closed_at)
SELECT c.customer_id, o.order_id, sd.staff_department_id, N'Vấn đề về giao hàng', N'Tôi đã đặt hàng từ 3 ngày trước nhưng chưa nhận được hàng. Xin hỗ trợ kiểm tra tình trạng giao hàng.', N'HIGH', N'OPEN', GETDATE(), NULL
FROM customers c, orders o, staff_departments sd
WHERE c.email = N'an.nguyen@email.com' AND o.customer_id = c.customer_id AND sd.name = N'tech';

IF NOT EXISTS (SELECT 1 FROM tickets WHERE customer_id = (SELECT customer_id FROM customers WHERE email = N'binh.tran@email.com'))
INSERT INTO tickets (customer_id, order_id, staff_department_id, subject, description, priority, status, created_at, closed_at)
SELECT c.customer_id, o.order_id, sd.staff_department_id, N'Hỏi về chính sách đổi trả', N'Tôi muốn đổi sản phẩm iPhone 14 sang màu khác. Xin hỗ trợ thông tin về chính sách đổi trả.', N'MEDIUM', N'ASSIGNED', GETDATE(), NULL
FROM customers c, orders o, staff_departments sd
WHERE c.email = N'binh.tran@email.com' AND o.customer_id = c.customer_id AND sd.name = N'finance';

IF NOT EXISTS (SELECT 1 FROM tickets WHERE customer_id = (SELECT customer_id FROM customers WHERE email = N'cuong.le@email.com'))
INSERT INTO tickets (customer_id, order_id, staff_department_id, subject, description, priority, status, created_at, closed_at)
SELECT c.customer_id, o.order_id, sd.staff_department_id, N'Lỗi sản phẩm', N'Bàn phím Logitech tôi mua có một số phím không hoạt động. Xin hỗ trợ xử lý.', N'HIGH', N'RESOLVED', GETDATE(), GETDATE()
FROM customers c, orders o, staff_departments sd
WHERE c.email = N'cuong.le@email.com' AND o.customer_id = c.customer_id AND sd.name = N'tech';

-- Insert Ticket Assignments (if not exists) - Updated according to ERD
IF NOT EXISTS (SELECT 1 FROM ticket_assign WHERE ticket_id = (SELECT ticket_id FROM tickets WHERE customer_id = (SELECT customer_id FROM customers WHERE email = N'an.nguyen@email.com')))
INSERT INTO ticket_assign (ticket_id, assigned_to, assigned_by, assigned_at, role_needed)
SELECT t.ticket_id, s1.staff_id, s2.staff_id, GETDATE(), N'TECHNICAL_SUPPORT'
FROM tickets t, staff s1, staff s2, customers c
WHERE c.email = N'an.nguyen@email.com' AND t.customer_id = c.customer_id AND s1.email = N'tech1@company.com' AND s2.email = N'admin@company.com';

IF NOT EXISTS (SELECT 1 FROM ticket_assign WHERE ticket_id = (SELECT ticket_id FROM tickets WHERE customer_id = (SELECT customer_id FROM customers WHERE email = N'binh.tran@email.com')))
INSERT INTO ticket_assign (ticket_id, assigned_to, assigned_by, assigned_at, role_needed)
SELECT t.ticket_id, s1.staff_id, s2.staff_id, GETDATE(), N'FINANCIAL_STAFF'
FROM tickets t, staff s1, staff s2, customers c
WHERE c.email = N'binh.tran@email.com' AND t.customer_id = c.customer_id AND s1.email = N'finance1@company.com' AND s2.email = N'admin@company.com';

IF NOT EXISTS (SELECT 1 FROM ticket_assign WHERE ticket_id = (SELECT ticket_id FROM tickets WHERE customer_id = (SELECT customer_id FROM customers WHERE email = N'cuong.le@email.com')))
INSERT INTO ticket_assign (ticket_id, assigned_to, assigned_by, assigned_at, role_needed)
SELECT t.ticket_id, s1.staff_id, s2.staff_id, GETDATE(), N'TECHNICAL_SUPPORT'
FROM tickets t, staff s1, staff s2, customers c
WHERE c.email = N'cuong.le@email.com' AND t.customer_id = c.customer_id AND s1.email = N'tech1@company.com' AND s2.email = N'admin@company.com';

-- Insert Evaluations (if not exists)
IF NOT EXISTS (SELECT 1 FROM evaluations WHERE ticket_id = (SELECT ticket_id FROM tickets WHERE customer_id = (SELECT customer_id FROM customers WHERE email = N'cuong.le@email.com')))
INSERT INTO evaluations (ticket_id, customer_id, score, comment, created_at)
SELECT t.ticket_id, c.customer_id, 5, N'Dịch vụ hỗ trợ rất tốt, nhân viên nhiệt tình và giải quyết vấn đề nhanh chóng.', GETDATE()
FROM tickets t, customers c
WHERE c.email = N'cuong.le@email.com' AND t.customer_id = c.customer_id;

-- Insert Staff Shift Assignments (if not exists) - Updated according to ERD
IF NOT EXISTS (SELECT 1 FROM staff_shift_assign WHERE staff_id = (SELECT staff_id FROM staff WHERE email = N'lead1@company.com') AND shift_id = (SELECT shift_id FROM shifts WHERE name = N'Ca sáng') AND date = CAST(GETDATE() AS DATE))
INSERT INTO staff_shift_assign (staff_id, shift_id, date)
SELECT s.staff_id, sh.shift_id, CAST(GETDATE() AS DATE)
FROM staff s, shifts sh
WHERE s.email = N'lead1@company.com' AND sh.name = N'Ca sáng';

IF NOT EXISTS (SELECT 1 FROM staff_shift_assign WHERE staff_id = (SELECT staff_id FROM staff WHERE email = N'finance1@company.com') AND shift_id = (SELECT shift_id FROM shifts WHERE name = N'Ca sáng') AND date = CAST(GETDATE() AS DATE))
INSERT INTO staff_shift_assign (staff_id, shift_id, date)
SELECT s.staff_id, sh.shift_id, CAST(GETDATE() AS DATE)
FROM staff s, shifts sh
WHERE s.email = N'finance1@company.com' AND sh.name = N'Ca sáng';

IF NOT EXISTS (SELECT 1 FROM staff_shift_assign WHERE staff_id = (SELECT staff_id FROM staff WHERE email = N'finance2@company.com') AND shift_id = (SELECT shift_id FROM shifts WHERE name = N'Ca sáng') AND date = CAST(GETDATE() AS DATE))
INSERT INTO staff_shift_assign (staff_id, shift_id, date)
SELECT s.staff_id, sh.shift_id, CAST(GETDATE() AS DATE)
FROM staff s, shifts sh
WHERE s.email = N'finance2@company.com' AND sh.name = N'Ca sáng';

IF NOT EXISTS (SELECT 1 FROM staff_shift_assign WHERE staff_id = (SELECT staff_id FROM staff WHERE email = N'lead2@company.com') AND shift_id = (SELECT shift_id FROM shifts WHERE name = N'Ca chiều') AND date = CAST(GETDATE() AS DATE))
INSERT INTO staff_shift_assign (staff_id, shift_id, date)
SELECT s.staff_id, sh.shift_id, CAST(GETDATE() AS DATE)
FROM staff s, shifts sh
WHERE s.email = N'lead2@company.com' AND sh.name = N'Ca chiều';

IF NOT EXISTS (SELECT 1 FROM staff_shift_assign WHERE staff_id = (SELECT staff_id FROM staff WHERE email = N'tech1@company.com') AND shift_id = (SELECT shift_id FROM shifts WHERE name = N'Ca chiều') AND date = CAST(GETDATE() AS DATE))
INSERT INTO staff_shift_assign (staff_id, shift_id, date)
SELECT s.staff_id, sh.shift_id, CAST(GETDATE() AS DATE)
FROM staff s, shifts sh
WHERE s.email = N'tech1@company.com' AND sh.name = N'Ca chiều';

IF NOT EXISTS (SELECT 1 FROM staff_shift_assign WHERE staff_id = (SELECT staff_id FROM staff WHERE email = N'tech2@company.com') AND shift_id = (SELECT shift_id FROM shifts WHERE name = N'Ca chiều') AND date = CAST(GETDATE() AS DATE))
INSERT INTO staff_shift_assign (staff_id, shift_id, date)
SELECT s.staff_id, sh.shift_id, CAST(GETDATE() AS DATE)
FROM staff s, shifts sh
WHERE s.email = N'tech2@company.com' AND sh.name = N'Ca chiều';