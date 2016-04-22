package vommond.de.vertx_cookie_session;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
public class KryoBase64ZipSerializer implements CookieSerializer{

	private static final Logger log = LoggerFactory.getLogger(KryoBase64ZipSerializer.class);

	private final Kryo kryo = new Kryo();

	@Override
	public String write(CookieSessionData data) throws IOException {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream gzos = new GZIPOutputStream(bos);
			Output output = new Output(gzos);
			  
		    kryo.writeClassAndObject(output, data);
		    output.close();
		    gzos.close();
		  

		   return Base64.encodeBase64String(bos.toByteArray());
		} catch (Exception e) {
			log.error("writeZipped() > " + e.getMessage());
			throw new IOException("Write Exception ");
		}
	}

	@Override
	public CookieSessionData read(String value) throws IOException {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(value.getBytes()));
			GZIPInputStream gis = new GZIPInputStream(bis);
		    Input input = new Input(gis);
			Object object = kryo.readClassAndObject(input);
			if (object instanceof CookieSessionData) {
				return (CookieSessionData) object;
			} else {
				throw new IOException("readZip() > Kryo gave us " + object.getClass());
			}
		} catch (Exception e) {
			log.error("readZip() > Cannot read " + value);
			throw new IOException("Read Exception : " + value);
		}
	}

}
