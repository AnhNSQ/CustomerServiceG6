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
    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.customer LEFT JOIN FETCH t.staffDepartment WHERE t.customer.customerId = :customerId ORDER BY t.createdAt DESC")
    List<Ticket> findByCustomerIdOrderByCreatedAtDesc(@Param("customerId") Long customerId);
    
    /**
     * Tìm ticket theo ID với customer và department
     */
    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.customer LEFT JOIN FETCH t.staffDepartment WHERE t.ticketId = :ticketId")
    Optional<Ticket> findByIdWithCustomer(@Param("ticketId") Long ticketId);

    /**
     * Tìm ticket theo status
     */
    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.customer LEFT JOIN FETCH t.staffDepartment WHERE t.status = :status ORDER BY t.createdAt DESC")
    List<Ticket> findByStatusOrderByCreatedAtDesc(@Param("status") Ticket.Status status);

    /**
     * Lấy tất cả ticket kèm thông tin customer và department, sắp xếp theo thời gian tạo mới nhất
     */
    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.customer LEFT JOIN FETCH t.staffDepartment ORDER BY t.createdAt DESC")
    List<Ticket> findAllWithCustomerOrderByCreatedAtDesc();

    /**
     * Đếm số lượng theo trạng thái
     */
    long countByStatus(Ticket.Status status);

    /**
     * Đếm số lượng ticket có priority và status cụ thể
     */
    long countByPriorityAndStatus(Ticket.Priority priority, Ticket.Status status);

    /**
     * Lấy 5 ticket gần nhất
     */
    List<Ticket> findTop5ByOrderByCreatedAtDesc();
    
    /**
     * Tìm ticket theo department
     */
    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.customer LEFT JOIN FETCH t.staffDepartment WHERE t.staffDepartment.staffDepartmentId = :departmentId ORDER BY t.createdAt DESC")
    List<Ticket> findByStaffDepartmentIdOrderByCreatedAtDesc(@Param("departmentId") Long departmentId);
}
