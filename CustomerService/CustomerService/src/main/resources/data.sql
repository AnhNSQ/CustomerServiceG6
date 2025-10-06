-- =============================================
-- Customer Service Database - Sample Data for MSSQL
-- =============================================
-- This file contains sample data for testing the Customer Service application
-- Note: Passwords are stored as plain text (not using bcrypt as requested)

-- =============================================
-- 1. ROLES TABLE
-- =============================================
INSERT INTO roles (role_name, description) VALUES 
('CUSTOMER', 'Regular customer role'),
('ADMIN', 'System administrator'),
('FINANCIAL_STAFF', 'Financial department staff'),
('TECHNICAL_SUPPORT', 'Technical support staff'),
('MANAGER', 'Department manager');

-- =============================================
-- 2. VENDORS TABLE
-- =============================================
INSERT INTO vendors (name, contact_info) VALUES 
('TechCorp Solutions', 'Email: contact@techcorp.com, Phone: +1-555-0101'),
('ElectroWorld', 'Email: sales@electroworld.com, Phone: +1-555-0102'),
('HomeGadgets Inc', 'Email: info@homegadgets.com, Phone: +1-555-0103'),
('MobileTech Pro', 'Email: support@mobiletech.com, Phone: +1-555-0104'),
('FashionStore Ltd', 'Email: orders@fashionstore.com, Phone: +1-555-0105');

-- =============================================
-- 3. SHIPPING METHODS TABLE
-- =============================================
INSERT INTO shipping_methods (name, cost_estimate, estimated_time) VALUES 
('Standard Shipping', 5.99, '3-5 business days'),
('Express Shipping', 12.99, '1-2 business days'),
('Overnight Shipping', 24.99, 'Next business day'),
('Free Shipping', 0.00, '5-7 business days'),
('International Shipping', 29.99, '7-14 business days');

-- =============================================
-- 4. SHIFTS TABLE
-- =============================================
INSERT INTO shifts (name, start_time, end_time) VALUES 
('Morning Shift', '08:00:00', '16:00:00'),
('Evening Shift', '16:00:00', '00:00:00'),
('Night Shift', '00:00:00', '08:00:00'),
('Weekend Shift', '10:00:00', '18:00:00'),
('Part-time Shift', '12:00:00', '16:00:00');

-- =============================================
-- 5. STAFF TABLE
-- =============================================
INSERT INTO staff (name, email, username, password, phone, is_active, register_date, role_id) VALUES 
('John Admin', 'john.admin@company.com', 'jadmin', 'admin123', '+1-555-1001', 1, GETDATE(), 2),
('Sarah Manager', 'sarah.manager@company.com', 'smanager', 'manager123', '+1-555-1002', 1, GETDATE(), 5),
('Mike Financial', 'mike.financial@company.com', 'mfinancial', 'finance123', '+1-555-1003', 1, GETDATE(), 3),
('Lisa Technical', 'lisa.technical@company.com', 'ltechnical', 'tech123', '+1-555-1004', 1, GETDATE(), 4),
('David Support', 'david.support@company.com', 'dsupport', 'support123', '+1-555-1005', 1, GETDATE(), 4),
('Emma Financial', 'emma.financial@company.com', 'efinancial', 'finance456', '+1-555-1006', 1, GETDATE(), 3),
('Tom Technical', 'tom.technical@company.com', 'ttechnical', 'tech456', '+1-555-1007', 1, GETDATE(), 4);

-- =============================================
-- 6. CUSTOMERS TABLE
-- =============================================
INSERT INTO customers (name, email, username, password, phone, is_active, register_date, role_id) VALUES 
('Alice Johnson', 'alice.johnson@email.com', 'alicej', 'password123', '+1-555-2001', 1, GETDATE(), 1),
('Bob Smith', 'bob.smith@email.com', 'bobsmith', 'password456', '+1-555-2002', 1, GETDATE(), 1),
('Carol Davis', 'carol.davis@email.com', 'carold', 'password789', '+1-555-2003', 1, GETDATE(), 1),
('David Wilson', 'david.wilson@email.com', 'davidw', 'password101', '+1-555-2004', 1, GETDATE(), 1),
('Eva Brown', 'eva.brown@email.com', 'evabrown', 'password202', '+1-555-2005', 1, GETDATE(), 1),
('Frank Miller', 'frank.miller@email.com', 'frankm', 'password303', '+1-555-2006', 1, GETDATE(), 1),
('Grace Lee', 'grace.lee@email.com', 'gracelee', 'password404', '+1-555-2007', 1, GETDATE(), 1),
('Henry Taylor', 'henry.taylor@email.com', 'henryt', 'password505', '+1-555-2008', 1, GETDATE(), 1);

