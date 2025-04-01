package farming.security;

import farming.accounting.entity.UserAccount;
import farming.accounting.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserAccount user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with login: " + login);
        }
        return user;
    }
    
   
    
}


