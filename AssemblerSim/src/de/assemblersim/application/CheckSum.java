package de.assemblersim.application;
import java.security.MessageDigest;

/**
 * 
 * 
 * @author Dominik Jahnke
 *
 */
public class CheckSum {

	private static String generateChecksum(String text, String method) {
		StringBuffer sb = null;
		try {
			MessageDigest md = MessageDigest.getInstance(method);
			md.update(text.getBytes());
			byte[] digest = md.digest();
			sb = new StringBuffer();
			for (byte b : digest) {
				sb.append(String.format("%02x", b & 0xff));
			}			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return sb.toString();
	}

	/**
	 * Generates the md5 checksum of the text
	 * @param text
	 * @return
	 */
	public static String generateMD5(String text) {
		return generateChecksum(text, "MD5");
	}

	/**
	 * Generates the sha1 checksum of the text
	 * @param text 
	 * @return
	 */
	public static String generateSHA1(String text) {
		return generateChecksum(text, "SHA1");
	}

}
