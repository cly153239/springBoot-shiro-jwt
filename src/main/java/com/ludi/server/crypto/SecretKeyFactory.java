package com.ludi.server.crypto;


import com.ludi.server.pojo.po.KeyPairPo;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

/**
 * @author 陆迪
 * @date 2019/3/18 19:19
 **/
public class SecretKeyFactory {

    private static final String AES_ALGORITHM = "AES";

    private final static String AES_SECRET_KEY_SEED = "dddseee";
    private final static String RSA_SECRET_KEY_SEED = "dddseee";


    private static volatile SecretKeyFactory  secretKeyFactory;

    public static SecretKeyFactory getInstance() {
        if (secretKeyFactory == null) {
            synchronized (SecretKeyFactory.class) {
                if (secretKeyFactory == null) {
                    secretKeyFactory = new SecretKeyFactory();
                }
            }
        }
        return secretKeyFactory;
    }

    private KeyGenerator aesKeyGenerator;

    private SecretKeyFactory() {
        this.aesKeyGenerator = initAESKeyGenerator();
        this.rsaKerPairGenerator = initKeyPairGenerator();
    }

    private KeyGenerator initAESKeyGenerator() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);

            SecureRandom secureRandom = new SecureRandom();
            secureRandom.setSeed(AES_SECRET_KEY_SEED.getBytes());
            keyGenerator.init(secureRandom);

            return keyGenerator;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


    public SecretKey nextAesSecretKey() {
        return aesKeyGenerator.generateKey();
    }


    /** 指定key的大小 */
    private final static int KEY_SIZE = 2048;

    private KeyPairGenerator rsaKerPairGenerator;

    private KeyPairGenerator initKeyPairGenerator() {
        try {

            /** RSA算法要求有一个可信任的随机数源 */
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.setSeed(RSA_SECRET_KEY_SEED.getBytes());
            /** 为RSA算法创建一个KeyPairGenerator对象 */
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
            keyPairGenerator.initialize(KEY_SIZE, secureRandom);

            return keyPairGenerator;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public KeyPair nextRsaKeyPair() {
        return rsaKerPairGenerator.generateKeyPair();
    }

    /**
     * 生成密钥对
     * @return
     */
    public KeyPairPo nextRsaKeyPairPo() {
        KeyPair keyPair = nextRsaKeyPair();
        return SecretKeyFactory.getRsaKeyPairPo(keyPair);
    }

    private static KeyPairPo getRsaKeyPairPo(KeyPair keyPair) {
        /** 得到公钥 */
        Key publicKey = keyPair.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();
        String publicKeyStr = Base64.getEncoder().encodeToString(publicKeyBytes);

        /** 得到私钥 */
        Key privateKey = keyPair.getPrivate();
        byte[] privateKeyBytes = privateKey.getEncoded();
        String privateKeyStr = Base64.getEncoder().encodeToString(privateKeyBytes);

        BigInteger modulus = ((RSAPublicKey) publicKey).getModulus();
        byte[] modulusBytes = modulus.toByteArray();
        String modulusStr = Base64.getEncoder().encodeToString(modulusBytes);

        KeyPairPo keyPairPo;
        keyPairPo = new KeyPairPo(publicKeyStr, privateKeyStr, modulusStr);
        return keyPairPo;
    }

}
