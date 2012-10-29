package net.unicon.cas.addons.authentication.handler;

import net.unicon.cas.addons.support.ThreadSafe;
import org.apache.shiro.crypto.hash.ConfigurableHashService;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashRequest;
import org.jasig.cas.authentication.handler.PasswordEncoder;

/**
 * Password encoder with salted and more cryptographically stronger hashing support which delegates all the work
 * to Apache Shiro HashService API.
 * <p/>
 * This encoder optionally supports configuration options for <b>digestAlgorithmName</b> , <b>salt</b>, <b>hashIterations</b>
 * <p/>
 * If these options are not set at configuration time, the defaults as defined in <code>DefaultHashService</code> are used.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @see <a href="DefaultHashService">http://shiro.apache.org/static/current/apidocs/org/apache/shiro/crypto/hash/DefaultHashService.html</a>
 * @since 1.0.2
 */
@ThreadSafe
public final class ShiroHashServicePasswordEncoder implements PasswordEncoder {

	private final ConfigurableHashService hashService = new DefaultHashService();

	private String digestAlgorithmName;

	private String salt;

	private int hashIterations;

	/**
	 * @param digestAlgorithmName one of:
	 *                            <ul>
	 *                            <li>MD2</li>
	 *                            <li>MD5</li>
	 *                            <li>SHA-1</li>
	 *                            <li>SHA-256</li>
	 *                            <li>SHA-384</li>
	 *                            <li>SHA-512</li>
	 *                            </ul>
	 */
	public void setDigestAlgorithmName(String digestAlgorithmName) {
		this.digestAlgorithmName = digestAlgorithmName;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public void setHashIterations(int hashIterations) {
		this.hashIterations = hashIterations;
	}

	@Override
	public String encode(String password) {
		if (password == null) {
			return null;
		}
		return this.hashService.computeHash(new HashRequest.Builder().setSalt(this.salt).setSource(password).build()).toHex();
	}

	/**
	 * This method is only intended to be called by the infrastructure code managing instances of this class
	 * once during initialization of this instance
	 */
	void init() throws Throwable {
		if (this.digestAlgorithmName != null) {
			this.hashService.setHashAlgorithmName(this.digestAlgorithmName);
		}
		if (this.hashIterations > 0) {
			this.hashService.setHashIterations(this.hashIterations);
		}
	}
}
