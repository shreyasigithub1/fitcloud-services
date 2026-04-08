package identity_service.Identity.Service.repository;

import identity_service.Identity.Service.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository  extends JpaRepository<Role,Integer> {
}
