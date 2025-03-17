package farming.security;

import farming.api.constants.AuthApiConstants;
import farming.api.constants.CustomerApiConstants;
import farming.api.constants.FarmerApiConstants;
import farming.api.constants.ProductApiConstants;
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
        http.httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/register", AuthApiConstants.SIGNUP, AuthApiConstants.LOGIN).permitAll()
                        .requestMatchers("/api/admin/**").hasAuthority("TYPE_ADMIN")
                        .requestMatchers(FarmerApiConstants.BASE_PATH + "/**",
                                ProductApiConstants.ADD, ProductApiConstants.UPDATE, ProductApiConstants.REMOVE,
                                ProductApiConstants.SOLD, ProductApiConstants.SURPRISE_BAG_CREATE)
                        .hasAuthority("TYPE_FARMER")
                        .requestMatchers(CustomerApiConstants.BASE_PATH + "/**",
                                ProductApiConstants.SURPRISE_BAG_BUY, ProductApiConstants.PURCHASED)
                        .hasAuthority("TYPE_CUSTOMER")
                        .anyRequest().authenticated())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
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
        return new BCryptPasswordEncoder();
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