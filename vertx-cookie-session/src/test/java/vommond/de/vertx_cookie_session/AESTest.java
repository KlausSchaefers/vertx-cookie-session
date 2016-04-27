package vommond.de.vertx_cookie_session;

import org.junit.Test;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.junit.*;

public class AESTest {
	
	private static final Logger log = LoggerFactory.getLogger(AESTest.class);

	
	@Test
	public void testWeak() throws Exception{
		log.info("testWeak() > enter");
		
		
		AESEncryptor aes = new AESEncryptor("123123123","mysalt", 128);
		
		String txt ="Papa Smurf is blue";
		String encrypted = aes.encrypt(txt);
		String decrypted = aes.decrypt(encrypted);
		
		Assert.assertEquals(decrypted, txt);
		
		log.info("# "  + encrypted.length() + " from " +txt.length());
		
		log.info("testWeak() > exit");
	}

	
	@Test
	public void testStrong() throws Exception{
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
