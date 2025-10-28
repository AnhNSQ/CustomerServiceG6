package CustomerService.repository;

import CustomerService.entity.TicketReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketReplyRepository extends JpaRepository<TicketReply, Long> {
    
    /**
     * Tìm tất cả replies của một ticket, sắp xếp theo thời gian tạo
     */
    @Query("SELECT tr FROM TicketReply tr LEFT JOIN FETCH tr.ticket WHERE tr.ticket.ticketId = :ticketId ORDER BY tr.createdAt ASC")
    List<TicketReply> findByTicketIdOrderByCreatedAtAsc(@Param("ticketId") Long ticketId);
}


