package net.unicon.cas.addons.web.flow;/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import net.unicon.cas.addons.serviceregistry.RegisteredServiceWithAttributes;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.services.UnauthorizedServiceException;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockRequestContext;

import javax.validation.constraints.NotNull;

/**
 * Performs a basic check if an authentication request for a provided service is authorized to proceed
 * based on the registered services registry configuration (or lack thereof).
 *
 * Adds an additional support for a custom <i>unauthorizedUrl</i> attribute in case of a registered service is
 * not enabled.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.0.2
 */
public final class ServiceAuthorizationCheckWithCustomView extends AbstractAction {

    @NotNull
    private final ServicesManager servicesManager;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ServiceAuthorizationCheckWithCustomView(final ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    @Override
    protected Event doExecute(final RequestContext context) throws Exception {
        final Service service = WebUtils.getService(context);
        //No service == plain /login request. Return success indicating transition to the login form
        if (service == null) {
            return success();
        }
        final RegisteredService registeredService = this.servicesManager.findServiceBy(service);

        if (registeredService == null) {
            logger.warn("Unauthorized Service Access for Service: [ {} ] - service is not defined in the service registry.", service.getId());
            throw new UnauthorizedServiceException();
        } else if (!registeredService.isEnabled()) {
            logger.warn("Unauthorized Service Access for Service: [ {} ] - service is not enabled in the service registry.", service.getId());
            if (registeredService instanceof RegisteredServiceWithAttributes) {
                String unauthorizedUrl = (String) ((RegisteredServiceWithAttributes) registeredService).getExtraAttributes().get("unauthorizedUrl");
                if (unauthorizedUrl != null) {
                    context.getRequestScope().put("unauthorizedUrl", unauthorizedUrl);
                    return no();
                }
            }
            throw new UnauthorizedServiceException();
        }
        return success();
    }
}