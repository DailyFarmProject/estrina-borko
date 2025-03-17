package farming.security;

import farming.security.filter.JwtAuthFilter;
import farming.user.entity.User;
import farming.user.repo.UserNewRepository;
import farming.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

	@Autowired
	@Lazy
	private JwtAuthFilter authFilter;

	@Autowired
	@Lazy
	private UserService userInfoService;

	private final UserNewRepository userRepository;

	public SecurityConfig(UserNewRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.httpBasic(Customizer.withDefaults()).csrf(csrf -> csrf.disable()) // Отключаем CSRF (можно включить при
																				// необходимости для форм)

				// Настройка авторизации запросов
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/api/auth/register", "/api/login", "/api/auth/signup", "/api/auth/login")
						.permitAll().requestMatchers("/api/admin/**").hasAuthority("TYPE_ADMIN")
						.requestMatchers("/api/farmer/**", "/products/add", "/products/update", "/products/remove", 
								"/products/sold/{farmerId}", "/products/surprise-bag/create").hasAuthority("TYPE_FARMER")
						.requestMatchers("/api/customer/**", "/products/surprise-bag/buy", "/purchased/{customerId}").hasAuthority("TYPE_CUSTOMER").anyRequest().authenticated())
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No sessions
				).authenticationProvider(authenticationProvider()) // Custom authentication provider
				.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return username -> {
	        log.debug("Looking up user: {}", username);
	        User user = userRepository.findByEmail(username)
	                .orElseThrow(() -> {
	                    log.error("Failed to find user '{}'", username);
	                    return new UsernameNotFoundException("User not found: " + username);
	                });

	        return user;  
	    };

	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); // Password encoding
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userInfoService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}