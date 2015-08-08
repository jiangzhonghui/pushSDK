package com.duowan.xgame.mobile.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
 





import javax.crypto.Cipher;
 
public class RSAUtils {
 
	
	private final static String modulus = "162754298438727767813374389555914288184033176307253928274283098855726434510543056500159662368777760099554763169127470716568868710375119846223215075501834238202672066298049835670270870897358711490964696456627655137428544773173798272449228016625208302317347696490580300383522629725673197619166187014931626397299";
     //公钥指数
	private final static String public_exponent = "65537";
	
	private final static String private_exponent = "146622160521856754802164232933621244315020564907709830771044576949523847901749738297845898132581713139719742622769656813356463944478042187479818137429296332601709807316790958948504271709507155822758379750356784583835308571121043202400115521457119408483044069033152302865022408884721576676879456334143089284833";
     
	public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        //HashMap<String, Object> map = RSAUtils.getKeys();
        //生成公钥和私钥
        //RSAPublicKey publicKey = (RSAPublicKey) map.get("public");
        //RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");
         
        //模
        //String modulus = publicKey.getModulus().toString();
        //公钥指数
        //String public_exponent = publicKey.getPublicExponent().toString();
        //私钥指数
        //String private_exponent = privateKey.getPrivateExponent().toString();
        
        String modulus = "162754298438727767813374389555914288184033176307253928274283098855726434510543056500159662368777760099554763169127470716568868710375119846223215075501834238202672066298049835670270870897358711490964696456627655137428544773173798272449228016625208302317347696490580300383522629725673197619166187014931626397299";
        System.err.println("Modulus:"+ new BigInteger(modulus).toString(16));
        //公钥指数
        String public_exponent = "65537";
        //私钥指数
        String private_exponent = "146622160521856754802164232933621244315020564907709830771044576949523847901749738297845898132581713139719742622769656813356463944478042187479818137429296332601709807316790958948504271709507155822758379750356784583835308571121043202400115521457119408483044069033152302865022408884721576676879456334143089284833";
        System.err.println("private_exponent:"+new BigInteger(private_exponent).toString(16));
        //明文
        String ming = "9e6f2f4bf7c275f76d8475ff30b24be5";
        System.err.println(ming);
        //使用模和指数生成公钥和私钥
        RSAPublicKey pubKey = RSAUtils.getPublicKey(modulus, public_exponent);
        RSAPrivateKey priKey = RSAUtils.getPrivateKey(modulus, private_exponent);
        //加密后的密文
        //String mi = RSAUtils.encryptByPublicKey(ming, pubKey);
        String mi = RSAUtils.encryptByPublicKey(ming);
        System.err.println(mi);
        //解密后的明文
        ming = RSAUtils.decryptByPrivateKey(mi, priKey);
        System.err.println(ming);
        
        //System.err.println(encryptByPublicKey(ming));
    }
	
	public static String encryptByPublicKey(String mi) throws Exception{
		RSAPublicKey pubKey = RSAUtils.getPublicKey(modulus, public_exponent);
		String entrcy= RSAUtils.encryptByPublicKey(mi, pubKey);
		return entrcy;
	}
	
    /**
     * 生成公钥和私钥
     * @throws NoSuchAlgorithmException 
     * @throws IOException 
     *
     */
    public static HashMap<String, Object> getKeys() throws NoSuchAlgorithmException, IOException{
        HashMap<String, Object> map = new HashMap<String, Object>();
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        map.put("public", publicKey);
        map.put("private", privateKey);
        
//        //保存公匙
//        FileOutputStream public_file_out;
//		try {
//			public_file_out = new FileOutputStream("d://public_key.pem");
//			ObjectOutputStream  public_object_out = new ObjectOutputStream(public_file_out);
//	        public_object_out.writeObject(keyPair.getPublic());
//	        //保存私匙
//	        FileOutputStream private_file_out = new FileOutputStream( "d://private_key.pem");
//	        ObjectOutputStream private_object_out = new ObjectOutputStream(private_file_out);
//	        private_object_out.writeObject(keyPair.getPrivate());
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        
        
        return map;
    }
    
    
    public static void write(String file, byte[] data) throws Exception {
    	  FileOutputStream outputStream = new FileOutputStream(file);
    	  outputStream.write(data);
    	  outputStream.close();
    }
    
    /**
     * 使用模和指数生成RSA公钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA
     * /None/NoPadding】
     * 
     * @param modulus
     *            模
     * @param exponent
     *            指数
     * @return
     */
    public static RSAPublicKey getPublicKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
 
    /**
     * 使用模和指数生成RSA私钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA
     * /None/NoPadding】
     * 
     * @param modulus
     *            模
     * @param exponent
     *            指数
     * @return
     */
    public static RSAPrivateKey getPrivateKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
 
    /**
     * 公钥加密
     * 
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data, RSAPublicKey publicKey)
            throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        // 模长
        int key_len = publicKey.getModulus().bitLength() / 8;
        // 加密数据长度 <= 模长-11
        String[] datas = splitString(data, key_len - 11);
        String mi = "";
        //如果明文长度大于模长-11则要分组加密
        for (String s : datas) {
            mi += bcd2Str(cipher.doFinal(s.getBytes()));
        }
        return mi;
    }
 
    /**
     * 私钥解密
     * 
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey)
            throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        //模长
        int key_len = privateKey.getModulus().bitLength() / 8;
        byte[] bytes = data.getBytes();
        byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
        System.err.println(bcd.length);
        //如果密文长度大于模长则要分组解密
        String ming = "";
        byte[][] arrays = splitArray(bcd, key_len);
        for(byte[] arr : arrays){
            ming += new String(cipher.doFinal(arr));
        }
        return ming;
    }
    /**
     * ASCII码转BCD码
     * 
     */
    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++) {
            bcd[i] = asc_to_bcd(ascii[j++]);
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
        }
        return bcd;
    }
    public static byte asc_to_bcd(byte asc) {
        byte bcd;
 
        if ((asc >= '0') && (asc <= '9'))
            bcd = (byte) (asc - '0');
        else if ((asc >= 'A') && (asc <= 'F'))
            bcd = (byte) (asc - 'A' + 10);
        else if ((asc >= 'a') && (asc <= 'f'))
            bcd = (byte) (asc - 'a' + 10);
        else
            bcd = (byte) (asc - 48);
        return bcd;
    }
    /**
     * BCD转字符串
     */
    public static String bcd2Str(byte[] bytes) {
        char temp[] = new char[bytes.length * 2], val;
 
        for (int i = 0; i < bytes.length; i++) {
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
 
            val = (char) (bytes[i] & 0x0f);
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
        }
        return new String(temp);
    }
    /**
     * 拆分字符串
     */
    public static String[] splitString(String string, int len) {
        int x = string.length() / len;
        int y = string.length() % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        String[] strings = new String[x + z];
        String str = "";
        for (int i=0; i<x+z; i++) {
            if (i==x+z-1 && y!=0) {
                str = string.substring(i*len, i*len+y);
            }else{
                str = string.substring(i*len, i*len+len);
            }
            strings[i] = str;
        }
        return strings;
    }
    /**
     *拆分数组 
     */
    public static byte[][] splitArray(byte[] data,int len){
        int x = data.length / len;
        int y = data.length % len;
        int z = 0;
        if(y!=0){
            z = 1;
        }
        byte[][] arrays = new byte[x+z][];
        byte[] arr;
        for(int i=0; i<x+z; i++){
            arr = new byte[len];
            if(i==x+z-1 && y!=0){
                System.arraycopy(data, i*len, arr, 0, y);
            }else{
                System.arraycopy(data, i*len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }
}