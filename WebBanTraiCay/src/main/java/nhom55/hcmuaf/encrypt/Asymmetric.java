package nhom55.hcmuaf.encrypt;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public interface Asymmetric {
    enum AVAILABLE_SIZE {
        KEY_SIZE_1(1024), KEY_SIZE_2(2048),
        KEY_SIZE_3(3072), KEY_SIZE_4(4096),
        ;
        int size;
        AVAILABLE_SIZE(int size) {
            this.size = size;
        }
    }
    Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
    Base64.Decoder BASE64_DECODER = Base64.getDecoder();
    KeyPair generateKeyPair(Asymmetric.AVAILABLE_SIZE size) throws NoSuchAlgorithmException;
    String getPrivateKeyAsString();
    String getPublicKeyAsString();
    void loadPrivateKeyAsString(String privateKeyAsString) throws NoSuchAlgorithmException, InvalidKeySpecException;
    void loadPublicKeyAsString(String publicKeyAsString) throws NoSuchAlgorithmException, InvalidKeySpecException;
    void loadKeyPair(KeyPair keyPair);
    File writePrivateKeyToFile(String filePath) throws IOException;
    File writePublicKeyToFile(String filePath) throws IOException;
    KeyPair getKeyPair();
    String encryptText(String plainText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException;
    String decryptText(String base64Text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException;
}
