package nhom55.hcmuaf.encrypt;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public interface DigitalSignature {
    void loadMode(Asymmetric.AVAILABLE_SIZE keySize, Hash.ALGORITHM hashAlgorithm);
    void loadKeyPair(KeyPair keyPair);
    String getHashFromSignature(String signature) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;
    String createSignature(String plainText) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException;
    String createSignature(File file) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;
    Hash getHashInstance();
    Asymmetric getAsymmetricInstance();
}
