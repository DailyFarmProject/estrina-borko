package telran.security;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import telran.entity.Customer;



@Configuration
public class AuthenticationConfiguration implements UserDetailsService{

	@Autowired
	MongoTemplate template;
	
	@Value("${activation.period:30}")
	int activation_period;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Customer user = template.findById(username, Customer.class);
		if(user == null)
			throw new UsernameNotFoundException(username);
		
		String password = user.getHash();
		String[] roles = user.getRoles().stream().map(r -> "ROLE_" + r).toArray(String[]::new);
		//boolean passwordNotExpired = ChronoUnit.DAYS.between(LocalDateTime.now(), 
				//user.getActivationDate()) > activation_period;
		return new User(username, password, AuthorityUtils.createAuthorityList(roles));
				
	}

}
