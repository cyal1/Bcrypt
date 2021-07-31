package burp;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class CryptUtils {
        public static byte[] AESEncrypt(int cipherMode, String cipherTransformation, byte[] secret, byte[] text, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
                Cipher cipher = Cipher.getInstance(cipherTransformation);
                SecretKeySpec keySpec = new SecretKeySpec(secret, "AES");
                if (null != iv) {
                    cipher.init(cipherMode, keySpec, new IvParameterSpec(iv));
                } else {
                    cipher.init(cipherMode, keySpec);
                }
                return cipher.doFinal(text);
        }
}
