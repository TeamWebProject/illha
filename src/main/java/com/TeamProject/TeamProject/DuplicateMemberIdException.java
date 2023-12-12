package com.TeamProject.TeamProject;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "already existing memberId")
public class DuplicateMemberIdException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public DuplicateMemberIdException(String message) {
    super(message);
  }
}
