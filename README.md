# CAS Addons
cas-addons is an open source collection of useful [Apereo CAS server](http://www.jasig.org/cas) addons.

==========================================================================

This project was developed as part of Unicon's [Open Source Support program](https://unicon.net/opensource)
Professional Support / Integration Assistance for this module is available. For more information [visit](https://unicon.net/opensource/cas) 


### NOTICE

Minimum supported version of CAS in versions of the `1.x` series of `cas-addons` is `3.5.1`.
`1.x` series of cas-addons is not supported on CAS `4.x`. For CAS `4.x` support look for the upcoming series of [micro addons](https://github.com/unicon-cas-addons) libraries grouped by distinct features in upcoming months.

## Project Information

* [About](http://unicon.github.io/cas-addons/)
* [Changelog](https://github.com/Unicon/cas-addons/blob/master/changelog.md)
* [JavaDocs](http://unicon.github.com/cas-addons/apidocs/index.html)
* [Wiki](https://github.com/Unicon/cas-addons/wiki)

## Current version
`1.13`

## Build

[![Build Status](https://secure.travis-ci.org/Unicon/cas-addons.png)](http://travis-ci.org/Unicon/cas-addons)

[![Codeship Status for Unicon/cas-addons](https://www.codeship.io/projects/f5a581c0-ca15-0130-5eff-02755495ea38/status?branch=master)](https://www.codeship.io/projects/4827)

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
    <version>${cas-addons.version}</version>
</dependency>
```

To have a more finer-grained control of transitive dependencies brought into overlays by cas-addons and exclude unused features' transitive dependencies, use Maven's exclude mechanism. Example cas-addons dependency with exclusions:

```xml
<dependency>
    <groupId>net.unicon.cas</groupId>
    <artifactId>cas-addons</artifactId>
    <version>${cas-addons.version}</version>
    <exclusions>
        <exclusion>
            <groupId>edu.internet2.middleware.grouper</groupId>
            <artifactId>grouperClient</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.sun.jersey</groupId>
            <artifactId>jersey-client</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.sun.jersey</groupId>
            <artifactId>jersey-core</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.sun.jersey</groupId>
            <artifactId>jersey-server</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.sun.jersey</groupId>
            <artifactId>jersey-servlet</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.sun.jersey.contribs</groupId>
            <artifactId>jersey-spring</artifactId>
        </exclusion>
        <exclusion>
            <groupId>javax.ws.rs</groupId>
            <artifactId>jsr311-api</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.stormpath.sdk</groupId>
            <artifactId>stormpath-sdk-api</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.stormpath.sdk</groupId>
            <artifactId>stormpath-sdk-httpclient</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-mongodb</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-cas</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.yubico</groupId>
            <artifactId>yubico-validation-client2</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.codehaus.groovy</groupId>
            <artifactId>groovy-all</artifactId>
        </exclusion>
        <exclusion>
            <groupId>javax.xml.stream</groupId>
            <artifactId>stax-api</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.codehaus.jettison</groupId>
            <artifactId>jettison</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```
