package CustomerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    
    private Long ticketId;
    private String subject;
    private String description;
    private String priority;
    private String status;
    private LocalDateTime createdAt;
    private Long customerId;
    private Long staffDepartmentId;
    private String staffDepartmentName;
}
