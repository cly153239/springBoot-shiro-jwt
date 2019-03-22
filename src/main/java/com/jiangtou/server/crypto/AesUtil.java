package com.jiangtou.server.crypto;



import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author 陆迪
 * @date 2019/3/18 16:48
 * AES加解密
 **/
public class AesUtil {

    private static final String AES_ALGORITHM = "AES";
    private final static String AES_CBC_NOPADDING = "AES/CBC/NoPadding";

    /**
     * 加密
     * 返回64位编码
     */
    public static String aesEncode(String data, String key, String iv) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), AES_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));

        return aesEncode(data, secretKeySpec, ivParameterSpec);
    }

    public static String aesEncode(String data, SecretKey secretKey, String iv) {
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        return aesEncode(data, secretKey, ivSpec);
    }

    private static String aesEncode(String data, SecretKey secretKey, IvParameterSpec ivParameterSpec) {
        try {
            Cipher aesCipher = Cipher.getInstance(AES_CBC_NOPADDING);
            int blockSize = aesCipher.getBlockSize();
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            int plainTextLength = dataBytes.length;
            if (plainTextLength % blockSize != 0) {
                plainTextLength = plainTextLength + (blockSize - (plainTextLength % blockSize));
            }

            byte[] plainText = new byte[plainTextLength];
            System.arraycopy(dataBytes, 0, plainText, 0, dataBytes.length);

            aesCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            byte[] encrypted = aesCipher.doFinal(plainText);

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 解密
     * @param encodeBase64Data
     * @param key
     * @param iv
     * @return
     */
    public static String aesDecode(String encodeBase64Data, String key, String iv) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), AES_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));

        return aesDecode(encodeBase64Data, secretKeySpec, ivParameterSpec);
    }

    public static String aesDecode(String encodeBase64Data, SecretKey secretKey, String iv) {
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        return aesDecode(encodeBase64Data, secretKey, ivParameterSpec);
    }


    private static String aesDecode(String encodeBase64Data, SecretKey secretKey, IvParameterSpec ivParameterSpec) {
        try {
            byte[] encodeBytes = Base64.getDecoder().decode(encodeBase64Data);

            Cipher cipher = Cipher.getInstance(AES_CBC_NOPADDING);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

            byte[] original = cipher.doFinal(encodeBytes);

            return new String(original);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException |
                IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException e) {
            e.printStackTrace();
            return "";
        }
    }


    public static void main(String[] args) {
        String message = "ffswe";
        String key = "1234123412ABCDEF";
        String iv = "ABCDEF1234123412";
        /*String result = aesEncode(message, key, iv);

        System.out.println(result);

        String encodeStr = "YAdc0odR3XVoGhJ+8/42hw==";
        String decoStr = aseDecode(encodeStr, key, iv);

        System.out.println(decoStr);*/
        com.jiangtou.server.crypto.SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance();

        int i = 0;
        while (i < 10) {
            SecretKey secretKey = secretKeyFactory.nextAesSecretKey();
            String generatorkey = new String(Base64.getEncoder().encode(secretKey.getEncoded()));
            System.out.println(generatorkey);

            String t1 = aesEncode(message, secretKey, iv);
            System.out.println("t1:\t" + t1);

            String t3 = aesDecode(t1, secretKey, iv);
            System.out.println("t3:\t" + t3);
            String t4 = aesDecode(t1, generatorkey, iv);
            System.out.println("t4:\t" + t4);

            String t2 = aesEncode(message, generatorkey, iv);

            System.out.println("t2:\t" + t2);

            String t5 = aesDecode(t2, secretKey, iv);
            System.out.println("t3:\t" + t5);
            String t6 = aesDecode(t2, generatorkey, iv);
            System.out.println("t4:\t" + t6);

            i++;
        }
    }

}
