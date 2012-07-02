# CAS-ADDONS CHANGELOG

Changes in version 0.6
======================

* added [EmailAddressToPrincipalNameTransformer](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/authentication/handler/EmailAddressToPrincipalNameTransformer.java)
* added [EmailAddressPasswordCredentialsToPrincipalResolver](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/authentication/principal/EmailAddressPasswordCredentialsToPrincipalResolver.java)
* upgraded Groovy dependency to 2.0.0
* upgraded Jackson dependency to 2.0.4


Changes in version 0.5 (2012-06-25)
===================================

* added [RegexRegisteredServiceWithAttributes](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/serviceregistry/RegexRegisteredServiceWithAttributes.java)
* added support for RegexRegisteredServiceWithAttributes in [JsonServiceRegistryDao](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/serviceregistry/JsonServiceRegistryDao.groovy)
* added [AdditionalAuthenticationFactorPolicy](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/authentication/strong/AdditionalAuthenticationFactorPolicy.java)
* added [TotpOathDetailsSource](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/authentication/strong/oath/totp/TotpOathDetailsSource.java)
* added [TOTP](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/authentication/strong/oath/totp/TOTP.java)
* added [TOTPUtils](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/authentication/strong/oath/totp/TOTPUtils.java)
* added [TotpAuthenticationHandler](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/authentication/strong/oath/totp/TotpAuthenticationHandler.java)
* added [PasswordExpirationStatusPolicySupport](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/authentication/support/PasswordExpirationStatusPolicySupport.java)
* upgraded CAS dependency to 3.5.0-RC2

Changes in version 0.3 (2012-06-18)
===================================

* added groovy-eclipse-compiler to maven-compiler-plugin so that JsonServiceRegistryDao.groovy is compiled into native Java class during build time an so could be used as a regular Spring bean without requireing dynamic Groovy class loading at runtime


