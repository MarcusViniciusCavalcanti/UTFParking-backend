package br.edu.utfpr.tsi.utfparking.domain.security.config;

import br.edu.utfpr.tsi.utfparking.domain.notification.filter.WebSocketFilter;
import br.edu.utfpr.tsi.utfparking.domain.security.filter.AccessDeniedFailureHandler;
import br.edu.utfpr.tsi.utfparking.domain.security.filter.AuthenticationFailureHandlerImpl;
import br.edu.utfpr.tsi.utfparking.domain.security.filter.JwtAuthenticationFilter;
import br.edu.utfpr.tsi.utfparking.domain.security.filter.JwtAuthorizationFilter;
import br.edu.utfpr.tsi.utfparking.domain.security.properties.JwtConfiguration;
import br.edu.utfpr.tsi.utfparking.domain.security.service.TokenConverter;
import br.edu.utfpr.tsi.utfparking.domain.security.service.TokenCreator;
import br.edu.utfpr.tsi.utfparking.structure.repositories.AccessCardRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityCredentialsConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    private final JwtConfiguration jwtConfiguration;

    private final ObjectMapper objectMapper;

    private final TokenCreator tokenCreator;

    private final TokenConverter tokenConverter;

    private final AccessCardRepository accessCardRepository;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors()
                .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .exceptionHandling()
                    .accessDeniedHandler(new AccessDeniedFailureHandler())
                    .authenticationEntryPoint(new AuthenticationFailureHandlerImpl())
                .and()
                    .addFilterBefore(new JwtAuthorizationFilter(authenticationManager(), jwtConfiguration, tokenConverter, accessCardRepository), BasicAuthenticationFilter.class)
                    .addFilterAfter(new JwtAuthenticationFilter(authenticationManager(), objectMapper, jwtConfiguration, tokenCreator), UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests()
                        .antMatchers(jwtConfiguration.getLoginUrl()).permitAll()
                        .antMatchers("/recognizer/plate").permitAll()
                        .antMatchers("/docs/**").permitAll()
                        .antMatchers("/ws/**").permitAll()
                    .anyRequest().authenticated();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final var source = new UrlBasedCorsConfigurationSource();
        var config = new CorsConfiguration().applyPermitDefaultValues();
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "TRACE", "CONNECT"));
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
