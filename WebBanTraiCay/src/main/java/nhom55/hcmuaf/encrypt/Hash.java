package nhom55.hcmuaf.encrypt;

import java.io.File;
import java.security.NoSuchAlgorithmException;

public interface Hash {
    enum ALGORITHM {
        MD5("MD5"),
        SHA_1("SHA-1"),
        SHA_256("SHA-256"),
        SHA_384("SHA-384"),
        SHA_512("SHA-512");
        String name;
        ALGORITHM(String name) {
            this.name = name;
        }
    }
    String hashText(String text, Hash.ALGORITHM algorithm) throws NoSuchAlgorithmException;
    String hashFile(File file, Hash.ALGORITHM algorithm);
}
