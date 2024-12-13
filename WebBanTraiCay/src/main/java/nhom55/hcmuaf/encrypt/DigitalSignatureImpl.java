package nhom55.hcmuaf.encrypt;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class DigitalSignatureImpl implements DigitalSignature {
    Asymmetric asymmetric;
    Hash hash;
    Asymmetric.AVAILABLE_SIZE keySize;
    Hash.ALGORITHM hashAlgorithm;

    public DigitalSignatureImpl() {
        this.asymmetric = new AsymmetricImpl();
        this.hash = new HashImpl();
        loadMode(Asymmetric.AVAILABLE_SIZE.KEY_SIZE_4, Hash.ALGORITHM.SHA_512);
    }

    private void loadMode(Asymmetric.AVAILABLE_SIZE keySize, Hash.ALGORITHM hashAlgorithm) {
        this.keySize = keySize;
        this.hashAlgorithm = hashAlgorithm;
    }

    @Override
    public void loadKeyPair(KeyPair keyPair) {
        this.asymmetric.loadKeyPair(keyPair);
    }

    @Override
    public void loadPublicKey(String publicKeyAsString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.asymmetric.loadPublicKeyAsString(publicKeyAsString);
    }

    @Override
    public String getHashFromSignature(String signature) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return this.asymmetric.decryptText(signature);
    }

    @Override
    public String createSignature(String plainText) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        var hashHex = this.hash.hashText(plainText);
        return this.asymmetric.encryptText(hashHex);
    }

    @Override
    public String createSignature(File file) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        var hashHex = this.hash.hashFile(file);
        return this.asymmetric.encryptText(hashHex);
    }

    @Override
    public Hash getHashInstance() {
        return this.hash;
    }

    @Override
    public Asymmetric getAsymmetricInstance() {
        return this.asymmetric;
    }
}
