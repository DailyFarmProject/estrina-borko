package farming.accounting.controller;

import farming.accounting.dto.UserRequestDto;
import farming.accounting.dto.UserResponseDto;
import farming.accounting.dto.UserType;
import farming.accounting.entity.UserAccount;
import farming.accounting.service.IAccountingManagement;
import farming.api.AuthApi;
import farming.api.CustomerApi;
import farming.api.UserApi;
import farming.api.FarmerApi;
import farming.api.ProductApi;
import farming.security.JwtUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AccountingController {

    private final IAccountingManagement service;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // Статический внутренний класс для запроса аутентификации
    @Getter
    @Setter
    @NoArgsConstructor
    public static class AuthenticationRequest {
        private String login;
        private String password;
    }

    // Статический внутренний класс для ответа аутентификации
    @Getter
    @Setter
    @NoArgsConstructor
    public static class AuthenticationResponse {
        private String token;

        public AuthenticationResponse(String token) {
            this.token = token;
        }
    }

    @PostMapping("/register")
    public UserResponseDto registration(@RequestBody UserRequestDto userDto) {
        log.info("registration user: {}", userDto);
        return service.registration(userDto, userDto.getUserType());
    }

    @PostMapping(AuthApi.LOGIN)
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getLogin());
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @GetMapping(UserApi.ME)
    public UserResponseDto getUser(@AuthenticationPrincipal UserAccount user) {
        return service.getUser(user.getUsername());
    }

    @PutMapping(UserApi.PASSWORD)
    public boolean updatePassword(@AuthenticationPrincipal UserAccount user, @RequestHeader("X-New-Password") String password) {
        return service.updatePassword(user.getUsername(), password);
    }

    @GetMapping(UserApi.TYPE)
    public UserType getUserType(@PathVariable String login) {
        return service.getUserType(login);
    }

    @DeleteMapping(UserApi.DELETE)
    public UserResponseDto removeUser(@PathVariable String login) {
        return service.removeUser(login);
    }

    @PutMapping(UserApi.REVOKE)
    public boolean revokeAccount(@PathVariable String login) {
        return service.revokeAccount(login);
    }

    @PutMapping(UserApi.ACTIVATE)
    public boolean activateAccount(@PathVariable String login) {
        return service.activateAccount(login);
    }

    @GetMapping(UserApi.PASSWORD_HASH)
    public String getPasswordHash(@PathVariable String login) {
        return service.getPasswordHash(login);
    }

    @GetMapping(UserApi.ACTIVATION_DATE)
    public LocalDateTime getActivationDate(@PathVariable String login) {
        return service.getActivationDate(login);
    }

    @GetMapping(UserApi.HOME)
    public String home(@AuthenticationPrincipal UserAccount user) {
        if (user == null) {
            return "Welcome, guest! Please log in.";
        }
        return "Welcome, " + user.getFirstName() + "! Your role: " + user.getUserType();
    }

    @GetMapping(UserApi.ADMIN_TEST)
    public String adminTest() {
        return "Admin access granted";
    }

    @GetMapping(FarmerApi.TEST)
    public String farmerTest() {  // Исправлено: удален лишний "farmer"
        return "Farmer access granted";
    }

    @GetMapping("/api/customer/test")
    public String customerTest() {
        return "Customer access granted";
    }
}