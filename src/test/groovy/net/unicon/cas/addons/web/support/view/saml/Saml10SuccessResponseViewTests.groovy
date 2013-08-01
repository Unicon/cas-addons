package net.unicon.cas.addons.web.support.view.saml
import org.jasig.cas.authentication.ImmutableAuthentication
import org.jasig.cas.authentication.principal.Service
import org.jasig.cas.authentication.principal.SimplePrincipal
import org.jasig.cas.validation.ImmutableAssertionImpl
import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Specification
/**
 * @author @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon , inc.
 */
@RunWith(Sputnik)
class Saml10SuccessResponseViewTests extends Specification{

    def Saml10SuccessResponseView response = new Saml10SuccessResponseView("testIssuer", 1000)

    def "Test successful SAML assertion with no namespaces"() {
        when:
            def servletResponse = new MockHttpServletResponse()
            this.response.renderMergedOutputModel(model, new MockHttpServletRequest(), servletResponse)
        then:
            def written = servletResponse.getContentAsString();
            written.contains("testPrincipal")
        and:
            !written.contains("<saml1:")
        and:
            !written.contains("<samlp:")
        where:
            model = getModelWithAssertion()
    }

    def getModelWithAssertion() {
        def model =   new HashMap<String, Object>()

        def attributes = [testAttribute:"testValue", testEmptyCollection:[], testAttributeCollection:["one", "two"]]
        def principal = new SimplePrincipal("testPrincipal", attributes)

        def authentication = new ImmutableAuthentication(principal, [:])

        def svc = Mock(Service)

        def assertion = new ImmutableAssertionImpl(Collections.singletonList(authentication), svc, true)
        model.put("assertion", assertion)

        model

    }
}

