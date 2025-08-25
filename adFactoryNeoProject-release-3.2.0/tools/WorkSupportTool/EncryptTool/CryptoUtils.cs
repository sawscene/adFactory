using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;

namespace WorkSupportTool.Utils
{
    class CryptoUtils
    {
        private static byte[] AesIV = { 0x77, 0x31, 0x95, 0x4D, 0x97, 0x7E, 0xCC, 0x3D, 0x90, 0x82, 0x60, 0x23, 0x5F, 0xDB, 0x21, 0x4C };
        private static byte[] AesKey = { 0x66, 0x00, 0x74, 0x00, 0x70, 0x00, 0x77, 0x00, 0x6B, 0x00, 0x31, 0x00, 0x00, 0x00, 0x00, 0x00 };

        /// <summary>
        /// 文字列をAESで暗号化する
        /// </summary>
        /// <param name="value"></param>
        /// <returns></returns>
        public static string Encrypt(string value)
        {
            // AES暗号化サービスプロバイダ
            AesCryptoServiceProvider aes = new AesCryptoServiceProvider();
            aes.BlockSize = 128;
            aes.KeySize = 128;
            aes.IV = AesIV;
            aes.Key = AesKey;
            aes.Mode = CipherMode.CBC;
            aes.Padding = PaddingMode.PKCS7;

            // 文字列をバイト型配列に変換
            byte[] src = Encoding.Unicode.GetBytes(value);

            // 暗号化する
            using (ICryptoTransform encrypt = aes.CreateEncryptor())
            {
                byte[] dest = encrypt.TransformFinalBlock(src, 0, src.Length);

                // バイト型配列からBase64形式の文字列に変換
                return Convert.ToBase64String(dest);
            }
        }
        
        /// <summary>
        /// 文字列をAESで複合化する
        /// </summary>
        /// <param name="value"></param>
        /// <returns></returns>
        public static string Decrypt(string value)
        {
            AesCryptoServiceProvider aes = new AesCryptoServiceProvider();
            aes.BlockSize = 128;
            aes.KeySize = 128;
            aes.IV = AesIV;
            aes.Key = AesKey;
            aes.Mode = CipherMode.CBC;
            aes.Padding = PaddingMode.PKCS7;
            
            // Base64形式の文字列からバイト型配列に変換
            byte[] src = System.Convert.FromBase64String(value);

            // 複号化する
            using (ICryptoTransform decrypt = aes.CreateDecryptor())
            {
                byte[] dest = decrypt.TransformFinalBlock(src, 0, src.Length);
                return Encoding.Unicode.GetString(dest);
            }
        }
    }
}
