package CustomerService.repository;

import CustomerService.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    /**
     * Tìm tất cả ticket của một customer
     */
    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.customer WHERE t.customer.customerId = :customerId ORDER BY t.createdAt DESC")
    List<Ticket> findByCustomerIdOrderByCreatedAtDesc(@Param("customerId") Long customerId);
    
    /**
     * Tìm ticket theo ID với customer
     */
    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.customer WHERE t.ticketId = :ticketId")
    Optional<Ticket> findByIdWithCustomer(@Param("ticketId") Long ticketId);
    
    
    /**
     * Tìm ticket theo status
     */
    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.customer WHERE t.status = :status ORDER BY t.createdAt DESC")
    List<Ticket> findByStatusOrderByCreatedAtDesc(@Param("status") Ticket.Status status);
}
