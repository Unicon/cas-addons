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
import org.springframework.core.io.Resource;


/**
 * Implementation of an {@link ApplicationListener} the receives {@link net.unicon.cas.addons.support.ResourceChangeDetectingEventNotifier.ResourceChangedEvent} events,
 * when the {@link ResourceChangeDetectingEventNotifier} issues the appropriate callback in the event
 * that the services registry configuration file is modified.
 *
 * @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon, Inc.
 * @see JsonServiceRegistryDao
 * @see ReloadableServicesManager
 * @since 0.9.5
 */
public final class JsonServiceRegistryResourceChangedEventListener implements
		ApplicationListener<ResourceChangeDetectingEventNotifier.ResourceChangedEvent> {

	private static final Logger logger = LoggerFactory.getLogger(JsonServiceRegistryResourceChangedEventListener.class);

	@NotNull
	private final ReloadableServicesManager servicesManager;

	@NotNull
	private final Resource servicesRegistryWatchedConfigFile;


	public JsonServiceRegistryResourceChangedEventListener(final ReloadableServicesManager servicesManager, final Resource servicesRegistryWatchedConfigFile) {
		this.servicesManager = servicesManager;
		this.servicesRegistryWatchedConfigFile = servicesRegistryWatchedConfigFile;
	}

	public void onApplicationEvent(final ResourceChangeDetectingEventNotifier.ResourceChangedEvent resourceChangedEvent) {
		try {
			if (!resourceChangedEvent.getResourceUri().equals(this.servicesRegistryWatchedConfigFile.getURI())) {
				//Not our resource. Just get out of here.
				return;
			}
		}
		catch (final Throwable e) {
			logger.error("An exception is caught while trying to access JSON resource: ", e);
			return;
		}
		logger.debug("Received change event for JSON resource {}. Reloading services...", resourceChangedEvent.getResourceUri());
		this.servicesManager.reload();
	}
}