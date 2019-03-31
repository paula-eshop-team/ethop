package com.eshop.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;

/**
 * 
 * @author Paula Lin
 *
 */
public class MD5Util {

	private static Logger logger = LoggerFactory.getLogger(MD5Util.class);
	
    private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

	/**
	 * 
	 * @param b
	 * @return
	 */
	//加密算法
    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    /**
     * 
     * @param b
     * @return
     */
    //加密算法
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * 返回大写MD5
     *
     * @param origin 需要加密的源字符串
     * @param charsetname 加密算法使用的字符集,如果为空,就使用默认的机密字符集
     * @return
     */
    private static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
        } catch (Exception exception) {
        } 
        return resultString.toUpperCase();
    }

    /**
     * 
     * @param origin
     * @return
     */
    //使用utf-8作为字符集进行MD5加密
    public static String MD5EncodeUtf8(String origin) {
        origin = origin + PropertiesUtil.getStringProperty("password.salt", "");
        return MD5Encode(origin, "utf-8");
    }


}
