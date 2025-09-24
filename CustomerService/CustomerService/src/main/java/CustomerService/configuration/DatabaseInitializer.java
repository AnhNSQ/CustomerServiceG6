package CustomerService.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // Kiểm tra và tạo database nếu cần
            createDatabaseIfNotExists();
            
            // Chuyển sang sử dụng database customerService_db
            switchToCustomerServiceDatabase();
            
            // Tạo bảng nếu chưa tồn tại
            createTablesIfNotExists();
            
            // Insert dữ liệu mẫu nếu chưa có
            insertSampleDataIfNotExists();
            
            log.info("Database initialization completed successfully!");
            
        } catch (Exception e) {
            log.error("Error during database initialization: ", e);
            throw e;
        }
    }

    private void createDatabaseIfNotExists() {
        try {
            String sql = """
                IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'customerService_db')
                BEGIN
                    CREATE DATABASE customerService_db;
                    PRINT 'Database customerService_db created successfully';
                END
                ELSE
                BEGIN
                    PRINT 'Database customerService_db already exists';
                END
                """;
            
            jdbcTemplate.execute(sql);
            log.info("Database check/creation completed");
            
        } catch (Exception e) {
            log.error("Error creating database: ", e);
            throw e;
        }
    }

    private void switchToCustomerServiceDatabase() {
        try {
            jdbcTemplate.execute("USE customerService_db");
            log.info("Switched to customerService_db database");
        } catch (Exception e) {
            log.error("Error switching to customerService_db: ", e);
            throw e;
        }
    }

    private void createTablesIfNotExists() {
        try {
            // Tạo bảng roles
            String createRolesTable = """
                IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='roles' AND xtype='U')
                BEGIN
                    CREATE TABLE roles (
                        role_id BIGINT IDENTITY(1,1) PRIMARY KEY,
                        role_name NVARCHAR(50) NOT NULL UNIQUE,
                        description NVARCHAR(255)
                    );
                    PRINT 'Table roles created successfully';
                END
                """;
            
            jdbcTemplate.execute(createRolesTable);

            // Tạo bảng customers với foreign key đến roles
            String createCustomersTable = """
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
                    PRINT 'Table customers created successfully';
                END
                """;
            
            jdbcTemplate.execute(createCustomersTable);

            
            log.info("All tables created/verified successfully");
            
        } catch (Exception e) {
            log.error("Error creating tables: ", e);
            throw e;
        }
    }

    private void insertSampleDataIfNotExists() {
        try {
            // Insert roles nếu chưa có
            String insertRoles = """
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
                """;
            
            jdbcTemplate.execute(insertRoles);

            // Insert sample customers nếu chưa có
            String insertCustomers = """
                IF NOT EXISTS (SELECT 1 FROM customers WHERE email = 'an.nguyen@email.com')
                BEGIN
                    INSERT INTO customers (name, email, username, password, phone, is_active, register_date, role_id) VALUES 
                    ('Nguyễn Văn An', 'an.nguyen@email.com', 'annguyen', 'password123', '0123456789', 1, GETDATE(), 1),
                    ('Trần Thị Bình', 'binh.tran@email.com', 'binhtran', 'password456', '0987654321', 1, GETDATE(), 1),
                    ('Lê Văn Cường', 'cuong.le@email.com', 'cuongle', 'password789', '0369258147', 1, GETDATE(), 1);
                    
                    PRINT 'Sample customers inserted successfully';
                END
                """;
            
            jdbcTemplate.execute(insertCustomers);
            
            log.info("Sample data inserted/verified successfully");
            
        } catch (Exception e) {
            log.error("Error inserting sample data: ", e);
            throw e;
        }
    }
}
