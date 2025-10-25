package CustomerService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerTicketCreateRequest {
    
    @NotBlank(message = "Subject cannot be blank")
    @Size(max = 255, message = "Subject cannot exceed 255 characters")
    private String subject;
    
    @NotBlank(message = "Description cannot be blank")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;
    
    @NotNull(message = "Department must be selected")
    private Long departmentId;
}

