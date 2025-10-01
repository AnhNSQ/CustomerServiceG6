package CustomerService.repository;

import CustomerService.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    
    /**
     * Tìm staff theo email hoặc username (chỉ những tài khoản active)
     */
    @Query("SELECT s FROM Staff s WHERE s.isActive = true AND (s.email = :identifier OR s.username = :identifier)")
    Optional<Staff> findActiveByEmailOrUsername(@Param("identifier") String identifier);
    
    /**
     * Tìm staff theo ID và load cả role
     */
    @Query("SELECT s FROM Staff s LEFT JOIN FETCH s.role WHERE s.staffId = :id")
    Optional<Staff> findByIdWithRole(@Param("id") Long id);
    
    /**
     * Kiểm tra email đã tồn tại
     */
    boolean existsByEmail(String email);
    
    /**
     * Kiểm tra username đã tồn tại
     */
    boolean existsByUsername(String username);
    
    /**
     * Tìm staff theo email
     */
    Optional<Staff> findByEmail(String email);
    
    /**
     * Tìm staff theo username
     */
    Optional<Staff> findByUsername(String username);
}

