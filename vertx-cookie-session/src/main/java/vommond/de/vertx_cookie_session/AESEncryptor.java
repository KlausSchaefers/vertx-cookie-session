package vommond.de.vertx_cookie_session;

import java.lang.reflect.Field;
import java.security.spec.KeySpec;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;


/**
 * 
 * @author Klaus Schaefers
 *
 */
public class AESEncryptor implements CookieEncryptor{

	private static final int ITERATION_COUNT = 65536;
	
	private Cipher ecipher;

	private Cipher dcipher;
	
	static {
	    try {
	        Field field = Class.forName("javax.crypto.JceSecurity").getDeclaredField("isRestricted");
	        field.setAccessible(true);
	        field.set(null, java.lang.Boolean.FALSE);
	    } catch (Exception ex) {
	    	ex.printStackTrace();
	    }
	}

	public AESEncryptor(String passPhrase, String salt, int key_length) throws Exception {
		
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(passPhrase.toCharArray(), salt.getBytes(), ITERATION_COUNT, key_length);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

		ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		ecipher.init(Cipher.ENCRYPT_MODE, secret);

		dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] iv = ecipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
		dcipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
	}

	public String encrypt(String encrypt) throws Exception {
		byte[] bytes = encrypt.getBytes("UTF8");
		byte[] encrypted = encrypt(bytes);
		return Hex.encodeHexString(encrypted);
		//return Base64.getUrlEncoder().encodeToString(encrypted);
	}

	public byte[] encrypt(byte[] plain) throws Exception {
		return ecipher.doFinal(plain);
	}

	public String decrypt(String encrypt) throws Exception {
	
		byte[] bytes = Hex.decodeHex(encrypt.toCharArray());
		byte[] decrypted = decrypt(bytes);
		return new String(decrypted, "UTF8");
	}

	public byte[] decrypt(byte[] encrypt) throws Exception {
		return dcipher.doFinal(encrypt);
	}
	
	
	

}
