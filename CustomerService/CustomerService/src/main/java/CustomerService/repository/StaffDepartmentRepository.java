package CustomerService.repository;

import CustomerService.entity.StaffDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffDepartmentRepository extends JpaRepository<StaffDepartment, Long> {
    
    /**
     * Tìm department theo tên
     */
    Optional<StaffDepartment> findByName(String name);
    
    /**
     * Kiểm tra department có tồn tại theo tên
     */
    boolean existsByName(String name);
    
    /**
     * Tìm department theo ID với staff
     */
    @Query("SELECT sd FROM StaffDepartment sd LEFT JOIN FETCH sd.staff WHERE sd.staffDepartmentId = :id")
    Optional<StaffDepartment> findByIdWithStaff(@Param("id") Long id);
}
