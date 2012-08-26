## CAS-Addons Changelog

###Changes in version 0.9.5 
======================================

* added [JsonServiceRegistryResourceChangedEventListener](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/serviceregistry/JsonServiceRegistryResourceChangedEventListener.java) 

###Changes in version 0.9 (2012-08-11)
======================================

* added [GrouperPersonAttributeDao](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/persondir/GrouperPersonAttributeDao.java)

###Changes in version 0.8 (2012-07-27) 
======================================

* added [StormpathBasicAuthenticationHandler](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/authentication/handler/StormpathBasicAuthenticationHandler.java)
* refactored [JsonServiceRegistryDao](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/serviceregistry/JsonServiceRegistryDao.java) as a 'raw' Java class instead of a Groovy one

###Changes in version 0.7 (2012-07-20)
======================================

* added [JsonBackedComplexStubPersonAttributeDao](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/persondir/JsonBackedComplexStubPersonAttributeDao.java)
* added [ResourceChangeDetectingEventNotifier](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/support/ResourceChangeDetectingEventNotifier.java)

###Changes in version 0.6 (2012-07-14)
======================================

* added [EmailAddressToPrincipalNameTransformer](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/authentication/handler/EmailAddressToPrincipalNameTransformer.java)
* added [EmailAddressPasswordCredentialsToPrincipalResolver](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/authentication/principal/EmailAddressPasswordCredentialsToPrincipalResolver.java)
* added [ServiceValidateSuccessJsonView](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/response/ServiceValidateSuccessJsonView.java)
* added [Cas20ServiceTicketJsonValidationFilter](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/client/validation/Cas20ServiceTicketJsonValidationFilter.java)
* added [Cas20ServiceTicketJsonValidator](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/client/validation/Cas20ServiceTicketJsonValidator.java)
* upgraded [Groovy dependency to 2.0.0](https://github.com/Unicon/cas-addons/blob/master/pom.xml)
* upgraded [Jackson dependency to 2.0.4](https://github.com/Unicon/cas-addons/blob/master/pom.xml)
* upgraded [CAS server dependency to 3.5.0](https://github.com/Unicon/cas-addons/blob/master/pom.xml)

###Changes in version 0.5 (2012-06-25)
======================================

* added [RegexRegisteredServiceWithAttributes](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/serviceregistry/RegexRegisteredServiceWithAttributes.java)
* added support for RegexRegisteredServiceWithAttributes in [JsonServiceRegistryDao](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/serviceregistry/JsonServiceRegistryDao.groovy)
* added [AdditionalAuthenticationFactorPolicy](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/authentication/strong/AdditionalAuthenticationFactorPolicy.java)
* added [TotpOathDetailsSource](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/authentication/strong/oath/totp/TotpOathDetailsSource.java)
* added [TOTP](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/authentication/strong/oath/totp/TOTP.java)
* added [TOTPUtils](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/authentication/strong/oath/totp/TOTPUtils.java)
* added [TotpAuthenticationHandler](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/authentication/strong/oath/totp/TotpAuthenticationHandler.java)
* added [PasswordExpirationStatusPolicySupport](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/authentication/support/PasswordExpirationStatusPolicySupport.java)
* upgraded CAS dependency to 3.5.0-RC2

###Changes in version 0.3 (2012-06-18)
======================================

* added groovy-eclipse-compiler to maven-compiler-plugin so that JsonServiceRegistryDao.groovy is compiled into native Java class during build time an so could be used as a regular Spring bean without requireing dynamic Groovy class loading at runtime


