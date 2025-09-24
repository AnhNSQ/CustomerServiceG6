package CustomerService.repository;

import CustomerService.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    /**
     * Tìm customer theo email
     */
    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.role WHERE c.email = :email")
    Optional<Customer> findByEmail(@Param("email") String email);
    
    /**
     * Tìm customer theo username
     */
    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.role WHERE c.username = :username")
    Optional<Customer> findByUsername(@Param("username") String username);
    
    /**
     * Tìm customer theo email hoặc username
     */
    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.role WHERE c.email = :emailOrUsername OR c.username = :emailOrUsername")
    Optional<Customer> findByEmailOrUsername(@Param("emailOrUsername") String emailOrUsername);
    
    /**
     * Tìm customer theo ID với Role
     */
    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.role WHERE c.customerId = :customerId")
    Optional<Customer> findByIdWithRole(@Param("customerId") Long customerId);
    
    /**
     * Kiểm tra email đã tồn tại chưa
     */
    boolean existsByEmail(String email);
    
    /**
     * Kiểm tra username đã tồn tại chưa
     */
    boolean existsByUsername(String username);
    
    /**
     * Tìm customer active theo email hoặc username
     */
    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.role WHERE (c.email = :emailOrUsername OR c.username = :emailOrUsername) AND c.isActive = true")
    Optional<Customer> findActiveByEmailOrUsername(@Param("emailOrUsername") String emailOrUsername);
}
