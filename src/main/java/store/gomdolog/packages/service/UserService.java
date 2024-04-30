package store.gomdolog.packages.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.gomdolog.packages.domain.Role;
import store.gomdolog.packages.domain.User;
import store.gomdolog.packages.dto.JwtAuthenticationResponse;
import store.gomdolog.packages.dto.UserSignInRequest;
import store.gomdolog.packages.dto.UserSignUpRequest;
import store.gomdolog.packages.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public JwtAuthenticationResponse signUp(UserSignUpRequest userSignUpRequest) {
        User user = User.builder()
            .email(userSignUpRequest.email())
            .password(passwordEncoder.encode(userSignUpRequest.password()))
            .role(Role.USER)
            .build();

        userRepository.save(user);
        String jwt = jwtService.generateToken(user);

        return new JwtAuthenticationResponse(jwt);
    }

    @Transactional(readOnly = true)
    public JwtAuthenticationResponse signIn(UserSignInRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        String jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    @Transactional(readOnly = true)
    public String getRole(String jwt) {
        String token = jwt.substring(7);
        if (!jwt.startsWith("Bearer ") || jwtService.isTokenExpired(token)) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        } else {
            String username = jwtService.extractUsername(token);
            User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));
            return user.getRole().toString();
        }
    }
}
