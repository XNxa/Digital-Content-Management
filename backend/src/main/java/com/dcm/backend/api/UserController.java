package com.dcm.backend.api;

import com.dcm.backend.annotations.LogEvent;
import com.dcm.backend.annotations.Loggable;
import com.dcm.backend.dto.CredentialsDTO;
import com.dcm.backend.dto.UserDTO;
import com.dcm.backend.exceptions.UserNotFoundException;
import com.dcm.backend.services.UserService;
import com.dcm.backend.utils.Permissions;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController extends Loggable {

    @Autowired
    UserService userService;

    @LogEvent
    @GetMapping("/count")
    public int countUsers() {
        return userService.count();
    }

    @LogEvent
    @PostMapping("/list")
    public Collection<UserDTO> listUsers(
            @RequestParam(value = "firstResult", defaultValue = "0", required = false) int firstResult,
            @RequestParam(value = "maxResults", defaultValue = "10", required = false) int maxResults,
            @RequestBody UserDTO filter) {
        return userService.list(firstResult, maxResults, filter);
    }

    @LogEvent
    @GetMapping("/user")
    public UserDTO getUser(@RequestParam("id") String id) {
        return userService.getUser(id);
    }

    @LogEvent
    @PreAuthorize("hasRole('" + Permissions.USER_ADD + "')")
    @PostMapping("/create")
    public void createUser(@RequestBody @Valid UserDTO user) {
        userService.create(user);
    }

    @LogEvent
    @PreAuthorize("hasRole('" + Permissions.USER_DELETE + "')")
    @DeleteMapping("/delete")
    public void deleteUser(@RequestParam("id") String id) {
        userService.delete(id);
    }

    @LogEvent
    @PreAuthorize("hasRole('" + Permissions.USER_MODIFY + "')")
    @PutMapping("/update")
    public void updateUser(@RequestBody @Valid UserDTO user) throws
            UserNotFoundException {
        userService.update(user);
    }

    @LogEvent
    @GetMapping("/functions")
    public Collection<String> getFunctions() {
        return userService.getFunctions();
    }

    @LogEvent
    @GetMapping("/validate")
    public boolean validateEmail(@RequestParam("email") String email) {
        return userService.validateEmail(email);
    }

    @PostMapping("/checkpassword")
    public boolean validatePassword(@RequestBody @Valid CredentialsDTO user) {
        return userService.validateUser(user);
    }
}
