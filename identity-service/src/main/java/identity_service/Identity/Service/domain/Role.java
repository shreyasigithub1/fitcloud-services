package identity_service.Identity.Service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    // ROLE_ADMIN          → seeded in DB manually
    // ROLE_PENDING_OWNER  → gym owner just registered
    // ROLE_GYM_OWNER      → approved by admin
    // ROLE_MEMBER         → self registered member

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @Column(unique = true)
    private String roleName;
}
