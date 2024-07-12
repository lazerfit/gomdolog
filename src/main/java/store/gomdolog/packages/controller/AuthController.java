package store.gomdolog.packages.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.gomdolog.packages.dto.JwtAuthenticationResponse;
import store.gomdolog.packages.dto.UserRoleResponse;
import store.gomdolog.packages.dto.UserSignInRequest;
import store.gomdolog.packages.dto.UserSignUpRequest;
import store.gomdolog.packages.service.UserService;

@Slf4j
@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PreAuthorize("permitAll()")
    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> sighUp(
        @RequestBody @Valid UserSignUpRequest userSignUpRequest) {
        return ResponseEntity.ok().body(userService.signUp(userSignUpRequest));
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signIn(
        @RequestBody @Valid UserSignInRequest userSignInRequest
    ) {
        return ResponseEntity.ok().body(userService.signIn(userSignInRequest));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/getRole")
    public UserRoleResponse getRole(@RequestHeader("Authorization") String jwt) {
        String role = userService.getRole(jwt);
        return new UserRoleResponse(role);
    }
}
