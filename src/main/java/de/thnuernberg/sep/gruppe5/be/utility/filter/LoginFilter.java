package de.thnuernberg.sep.gruppe5.be.utility.filter;

import de.thnuernberg.sep.gruppe5.be.control.services.LoginAttemptService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class LoginFilter extends OncePerRequestFilter {
  private final LoginAttemptService loginAttemptService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    if (loginAttemptService.isBlocked(request)) {
      response.resetBuffer();
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setHeader("Content-Type", "application/json");
      response.getOutputStream().print("{\"message\":\"Sie hatten zu viele falsche Anmeldeversuche!\"}");
      response.flushBuffer();
    } else {
      filterChain.doFilter(request, response);
    }
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();

    return !path.startsWith("/login");
  }
}
