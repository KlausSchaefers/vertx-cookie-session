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
	
	public boolean isExpired(){
		if (this.payload.timeout() > 0) {
			long now = System.currentTimeMillis();
			return (now - this.payload.lastAccessed() > this.payload.timeout());
		}
		return false;
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
		// Avoid not needed writes to cookie...
		if (this.payload.timeout() > 0) {
			this.payload.setAccessed();
			this.save();
		}
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
		// TODO: check if dirty
		if(this.handler != null){
			handler.handle(this.payload);
		}
	}

	@Override
	public Session regenerateId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRegenerated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String oldId() {
		// TODO Auto-generated method stub
		return null;
	}
}
