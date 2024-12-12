package nhom55.hcmuaf.encrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class HashImpl implements Hash {
    @Override
    public String hashText(String text, ALGORITHM algorithm) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm.name);
        byte[] hashedBytes = digest.digest(text.getBytes());
        return HexFormat.of().formatHex(hashedBytes);
    }

    @Override
    public String hashFile(File file, ALGORITHM algorithm) {
        try (FileInputStream fis = new FileInputStream(file)) {
            // Tạo đối tượng MessageDigest với thuật toán được chỉ định
            MessageDigest digest = MessageDigest.getInstance(algorithm.name);

            // Đọc file theo từng khối để băm
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }

            // Lấy kết quả băm cuối cùng
            byte[] hashedBytes = digest.digest();

            // Chuyển đổi mảng byte thành chuỗi hex
            return HexFormat.of().formatHex(hashedBytes);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm, e);
        }
    }
}
