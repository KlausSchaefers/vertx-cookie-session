package vommond.de.vertx_cookie_session;

import org.junit.Assert;
import org.junit.Test;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class CookieSerializationTest {

	private static final Logger log = LoggerFactory.getLogger(CookieSerializationTest.class);

	
	@Test
	public void test() throws Exception{
		log.info("test() > enter"); 
		
		KryoBase64Serializer handler = new KryoBase64Serializer();
		
		CookieSessionData data = new CookieSessionData();
		data.setAccessed();
		data.put("user", new CookieUser("Papa@smurf.io", "Papa", "Smurf"));
		
		
		String value= handler.write(data);
		CookieSessionData data2 = handler.read(value);
		
		assertEquals(data, data2);
		
		log.info("test() > exit"); 
	}
	
	
	@Test
	public void testZipped() throws Exception{
		log.info("testZipped() > enter"); 
		
		KryoBase64ZipSerializer handler = new KryoBase64ZipSerializer();
		
		CookieSessionData data = new CookieSessionData();
		data.setAccessed();
		data.put("user", new CookieUser("Papa@smurf.io", "Papa", "Smurf"));
		
		String value= handler.write(data);
		CookieSessionData data2 = handler.read(value);
		
		assertEquals(data, data2);
		
		log.info("testZipped() > exit"); 
	}


	private void assertEquals(CookieSessionData data, CookieSessionData data2) {
		Assert.assertEquals(data.id(), data2.id());
		Assert.assertEquals(data.lastAccessed(), data2.lastAccessed());
		Assert.assertEquals((CookieUser)data.get("user"),(CookieUser)data2.get("user"));
	}
}
