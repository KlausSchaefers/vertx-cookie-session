package vommond.de.vertx_cookie_session;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * 
 * @author Klaus Schaefers
 *
 */
public class KryoBase64Serializer implements CookieSerializer {


	private static final Logger log = LoggerFactory.getLogger(KryoBase64Serializer.class);

	private final Kryo kryo = new Kryo();
	

	@Override
	public String write(CookieSessionData data) throws IOException {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			Output output = new Output(bos);

			kryo.writeClassAndObject(output, data);
			output.close();

			return Base64.encodeBase64String(bos.toByteArray());

		} catch (Exception e) {
			log.error("write()", e);
			throw new IOException("Cannot wrote CookieSessionData");
		}

	}


	@Override
	public CookieSessionData read(String value) throws IOException {
		try {
			Input input = new Input(Base64.decodeBase64(value.getBytes()));
			Object object = kryo.readClassAndObject(input);
			if (object instanceof CookieSessionData) {
				return (CookieSessionData) object;
			} else {
				throw new IOException("read() > Kryo gave us " + object.getClass());
			}
		} catch (Exception e) {
			log.error("read() > Cannot read " + value);
			throw new IOException("Read Exception : " + value);
		}
	}
}
