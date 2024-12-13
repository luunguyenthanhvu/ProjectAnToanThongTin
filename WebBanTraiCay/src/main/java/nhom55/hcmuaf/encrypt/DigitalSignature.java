package nhom55.hcmuaf.encrypt;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public interface DigitalSignature {
    void loadKeyPair(KeyPair keyPair);
    void loadPublicKey(String publicKeyAsString) throws NoSuchAlgorithmException, InvalidKeySpecException;
    String getHashFromSignature(String signature) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;
    String createSignature(String plainText) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException;
    String createSignature(File file) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;
    Hash getHashInstance();
    Asymmetric getAsymmetricInstance();
}
