package vommond.de.vertx_cookie_session;

import java.io.IOException;

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

	private final CookieEncryptor encryptor;

	private String sessionCookieName = "MatcSessionCookie"; // Name cannot start with Vertx apprently

	// IN MS
	private long sessionTimeout = 24 * 3600 * 1000;

	private boolean sessionCookieSecure = false;

	private boolean sessionCookieHttpOnly = true;

	private CookieSerializer serializer = new KryoBase64ZipSerializer();

	public CookieSesssionHandler(String password) throws Exception {
		this(password, -1);
	}

	public CookieSesssionHandler(String password, long timeout) throws Exception {
		log.error("CookieSesssionHandler() > Set timeout " + timeout);
		try{
			this.encryptor = new JasyptEncryptor(password);
			this.sessionTimeout = timeout;
		} catch(Exception e){
			log.error("constructor() > Could not init Encryptor. Check if you have installed Java JCE if keySize is larger 128 ");
			throw new Exception("Could not init Encryptor", e);
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
		log.debug("handle() > enter > " + context.request().absoluteURI());
		context.response().ended();

		try{
			CookieSession session = readSession(context);
	
			if (session == null || session.isDestroyed() || session.isExpired()) {
				createNewSession(context);
				context.next();
			} else {
				context.setSession(session);
				session.setAccessed();
				context.next();
			}
		} catch(Exception e){
			e.printStackTrace();
			context.next();
		}

	}


	private void createNewSession(RoutingContext context) {
		log.info("createNewSession() > enter " + context.request().absoluteURI());
		CookieSession session = new CookieSession(sessionTimeout);
		session.setAccessed();
		session.onchange(data -> {
			writeSession(data, context);
		});

		context.setSession(session);

		/**
		 * Make sure to save once. Dunno why
		 */
		session.put("created", System.currentTimeMillis());
		session.save();
		
		Cookie cookie = Cookie.cookie(sessionCookieName, "");
		cookie.setPath("/");
		cookie.setSecure(sessionCookieSecure);
		if (this.sessionTimeout > 0 ){
			log.info("createNewSession() > set timeout");
			cookie.setMaxAge(sessionTimeout / 1000);
		}
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
		log.info("writeSession() > " + context.request().absoluteURI());
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
				String encrpyted = encryptor.encrypt(value);
				/**
				 * 3) store in cookie
				 */
				if(encrpyted.length() >= 4048){
					log.error("writeSession() > Session data to big for cookie");
					return;
				}
				
				cookie = Cookie.cookie(sessionCookieName, encrpyted);
				cookie.setPath("/");
				cookie.setSecure(sessionCookieSecure);
				cookie.setHttpOnly(sessionCookieHttpOnly);
				context.addCookie(cookie);
				log.debug("writeSession() > exit > " + encrpyted.length());
			} catch (Exception e) {
				log.error("writeSession()", e);
				log.equals("writeSession() > error " + context.request().absoluteURI());
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
				if (value != null && !value.isEmpty()){
					/**
					 * 2) Decrypt value to new string
					 */
					try{
						String decrypted = encryptor.decrypt(value);
						
						/**
						 * Deserialize value
						 */
						CookieSessionData sessionData = read(decrypted);
						session = new CookieSession(sessionData);
						session.onchange(data -> {
							writeSession(data, context);
						});
					} catch(Exception e) {
						log.error("readSession() > Error: Wrong string", e);
						log.error("readSession() > ", value);
					}
				} else {
					log.info("readSession() > Cookie is empty");
				}
				
			} catch (Exception e) {
				log.error("readSession()", e);
			}
		}  else {
			log.info("readSession() > No Cookie");
		}

		return session;

	}

	private String write(CookieSessionData data) throws IOException {
		return this.serializer.write(data);
	}

	private CookieSessionData read(String value) throws IOException {
		return this.serializer.read(value);
	}

	@Override
	public SessionHandler setMinLength(int minLength) {
		return this;
	}

}
