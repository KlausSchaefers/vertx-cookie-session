package vommond.de.vertx_cookie_session;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * @author Klaus Schaefers
 *
 */
public class AESEncryptor {

	private static final int ITERATION_COUNT = 65536;
	
	private Cipher ecipher;

	private Cipher dcipher;

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
		return Base64.encodeBase64String(encrypted);
	}

	public byte[] encrypt(byte[] plain) throws Exception {
		return ecipher.doFinal(plain);
	}

	public String decrypt(String encrypt) throws Exception {
		byte[] bytes = Base64.decodeBase64(encrypt);
		byte[] decrypted = decrypt(bytes);
		return new String(decrypted, "UTF8");
	}

	public byte[] decrypt(byte[] encrypt) throws Exception {
		return dcipher.doFinal(encrypt);
	}
	
	
	

}
