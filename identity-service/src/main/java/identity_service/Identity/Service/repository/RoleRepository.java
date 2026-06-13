package identity_service.Identity.Service.repository;

import identity_service.Identity.Service.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface RoleRepository  extends JpaRepository<Role,Integer> {
    Optional<Role>  findByRoleName(String roleName);
}
