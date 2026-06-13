package identity_service.Identity.Service.repository;

import identity_service.Identity.Service.domain.Status;
import identity_service.Identity.Service.domain.Type;
import identity_service.Identity.Service.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserNameOrEmail(String userName, String email);

    @Query("""
                SELECT u
                FROM User u
                WHERE u.status IN :statuses
                AND u.type = :type 
                AND (
                    LOWER(u.name) LIKE LOWER(CONCAT('%', :searchKey, '%'))
                    OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :searchKey, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchKey, '%'))           
                    OR LOWER(u.businessName) LIKE LOWER(CONCAT('%', :searchKey, '%'))           
                )           
            """)
    Page<User> findGymOwnersByFilter(@Param("statuses") List<Status> statuses, @Param("searchKey") String searchKey, @Param("type") Type type, Pageable pageable);

    // ← ADD THIS
    Page<User> findByStatusInAndType(List<Status> statuses, Type type, Pageable pageable);

    Page<User> findByStatusAndType(Status status, Type type, Pageable pageable);

    Optional<User> findById(Integer id);

    Page<User> findByType(Type type, Pageable pageable);
}
//give me that column if search key equals to name or userName or businessName
//or stausses is equals to statusses and type is equal to Type