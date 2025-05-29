//package edu.teamsync.teamsync.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
//@Configuration
//public class AppConfig {
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtValidator jwtValidator) throws Exception {
//        http
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/tasks").authenticated()
//                        .anyRequest().permitAll()
//                )
//                .addFilterBefore(jwtValidator, BasicAuthenticationFilter.class)
//                .csrf(csrf -> csrf.disable())
//                .cors(withDefaults());
//
//        return http.build();
//    }
//
//    @Bean
//    public JwtValidator jwtValidator(JwtConstant jwtConstant) {
//        return new JwtValidator(jwtConstant);
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
package edu.teamsync.teamsync.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
public class AppConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtValidator jwtValidator, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register", "/auth/login","/api/health").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtValidator, BasicAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource));

        return http.build();
    }

    @Bean
    public JwtValidator jwtValidator(JwtConstant jwtConstant) {
        return new JwtValidator(jwtConstant);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow multiple origins for flexibility
        config.setAllowedOrigins(List.of(
                "http://localhost:5000", // Frontend
                "http://127.0.0.1:5000", // Alternate frontend form
                "chrome-extension://*", // For Postman/Bruno browser extensions (optional)
                "*" // Allows any origin (only safe without credentials)
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        // WARNING: allowCredentials + allowedOrigins=* is invalid and will be blocked by Spring
        config.setAllowCredentials(false); // Set to false if using "*" in allowedOrigins

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}