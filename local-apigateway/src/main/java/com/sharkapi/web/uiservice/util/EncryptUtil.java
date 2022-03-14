package com.sharkapi.web.uiservice.util;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.DES;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

public class EncryptUtil {

    /* MD5加密 ，适用密码
    * */
    public static String encryptMD5String(String str){
        String en = str.trim();
        return DigestUtil.md5Hex(en);
    }

    private static String AES_KEY = "78hhj*7hfuf4op1A";
    //加密
    public static String encrypt( Object content) {
        if( content instanceof String) return encrypt( (String)content);
        return "";
    }
    public static String encrypt( String content) {
        //随机生成密钥
        byte[] key = AES_KEY.getBytes();
        //byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        //构建
        AES aes = SecureUtil.aes(key);
        // 加密为16进制表示
        return aes.encryptHex( content);
    }

    //解密
    public static String decrypt( Object encrypt) {
        if( encrypt instanceof String) return decrypt( (String)encrypt);
        return "";
    }
    public static String decrypt( String encrypt) {
        //随机生成密钥
        byte[] key = AES_KEY.getBytes();
        //byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        //构建
        AES aes = SecureUtil.aes(key);
        //解密为原字符串
        return aes.decryptStr(encrypt, CharsetUtil.CHARSET_UTF_8);
    }

}
