package CustomerService.repository;

import CustomerService.entity.TicketAssign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketAssignRepository extends JpaRepository<TicketAssign, Long> {
    
    /**
     * Tìm ticket assignments theo staff được phân công
     */
    @Query("SELECT ta FROM TicketAssign ta LEFT JOIN FETCH ta.ticket LEFT JOIN FETCH ta.assignedTo LEFT JOIN FETCH ta.assignedBy WHERE ta.assignedTo.staffId = :staffId ORDER BY ta.assignedAt DESC")
    List<TicketAssign> findByAssignedToStaffIdOrderByAssignedAtDesc(@Param("staffId") Long staffId);
    
    /**
     * Tìm ticket assignments theo ticket
     */
    @Query("SELECT ta FROM TicketAssign ta LEFT JOIN FETCH ta.ticket LEFT JOIN FETCH ta.assignedTo LEFT JOIN FETCH ta.assignedBy WHERE ta.ticket.ticketId = :ticketId ORDER BY ta.assignedAt DESC")
    List<TicketAssign> findByTicketIdOrderByAssignedAtDesc(@Param("ticketId") Long ticketId);
}


