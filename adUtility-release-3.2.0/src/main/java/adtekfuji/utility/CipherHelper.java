/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 暗号化・復号化ヘルパークラス
 *
 * @author s-heya
 */
public class CipherHelper {

    private CipherHelper() {
    }

    /**
     * 文字列を16文字の秘密鍵でAES暗号化してBase64した文字列で返す
     *
     * @param originalSource
     * @param secretKey
     * @param algorithm
     * @return
     * @throws java.security.NoSuchAlgorithmException
     * @throws javax.crypto.NoSuchPaddingException
     * @throws javax.crypto.IllegalBlockSizeException
     * @throws java.security.InvalidKeyException
     * @throws javax.crypto.BadPaddingException
     */
    public static String encrypt(String originalSource, String secretKey, String algorithm)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        byte[] originalBytes = originalSource.getBytes();
        byte[] encryptBytes = cipher(Cipher.ENCRYPT_MODE, originalBytes, secretKey, algorithm);
        byte[] encryptBytesBase64 = Base64.getEncoder().encode(encryptBytes);
        return new String(encryptBytesBase64);
    }

    /**
     * Base64されたAES暗号化文字列を元の文字列に復元する
     *
     * @param encryptBytesBase64String
     * @param secretKey
     * @param algorithm
     * @return
     * @throws java.security.NoSuchAlgorithmException
     * @throws javax.crypto.NoSuchPaddingException
     * @throws java.security.InvalidKeyException
     * @throws javax.crypto.IllegalBlockSizeException
     * @throws javax.crypto.BadPaddingException
     */
    public static String decrypt(String encryptBytesBase64String, String secretKey, String algorithm)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        byte[] encryptBytes = Base64.getDecoder().decode(encryptBytesBase64String);
        byte[] originalBytes = cipher(Cipher.DECRYPT_MODE, encryptBytes, secretKey, algorithm);
        return new String(originalBytes);
    }

    /**
     * 暗号化・複合化の共通処理
     */
    private static byte[] cipher(int mode, byte[] source, String secretKey, String algorithm)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

        byte[] secretKeyBytes = secretKey.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(mode, secretKeySpec);
        return cipher.doFinal(source);
    }

    /**
     * AES(CBCモード)で暗号化された文字列を複合化する
     *
     * @param encryptBytesBase64String
     * @param secretKey
     * @param iv
     * @param algorithm
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws UnsupportedEncodingException
     */
    public static String decrypt(byte[] encryptBytesBase64String, byte[] secretKey, byte[] iv, String algorithm)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException {

        byte[] encryptBytes = Base64.getDecoder().decode(encryptBytesBase64String);
        byte[] originalBytes = cipher(Cipher.DECRYPT_MODE, encryptBytes, secretKey, iv, algorithm);
        return new String(originalBytes);
    }

    /**
     * 文字列をAES(CBCモード)を使用して暗号化・複合化する
     */
    private static byte[] cipher(int mode, byte[] source, byte[] secretKey, byte[] iv, String algorithm)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "AES");
        IvParameterSpec ivpSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(mode, secretKeySpec, ivpSpec);
        return cipher.doFinal(source);
    }
}