-- =============================================
-- 7. PRODUCTS TABLE
-- =============================================
INSERT INTO products (vendor_id, name, description, price, quantity, status) VALUES 
(1, 'Laptop Pro 15"', 'High-performance laptop with 16GB RAM and 512GB SSD', 1299.99, 50, 'ACTIVE'),
(1, 'Gaming Mouse', 'RGB gaming mouse with programmable buttons', 79.99, 100, 'ACTIVE'),
(2, 'Smart TV 55"', '4K Ultra HD Smart TV with HDR support', 899.99, 25, 'ACTIVE'),
(2, 'Bluetooth Speaker', 'Portable wireless speaker with 20W output', 149.99, 75, 'ACTIVE'),
(3, 'Coffee Maker', 'Programmable coffee maker with thermal carafe', 89.99, 30, 'ACTIVE'),
(3, 'Air Purifier', 'HEPA air purifier for rooms up to 500 sq ft', 199.99, 20, 'ACTIVE'),
(4, 'Smartphone Pro', 'Latest smartphone with 128GB storage', 699.99, 40, 'ACTIVE'),
(4, 'Wireless Earbuds', 'True wireless earbuds with noise cancellation', 199.99, 60, 'ACTIVE'),
(5, 'Designer T-Shirt', 'Premium cotton t-shirt with modern design', 29.99, 200, 'ACTIVE'),
(5, 'Jeans Classic', 'Classic fit jeans made from organic cotton', 79.99, 150, 'ACTIVE');

-- =============================================
-- 8. ORDERS TABLE
-- =============================================
INSERT INTO orders (customer_id, order_date, total_amount, shipping_method_id, shipping_status, order_status, shipping_address) VALUES 
(1, GETDATE(), 1379.98, 1, 'PENDING', 'PENDING', '123 Main St, New York, NY 10001'),
(2, GETDATE(), 1049.98, 2, 'PENDING', 'PENDING', '456 Oak Ave, Los Angeles, CA 90210'),
(3, GETDATE(), 279.98, 3, 'PENDING', 'PENDING', '789 Pine Rd, Chicago, IL 60601'),
(4, GETDATE(), 899.99, 4, 'PENDING', 'PENDING', '321 Elm St, Houston, TX 77001'),
(5, GETDATE(), 289.98, 1, 'PENDING', 'PENDING', '654 Maple Dr, Phoenix, AZ 85001'),
(6, GETDATE(), 199.99, 2, 'PENDING', 'PENDING', '987 Cedar Ln, Philadelphia, PA 19101'),
(7, GETDATE(), 699.99, 3, 'PENDING', 'PENDING', '147 Birch St, San Antonio, TX 78201'),
(8, GETDATE(), 109.98, 4, 'PENDING', 'PENDING', '258 Spruce Ave, San Diego, CA 92101');

-- =============================================
-- 9. ORDER DETAILS TABLE
-- =============================================
INSERT INTO order_details (order_id, product_id, unit_price, quantity, sub_total) VALUES 
-- Order 1: Alice Johnson
(1, 1, 1299.99, 1, 1299.99),
(1, 2, 79.99, 1, 79.99),
-- Order 2: Bob Smith
(2, 3, 899.99, 1, 899.99),
(2, 4, 149.99, 1, 149.99),
-- Order 3: Carol Davis
(3, 5, 89.99, 1, 89.99),
(3, 6, 199.99, 1, 199.99),
-- Order 4: David Wilson
(4, 3, 899.99, 1, 899.99),
-- Order 5: Eva Brown
(5, 5, 89.99, 1, 89.99),
(5, 6, 199.99, 1, 199.99),
-- Order 6: Frank Miller
(6, 6, 199.99, 1, 199.99),
-- Order 7: Grace Lee
(7, 7, 699.99, 1, 699.99),
-- Order 8: Henry Taylor
(8, 9, 29.99, 1, 29.99),
(8, 10, 79.99, 1, 79.99);

