package CustomerService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLoginRequest {
    
    @NotBlank(message = "Email hoặc Username không được để trống")
    private String emailOrUsername;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
}
