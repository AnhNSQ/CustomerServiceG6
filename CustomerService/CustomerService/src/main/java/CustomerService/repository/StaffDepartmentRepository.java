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
     * Tìm staff department theo tên
     */
    Optional<StaffDepartment> findByName(String name);
}
