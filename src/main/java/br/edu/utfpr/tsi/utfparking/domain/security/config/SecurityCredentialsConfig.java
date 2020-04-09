package br.edu.utfpr.tsi.utfparking.domain.security.config;

import br.edu.utfpr.tsi.utfparking.domain.security.filter.JwtAuthenticationFilter;
import br.edu.utfpr.tsi.utfparking.domain.security.filter.JwtAuthorizationFilter;
import br.edu.utfpr.tsi.utfparking.domain.security.properties.JwtConfiguration;
import br.edu.utfpr.tsi.utfparking.structure.repositories.AccessCardRepository;
import br.edu.utfpr.tsi.utfparking.domain.security.service.TokenConverter;
import br.edu.utfpr.tsi.utfparking.domain.security.service.TokenCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@EnableGlobalMethodSecurity(prePostEnabled=true)
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
                    .configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .exceptionHandling()
                    .authenticationEntryPoint((req, resp, ex) -> resp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                    .addFilter(new JwtAuthenticationFilter(authenticationManager(), objectMapper, jwtConfiguration, tokenCreator))
                    .addFilterAfter(new JwtAuthorizationFilter(jwtConfiguration, tokenConverter, accessCardRepository), UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests()
                        .antMatchers(jwtConfiguration.getLoginUrl()).permitAll()
                        .antMatchers("/recognizer/plate").permitAll();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
