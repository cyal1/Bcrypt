package burp;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;




public class CryptUtils {
        public static String AESEncrypt(int cipherMode, String cipherTransformation, byte[] secret, String text, byte[] iv) {
            try {
                Cipher cipher = Cipher.getInstance(cipherTransformation);
                SecretKeySpec keySpec = new SecretKeySpec(secret, "AES");
                if (null != iv) {
                    cipher.init(cipherMode, keySpec, new IvParameterSpec(iv));
                } else {
                    cipher.init(cipherMode, keySpec);
                }
                if (cipherMode == Cipher.ENCRYPT_MODE){
                    return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes()));
                }
                return new String(cipher.doFinal(Base64.getDecoder().decode(text)));
            } catch (Exception ex) {
                return ex.toString();
            }
        }
}
