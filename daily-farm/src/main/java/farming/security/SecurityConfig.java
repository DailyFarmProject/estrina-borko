package farming.security;

import farming.api.AuthApi;
import farming.api.CustomerApi;
import farming.api.FarmerApi;
import farming.api.ProductApi;
import farming.api.UserApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter, CustomUserDetailsService userDetailsService) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(AuthApi.BASE + AuthApi.REGISTER, AuthApi.BASE + AuthApi.LOGIN).permitAll()
                        .requestMatchers(UserApi.BASE + "/admin/**").hasAuthority("TYPE_ADMIN")
                        .requestMatchers(
                                FarmerApi.BASE + "/**",
                                ProductApi.ADD,
                                ProductApi.UPDATE,
                                ProductApi.REMOVE,
                                ProductApi.SOLD,
                                ProductApi.SURPRISE_BAG_CREATE
                        ).hasAuthority("TYPE_FARMER")
                        .requestMatchers(
                                CustomerApi.BASE + "/**",
                                ProductApi.SURPRISE_BAG_BUY,
                                ProductApi.PURCHASED
                        ).hasAuthority("TYPE_CUSTOMER")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}