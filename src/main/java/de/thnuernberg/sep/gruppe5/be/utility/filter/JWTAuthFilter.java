package de.thnuernberg.sep.gruppe5.be.utility.filter;

import de.thnuernberg.sep.gruppe5.be.control.services.JwtTokenService;
import de.thnuernberg.sep.gruppe5.be.utility.exceptions.AppException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {
  private final JwtTokenService jwtTokenService;

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain) throws ServletException, IOException {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (header != null) {
      String[] authElements = header.split(" ");
      if (authElements.length == 2
        && "Bearer".equals(authElements[0])) {
        try {
          jwtTokenService.validateToken(authElements[1]);
          filterChain.doFilter(request, response);
        } catch (AppException e) {
          response.sendError(e.getStatus().value(), e.getMessage());
          SecurityContextHolder.clearContext();
        } catch (RuntimeException e) {
          SecurityContextHolder.clearContext();
          throw e;
        }
      } else {
        filterChain.doFilter(request, response);
      }
    } else {
      filterChain.doFilter(request, response);
    }
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    String[] freePaths = {
      "/login",
      "/register",
      "/confirmRegistration",
      "/resendRegistrationCode",
      "/resetPassword",
      "/renewPassword"
    };
    for (String freePath : freePaths) {
      if (path.startsWith(freePath)) return true;
    }

    return false;
  }
}
