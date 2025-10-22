package CustomerService.repository;

import CustomerService.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Tìm role theo tên role (enum)
     */
    Optional<Role> findByRoleName(Role.RoleName roleName);
    
    /**
     * Kiểm tra role có tồn tại không (enum)
     */
    boolean existsByRoleName(Role.RoleName roleName);
}
