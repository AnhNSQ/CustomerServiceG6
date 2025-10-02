package CustomerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDashboardStats {
    private long totalTickets;
    private long pendingTickets;
    private long resolvedTickets;
    private long urgentTickets;
}
