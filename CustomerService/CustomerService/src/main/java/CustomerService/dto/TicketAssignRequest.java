package CustomerService.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketAssignRequest {
    
    @NotNull(message = "Staff ID cannot be null")
    private Long staffId;
    
    @Size(max = 500, message = "Note cannot exceed 500 characters")
    private String note;
}
