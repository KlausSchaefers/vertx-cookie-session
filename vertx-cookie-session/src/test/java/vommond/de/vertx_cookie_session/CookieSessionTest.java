package vommond.de.vertx_cookie_session;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.io.CharStreams;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class CookieSessionTest {

	private static final Logger log = LoggerFactory.getLogger(CookieSessionTest.class);

	protected Vertx vertx;

	private BasicCookieStore cookieStore;

	protected CloseableHttpClient httpClient;

	@Before
	public void before(TestContext contex) {

		vertx = Vertx.vertx();

		cookieStore = new BasicCookieStore();
        
		httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

	}

	@Test
	public void testSession(TestContext context) {
		log.info("testSession() > enter");
		
		deploy(new  CookieTestVerticle(), context);
		
		JsonObject count = get("/count");
		Assert.assertEquals(1, count.getInteger("count").intValue());
		
		count = get("/count");
		Assert.assertEquals(2, count.getInteger("count").intValue());
		
		count = get("/count");
		Assert.assertEquals(3, count.getInteger("count").intValue());
		
		count = get("/count");
		Assert.assertEquals(4, count.getInteger("count").intValue());
		
		count = get("/logout");
		Assert.assertEquals(-1, count.getInteger("count").intValue());
		
		count = get("/count");
		Assert.assertEquals(1, count.getInteger("count").intValue());
		
		log.info("testSession() > exit");
	}
	
	
	public void deploy(Verticle v, TestContext context){
		
		CountDownLatch l = new CountDownLatch(1);
		

		DeploymentOptions options = new DeploymentOptions();
		
		vertx.deployVerticle(v, options, new Handler<AsyncResult<String>>() {
			
			@Override
			public void handle(AsyncResult<String> event) {
				
				if(event.succeeded()){
					log.info("deploy() > Deployed " + v.getClass()); 
					
				} else {
					//context.fail("Could not deploy verticle");
					event.cause().printStackTrace();
				}
				
			
				l.countDown();
			}
		});
		
		try {
			l.await();
		} catch (InterruptedException e) {
			
		}
	}
	
	
	public JsonObject get(String url){
		System.out.println("get() > " + url);
		url = "http://localhost:8080" + url;
		try {
			HttpGet httpget = new HttpGet(url);
	        CloseableHttpResponse resp = httpClient.execute(httpget);
	        
	        if(resp.getStatusLine().getStatusCode() == 200){
				

				InputStream is = resp.getEntity().getContent();
 
				String json = CharStreams.toString( new InputStreamReader(is ));
				resp.close();
				
				return new JsonObject(json);
				
			} else {
				resp.close();
			}

	    
	      
		} catch (Exception e) {
			e.printStackTrace();
			
		}
      
	
		return null;
	}
	
	
	
}
