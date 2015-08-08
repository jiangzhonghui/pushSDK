package com.duowan.xgame.mobile.util;

/**
 * MD5工具
 *
 */

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5 {

	/**
	 * MD5值计算<p>
	 * MD5的算法在RFC1321 中定义:
	 * 在RFC 1321中，给出了Test suite用来检验你的实现是否正确：
	 * MD5 ("") = d41d8cd98f00b204e9800998ecf8427e
	 * MD5 ("a") = 0cc175b9c0f1b6a831c399e269772661
	 * MD5 ("abc") = 900150983cd24fb0d6963f7d28e17f72
	 * MD5 ("message digest") = f96b697d7cb7938d525a2f31aaf161d0
	 * MD5 ("abcdefghijklmnopqrstuvwxyz") = c3fcd3d76192e4007dfb496cca67e13b
	 *
	 * @param res 源字符串
	 * @return md5值
	 */
	public final static String md5Digest(String res) {
		if(res ==null||"".equals(res)){
			return null;
		}
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		byte[] strTemp;
		try {
			strTemp = res.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			return null;
		}
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			String dd = new String(str);
			return dd;
		} catch (Exception e) {
			return null;
		}
	}
	
	


	/**
	 * MD5值计算+Base64<p>
	 * MD5的算法在RFC1321 中定义:
	 * 在RFC 1321中
	 * 
	 * @param res 源字符串
	 * @return md5值
	 */
	public final static byte[] md5SrcDigest(String res) {
		if(res == null || "".equals(res)){
			return null;
		}
		byte[] strTemp = res.getBytes();
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			return md;
		} catch (Exception e) {
			return null;
		}
	}

	
	
	public static void main(String[] args) {
		// String reqJson =
		// "{\"exorderno\":\"1\",\"transid\":\"2\",\"waresid\":\"3\",\"chargepoint\":31,\"feetype\":4,\"money\":5,\"count\":6,\"result\":0,\"transtype\":0,\"transtime\":\"2012-12-12 12:11:10\",\"cpprivate\":\"7\",\"sign\":\"64a04bc23987c621264a6295b8c61191 9c9ccd91cbc584316b9d99919921a9be 89c38dfa9329001a521bf4c904bb83cd \"}";
		// boolean b = CpTransSyncSignValid.validSign(reqJson,
		// "MjdFN0ExMURCM0JDMDc0QTQ3OTY1NzEwNDEzODMzMjhERkFDRDA5MU1UVTRNalkyTXpNek1ESTFNREUxT1RjME16RXJNakk0TnpjeE56ZzBNVEEyTlRJME16TTNORE00TkRBM09EY3hNemcxTkRrMU1UTXhPVEl4");
		String md5= MD5.md5Digest("uid=2147483647&month=1&forever=0&timestamp=1419308321");
		
		System.out.println(md5);
		
		
		//3ae5508e339425e9d0d89c1bf6755183 5777a6fb3489bc48a8902636adc2e7bc 8404d152e2a891a488145c8c85f7f4ad 
		
	}
	
}
