package vommond.de.vertx_cookie_session;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonSerializer implements CookieSerializer{
	
	ObjectMapper mapper = new ObjectMapper();

	@Override
	public String write(CookieSessionData data) throws IOException {
		String json = mapper.writeValueAsString(data);
		return Base64.encodeBase64String(json.getBytes());
	}

	@Override
	public CookieSessionData read(String value) throws IOException {
		return mapper.readValue(Base64.decodeBase64(value.getBytes()), CookieSessionData.class);
	}

}
