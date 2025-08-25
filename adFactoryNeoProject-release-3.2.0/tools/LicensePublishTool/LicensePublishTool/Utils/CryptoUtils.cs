using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;

namespace LicensePublishTool.Utils
{
    class CryptoUtils
    {
        // ruby -r securerandom -e "puts SecureRandom.hex(16)" で生成した16進数
        private static byte[] AesKey = { 0x97, 0xa2, 0xd4, 0x26, 0xe2, 0xe8, 0xac, 0x52, 0xe5, 0x7f, 0x5c, 0x0a, 0x1a, 0x48, 0x5a, 0x67 };

        /// <summary>
        /// 文字列をAESで暗号化する
        /// </summary>
        /// <param name="value"></param>
        /// <returns></returns>
        public static string Encrypt(string value, out byte[] iv)
        {
            // AES暗号化サービスプロバイダ
            AesCryptoServiceProvider aes = new AesCryptoServiceProvider();
            aes.BlockSize = 128;
            aes.KeySize = 128;

            aes.GenerateIV();
            iv = aes.IV;

            aes.Key = AesKey;
            aes.Mode = CipherMode.CBC;
            aes.Padding = PaddingMode.PKCS7;

            // 文字列をバイト型配列に変換
            byte[] src = Encoding.GetEncoding("ISO-8859-1").GetBytes(value);

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
        public static string Decrypt(string value, byte[] iv)
        {
            AesCryptoServiceProvider aes = new AesCryptoServiceProvider();
            aes.BlockSize = 128;
            aes.KeySize = 128;
            aes.IV = iv;
            aes.Key = AesKey;
            aes.Mode = CipherMode.CBC;
            aes.Padding = PaddingMode.PKCS7;
            
            // Base64形式の文字列からバイト型配列に変換
            byte[] src = System.Convert.FromBase64String(value);

            // 複号化する
            using (ICryptoTransform decrypt = aes.CreateDecryptor())
            {
                byte[] dest = decrypt.TransformFinalBlock(src, 0, src.Length);
                return Encoding.GetEncoding("ISO-8859-1").GetString(dest);
            }
        }
    }
}
