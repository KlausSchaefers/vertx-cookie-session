package vommond.de.vertx_cookie_session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.vertx.ext.web.Session;

public class CookieSessionData implements Session{


	private String id;
	private long timeout;
	private Map<String, Object> data;
	private long lastAccessed;
	private boolean destroyed;
	
	public CookieSessionData() {
	}

	public CookieSessionData(long timeout) {
		this.id = UUID.randomUUID().toString();
		this.timeout = timeout;
		this.lastAccessed = System.currentTimeMillis();
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public long timeout() {
		return timeout;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		Object obj = getData().get(key);
		return (T) obj;
	}

	@Override
	public CookieSessionData put(String key, Object obj) {
		getData().put(key, obj);
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T remove(String key) {
		Object obj = getData().remove(key);
		return (T) obj;
	}

	@Override
	public Map<String, Object> data() {
		return getData();
	}

	@Override
	public long lastAccessed() {
		return lastAccessed;
		
	}

	@Override
	public void setAccessed() {
		this.lastAccessed = System.currentTimeMillis();
	}

	@Override
	public void destroy() {
		destroyed = true;
		data.clear();
	}

	@Override
	public boolean isDestroyed() {
		return destroyed;
	}

	private Map<String, Object> getData() {
		if (data == null) {
			data = new HashMap<>();
		}
		return data;
	}
	

	@Override
	public String toString() {
		return "CookieSessionData [id=" + id + ", timeout=" + timeout + ", data=" + data + ", lastAccessed="
				+ lastAccessed + ", destroyed=" + destroyed + "]";
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
