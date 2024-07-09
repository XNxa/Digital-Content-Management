package com.dcm.backend.api;

import com.dcm.backend.dto.UserDTO;
import com.dcm.backend.exceptions.UserNotFoundException;
import com.dcm.backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/count")
    public int countUsers() {
        return userService.count();
    }

    @PostMapping("/list")
    public Collection<UserDTO> listUsers(
            @RequestParam(value = "firstResult", defaultValue = "0", required = false) int firstResult,
            @RequestParam(value = "maxResults", defaultValue = "10", required = false) int maxResults,
            @RequestBody UserDTO filter) {
        return userService.list(firstResult, maxResults, filter);
    }

    @GetMapping("/user")
    public UserDTO getUser(@RequestParam("id") String id) {
        return userService.getUser(id);
    }

    @PostMapping("/create")
    public void createUser(@RequestBody @Valid UserDTO user) {
        userService.create(user);
    }

    @DeleteMapping("/delete")
    public void deleteUser(@RequestParam("id") String id) {
        userService.delete(id);
    }

    @PutMapping("/update")
    public void updateUser(@RequestBody @Valid UserDTO user) throws
            UserNotFoundException {
        userService.update(user);
    }

}
