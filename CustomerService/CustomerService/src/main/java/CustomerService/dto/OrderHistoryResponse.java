package CustomerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryResponse {
    private Long historyId;
    private String action;
    private String oldStatus;
    private String newStatus;
    private String description;
    private Long performedByStaffId;
    private String performedByStaffName;
    private Long performedByCustomerId;
    private LocalDateTime createdAt;
}

