package net.unicon.cas.addons.response.view;

import org.springframework.webflow.execution.RequestContext;

/**
 * This really is a <i>sample</i> implementation of the {@link CasLoginViewSelector}
 * that shows how the login view may be routed to a different screen based on the
 * existence of a particular parameter. By default the value of {@link #setParameterName(String)}
 * is mapped to a view state in the flow that can be rendered.
 * 
* @author Misagh Moayyed
* @since 1.9
*/
public class RequestPararameterCasLoginViewSelector implements CasLoginViewSelector {

    private String parameterName = "view";
    private String defaultView = "casLoginView";
    
    @Override
    public String selectLoginView(final RequestContext request) {
         if (request.getRequestParameters().contains(parameterName)) {
             return request.getRequestParameters().get(parameterName);
         }
         return defaultView;
    }

    public final void setParameterName(final String parameterName) {
        this.parameterName = parameterName;
    }

    public final void setDefaultView(final String defaultView) {
        this.defaultView = defaultView;
    }

    public final String getParameterName() {
        return this.parameterName;
    }

    public final String getDefaultView() {
        return this.defaultView;
    }

}
