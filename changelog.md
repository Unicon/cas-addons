# CAS-ADDONS CHANGELOG

Changes in version 0.5
======================

* Add [RegexRegisteredServiceWithAttributes](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/serviceregistry/RegexRegisteredServiceWithAttributes.java)
* Add support for RegexRegisteredServiceWithAttributes in [JsonServiceRegistryDao](https://github.com/Unicon/cas-addons/blob/master/src/main/java/net/unicon/cas/addons/serviceregistry/JsonServiceRegistryDao.groovy)
* Upgrade CAS dependency to 3.5.0-RC2

Changes in version 0.3 (2012-06-18)
===================================

* add groovy-eclipse-compiler to maven-compiler-plugin so that JsonServiceRegistryDao.groovy is compiled into native Java class during build time an so could be used as a regular Spring bean without requireing dynamic Groovy class loading at runtime


