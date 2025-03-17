package farming.auth.controller;


import farming.auth.dto.LoginRequestDTO;
import farming.auth.service.AuthenticationService;
import farming.jwt.dto.JwtTokenDTO;
import farming.user.dto.UserRequestDto;
import farming.user.dto.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);


    @Autowired
    private AuthenticationService authenticationService;


    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signUp(@RequestBody UserRequestDto userRequestDto, HttpServletRequest request) {
        logger.info("signup user: {}", userRequestDto);

        UserResponseDto response;
        try {
            response = authenticationService.signUp(userRequestDto, request);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Login
     *
     * @param loginRequestDTO User credentials
     * @return JWT Token
     */
    @PostMapping("/login")
    public ResponseEntity<JwtTokenDTO> authenticateAndGetToken(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
        logger.info("authenticateAndGetToken: {}", loginRequestDTO);
        try {
            JwtTokenDTO token = authenticationService.login(loginRequestDTO.getEmail(), loginRequestDTO.getPassword(), response);
            if (token != null) {
                return ResponseEntity.ok(token);
            } else {
                return new ResponseEntity("User email not found", HttpStatus.NOT_FOUND);
            }
        } catch (BadCredentialsException e) {
            logger.error(e.getMessage());
            return new ResponseEntity("Bad credentials", HttpStatus.UNAUTHORIZED);
        }
    }
}
