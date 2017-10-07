package com.pj.magic.util;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;

public class PasswordTransformer {

	private static final String HASH_ALGORITHM = "SHA-256";
	private static final String ENCODING = "UTF-8";
	
	public static final String transform(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
			byte[] bytes = md.digest(plainText.getBytes(ENCODING));
			return Base64.encodeBase64String(bytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
