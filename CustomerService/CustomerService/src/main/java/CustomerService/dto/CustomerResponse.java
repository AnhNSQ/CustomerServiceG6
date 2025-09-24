package CustomerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    
    private Long customerId;
    private String name;
    private String email;
    private String username;
    private String phone;
    private Boolean isActive;
    private LocalDateTime registerDate;
    private Set<String> roles;

    public CustomerResponse(Long customerId, String name, String email, String username, 
                           String phone, Boolean isActive, LocalDateTime registerDate) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.isActive = isActive;
        this.registerDate = registerDate;
    }
}
