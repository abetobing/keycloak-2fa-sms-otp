# Keycloak SMS OTP Plugin

A starter project to create two factor authentication plugins in Keycloak[](https://www.keycloak.org/) by sending TOTP over SMS.

> Note: This is just a skeleton to start writing Keycloak SMS OTP plugins, you have to write the 3rd party (SMS provider) integration yourself 

## Development

The meat of the authentication is in `OtpAuthenticator` class, which is responsible in authenticating and rendering the `otp-form.ftl` form.

## Installation

### Build & Package
```bash
mvn clean package
```
copy `target/keycloak-sms-otp-spi.jar` to `[KEYCLOAK_HOME]/providers`
then restart Keycloak

### Setup in Keycloak
* Go to Authentication menu
* Duplicate the built-in `browser` flow and name it `browser with OTP`
* Customize the flow accordingly
* Click Add Step
* Search and select `SMS OTP authenticator`
* Add it, then select `Required`
* Save
* Bind flow, then select the binding type to browser flow.
