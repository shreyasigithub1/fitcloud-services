package identity_service.Identity.Service.service;

import identity_service.Identity.Service.domain.Status;
import identity_service.Identity.Service.domain.Type;
import identity_service.Identity.Service.domain.User;
import identity_service.Identity.Service.dto.AdminUserResponse;
import identity_service.Identity.Service.dto.CustomPageResponse;
import identity_service.Identity.Service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

//   └── getPendingOwners()
//   └── approveGymOwner()
//   └── rejectGymOwner()
//   └── suspendGymOwner()

    private final UserRepository userRepository;

    public CustomPageResponse<AdminUserResponse> getAllGymOwners(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));

        Page<AdminUserResponse> pageResult =
                userRepository.findByType(Type.GYM_OWNER, pageable)
                        .map(this::mapToResponse);

        return mapToCustomPageResponse(pageResult);
    }

    public CustomPageResponse<AdminUserResponse> getGymOwnersByFilter(
            List<Status> statuses, String searchKey, int page, int size, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));

        Page<User> pageResult;

        if (searchKey == null || searchKey.isBlank()) {
            // No search key — skip LIKE conditions entirely
            pageResult = userRepository.findByStatusInAndType(statuses, Type.GYM_OWNER, pageable);
        } else {
            pageResult = userRepository.findGymOwnersByFilter(statuses, searchKey.trim(), Type.GYM_OWNER, pageable);
        }

        log.info("FILTER SERVICE HIT");
        return mapToCustomPageResponse(pageResult.map(this::mapToResponse));
    }

    public CustomPageResponse<AdminUserResponse> getPendingOwners(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size);

        Page<AdminUserResponse> pageResult = userRepository.findByStatusAndType(Status.PENDING, Type.GYM_OWNER, pageable).map(this::mapToResponse);

        return mapToCustomPageResponse(pageResult);
    }

    public AdminUserResponse approveGymOwner(Integer id) {

        User user = new User();
        //Retrieving the User wih the id from DB
        user = userRepository.findById(id).orElseThrow(() -> new RuntimeException());
        //Keeping the status of that user
        String gymOwnerStatus = user.getStatus().name();

//        if (gymOwnerStatus.equals("ACTIVE")) {
//
//            throw new RuntimeException("User already ACTIVE");
//        }
//
        if (user.getStatus() == Status.ACTIVE) {
            throw new RuntimeException("User already ACTIVE");
        }
        if (user.getStatus() == Status.REJECTED) {
            throw new RuntimeException("Rejected user can't be approved.");
        }
        if (user.getStatus() == Status.SUSPENDED) {
            throw new RuntimeException("Suspended user can not be approved");
        }
        if (user.getType() != Type.GYM_OWNER) {
            throw new RuntimeException("Admin can only approve a Gym Owner");
        }


        //Now have to set the Users's status to ACTIVE
        user.setStatus(Status.ACTIVE);

        userRepository.save(user);
        return mapToResponse(user);


    }

    public AdminUserResponse rejectGymOwner(Integer id) {

        User user = new User();
        //Retrieving the User wih the id from DB
        user = userRepository.findById(id).orElseThrow(() -> new RuntimeException());


        if (user.getStatus() == Status.REJECTED) {
            throw new RuntimeException("User is already Rejected");
        }
        if (user.getStatus() == Status.SUSPENDED) {
            throw new RuntimeException("Suspended User can not be Rejected");
        }
        if (user.getType() != Type.GYM_OWNER) {
            throw new RuntimeException("Admin can only approve a Gym Owner");
        }

        //Now have to set the Users's status to ACTIVE
        user.setStatus(Status.REJECTED);

        userRepository.save(user);
        return mapToResponse(user);

    }

    public AdminUserResponse suspendGymOwner(Integer id) {

        User user = new User();
        //Retrieving the User wih the id from DB
        user = userRepository.findById(id).orElseThrow(() -> new RuntimeException());

        if (user.getStatus() == Status.REJECTED) {
            throw new RuntimeException("Rejected User can not be suspended");
        }
        if (user.getStatus() == Status.SUSPENDED) {
            throw new RuntimeException("The user is already Suspended");
        }
        if (user.getType() != Type.GYM_OWNER) {
            throw new RuntimeException("Admin can only approve a Gym Owner");
        }

        //Now have to set the Users's status to ACTIVE
        user.setStatus(Status.SUSPENDED);

        userRepository.save(user);

        return mapToResponse(user);
    }


    //This method takes the User and turn it to AdminUserResponse object
    private AdminUserResponse mapToResponse(User user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .userName(user.getUserName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .bio(user.getBio())
                .businessName(user.getBusinessName())
                .role(user.getRole().getRoleName())
                .status(user.getStatus().name())
                .type(user.getType().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    //This method is taking a paginated AdminUserResponse and returning custom paginated formatted AdminUserResponse following CustomPageResponse
    private CustomPageResponse<AdminUserResponse> mapToCustomPageResponse(
            Page<AdminUserResponse>
                    page
    ) {
        return CustomPageResponse.<AdminUserResponse>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }


}
