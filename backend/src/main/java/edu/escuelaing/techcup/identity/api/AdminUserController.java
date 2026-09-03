package edu.escuelaing.techcup.identity.api;

import edu.escuelaing.techcup.identity.api.dto.AssignRoleRequest;
import edu.escuelaing.techcup.identity.api.dto.UserResponse;
import edu.escuelaing.techcup.identity.application.RoleService;
import edu.escuelaing.techcup.identity.application.UserService;
import edu.escuelaing.techcup.identity.domain.Role;
import edu.escuelaing.techcup.shared.security.AuthenticatedUser;
import edu.escuelaing.techcup.shared.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "Admin - Users")
public class AdminUserController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminUserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @Operation(summary = "Search users by name or e-mail")
    public List<UserResponse> search(@RequestParam(required = false) String search) {
        return userService.search(search);
    }

    @GetMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Role> roles(@PathVariable Long id) {
        return roleService.listRoles(id);
    }

    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign a role")
    public List<Role> assignRole(@CurrentUser AuthenticatedUser actor, @PathVariable Long id,
                                 @Valid @RequestBody AssignRoleRequest request) {
        return roleService.assignRole(actor, id, request.role());
    }

    @DeleteMapping("/{id}/roles/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove a role (an admin cannot remove their own ADMIN role)")
    public List<Role> removeRole(@CurrentUser AuthenticatedUser actor, @PathVariable Long id,
                                 @PathVariable Role role) {
        return roleService.removeRole(actor, id, role);
    }

    @PostMapping("/{id}/inactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Inactivate a user not locked by an active tournament")
    public UserResponse inactivate(@CurrentUser AuthenticatedUser actor, @PathVariable Long id) {
        return userService.inactivate(actor, id);
    }
}
