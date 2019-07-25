package com.test.testEnc;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

@Service
public class AesEncryption {

//    private final SecretCache secretCache = new SecretCache();

    private static final String TOKEN = "passwd";
    private String salt;
    private int pwdIterations = 65536;
    private int keySize = 256;
    private byte[] ivBytes;
    private String keyAlgorithm = "AES";
    private String encryptAlgorithm = "AES/CBC/PKCS5Padding";
    private String secretKeyFactoryAlgorithm = "PBKDF2WithHmacSHA1";

    public AesEncryption() {
        this.salt = getSalt();
    }

    private String getSalt() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        String text = new String(bytes);
        return text;
    }

    /**
     * @param plainText
     * @return encrypted text
     * @throws Exception
     */
    public String encyrpt(String plainText) throws Exception {
        //generate key
        byte[] saltBytes = salt.getBytes("UTF-8");

        SecretKeyFactory skf = SecretKeyFactory.getInstance(this.secretKeyFactoryAlgorithm);
        PBEKeySpec spec = new PBEKeySpec(TOKEN.toCharArray(), saltBytes, this.pwdIterations, this.keySize);
        SecretKey secretKey = skf.generateSecret(spec);
        SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(), keyAlgorithm);

        //AES initialization
        Cipher cipher = Cipher.getInstance(encryptAlgorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        //generate IV
        this.ivBytes = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
        byte[] encryptedText = cipher.doFinal(plainText.getBytes("UTF-8"));
        return new Base64().encodeAsString(encryptedText);
    }

    /**
     * @param encryptText
     * @return decrypted text
     * @throws Exception
     */
    public String decrypt(String encryptText) throws Exception {
        byte[] saltBytes = salt.getBytes("UTF-8");
        byte[] encryptTextBytes = new Base64().decode(encryptText);

        SecretKeyFactory skf = SecretKeyFactory.getInstance(this.secretKeyFactoryAlgorithm);
        PBEKeySpec spec = new PBEKeySpec(TOKEN.toCharArray(), saltBytes, this.pwdIterations, this.keySize);
        SecretKey secretKey = skf.generateSecret(spec);
        SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(), keyAlgorithm);

        //decrypt the message
        Cipher cipher = Cipher.getInstance(encryptAlgorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivBytes));

        byte[] decyrptTextBytes = null;
        try {
            decyrptTextBytes = cipher.doFinal(encryptTextBytes);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        String text = new String(decyrptTextBytes);
        return text;
    }
}