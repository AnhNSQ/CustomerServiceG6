package CustomerService.repository;

import CustomerService.entity.Role.RoleName;
import CustomerService.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    
    /**
     * Tìm staff theo email hoặc username (chỉ những tài khoản active) và load cả role
     */
    @Query("SELECT s FROM Staff s LEFT JOIN FETCH s.role WHERE s.isActive = true AND (s.email = :identifier OR s.username = :identifier)")
    Optional<Staff> findActiveByEmailOrUsernameWithRole(@Param("identifier") String identifier);
    
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
    
    /**
     * Tìm nhân viên trong phòng ban (trừ role cụ thể)
     */
    @Query("SELECT s FROM Staff s LEFT JOIN FETCH s.role LEFT JOIN FETCH s.staffDepartment " +
           "WHERE s.staffDepartment.staffDepartmentId = :departmentId AND s.role.roleName != :roleName AND s.isActive = true")
    List<Staff> findByStaffDepartmentIdAndRoleNameNot(@Param("departmentId") Long departmentId, @Param("roleName") RoleName roleName);
}

