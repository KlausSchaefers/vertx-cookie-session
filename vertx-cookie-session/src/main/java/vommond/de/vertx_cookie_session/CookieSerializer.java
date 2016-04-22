package vommond.de.vertx_cookie_session;

import java.io.IOException;

/**
 * 
 * @author Klaus Schaefers
 *
 */
public interface CookieSerializer {
	
	/**
	 * Convert CookieSessionData to string
	 * 
	 * @param data The CookieSessionData
	 * 
	 * @return CookieSessionData encoded as String
	 * 
	 * @throws IOException
	 */
	public String write(CookieSessionData data) throws IOException;
	
	/**
	 * 
	 * Convert String to CookieSessionData
	 * 
	 * @param value The String encoded cookie data
	 * 
	 * @return CookieSessionData the decoded string
	 * 
	 * @throws IOException
	 */
	public CookieSessionData read(String value) throws IOException;

}
