package CustomerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponse {
    private Long staffId;
    private String name;
    private String email;
    private String username;
    private String phone;
    private Boolean isActive;
    private LocalDateTime registerDate;
    private Set<String> roles;
}

