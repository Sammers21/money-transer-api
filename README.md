# money-transfer-api

[![Build Status](https://travis-ci.org/Sammers21/money-transfer-api.svg?branch=master)](https://travis-ci.org/Sammers21/money-transfer-api)

## Design

This app has 4 endpoints:

1. `GET /user/<nick>` - get user information by nick
2. `GET /user/<nick>/withdraw?sum=<sum>` - withdraw any sum of money
3. `GET /user/<nick>/contribute?sum=<sum>` - contribute any sum of money
4. `GET/user/<nick>/transfer?sum=<sum>&to=<nick>` - transfer money between users

It is also assumed that:
* there is no currencies, exchange rates, transactions, only abstract sums of money
* each user is an account itself

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
