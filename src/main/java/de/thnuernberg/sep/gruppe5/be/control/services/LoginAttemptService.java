package de.thnuernberg.sep.gruppe5.be.control.services;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

  public static final int MAX_ATTEMPT = 3;
  private final LoadingCache<String, Integer> attemptsCache;

  public LoginAttemptService() {
    super();
    attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build(new CacheLoader<String, Integer>() {
      @Override
      public Integer load(final String key) {
        return 0;
      }
    });
  }

  public int loginFailed(final String key) {
    int attempts;
    try {
      attempts = attemptsCache.get(key);
    } catch (final ExecutionException e) {
      attempts = 0;
    }

    attempts++;
    attemptsCache.put(key, attempts);

    return MAX_ATTEMPT - attempts;
  }

  public void loginSuccessful(final String key) {
    attemptsCache.put(key, 0);
  }

  public boolean isBlocked(HttpServletRequest request) {
    try {
      return attemptsCache.get(getClientIP(request)) >= MAX_ATTEMPT;
    } catch (final ExecutionException e) {
      return false;
    }
  }

  private String getClientIP(HttpServletRequest request) {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
      return request.getRemoteAddr();
    }

    return xfHeader.split(",")[0];
  }
}
