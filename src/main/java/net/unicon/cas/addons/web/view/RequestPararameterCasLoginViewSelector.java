package net.unicon.cas.addons.web.view;

import java.util.Collections;
import java.util.Map;

import org.springframework.webflow.execution.RequestContext;

/**
 * This really is an implementation of the {@link CasLoginViewSelector}
 * that shows how the login view may be routed to a different screen based on the
 * existence of a particular parameter.
 * 
* @author Misagh Moayyed
* @since 1.9
*/
public class RequestPararameterCasLoginViewSelector implements CasLoginViewSelector {

    private String parameterName = "view";
    private String defaultView = "casLoginView";
    private Map<String, String> viewMappings = Collections.emptyMap();
    
    @Override
    public String selectLoginView(final RequestContext request) {
         if (request.getRequestParameters().contains(parameterName)) {
             final String key = request.getRequestParameters().get(parameterName);
             if (this.viewMappings.containsKey(key)) {
                 return this.viewMappings.get(key);
             }
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

    public final Map<String, String> getViewMappings() {
        return this.viewMappings;
    }

    public final void setViewMappings(final Map<String, String> viewMappings) {
        this.viewMappings = viewMappings;
    }

}
