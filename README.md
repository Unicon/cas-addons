# CAS Addons
CAS Addons is an open source collection of useful JASIG-CAS server addons.

==========================================================================

For versions **1.0+**, the minimum supported version of CAS is **3.5.1**


## Project Information

* [About](http://unicon.github.com/cas-addons/)
* [Changelog](https://github.com/Unicon/cas-addons/blob/master/changelog.md) 
* [JavaDocs](http://unicon.github.com/cas-addons/apidocs/index.html)
* [Wiki](https://github.com/Unicon/cas-addons/wiki)

## Current version
`1.1`

## Build [![Build Status](https://secure.travis-ci.org/Unicon/cas-addons.png)](http://travis-ci.org/Unicon/cas-addons)
You can build the project from source using the following Maven command:

```bash
$ mvn clean package
```

## Usage

`cas-addons` is, not surprisingly, intended to be added on to your CAS server.  The easiest way to do this is to declare `cas-addsons` as a dependency in [your local CAS server build that's structured as a Maven overlay](https://wiki.jasig.org/display/CASUM/Best+Practice+-+Setting+Up+CAS+Locally+using+the+Maven2+WAR+Overlay+Method).

Declare the project dependency in your Local CAS server `pom.xml` file as:
```xml
<dependency>
    <groupId>net.unicon.cas</groupId>
    <artifactId>cas-addons</artifactId>
    <version>1.1</version>
</dependency>
```
