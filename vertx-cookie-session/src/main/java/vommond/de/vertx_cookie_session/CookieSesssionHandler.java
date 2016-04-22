package vommond.de.vertx_cookie_session;

import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.SessionHandler;

/**
 * SessionHnalder which stores the session data in an encrypted cookie. There are two limitations
 * 
 * 1) Session Data cannot be bigger 4k
 * 
 * 2) Strong encryption requires Java JCE installed
 * 
 * @author Klaus Schaefers
 *
 */
public class CookieSesssionHandler implements SessionHandler {

	private static final Logger log = LoggerFactory.getLogger(CookieSesssionHandler.class);

	private AESEncryptor aes;

	private String sessionCookieName = "VertxSessionCookie";

	private long sessionTimeout = 30 * 1000 * 60;;

	private boolean sessionCookieSecure = false;

	private boolean sessionCookieHttpOnly = false;

	private CookieSerializer serializer = new KryoBase64Serializer();

	public CookieSesssionHandler(String password) throws Exception {
		this(password, 128);
	}

	public CookieSesssionHandler(String password, int keySize) throws Exception {

		try{
			HashFunction hf = Hashing.sha256();
			String salt = hf.hashString(password, Charsets.UTF_8).toString();
			aes = new AESEncryptor(password, salt, keySize);

		} catch(Exception e){
			log.error("constructor() > Could not init AES. Check if you have installed Java JCE if keySize is larger 128 ");
			throw new Exception("Could not init AES", e);
		}
	
	}

	public CookieSesssionHandler setZipped() {
		this.serializer = new KryoBase64ZipSerializer();
		return this;
	}

	@Override
	public SessionHandler setSessionTimeout(long timeout) {
		this.sessionTimeout = timeout;
		return this;
	}

	@Override
	public SessionHandler setNagHttps(boolean nag) {

		return this;
	}

	@Override
	public SessionHandler setCookieSecureFlag(boolean secure) {
		this.sessionCookieSecure = secure;
		return this;
	}

	@Override
	public SessionHandler setCookieHttpOnlyFlag(boolean httpOnly) {
		this.sessionCookieHttpOnly = httpOnly;
		return this;
	}

	@Override
	public SessionHandler setSessionCookieName(String sessionCookieName) {
		this.sessionCookieName = sessionCookieName;
		return this;
	}

	@Override
	public void handle(RoutingContext context) {

		context.response().ended();

		CookieSession session = readSession(context);

		if (session == null || isDestroyedOrTimedOut(session)) {
			createNewSession(context);
			context.next();
		} else {
			context.setSession(session);
			session.setAccessed();
			context.next();
		}

	}

	private boolean isDestroyedOrTimedOut(CookieSession session) {
		return session.isDestroyed() || ((session.lastAccessed() + session.timeout()) < System.currentTimeMillis());
	}

	private void createNewSession(RoutingContext context) {
		
		CookieSession session = new CookieSession(sessionTimeout);
		session.setAccessed();
		session.onchange(data -> {
			writeSession(data, context);
		});

		context.setSession(session);

		/**
		 * Make sure to save once
		 */
		session.save();
		Cookie cookie = Cookie.cookie(sessionCookieName, "");
		cookie.setPath("/");
		cookie.setSecure(sessionCookieSecure);
		cookie.setHttpOnly(sessionCookieHttpOnly);
		context.addCookie(cookie);
	
	}

	/**
	 * Writes the session data in the cookie. The data is :
	 * 
	 * - first serialized and 
	 * 
	 * - then encrypted
	 * 
	 * before its written to the cookie
	 * 
	 * @param session
	 * 			The session 
	 * @param context
	 * 			The RoutingContext
	 */
	public void writeSession(CookieSessionData session, RoutingContext context) {

		Cookie cookie = context.getCookie(sessionCookieName);
		if (cookie != null) {

			try {
				/**
				 * 1) serialize the session data
				 */
				String value = write(session);
				/**
				 * 2) encrypt the date
				 */
				String encrpyted = aes.encrypt(value);
				/**
				 * 3) store in cookie
				 */
				if(encrpyted.length() >= 4048){
					log.error("writeSession() > Session data to big for cookie");
					return;
				}
				cookie.setValue(encrpyted);
			} catch (Exception e) {
				log.error("writeSession()", e);
			}
		}
	}

	/**
	 * Reads session data from cookie. First the data is decrypted and the 
	 * deserialized. This is the exact reverse order as the writeSession method
	 * 
	 * @param context
	 * 			The Routing Context
	 * @return
	 */
	public CookieSession readSession(RoutingContext context) {
		CookieSession session = null;

		Cookie cookie = context.getCookie(sessionCookieName);
		if (cookie != null) {

			try {
				/**
				 * 1) Read value form cookie
				 */
				String value = cookie.getValue();

				/**
				 * 2) Decrypt value to new string
				 */
				String decrypted = aes.decrypt(value);
				
				/**
				 * Deserialize value
				 */
				session = new CookieSession(read(decrypted));
				session.onchange(data -> {
					writeSession(data, context);
				});
			} catch (Exception e) {
				log.error("readSession()", e);
			}

		}  else {
			System.out.println("No cookie");
		}

		return session;

	}

	private String write(CookieSessionData data) throws IOException {
		return this.serializer.write(data);
	}

	private CookieSessionData read(String value) throws IOException {
		return this.serializer.read(value);
	}

}
