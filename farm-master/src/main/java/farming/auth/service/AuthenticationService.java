package farming.auth.service;

import farming.jwt.dto.JwtTokenDTO;
import farming.jwt.service.JwtService;
import farming.user.dto.UserRequestDto;
import farming.user.dto.UserResponseDto;
import farming.user.entity.User;
import farming.user.mapper.UserMapper;
import farming.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.UnsupportedEncodingException;

@Service
public class AuthenticationService implements IAuthenticationService {
    private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);


    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public JwtTokenDTO login(String email, String password, HttpServletResponse response) {
        logger.info("Login user : {}", email);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getUserByEmail(email).orElse(null);
            if (user != null) {
                switch (user.getRoles()) {
                    case "USER":
                        response.addHeader("user_type", "user");
                        break;
                    case "ADMIN":
                        response.addHeader("user_type", "admin");
                        break;
                }
            }
            if (user != null) {
                response.addHeader("user_id", user.getId().toString());
            }
            return jwtService.generateToken(email);
        }
        return new JwtTokenDTO();
    }

    @Override
    public UserResponseDto signUp(UserRequestDto userDto, HttpServletRequest request) throws UnsupportedEncodingException {
        logger.info("Sign up freelancer : {}", userDto.getEmail());

        User user =userService.addUser(UserMapper.INSTANCE.toEntity(userDto));

        return UserMapper.INSTANCE.toDto(user);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return userService.getUserByEmail(currentUserName).orElseThrow(() -> new UsernameNotFoundException("User not found: " + currentUserName));
        }
        return null;
    }
}
