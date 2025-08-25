/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import jakarta.xml.bind.DatatypeConverter;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author ke.yokoi
 */
public class PasswordEncoder {
    
    private final String ENCRYPT_KEY = "3F-/qbLPhvq3U~cN";
    private final String ENCRYPT_IV = "xrgtK+c358RzE#)%";

    public String encode(String password) {
        String returnValue = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes());
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                String tmp = Integer.toHexString(digest[i] & 0xff);
                if (tmp.length() == 1) {
                    builder.append('0').append(tmp);
                } else {
                    builder.append(tmp);
                }
            }
            returnValue = builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            LogManager.getLogger().fatal(ex);
        }
        return returnValue;
    }
    
    /**
     * AES方式で暗号化
     *
     * @param password 暗号化対象の文字列
     * @return　暗号化した文字列
     */
    public String encodeAES(String password) {
        String returnValue = "";
        try {
            // 文字列をバイト配列へ変換
            byte[] bytePassword = password.getBytes("UTF-8");

            // 暗号化キーと初期化ベクトルをバイト配列へ変換
            byte[] byteKey = ENCRYPT_KEY.getBytes("UTF-8");
            byte[] byteIv = ENCRYPT_IV.getBytes("UTF-8");

            // 暗号化キーと初期化ベクトルのオブジェクト生成
            SecretKeySpec key = new SecretKeySpec(byteKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(byteIv);

            // Cipherオブジェクト生成
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            // 暗号化
            byte[] byteResult = cipher.doFinal(bytePassword);

            returnValue = DatatypeConverter.printHexBinary(byteResult);

        } catch (UnsupportedEncodingException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
            LogManager.getLogger().fatal(ex);
        }
        return returnValue;
    }
    
    /**
     * AES方式で復号化
     *
     * @param password 復号化対象の文字列
     * @return　復号化した文字列
     */
    public String decodeAES(String password) {
        // 変数初期化
        String strResult = null;

        try {
            // 暗号化キーと初期化ベクトルをバイト配列へ変換
            byte[] byteKey = ENCRYPT_KEY.getBytes("UTF-8");
            byte[] byteIv = ENCRYPT_IV.getBytes("UTF-8");

            // 復号化キーと初期化ベクトルのオブジェクト生成
            SecretKeySpec key = new SecretKeySpec(byteKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(byteIv);

            // Cipherオブジェクト生成
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            // 復号化
            byte[] byteResult = cipher.doFinal(DatatypeConverter.parseHexBinary(password));

            // バイト配列を文字列へ変換
            strResult = new String(byteResult, "UTF-8");

        } catch (UnsupportedEncodingException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
            LogManager.getLogger().fatal(ex);
        }

        // 復号化文字列を返却
        return strResult;

    }
}
