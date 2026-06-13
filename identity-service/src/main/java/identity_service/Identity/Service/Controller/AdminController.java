package identity_service.Identity.Service.Controller;

import identity_service.Identity.Service.domain.Status;
import identity_service.Identity.Service.domain.Type;
import identity_service.Identity.Service.domain.User;
import identity_service.Identity.Service.dto.AdminUserResponse;
import identity_service.Identity.Service.dto.CustomPageResponse;
import identity_service.Identity.Service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
//
//   └── GET  /admin/pending-owners
//   └── POST /admin/approve/{userId}
//   └── POST /admin/reject/{userId}
//   └── POST /admin/suspend/{userId}
//   └── GET  /admin/gym-owners

    private final AdminService adminService;

    @GetMapping("/gym-owners")
    public ResponseEntity<CustomPageResponse<AdminUserResponse>> getAllGymOwners(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy) {
        return ResponseEntity.ok(adminService.getAllGymOwners(page,size,sortBy));
    }

    //Here searchKey can be name,userName or businessName
    @GetMapping("/gym-owners/filter")
    public ResponseEntity<CustomPageResponse<AdminUserResponse>> getGymOwnersByFilter(@RequestParam List<Status> statuses,@RequestParam(required = false) String searchKey,@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy) {
        log.info("FILTER API HIT");
        return ResponseEntity.ok(adminService.getGymOwnersByFilter(statuses,searchKey,page,size,sortBy));
    }

    @GetMapping("/pending-owners")
    public ResponseEntity<CustomPageResponse<AdminUserResponse>> getPendingOwners(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy){
        return ResponseEntity.ok(adminService.getPendingOwners(page,size,sortBy));
    }

    @PostMapping("/approve/{userId}")
    public AdminUserResponse approveGymOwner(@PathVariable Integer userId) {

        return adminService.approveGymOwner(userId);
    }

    @PostMapping("/reject/{userId}")
    public AdminUserResponse rejectGymOwner(@PathVariable Integer userId) {

        return adminService.rejectGymOwner(userId);
    }

    @PostMapping("/suspend/{userId}")
    public AdminUserResponse suspendGymOwner(@PathVariable Integer userId) {

        return adminService.suspendGymOwner(userId);
    }


}
