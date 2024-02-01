package de.thnuernberg.sep.gruppe5.be.utility.config;

import de.thnuernberg.sep.gruppe5.be.control.services.JwtTokenService;
import de.thnuernberg.sep.gruppe5.be.control.services.LoginAttemptService;
import de.thnuernberg.sep.gruppe5.be.utility.filter.JWTAuthFilter;
import de.thnuernberg.sep.gruppe5.be.utility.filter.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SpringSecurityAuthentication {
  private final JwtTokenService jwtTokenService;
  private final LoginAttemptService loginAttemptService;
  private final JWTConfig jwtConfig;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .addFilterBefore(new LoginFilter(loginAttemptService), BasicAuthenticationFilter.class)
      .addFilterBefore(new JWTAuthFilter(jwtTokenService), BasicAuthenticationFilter.class)
      .authorizeHttpRequests((requests) -> requests
        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/contact")).permitAll()
        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/login")).permitAll()
        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/register")).permitAll()
        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/confirmRegistration")).permitAll()
        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/resendRegistrationCode")).permitAll()
        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/resetPassword")).permitAll()
        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PUT, "/renewPassword")).permitAll()
        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
        .anyRequest().authenticated())
      .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
      .csrf(AbstractHttpConfigurer::disable)
      .sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .oauth2ResourceServer(server -> server.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConfig.jwtAuthenticationConverter())));

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authManager(UserDetailsService detailsService) {
    DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
    daoProvider.setUserDetailsService(detailsService);
    daoProvider.setPasswordEncoder(passwordEncoder());

    return new ProviderManager(daoProvider);
  }
}
