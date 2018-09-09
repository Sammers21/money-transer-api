package com.github.sammers21.domain;

/**
 * Represent a user.
 */
public interface User {

  /**
   * @return id of the user
   */
  String userId();

  /**
   * @return just created account
   */
  Account createAccount();

  /**
   * @param accountId id of account we are looking for
   * @return {@code null} if no account were found, or an instance of {@link Account} with given id
   */
  Account accountById(Long accountId);
}