-- =============================================
-- 10. INVOICES TABLE
-- =============================================
INSERT INTO invoices (order_id, invoice_date, total_amount, tax_amount, invoice_number, issued_by, status) VALUES 
(1, GETDATE(), 1379.98, 124.80, 'INV-2024-001', 3, 'PENDING'),
(2, GETDATE(), 1049.98, 94.50, 'INV-2024-002', 3, 'PENDING'),
(3, GETDATE(), 279.98, 25.20, 'INV-2024-003', 6, 'PENDING'),
(4, GETDATE(), 899.99, 81.00, 'INV-2024-004', 3, 'PENDING'),
(5, GETDATE(), 289.98, 26.10, 'INV-2024-005', 6, 'PENDING'),
(6, GETDATE(), 199.99, 18.00, 'INV-2024-006', 3, 'PENDING'),
(7, GETDATE(), 699.99, 63.00, 'INV-2024-007', 6, 'PENDING'),
(8, GETDATE(), 109.98, 9.90, 'INV-2024-008', 3, 'PENDING');

-- =============================================
-- 11. PAYMENTS TABLE
-- =============================================
INSERT INTO payments (invoice_id, payment_date, method, status) VALUES 
(1, GETDATE(), 'Credit Card', 'SUCCEEDED'),
(2, GETDATE(), 'PayPal', 'SUCCEEDED'),
(3, GETDATE(), 'Bank Transfer', 'SUCCEEDED'),
(4, GETDATE(), 'Credit Card', 'SUCCEEDED'),
(5, GETDATE(), 'PayPal', 'SUCCEEDED'),
(6, GETDATE(), 'Credit Card', 'SUCCEEDED'),
(7, GETDATE(), 'Bank Transfer', 'SUCCEEDED'),
(8, GETDATE(), 'PayPal', 'SUCCEEDED');

-- =============================================
-- 12. TICKETS TABLE
-- =============================================
INSERT INTO tickets (customer_id, order_id, subject, description, priority, status, created_at) VALUES 
(1, 1, 'Order Delivery Issue', 'My order was supposed to arrive yesterday but I haven''t received it yet. Can you please check the status?', 'HIGH', 'OPEN', GETDATE()),
(2, 2, 'Product Defect', 'The smart TV I received has a cracked screen. I need a replacement or refund.', 'HIGH', 'OPEN', GETDATE()),
(3, 3, 'Wrong Item Shipped', 'I ordered a coffee maker but received an air purifier instead. Please help me exchange this.', 'MEDIUM', 'OPEN', GETDATE()),
(4, 4, 'Billing Question', 'I was charged twice for the same order. Can you please check my invoice?', 'HIGH', 'OPEN', GETDATE()),
(5, 5, 'Return Request', 'I want to return the air purifier as it doesn''t work as expected.', 'MEDIUM', 'OPEN', GETDATE()),
(6, 6, 'Technical Support', 'I need help setting up the air purifier. The instructions are unclear.', 'LOW', 'OPEN', GETDATE()),
(7, 7, 'Warranty Claim', 'My smartphone stopped working after 2 weeks. I need warranty service.', 'HIGH', 'OPEN', GETDATE()),
(8, 8, 'Size Exchange', 'The t-shirt I ordered is too small. Can I exchange it for a larger size?', 'LOW', 'OPEN', GETDATE());

-- =============================================
-- 13. TICKET ASSIGNMENTS TABLE
-- =============================================
INSERT INTO ticket_assign (ticket_id, assigned_to, assigned_by, assigned_at, role_needed) VALUES 
(1, 4, 2, GETDATE(), 'TECHNICAL_SUPPORT'),
(2, 4, 2, GETDATE(), 'TECHNICAL_SUPPORT'),
(3, 7, 2, GETDATE(), 'TECHNICAL_SUPPORT'),
(4, 3, 2, GETDATE(), 'FINANCIAL_STAFF'),
(5, 6, 2, GETDATE(), 'FINANCIAL_STAFF'),
(6, 7, 2, GETDATE(), 'TECHNICAL_SUPPORT'),
(7, 4, 2, GETDATE(), 'TECHNICAL_SUPPORT'),
(8, 6, 2, GETDATE(), 'FINANCIAL_STAFF');

