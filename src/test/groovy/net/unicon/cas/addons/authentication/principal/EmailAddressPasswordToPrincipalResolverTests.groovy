package net.unicon.cas.addons.authentication.principal

import spock.lang.Specification
import org.jasig.cas.authentication.principal.CredentialsToPrincipalResolver
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials
import org.jasig.cas.authentication.principal.SimplePrincipal

/**
 *
 * @author Dmitriy Kopylenko
 * @author @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon , inc.
 */
class EmailAddressPasswordToPrincipalResolverTests extends Specification {

    CredentialsToPrincipalResolver resolverUnderTest = new EmailAddressPasswordCredentialsToPrincipalResolver()

    def "Test correct resolving logic"() {
        expect: """Principal id equals to credentials username for non-email user name passed in username, --OR-- principal id equals to part before @ sign for email user name passed in,
                --OR-- principal equals null for null credentials passed in"""
        resolverUnderTest.resolvePrincipal(passedInCredentials) == expectedPrincipal

        where:
        passedInCredentials << [buildCredentials('userId', 'somePassword'), buildCredentials('userId@example.com', 'somePassword'), null]
        expectedPrincipal << [buildPrincipal('userId'), buildPrincipal('userId'), null]

    }

    private buildCredentials(username, password) {
        new UsernamePasswordCredentials(username: username, password: password)
    }

    private buildPrincipal(id) {
        new SimplePrincipal(id)
    }
}
