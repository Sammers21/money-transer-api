# money-transfer-api

[![Build Status](https://travis-ci.org/Sammers21/money-transfer-api.svg?branch=master)](https://travis-ci.org/Sammers21/money-transfer-api)

## Design

This app has 5 endpoints:

1. PUT /user/<userId>/create-account - for user and account creation

2. GET /user/<userId>/account/<accountId> - in order to obtain account information, we can use this endpoint

3. POST /user/<userId>/account/<accountId>/withdraw?sum=<sum> - to withdraw any sum of money

4. POST /user/<userId>/account/<accountId>/contribute?sum=<sum> - to contribute any sum of money

5. POST /user/:user_id/account/:account_id/transfer-money?sum=<sum>&to_account=<accountId>&to_user=<userId> - to transfer money between accounts

Is is also assumed that there is currencies and excchange rates, only abstact sum of money.

## Builing & Running

Building jar with all dependecies:
```bash
./gradlew jar
```

Standalone executable jar can be found at _build/libs/money-transfer-api.jar_.
So you can run this jar:
```
java -jar build/libs/money-transfer-api.jar
```

The app will be listening on port 8080.
