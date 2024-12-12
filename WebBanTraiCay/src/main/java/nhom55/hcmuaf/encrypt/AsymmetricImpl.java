package nhom55.hcmuaf.encrypt;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class AsymmetricImpl implements Asymmetric {
    PublicKey publicKey;
    PrivateKey privateKey;

    public Cipher createInstance(int mode, PublicKey key) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        var cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(mode, key);
        return cipher;
    }

    public Cipher createInstance(int mode, PrivateKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        var cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(mode, key);
        return cipher;
    }

    @Override
    public KeyPair generateKeyPair(Asymmetric.AVAILABLE_SIZE constSize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(constSize.size);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    @Override
    public String getPrivateKeyAsString() {
        if (privateKey == null) {
            throw new IllegalStateException("Private key is not initialized");
        }
        return BASE64_ENCODER.encodeToString(privateKey.getEncoded());
    }

    @Override
    public String getPublicKeyAsString() {
        if (publicKey == null) {
            throw new IllegalStateException("Public key is not initialized");
        }
        return BASE64_ENCODER.encodeToString(publicKey.getEncoded());
    }

    @Override
    public void loadPrivateKeyAsString(String privateKeyAsBase64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = BASE64_DECODER.decode(privateKeyAsBase64);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.privateKey = keyFactory.generatePrivate(keySpec);
    }

    @Override
    public void loadPublicKeyAsString(String publicKeyAsBase64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = BASE64_DECODER.decode(publicKeyAsBase64);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.publicKey = keyFactory.generatePublic(keySpec);
    }

    @Override
    public File writePrivateKeyToFile(String filePath) throws IOException {
        if (publicKey == null) {
            throw new IllegalStateException("Private key is not initialized");
        }
        // Mã hóa Public Key sang Base64
        String publicKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        File file = new File(filePath);
        // Ghi vào file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Private key:\n");
            writer.write(publicKeyBase64);
        }
        return file;
    }

    @Override
    public File writePublicKeyToFile(String filePath) throws IOException {
        if (publicKey == null) {
            throw new IllegalStateException("Public key is not initialized");
        }
        // Mã hóa Public Key sang Base64
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        File file = new File(filePath);
        // Ghi vào file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Public key:\n");
            writer.write(publicKeyBase64);
        }
        return file;
    }

    @Override
    public KeyPair getKeyPair() {
        return new KeyPair(publicKey, privateKey);
    }

    @Override
    public String encryptText(String plainText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher;
        if (privateKey == null) throw new IllegalStateException("Private key is not initialized");
        cipher = createInstance(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return BASE64_ENCODER.encodeToString(encryptedBytes);
    }

    @Override
    public String decryptText(String base64Text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher;
        if (publicKey == null) throw new IllegalStateException("Public key is not initialized");
        cipher = createInstance(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedBytes = cipher.doFinal(BASE64_DECODER.decode(base64Text));
        return new String(decryptedBytes);
    }
}
