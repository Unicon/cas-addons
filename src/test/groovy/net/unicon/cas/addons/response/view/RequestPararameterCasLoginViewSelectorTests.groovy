package net.unicon.cas.addons.response.view

import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.RequestContext

import spock.lang.Specification

/**
 * @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon , inc.
 */
@RunWith(Sputnik)
class RequestPararameterCasLoginViewSelectorTests extends Specification {
    def selector = new RequestPararameterCasLoginViewSelector()

    def "Test alternative view based on request parameter"() {
        given:
            RequestContext ctx = Mock()
            ParameterMap params = Mock()
            
            params.contains(selector.getParameterName()) >> true
            params.get(selector.getParameterName()) >> "staff"
            params.size() >> 1
            
            ctx.requestParameters >> params

            selector.setViewMappings([staff:'otherCasView'])

        expect:
            def view = selector.selectLoginView(ctx)
            view == "otherCasView"
    }

    def "Test default view when param points to non-existing view name"() {
        given:
            RequestContext ctx = Mock()
            ParameterMap params = Mock()
            
            params.contains(selector.getParameterName()) >> true
            params.get(selector.getParameterName()) >> "student"
            params.size() >> 1
            
            ctx.requestParameters >> params

            selector.setViewMappings([staff:'otherCasView'])

        expect:
            def view = selector.selectLoginView(ctx)
            view == selector.getDefaultView()
    }
    
    def "Test default view"() {
        given:
        RequestContext ctx = Mock()
        ParameterMap params = Mock()
        
        ctx.requestParameters >> params

    expect:
        def view = selector.selectLoginView(ctx)
        view == selector.getDefaultView()
    }
}