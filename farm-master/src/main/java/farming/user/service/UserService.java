package farming.user.service;

import farming.user.entity.User;
import farming.user.entity.UserInfoDetails;
import farming.user.repo.UserNewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService, IUserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserNewRepository repository;

    @Autowired
    private PasswordEncoder encoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userDetail = repository.findByEmail(username); // Assuming 'email' is used as username
        userDetail.ifPresent(userInfo -> userInfo.setName(userInfo.getEmail()));
        // Converting UserInfo to UserDetails
        return userDetail.map(UserInfoDetails::new).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public User addUser(User user) {
        // Encode password before saving the user
        user.setPassword(encoder.encode(user.getPassword()));
        return repository.save(user);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        logger.info("getUserByEmail : {}", email);
        return repository.findByEmail(email);
    }

    @Override
    public User updateUser(User user) {
        logger.info("Update user ");

        return repository.save(user);
    }


    @Override
    public User getUserById(Long id) {
        logger.info("getUserById : {}", id);
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found: " + id));
    }


}

