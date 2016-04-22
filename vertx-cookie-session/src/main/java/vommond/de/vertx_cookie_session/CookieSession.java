package vommond.de.vertx_cookie_session;

import java.util.Map;

import io.vertx.core.Handler;
import io.vertx.ext.web.Session;

/**
 * 
 * Wrapper around CookieSessionData which notifies the handler about changes
 * 
 * @author Klaus Schaefers
 *
 */
public class CookieSession implements Session {

	private Handler<CookieSessionData> handler;
	
	private CookieSessionData payload;
	
	public CookieSession() {
	}

	public CookieSession(long timeout) {
		this.payload = new CookieSessionData(timeout);
	}
	
	public CookieSession(CookieSessionData payload) {
		this.payload = payload;
	}
	
	public CookieSessionData getPayLoad(){
		return payload;
	}

	@Override
	public String id() {
		return this.payload.id();
	}

	@Override
	public long timeout() {
		return this.payload.timeout();
	}

	@Override
	public <T> T get(String key) {
		return this.payload.get(key);
	}

	@Override
	public Session put(String key, Object obj) {
		this.payload.put(key, obj);
		this.save();
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T remove(String key) {
		Object obj = this.payload.remove(key);
		this.save();
		return (T) obj;
	}

	@Override
	public Map<String, Object> data() {
		return this.payload.data();
	}

	@Override
	public long lastAccessed() {
		return this.payload.lastAccessed();
		
	}

	@Override
	public void setAccessed() {
		this.payload.setAccessed();
		this.save();
	}

	@Override
	public void destroy() {
		this.payload.destroy();
		this.save();
	}

	@Override
	public boolean isDestroyed() {
		return this.payload.isDestroyed();
	}

	/**
	 * The handler will be called when the session object was changed
	 * @param handler
	 */
	public void onchange(Handler<CookieSessionData> handler){
		this.handler = handler;
	}
	
	public void save(){
		if(this.handler != null){
			handler.handle(this.payload);
		}
	}
}
