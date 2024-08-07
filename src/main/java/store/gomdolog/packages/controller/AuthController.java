package store.gomdolog.packages.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.gomdolog.packages.dto.JwtAuthenticationResponse;
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
    public JwtAuthenticationResponse signIn(
        @RequestBody @Valid UserSignInRequest userSignInRequest
    ) {
        return userService.signIn(userSignInRequest);
    }
}
