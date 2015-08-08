package com.duowan.xgame.mobile.util;

import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import java.io.*;

class Encrypt {
	public static void main(String[] args) throws Exception {

		String message = "Message to Decode";

		KeyGenerator key = KeyGenerator.getInstance("AES");
		key.init(256);

		SecretKey skey = key.generateKey();
		byte[] raw = skey.getEncoded();

		SecretKeySpec sskey = new SecretKeySpec(raw, "AES");

		Cipher cipher = Cipher.getInstance("AES");

		cipher.init(Cipher.ENCRYPT_MODE, sskey);

		byte[] encrypted = cipher.doFinal(message.getBytes());
		//System.out.println("encrypted string: " + asHex(encrypted));

	}
}
