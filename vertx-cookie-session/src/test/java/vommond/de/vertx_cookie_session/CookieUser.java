package vommond.de.vertx_cookie_session;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;

public class CookieUser implements User{
	
	public CookieUser() {
		
	}
	
	public CookieUser(String id, String name, String lastname) {
		super();
		this.name = name;
		this.id = id;
		this.lastname = lastname;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastname == null) ? 0 : lastname.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CookieUser other = (CookieUser) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastname == null) {
			if (other.lastname != null)
				return false;
		} else if (!lastname.equals(other.lastname))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String name, id, lastname;

	@Override
	public User clearCache() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User isAuthorised(String arg0, Handler<AsyncResult<Boolean>> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonObject principal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAuthProvider(AuthProvider arg0) {
		// TODO Auto-generated method stub
		
	}

}
