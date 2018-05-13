package vommond.de.vertx_cookie_session;

public interface CookieEncryptor {

	public String encrypt(String encrypt) throws Exception;
	
	public String decrypt(String encrypt) throws Exception;
}
