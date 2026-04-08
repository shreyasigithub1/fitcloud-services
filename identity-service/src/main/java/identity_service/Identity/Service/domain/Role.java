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

    //ROLE_ADMIN->
    //Can Create new Gyms, Delete Gyms, and view global stats.

    //ROLE_GYM_OWNER->
    //Can manage their specific Gym and add trainers.

    //ROLE_MEMBER->
    //Can only log their workouts and view their profile.

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @Column(unique = true)
    private String roleName;
}
