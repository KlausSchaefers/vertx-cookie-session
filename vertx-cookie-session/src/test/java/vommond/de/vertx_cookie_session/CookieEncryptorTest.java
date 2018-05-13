package vommond.de.vertx_cookie_session;

import org.junit.Test;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.junit.*;

public class CookieEncryptorTest {
	
	private static final Logger log = LoggerFactory.getLogger(CookieEncryptorTest.class);

	
	
	@Test
	public void testAsypt() throws Exception{
		log.info("testAsypt() > enter");
		
		JasyptEncryptor enc1 = new JasyptEncryptor("123123123");
		
		String txt ="Papa Smurf is blue";
		String encrypted = enc1.encrypt(txt);
		String decrypted = enc1.decrypt(encrypted);
		
		Assert.assertEquals(txt, decrypted);
		
		
		JasyptEncryptor enc2 = new JasyptEncryptor("123123123");
		String decrypted2 = enc2.decrypt(encrypted);
		System.out.println(decrypted2 + " ? " + txt);
		Assert.assertEquals(txt, decrypted2);
		
		log.info("# "  + encrypted.length() + " from " +txt.length());
		
		log.info("testWeak() > exit");
	}
		
	@Test
	public void testAESWeak() throws Exception{
		log.info("testWeak() > enter");
		
		
		AESEncryptor aes = new AESEncryptor("123123123","mysalt", 128);
		
		String txt ="Papa Smurf is blue";
		String encrypted = aes.encrypt(txt);
		String decrypted = aes.decrypt(encrypted);
		
		Assert.assertEquals(decrypted, txt);
		
		
		AESEncryptor aes2 = new AESEncryptor("123123123","mysalt", 128);
		String decrypted2 = aes2.decrypt(encrypted);
		Assert.assertEquals(decrypted2, txt, "New instance has same result");
		
		log.info("# "  + encrypted.length() + " from " +txt.length());
		
		log.info("testWeak() > exit");
	}

	
	@Test
	public void testAESStrong() throws Exception{
		log.info("testStrong() > enter");
		
		
		AESEncryptor aes = new AESEncryptor("123123123","mysalt", 256);
		
		String txt ="Papa Smurf is blue";
		String encrypted = aes.encrypt(txt);
		String decrypted = aes.decrypt(encrypted);
		
		Assert.assertEquals(decrypted, txt);
		
		log.info("# "  + encrypted.length() + " from " +txt.length());
		
		log.info("testStrong() > exit");
	}
}
