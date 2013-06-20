package net.unicon.cas.addons.serviceregistry.services.authorization

import spock.lang.Specification

/**
 * Spock-based tests for ${link DefaultRegisteredServiceAuthorizer}
 *
 * @author Eric Pierce
 */
class DefaultRegisteredServiceAuthorizerTests extends Specification {

  def "Authorization test is successful - single value"() {
    given: 'The principal has a single attribute that matches the RBAC rule'
      def registeredServiceAuthorizer = new DefaultRegisteredServiceAuthorizer()
      def serviceAttributes = [attribute1: ["some_value"]]
      def principalAttributes = [attribute1: "some_value"]

    when: 'The authorizer is called'
      def result = registeredServiceAuthorizer.authorized(serviceAttributes, principalAttributes)

    then:
      result == true
  }

  def "Authorization test is not successful - single value"() {
    given: 'The principal has a single attribute that does not match RBAC rules'
      def registeredServiceAuthorizer = new DefaultRegisteredServiceAuthorizer()
      def serviceAttributes = [attribute1: ["other_value"]]
      def principalAttributes = [attribute1: "some_value"]

    when: 'The authorizer is called'
      def result = registeredServiceAuthorizer.authorized(serviceAttributes, principalAttributes)

    then:
      result == false
  }

  def "Authorization test is successful - multiple values"() {
    given: 'The principal has a list of attributes - one matches the RBAC rules'
      def registeredServiceAuthorizer = new DefaultRegisteredServiceAuthorizer()
      def serviceAttributes = [attribute1: ["some_value", "other_value"]]
      def principalAttributes = [attribute1: ["some_value", "some_other_value"]]

    when: 'The authorizer is called'
      def result = registeredServiceAuthorizer.authorized(serviceAttributes, principalAttributes)

    then:
      result == true
  }

  def "Authorization test is not successful - multiple values"() {
    given: 'The principal has a list of attributes - none match the RBAC rules'
      def registeredServiceAuthorizer = new DefaultRegisteredServiceAuthorizer()
      def serviceAttributes = [attribute1: ["some_value", "other_value"]]
      def principalAttributes = [attribute1: ["value", "some_other_value"]]

    when: 'The authorizer is called'
      def result = registeredServiceAuthorizer.authorized(serviceAttributes, principalAttributes)

    then:
      result == false
  }
}