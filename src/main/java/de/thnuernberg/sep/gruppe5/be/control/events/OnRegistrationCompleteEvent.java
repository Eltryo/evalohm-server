package de.thnuernberg.sep.gruppe5.be.control.events;

import de.thnuernberg.sep.gruppe5.be.control.models.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
  @NotBlank
  private final User user;

  public OnRegistrationCompleteEvent(final User user) {
    super(user);
    this.user = user;
  }
}
