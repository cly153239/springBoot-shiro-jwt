package com.jiangtou.server.crypto;

import com.jiangtou.server.pojo.po.KeyPairPo;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author 陆迪
 * @date 2019/3/18 21:20
 **/
public class RsaUtil {

    /** 指定key的大小 */
    private static int KEYSIZE = 2048;

    public static final String CHAR_ENCODING = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";


    /**
     * 加密方法 source： 源数据
     */
    public static String encrypt(String source, String publicKey)
            throws Exception {
        Key key = getPublicKey(publicKey);
        /** 得到Cipher对象来实现对源数据的RSA加密 */
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] b = source.getBytes();
        /** 执行加密操作 */
        byte[] b1 = cipher.doFinal(b);
        /*return new String(Base64.encodeBase64(b1),
                ConfigureEncryptAndDecrypt.CHAR_ENCODING);*/
        return Base64.getEncoder().encodeToString(b1);
    }

    /**
     * 解密算法 cryptograph:密文
     */
    public static String decrypt(String cryptograph, String privateKey)
            throws Exception {
        Key key = getPrivateKey(privateKey);
        /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        /*byte[] b1 = Base64.decodeBase64(cryptograph.getBytes());*/
        byte[] b1 = Base64.getDecoder().decode(cryptograph);
        /** 执行解密操作 */
        byte[] b = cipher.doFinal(b1);
        return new String(b);
    }

    /**
     * 得到公钥
     *
     * @param key
     *            密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PublicKey getPublicKey(String key) throws Exception {
       /* X509EncodedKeySpec keySpec = new X509EncodedKeySpec(
                Base64.decodeBase64(key.getBytes()));*/

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(key));

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 得到私钥
     *
     * @param key
     *            密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        /*PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(
                Base64.decodeBase64(key.getBytes()));*/
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    public static String sign(String content, String privateKey) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                    Base64.getDecoder().decode(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyFactory.generatePrivate(priPKCS8);

            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initSign(priKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            byte[] signed = signature.sign();

            return Base64.getEncoder().encodeToString(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean checkSign(String content, String sign, String publicKey)
    {
        try
        {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.getDecoder().decode(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));


            java.security.Signature signature = java.security.Signature
                    .getInstance("SHA256WithRSA");

            signature.initVerify(pubKey);
            signature.update( content.getBytes(StandardCharsets.UTF_8) );

            boolean isVerifySuccess;
            isVerifySuccess = signature.verify(Base64.getDecoder().decode(sign));

            return isVerifySuccess;

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public static void main(String[] args) throws Exception {
        String message = "dddd";
        KeyPairPo keyPairPo = SecretKeyFactory.getInstance().nextRsaKeyPairPo();
        String pub = keyPairPo.getPublicKey();
        String pri = keyPairPo.getPrivateKey();
        System.out.println(pub);
        System.out.println(pri);

        String t1 = encrypt(message, pub);
        System.out.println("t1:\n" + t1);

        String t2 = decrypt(t1, pri);
        System.out.println("t2:\n" + t2);
    }
}
