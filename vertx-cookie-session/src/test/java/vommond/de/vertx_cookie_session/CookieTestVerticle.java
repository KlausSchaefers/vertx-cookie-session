package vommond.de.vertx_cookie_session;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.impl.JWTAuthHandlerImpl;


public class CookieTestVerticle extends AbstractVerticle {
	
	
	private final Logger logger = LoggerFactory.getLogger(CookieTestVerticle.class);
	
	private HttpServer server;
	

	
	@Override
	public void start() throws Exception {
		this.logger.info("start() > enter");

	
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create().setMergeFormAttributes(false));
		router.route().handler(CookieHandler.create());
		
		router.route().handler(new CookieSesssionHandler("MyPassword"));
		
	
		router.route(HttpMethod.GET, "/count").handler(context ->{
				
			
			Session s = context.session();
			Integer count = s.get("count");
			if(count == null){
				count = new Integer(1);
			} else {
				count = new Integer(count.intValue() + 1);
			}
			s.put("count", count);
	

			context.response().end("{\"count\": " +  count +"}");
		});
		

		this.server = vertx.createHttpServer()
			.requestHandler(router::accept)
			.listen(8080);
		
		
	
	}


	
	
	
	@Override
	public void stop(){
	
		try {
			
		
			server.close();
	
		
			super.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}