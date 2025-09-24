# Customer Service - Register & Login API

Dự án Customer Service cung cấp các API để đăng ký và đăng nhập cho khách hàng, được phát triển theo nguyên tắc SOLID và sử dụng MSSQL database.

## Công nghệ sử dụng

- **Spring Boot 3.5.6**
- **Spring Security** (không sử dụng PasswordEncoder)
- **Spring Data JPA**
- **Microsoft SQL Server**
- **Lombok**
- **Jakarta Validation**

## Cấu trúc dự án

```
src/main/java/CustomerService/
├── Application.java                 # Main application class
├── configuration/
│   ├── SecurityConfig.java         # Cấu hình Spring Security
│   └── GlobalExceptionHandler.java # Xử lý exception toàn cục
├── controller/
│   └── CustomerController.java     # REST API endpoints
├── dto/
│   ├── ApiResponse.java            # Response wrapper
│   ├── CustomerLoginRequest.java   # DTO cho login
│   ├── CustomerRegisterRequest.java # DTO cho register
│   └── CustomerResponse.java       # DTO cho response
├── entity/
│   ├── Customer.java               # Entity Customer
│   └── Role.java                   # Entity Role
├── repository/
│   ├── CustomerRepository.java     # Repository cho Customer
│   └── RoleRepository.java         # Repository cho Role
└── service/
    └── CustomerService.java        # Business logic
```

## Database Setup

1. Chạy file SQL `database_setup.sql` để tạo database và dữ liệu mẫu:

```sql
-- Tạo database customerService_db
-- Tạo bảng customers, roles, customer_roles
-- Insert 3 tài khoản mẫu
```

### Tài khoản mẫu:
1. **Email:** an.nguyen@email.com | **Username:** annguyen | **Password:** password123
2. **Email:** binh.tran@email.com | **Username:** binhtran | **Password:** password456  
3. **Email:** cuong.le@email.com | **Username:** cuongle | **Password:** password789

## API Endpoints

### 1. Đăng ký tài khoản
```http
POST /api/customers/register
Content-Type: application/json

{
    "name": "Nguyễn Văn A",
    "email": "a.nguyen@email.com",
    "username": "anguyen",
    "password": "password123",
    "phone": "0123456789"
}
```

**Response:**
```json
{
    "success": true,
    "message": "Đăng ký thành công",
    "data": {
        "customerId": 1,
        "name": "Nguyễn Văn A",
        "email": "a.nguyen@email.com",
        "username": "anguyen",
        "phone": "0123456789",
        "isActive": true,
        "registerDate": "2024-01-01T10:00:00",
        "roles": ["CUSTOMER"]
    }
}
```

### 2. Đăng nhập
```http
POST /api/customers/login
Content-Type: application/json

{
    "emailOrUsername": "a.nguyen@email.com",
    "password": "password123"
}
```

**Response:**
```json
{
    "success": true,
    "message": "Đăng nhập thành công",
    "data": {
        "customerId": 1,
        "name": "Nguyễn Văn A",
        "email": "a.nguyen@email.com",
        "username": "anguyen",
        "phone": "0123456789",
        "isActive": true,
        "registerDate": "2024-01-01T10:00:00",
        "roles": ["CUSTOMER"]
    }
}
```

### 3. Đăng xuất
```http
POST /api/customers/logout
```

### 4. Lấy thông tin profile
```http
GET /api/customers/profile
```

### 5. Kiểm tra email đã tồn tại
```http
GET /api/customers/check-email?email=a.nguyen@email.com
```

### 6. Kiểm tra username đã tồn tại
```http
GET /api/customers/check-username?username=anguyen
```

## Cấu hình

### Database Configuration (application.properties)
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=customerService_db;encrypt=false;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=123456789
```

### Security Configuration
- Các endpoint `/api/customers/register`, `/api/customers/login` được phép truy cập công khai
- Các endpoint khác yêu cầu authentication
- Sử dụng session-based authentication
- Không sử dụng PasswordEncoder (theo yêu cầu)

## Chạy ứng dụng

1. **Cài đặt database:**
   - Chạy file `database_setup.sql` trong SQL Server Management Studio

2. **Cấu hình database:**
   - Cập nhật thông tin database trong `application.properties`

3. **Chạy ứng dụng:**
   ```bash
   mvn spring-boot:run
   ```

4. **Truy cập:**
   - Server sẽ chạy tại: `http://localhost:8080`
   - API endpoints: `http://localhost:8080/api/customers/`

## Validation Rules

### CustomerRegisterRequest:
- **name:** Không được để trống, 2-100 ký tự
- **email:** Không được để trống, định dạng email hợp lệ, tối đa 100 ký tự
- **username:** Không được để trống, 3-50 ký tự, chỉ chứa chữ cái, số và dấu gạch dưới
- **password:** Không được để trống, 6-50 ký tự
- **phone:** Tối đa 20 ký tự, chỉ chứa số và ký tự đặc biệt cho số điện thoại

### CustomerLoginRequest:
- **emailOrUsername:** Không được để trống
- **password:** Không được để trống

## Error Handling

Tất cả API đều trả về response theo format:
```json
{
    "success": boolean,
    "message": string,
    "data": object | null
}
```

## Session Management

- Sử dụng HTTP Session để quản lý trạng thái đăng nhập
- Session timeout: 30 phút
- Thông tin lưu trong session: customerId, customerName, customerEmail, customerRoles
