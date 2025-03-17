package farming.user.service;


import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface IUserService {
    farming.user.entity.User addUser(farming.user.entity.User user);

    public Optional<farming.user.entity.User> getUserByEmail(String email);

    farming.user.entity.User updateUser(farming.user.entity.User user);

    farming.user.entity.User getUserById(Long id);

}

