package vommond.de.vertx_cookie_session;

import java.lang.reflect.Field;

import org.jasypt.util.text.StrongTextEncryptor;

public class JasyptEncryptor implements CookieEncryptor{
	
	
	static {
	    try {
	        Field field = Class.forName("javax.crypto.JceSecurity").getDeclaredField("isRestricted");
	        field.setAccessible(true);
	        field.set(null, java.lang.Boolean.FALSE);
	    } catch (Exception ex) {
	    	ex.printStackTrace();
	    }
	}
	
	private final StrongTextEncryptor textEncryptor;
	
	public JasyptEncryptor(String password){
		textEncryptor = new StrongTextEncryptor();
		textEncryptor.setPassword(password);
	}
	

	@Override
	public String encrypt(String message) throws Exception {
		return textEncryptor.encrypt(message);
	}

	@Override
	public String decrypt(String message) throws Exception {
		return textEncryptor.decrypt(message);
	}

}
