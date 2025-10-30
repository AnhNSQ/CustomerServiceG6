package CustomerService.repository;

import CustomerService.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    @Query("SELECT e FROM Evaluation e LEFT JOIN FETCH e.ticket LEFT JOIN FETCH e.customer WHERE e.ticket.ticketId = :ticketId")
    Optional<Evaluation> findByTicketId(@Param("ticketId") Long ticketId);

    boolean existsByTicket_TicketId(Long ticketId);

    @Query("SELECT AVG(e.score) FROM Evaluation e JOIN e.ticket t JOIN t.ticketAssignments ta WHERE ta.assignedTo.staffId = :staffId")
    Double getAverageScoreForStaff(@Param("staffId") Long staffId);

    @Query("SELECT e FROM Evaluation e JOIN e.ticket t JOIN t.ticketAssignments ta WHERE ta.assignedTo.staffId = :staffId ORDER BY e.createdAt DESC")
    List<Evaluation> findByStaffId(@Param("staffId") Long staffId);
}


