package farming.auth.controller;

import farming.api.constants.AuthApiConstants;
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
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(AuthApiConstants.BASE_PATH)
public class AuthenticationController {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping(AuthApiConstants.SIGNUP)
    public ResponseEntity<UserResponseDto> signUp(@RequestBody UserRequestDto userRequestDto, HttpServletRequest request) {
        logger.info("signup user: {}", userRequestDto);
        try {
            UserResponseDto response = authenticationService.signUp(userRequestDto, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error during signup: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to sign up user: " + e.getMessage(), e);
        }
    }

    @PostMapping(AuthApiConstants.LOGIN)
    public ResponseEntity<JwtTokenDTO> authenticateAndGetToken(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
        logger.info("authenticateAndGetToken: {}", loginRequestDTO);
        try {
            JwtTokenDTO token = authenticationService.login(loginRequestDTO.getEmail(), loginRequestDTO.getPassword(), response);
            if (token == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User email not found");
            }
            return ResponseEntity.ok(token);
        } catch (BadCredentialsException e) {
            logger.error("Bad credentials: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials", e);
        } catch (Exception e) {
            logger.error("Error during login: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to login: " + e.getMessage(), e);
        }
    }
}