-- =============================================
-- 14. EVALUATIONS TABLE
-- =============================================
INSERT INTO evaluations (ticket_id, customer_id, score, comment, created_at) VALUES 
(1, 1, 4, 'Issue was resolved quickly. Staff was very helpful.', GETDATE()),
(2, 2, 5, 'Excellent service! Got a replacement within 2 days.', GETDATE()),
(3, 3, 3, 'The exchange process took longer than expected.', GETDATE()),
(4, 4, 4, 'Billing issue was resolved promptly.', GETDATE()),
(5, 5, 5, 'Return process was smooth and easy.', GETDATE()),
(6, 6, 4, 'Technical support was knowledgeable and helpful.', GETDATE()),
(7, 7, 3, 'Warranty claim is still being processed.', GETDATE()),
(8, 8, 5, 'Size exchange was handled perfectly.', GETDATE());

-- =============================================
-- 15. STAFF SHIFT ASSIGNMENTS TABLE
-- =============================================
INSERT INTO staff_shift_assign (staff_id, shift_id, date) VALUES 
-- Assignments for current week
(1, 1, CAST(GETDATE() AS DATE)), -- John Admin - Morning Shift today
(2, 1, CAST(GETDATE() AS DATE)), -- Sarah Manager - Morning Shift today
(3, 1, CAST(GETDATE() AS DATE)), -- Mike Financial - Morning Shift today
(4, 2, CAST(GETDATE() AS DATE)), -- Lisa Technical - Evening Shift today
(5, 2, CAST(GETDATE() AS DATE)), -- David Support - Evening Shift today
(6, 1, CAST(GETDATE() AS DATE)), -- Emma Financial - Morning Shift today
(7, 2, CAST(GETDATE() AS DATE)), -- Tom Technical - Evening Shift today

-- Assignments for tomorrow
(1, 1, CAST(DATEADD(day, 1, GETDATE()) AS DATE)), -- John Admin - Morning Shift tomorrow
(2, 1, CAST(DATEADD(day, 1, GETDATE()) AS DATE)), -- Sarah Manager - Morning Shift tomorrow
(3, 1, CAST(DATEADD(day, 1, GETDATE()) AS DATE)), -- Mike Financial - Morning Shift tomorrow
(4, 2, CAST(DATEADD(day, 1, GETDATE()) AS DATE)), -- Lisa Technical - Evening Shift tomorrow
(5, 2, CAST(DATEADD(day, 1, GETDATE()) AS DATE)), -- David Support - Evening Shift tomorrow
(6, 1, CAST(DATEADD(day, 1, GETDATE()) AS DATE)), -- Emma Financial - Morning Shift tomorrow
(7, 2, CAST(DATEADD(day, 1, GETDATE()) AS DATE)); -- Tom Technical - Evening Shift tomorrow

-- =============================================
-- UPDATE INVOICE STATUS AFTER PAYMENT
-- =============================================
UPDATE invoices SET status = 'PAID' WHERE invoice_id IN (1, 2, 3, 4, 5, 6, 7, 8);

-- =============================================
-- UPDATE ORDER STATUS AFTER PAYMENT
-- =============================================
UPDATE orders SET order_status = 'PAID' WHERE order_id IN (1, 2, 3, 4, 5, 6, 7, 8);

-- =============================================
-- UPDATE TICKET STATUS AFTER ASSIGNMENT
-- =============================================
UPDATE tickets SET status = 'ASSIGNED' WHERE ticket_id IN (1, 2, 3, 4, 5, 6, 7, 8);

-- =============================================
-- SAMPLE DATA SUMMARY
-- =============================================
-- Total Records Inserted:
-- - 5 Roles
-- - 5 Vendors  
-- - 5 Shipping Methods
-- - 5 Shifts
-- - 7 Staff Members
-- - 8 Customers
-- - 10 Products
-- - 8 Orders
-- - 13 Order Details
-- - 8 Invoices
-- - 8 Payments
-- - 8 Tickets
-- - 8 Ticket Assignments
-- - 8 Evaluations
-- - 14 Staff Shift Assignments
-- =============================================
