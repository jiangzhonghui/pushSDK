package com.duowan.xgame.mobile.auth;

import java.security.MessageDigest;
import java.util.UUID;

import com.duowan.xgame.mobile.common.OAuthSystemException;
import com.duowan.xgame.mobile.util.CipherEncryptor;


/**
* Exemplar OAuth Token Generator
*
* @author Timo Jiang
*/
public class MD5Generator implements ValueGenerator {
	
	private String password;
	public MD5Generator(String password){
		this.password = password;
	}

    public String generateValue() throws OAuthSystemException {
        return generateValue(password);
    }

    public String generateValue(String param) throws OAuthSystemException {
        try {
            /*
            */
        	String encryptString =  CipherEncryptor.encrypt(CipherEncryptor.cpKey1, CipherEncryptor.cpKey2, param);
        	MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(encryptString.getBytes());
            byte[] messageDigest = algorithm.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new OAuthSystemException("Auth Token cannot be generated.", e);
        }
    }
}