package com.smart.parking.controller;

import com.smart.parking.dto.user.ChangePasswordRequest;
import com.smart.parking.dto.user.UserRequest;
import com.smart.parking.entity.User;
import com.smart.parking.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/me")
    public UserRequest getCurrentUser(@AuthenticationPrincipal User user) {
        return service.user(user);
    }

    @PatchMapping
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, Principal connectedUser) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }
}
