package farming.auth.service;

import farming.jwt.dto.JwtTokenDTO;
import farming.user.dto.UserRequestDto;
import farming.user.dto.UserResponseDto;
import farming.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public interface IAuthenticationService {

    JwtTokenDTO login(String email, String password, HttpServletResponse response);

    UserResponseDto signUp(UserRequestDto userDto, HttpServletRequest request)throws UnsupportedEncodingException;

    User getCurrentUser();

}

