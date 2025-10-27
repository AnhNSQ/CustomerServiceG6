package CustomerService.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TicketResponse {
    
    private Long ticketId;
    private String subject;
    private String description;
    private String priority;
    private String status;
    private LocalDateTime createdAt;
    private Long customerId;
    private String customerName;  // Thêm field customer name
    private Long staffDepartmentId;
    private String staffDepartmentName;
    private Long assignedToStaffId;  // ID nhân viên được assign
    private String assignedToStaffName;  // Tên nhân viên được assign
    
    // Constructor cho backward compatibility
    public TicketResponse(Long ticketId, String subject, String description, 
                         String priority, String status, LocalDateTime createdAt,
                         Long customerId, String customerName, 
                         Long staffDepartmentId, String staffDepartmentName) {
        this.ticketId = ticketId;
        this.subject = subject;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
        this.customerId = customerId;
        this.customerName = customerName;
        this.staffDepartmentId = staffDepartmentId;
        this.staffDepartmentName = staffDepartmentName;
        this.assignedToStaffId = null;
        this.assignedToStaffName = null;
    }
    
    // Full constructor
    public TicketResponse(Long ticketId, String subject, String description, 
                         String priority, String status, LocalDateTime createdAt,
                         Long customerId, String customerName, 
                         Long staffDepartmentId, String staffDepartmentName,
                         Long assignedToStaffId, String assignedToStaffName) {
        this.ticketId = ticketId;
        this.subject = subject;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
        this.customerId = customerId;
        this.customerName = customerName;
        this.staffDepartmentId = staffDepartmentId;
        this.staffDepartmentName = staffDepartmentName;
        this.assignedToStaffId = assignedToStaffId;
        this.assignedToStaffName = assignedToStaffName;
    }
}
