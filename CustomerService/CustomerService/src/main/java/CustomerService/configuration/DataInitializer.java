package CustomerService.configuration;

import CustomerService.entity.*;
import CustomerService.repository.CustomerRepository;
import CustomerService.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");
        
        // Kiểm tra xem đã có dữ liệu chưa
        if (roleRepository.count() > 0) {
            log.info("Data already exists, skipping initialization");
            return;
        }

        try {
            // Tạo roles
            createRoles();
            
            // Tạo customers
            createCustomers();
            
            log.info("Data initialization completed successfully!");
            
        } catch (Exception e) {
            log.error("Error during data initialization: ", e);
            throw e;
        }
    }

    private void createRoles() {
        log.info("Creating roles...");
        
        Role customerRole = new Role("CUSTOMER", "Khách hàng");
        Role adminRole = new Role("ADMIN", "Quản trị viên");
        Role csAgentRole = new Role("CUSTOMER_SERVICE_AGENT", "Nhân viên chăm sóc khách hàng");
        Role techSupportRole = new Role("TECHNICAL_SUPPORT", "Hỗ trợ kỹ thuật");
        Role financialSupportRole = new Role("FINANCIAL_SUPPORT", "Hỗ trợ tài chính");

        roleRepository.save(customerRole);
        roleRepository.save(adminRole);
        roleRepository.save(csAgentRole);
        roleRepository.save(techSupportRole);
        roleRepository.save(financialSupportRole);
        
        log.info("Roles created successfully");
    }

    private void createCustomers() {
        log.info("Creating sample customers...");
        
        Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Customer role not found"));

        Customer customer1 = new Customer(
                "Nguyễn Văn An",
                "an.nguyen@email.com",
                "annguyen",
                "password123",
                "0123456789",
                customerRole
        );

        Customer customer2 = new Customer(
                "Trần Thị Bình",
                "binh.tran@email.com",
                "binhtran",
                "password456",
                "0987654321",
                customerRole
        );

        Customer customer3 = new Customer(
                "Lê Văn Cường",
                "cuong.le@email.com",
                "cuongle",
                "password789",
                "0369258147",
                customerRole
        );

        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);
        
        log.info("Sample customers created successfully");
    }
}
