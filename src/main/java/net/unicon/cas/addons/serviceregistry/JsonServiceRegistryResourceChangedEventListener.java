/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at the following location:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.unicon.cas.addons.serviceregistry;

import org.springframework.context.ApplicationListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;

import org.jasig.cas.services.ReloadableServicesManager;

import net.unicon.cas.addons.support.ResourceChangeDetectingEventNotifier;
import net.unicon.cas.addons.support.ResourceChangeDetectingEventNotifier.ResourceChangedEvent;

/**
 * Implementation of an {@link ApplicationListener} the receives {@link ResourceChangedEvent} events, 
 * when the {@link ResourceChangeDetectingEventNotifier} issues the appropriate callback in the event
 * that the services registry configuration file is modified. 
 * 
 * @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon, Inc.
 * @since 0.9.5
 * 
 * @see JsonServiceRegistryDao
 * @see ReloadableServicesManager
 */
public final class JsonServiceRegistryResourceChangedEventListener implements ApplicationListener<ResourceChangedEvent> {
    private static final Logger log = LoggerFactory.getLogger(JsonServiceRegistryResourceChangedEventListener.class);

    @NotNull
    private final ReloadableServicesManager servicesManager;

    public JsonServiceRegistryResourceChangedEventListener(final ReloadableServicesManager servicesManager) {
        super();
        this.servicesManager = servicesManager;
    }

    public void onApplicationEvent(final ResourceChangedEvent event) {
      final ResourceChangedEvent resourceEvent = (ResourceChangedEvent) event;
      log.debug("Received change event for JSON resource {}. Reloading services...", resourceEvent.getResourceUri());
      this.servicesManager.reload();
    }
}