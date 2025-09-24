package CustomerService.repository;

import CustomerService.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Tìm role theo tên role
     */
    Optional<Role> findByRoleName(String roleName);
    
    /**
     * Kiểm tra role có tồn tại không
     */
    boolean existsByRoleName(String roleName);
}
