package de.thnuernberg.sep.gruppe5.be.control.services;

import de.thnuernberg.sep.gruppe5.be.utility.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
  private final JwtEncoder jwtEncoder;
  private final JwtDecoder jwtDecoder;
  private final UserService userService;

  public String generateJwt(Authentication auth) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + (3 * 60 * 60 * 1000)); // 30 min

    String scope = auth.getAuthorities().stream()
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.joining(" "));

    JwtClaimsSet claims = JwtClaimsSet.builder()
      .issuer("self")
      .issuedAt(now.toInstant())
      .expiresAt(expiryDate.toInstant())
      .subject(auth.getName())
      .claim("roles", scope)
      .build();

    return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  }

  public void validateToken(String token) {
    Jwt jwt = jwtDecoder.decode(token);

    UserDetails user = userService.loadUserByUsername(jwt.getSubject());

    if (!user.getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName()))
      throw new AppException("Ungültige Authentifizierungsdaten!", HttpStatus.UNAUTHORIZED);

    if (jwt.getExpiresAt().isBefore(Instant.now()))
      throw new AppException("Authentifizierung ist abgelaufen!", HttpStatus.UNAUTHORIZED);
  }
}
