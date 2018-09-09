package com.github.sammers21.handlers;

import com.github.sammers21.domain.Account;
import com.github.sammers21.domain.User;

public interface UserAccountHandler {

  void handle(User user, Account account);
}
