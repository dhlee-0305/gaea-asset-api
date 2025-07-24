package com.gaea.asset.manager.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Util {

	public static String encrypt(String text) {
		String textHash = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(text.getBytes());
			
			StringBuilder builder = new StringBuilder();
			for (byte b : md.digest()) {
				builder.append(String.format("%02x", b));
			}
			
			textHash = builder.toString();
		} catch (NoSuchAlgorithmException e) {
			textHash = null;
		}
		return textHash;
	}
}