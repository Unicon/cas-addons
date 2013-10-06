package net.unicon.cas.addons.response.view;

import org.springframework.webflow.execution.RequestContext;

/**
 * A strategy interface that defines public operations whereby the routing
 * to the appropriate login view may be conditionally decided. This satisfies the use case
 * where simple construction of flows and states in the login webflow may not be able to
 * select the login form dynamically due to limitations in the webflow programming API.
 * 
 * <p>An example use case may be that the CAS server may be intended
 * for different types of users where presentation of the login page is
 * drastically different for each. This difference could be text, images, and additional
 * HTML elements, such that it would better require an entirely different page. 
 * Example types of users would be staff, faculty, etc and specially when 
 * these users are geographically dispersed. A staff member from UK for instance
 * would want to see a very different page, than a student from Poland. Implementations should 
 * allow the CAS server to route the login view to other JSPs other than the default. 
 * 
 * @author Misagh Moayyed
 * @since 1.9
 * @see RequestPararameterCasLoginViewSelector
 */
public interface CasLoginViewSelector {
    
    /**
     * Decide on the login view based on the current state of the webflow request.
     * @param request the flow request context
     * @return the view id in the webflow that is responsible to render the login screen
     */
    String selectLoginView(final RequestContext request);
}
