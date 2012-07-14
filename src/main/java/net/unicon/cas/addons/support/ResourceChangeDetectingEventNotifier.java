package net.unicon.cas.addons.support;

import org.apache.shiro.crypto.hash.Sha1Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.io.Resource;

import java.net.URI;

/**
 * A class responsible for detecting contents changes of configured resource (file, classpath, URL, etc.)
 * by comparing their SHA1 digests - the one saved at the last check and the latest one. If the change
 * is detected, it publishes Spring's <code>ApplicationEvent</code> typed as <code>ResourceChangedEvent</code>
 * wrapping the resource's URI in question within it.
 * <p/>
 * Any interested <code>ApplicationListener</code>s within ApplicationContext could then pick up those events and
 * react to them appropriately.
 * <p/>
 * <p>Note that the periodic polling of the configured resource is not a concern of this class and is configured outside of it.
 * Typically, within Spring ApplicationContext, it is done by <code>TaskScheduler</code> abstraction or the likes.
 * <p/>
 * <p>This class is thread-safe. It uses proper synchronization to protect <code>resourceSha1Hex</code> field updates from race conditions.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 0.7
 */
public class ResourceChangeDetectingEventNotifier implements ApplicationEventPublisherAware {

	private ApplicationEventPublisher applicationEventPublisher;

	private final Resource watchedResource;

	private volatile String resourceSha1Hex;

	private static final Logger logger = LoggerFactory.getLogger(ResourceChangeDetectingEventNotifier.class);

	private static final String EMPTY_STRING_SHA1 = "da39a3ee5e6b4b0d3255bfef95601890afd80709";

	public ResourceChangeDetectingEventNotifier(Resource watchedResource) throws Exception {
		this.watchedResource = watchedResource;
		if (!this.watchedResource.exists()) {
			throw new BeanCreationException(String.format("The 'watchedResource' [%s] must point to an existing resource. " +
					"Please double-check that such resource exists.", this.watchedResource.getURI()));
		}
		//Initial SHA1 of the resource
		//TODO: currently assumes file-based resources. Think about refactoring later to support diff kinds of resources?
		String initialSha1Hex = new Sha1Hash(this.watchedResource.getFile()).toHex();
		if (EMPTY_STRING_SHA1.equals(initialSha1Hex)) {
			logger.warn("The 'watchedResource' [{}] is empty!", this.watchedResource.getURI());
		}
		this.resourceSha1Hex = initialSha1Hex;
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	/**
	 * Compare the SHA1 digests (since last check and the latest) of the configured resource, and if change is detected,
	 * publish the <code>ResourceChangeEvent</code> to ApplicationContext.
	 */
	public void notifyOfTheResourceChangeEventIfNecessary() {
		String currentResourceSha1 = this.resourceSha1Hex;
		String newResourceSha1 = null;
		try {
			newResourceSha1 = new Sha1Hash(this.watchedResource.getFile()).toHex();
			if (!newResourceSha1.equals(currentResourceSha1)) {
				logger.debug("Resource: [{}] | Old Hash: [{}] | New Hash: [{}]", new Object[] {this.watchedResource.getURI(), currentResourceSha1, newResourceSha1});
				synchronized (this.resourceSha1Hex) {
					this.resourceSha1Hex = newResourceSha1;
					this.applicationEventPublisher.publishEvent(new ResourceChangedEvent(this, this.watchedResource.getURI()));
				}
			}
		}
		catch (Throwable e) {
			//TODO: Possibly introduce an exception handling strategy?
			logger.error("An exception is caught during 'watchedResource' access", e);
			return;
		}
	}

	/**
	 * Application event representing the resource contents change.
	 * Intended to be processed by subscribed <code>ApplicationListener</code>s
	 * managed by ApplicationContext.
	 */
	public static class ResourceChangedEvent extends ApplicationEvent {
		private URI resourceUri;

		public ResourceChangedEvent(Object source, URI resourceUri) {
			super(source);
			this.resourceUri = resourceUri;
		}

		public URI getResourceUri() {
			return resourceUri;
		}
	}
}
