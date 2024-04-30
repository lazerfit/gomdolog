package store.gomdolog.packages.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import store.gomdolog.packages.service.JwtService;

@RestController
@RequiredArgsConstructor
public class HelloController {

    private final JwtService jwtService;

    @PreAuthorize("permitAll()")
    @GetMapping("/api/sc")
    public String scretKey() {
        return jwtService.secretKeyGenerate();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/api/auth/test")
    public String test() {
        return "HOLA AMIGO!";
    }
}
