# PlayerLogins
A simple plugin which keeps track of all addresses a player has logged in with.

### API Methods
Get the first location that a player has logged in from.
```java
HostnameAPI.getFirstJoinHostname(UUID uuid);
```

Get the last location that a player has logged in from.
```java
HostnameAPI.getLastJoinHostname(UUID uuid);
```

Get the amount of logins from a specific hostname / address.
```java
HostnameAPI.getAmountOfJoins(String hostname);
```