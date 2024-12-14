package nhom55.hcmuaf.services;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import nhom55.hcmuaf.beans.Bills;
import nhom55.hcmuaf.beans.PublicKey;
import nhom55.hcmuaf.beans.UserPublicKey;
import nhom55.hcmuaf.dao.BillDao;
import nhom55.hcmuaf.dao.LogDao;
import nhom55.hcmuaf.dao.PublicKeyDAO;
import nhom55.hcmuaf.dao.UserPublicKeyDAO;
import nhom55.hcmuaf.dao.daoimpl.BillDaoImpl;
import nhom55.hcmuaf.dao.daoimpl.LogDaoImpl;
import nhom55.hcmuaf.dao.daoimpl.PublicKeyDAOImpl;
import nhom55.hcmuaf.dao.daoimpl.UserPublicKeyDAOImpl;
import nhom55.hcmuaf.dto.request.VerifyUserBillRequestDTO;
import nhom55.hcmuaf.dto.response.MessageResponseDTO;
import nhom55.hcmuaf.encrypt.Asymmetric;
import nhom55.hcmuaf.encrypt.AsymmetricImpl;
import nhom55.hcmuaf.encrypt.DigitalSignature;
import nhom55.hcmuaf.encrypt.DigitalSignatureImpl;
import nhom55.hcmuaf.encrypt.Hash;
import nhom55.hcmuaf.encrypt.HashImpl;
import nhom55.hcmuaf.log.AbsDAO;
import nhom55.hcmuaf.my_handle_exception.MyHandleException;
import nhom55.hcmuaf.util.MyUtils;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillService extends AbsDAO {

  BillDao billDao;
  Hash hash;
  Asymmetric asymmetric;
  DigitalSignature digitalSignature;
  UserPublicKeyDAO userPublicKeyDAO;
  PublicKeyDAO publicKeyDAO;
  LogDao logDao;
  private static BillService instance;

  public static BillService getInstance() {
    if (instance == null) {
      instance = new BillService();
    }
    return instance;
  }

  public BillService() {
    billDao = new BillDaoImpl();
    hash = new HashImpl();
    asymmetric = new AsymmetricImpl();
    digitalSignature = new DigitalSignatureImpl();
    userPublicKeyDAO = new UserPublicKeyDAOImpl();
    publicKeyDAO = new PublicKeyDAOImpl();
    logDao = new LogDaoImpl();
  }

  /**
   * This method use for check user signature in bill
   *
   * @param requestDTO
   * @return
   */
  public MessageResponseDTO checkVerifyUserBill(String requestDTO, HttpServletRequest request)
      throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException, MyHandleException {
    // Convert request dto to entity
    VerifyUserBillRequestDTO dto = MyUtils.convertJsonToObject(requestDTO,
        VerifyUserBillRequestDTO.class);

    // get signature by dto id
    var bill = billDao.getABill(dto.getIdBill());
    if (bill.getSignature() == null || bill.getSignature().isEmpty()) {
      setUpDefaultData(bill, request);
      return null;
    }
    // Check bill and check key used when sign the bill
    // Lấy log check xem public key nào đang được sử dụng để ký đơn này
    // Get userPublic key in used
    UserPublicKey userPublicKey = userPublicKeyDAO.getUserPublicKey(bill.getUserId());
    PublicKey publicKey = publicKeyDAO.getPublicKey(userPublicKey.getIdPublicKey());
    System.out.println("public key n" + publicKey);
    // the bill json
    String billJson = MyUtils.convertBillsJson(bill);
    System.out.println("bill json" + billJson);
    String hashBill = hash.hashText(billJson);
    hashBill = hash.hashText(hashBill);
    System.out.println("hashbill ne " + hashBill);
    asymmetric.loadPublicKeyAsString(publicKey.getKey());
    String decryptedSignatureHash = asymmetric.decryptText(bill.getSignature());
    System.out.println("hash sau khi giai hoa" + decryptedSignatureHash);
    System.out.println();
    System.out.println(hashBill);
    // Compare the hash and decrypted hash
    if (!hashBill.equals(decryptedSignatureHash)) {
      System.out.println("signature khong dung co le da sai key bi report");
      // IF not match check the public key create before create bill
      PublicKey publicKeyBefore = publicKeyDAO.getLatestPublicKeyBefore(bill.getUserId(),
          bill.getCreationTime());
      asymmetric.loadPublicKeyAsString(publicKeyBefore.getKey());
      decryptedSignatureHash = asymmetric.decryptText(bill.getSignature());
      if (hashBill.equals(decryptedSignatureHash)) {
        return MessageResponseDTO.builder().message("Verify Success!").build();
      }
      throw new MyHandleException("Don hang khong hop le", 500);
    }
    return MessageResponseDTO.builder().message("Verify Success!").build();
  }

  private void setUpDefaultData(Bills bill, HttpServletRequest request)
      throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
// the bill json
    String billJson = MyUtils.convertBillsJson(bill);
    String hashBill = hash.hashText(billJson);
    System.out.println("thử hash");
    System.out.println(hashBill);
    System.out.println("billl id:" + bill.getId());

//    asymmetric.loadPublicKeyAsString(
//        "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAkqswY/JOlcx8Ubaht7Z6MUPt4SKy/MVyd50EBqv9CTzMPKPR7uK0I3vETO6po9Wq3RygIycOn7UQ6Ycb1k9SdqcUBmap8vNaGf78EgCtiYeh+hF/NoMIBpvadT3JQ2ZgNrPvBlMG8d3cD1saHuLfReFWq8x59WLPOPa2HjKd82u/Mmlc+wM3lTIFVH0NWXaGHOniWEbixaJXivaHPRpMMI/q741tsrGwwKsWYeZUX9BBEGkOhePbUXCwIUImSfeD/VQUDwWn3fg6EWg0nVBVMoyEjAfOK9F+UAjApX4PgXcHN/F7AYmp0Cp83Td7SHz/rpmIunCYjAjugYIP1MxzwnbUWjRJYVLIz+wTtPjvNC7I7ccCW7A03s8pGIy+ItlLOmKKiHaq2RtzNXbEmaTGc0LrEE/96PjEcEypSIRqOT3ku0pB7dlL4QanmMyQzQkXlYFFbLD/wHG43imU7UhgOShp0tcb/4V5kOtwDvXNC/mpDAgOl2GqqewtwTCcUyOdfOBTqrIsaIcPgpbgN9VVd8PqBJMRTC7PQmDN5T2rAPS+leH0JVHJ1s2gwI0P9t9Jd3UDADft0yotBev4M03KocpO3mZ2s0TFaf27AM+S/2vF94kV+T6xjjeywnE31zdZdIcFzP3aGSeARyie+sdYPhdmAS/fB7uz2m4geoQ5SbkCAwEAAQ==");
//    asymmetric.loadPrivateKeyAsString(
//        "MIIJQgIBADANBgkqhkiG9w0BAQEFAASCCSwwggkoAgEAAoICAQCSqzBj8k6VzHxRtqG3tnoxQ+3hIrL8xXJ3nQQGq/0JPMw8o9Hu4rQje8RM7qmj1ardHKAjJw6ftRDphxvWT1J2pxQGZqny81oZ/vwSAK2Jh6H6EX82gwgGm9p1PclDZmA2s+8GUwbx3dwPWxoe4t9F4VarzHn1Ys849rYeMp3za78yaVz7AzeVMgVUfQ1ZdoYc6eJYRuLFoleK9oc9Gkwwj+rvjW2ysbDAqxZh5lRf0EEQaQ6F49tRcLAhQiZJ94P9VBQPBafd+DoRaDSdUFUyjISMB84r0X5QCMClfg+Bdwc38XsBianQKnzdN3tIfP+umYi6cJiMCO6Bgg/UzHPCdtRaNElhUsjP7BO0+O80LsjtxwJbsDTezykYjL4i2Us6YoqIdqrZG3M1dsSZpMZzQusQT/3o+MRwTKlIhGo5PeS7SkHt2UvhBqeYzJDNCReVgUVssP/AcbjeKZTtSGA5KGnS1xv/hXmQ63AO9c0L+akMCA6XYaqp7C3BMJxTI5184FOqsixohw+CluA31VV3w+oEkxFMLs9CYM3lPasA9L6V4fQlUcnWzaDAjQ/230l3dQMAN+3TKi0F6/gzTcqhyk7eZnazRMVp/bsAz5L/a8X3iRX5PrGON7LCcTfXN1l0hwXM/doZJ4BHKJ76x1g+F2YBL98Hu7PabiB6hDlJuQIDAQABAoICABvJ5Yw6P5twN+m/gZpuL26gITTzvxfXTR3OmRGuim2yoDupCkCJoXnLnGC7I1cQR6RHJSwj3UhzJPYqDCMpukUS2N9xmieW5e5g+l9q03ZoRU88+25XmzCtXNGz2Cs1/Wq/4ry/BGTVFTmvmB5b+9Ogoyw/vKe6heqUC+Ni0bxqILqrbdt9O43AAXSFkP7vTu+xfJofLB4n6RBCL1NrZR4aVnf2wt8FfaizgbUgz8GrknhKf17s0gP3JtfWUazYAaEAyiAigTC0NERSCw33R30hVIe3ft+cfKMrVVzFqqhr5ek6joS3EX6IXkoHlgsg68yn+FjitnRpyM6u1tMJvbDEIOlTY64tK9ph8VpUGSSK3wUTmc+ZAjepZToaOSBh23v/J0XVkLziAZXoSQBm5GERZPh4aWrKQtBnF15G176f6+1xBHy4yyZ1Ciedm8OvHcYEM8wEPnrGq7ojnHDQzu18nYSAlcDM1tmLtjPYZNSElBcWCcD5tGIkJQJEapLc3upp/sPhXMXCE55GwHc36VFLHEEVU3JYemkgLiAiYQmSFAg/qosDYqkEpUcmYaRCot9yWDoUNlBymWJfgObMAXnc8fQGkEM7B4w7oTqW7JB4V9Ic5bdYffaFQF7bPkE/1h51tzgnyHFcUYZ1EGFJkL7SvDoDlg6d1sNFg5r7TkRRAoIBAQC8qJ3qvqneq3eaHldt+nXKLroizzBibx6yrDgoNIz0xQ5q4Rz6Bg1t2N9dkVzqHfIA0RKTylOHftyTyKi+CXw2k83twqoOvZqIuWIt3tQPozNBuBgbZeGUFSEYlcsIizW6PAbSsBoT/mH2hqlfxoT7Wn2ymw64+CUHG22QE32M4VhaEWZ9/6hBemEpYQAWAblYUZX4505pC1ywYTqW+Nt2FH9evpjFzogXv68NDZhA8GX7jrohel/dZbu04uRDPeL/8FmjqdhVNCsPl9KnC5l+X59kFNXbIGDdf+c8kkmmVshjzfJF5L6Q66LEwXfsoid2ofe2+2qZy18yK+6h+C3NAoIBAQDHBZW6O9Mf35YClr+2DmYHVy1htnQVzSigVcwYruO+DkRSzbSzaTliy4z2cSK2ej3N7K2Rm/vKl6Oj73XwWntZm32ziwrcuM2HVv7bKB84os9vkpqT8QSM5LoXMLqvf5y0sEdi/GRj0cQ2IM21rdsaFf16Eflxzsm+jl67F6ROJbyOJsQbwD3qDm7gtaBZh8wZ4R+LiSUhuLsk8izWIb8rtc/tLy9cIvdPI5pxybN+GbCxuXSR2wlkiFjbAKT5bqBvCkdP2uZKKZ6xKerh2duMdAEAs50bVvmz3SEFva9q4dgd59fFM+cOWLak/1YTWGBn7IikHwxSTwxUiHuo/v+dAoIBAEN1X5Iyvvb6BPg85TJUHo4/CxurTjfEWAu3fRUTRVu4oqEhAYV9aIvgwm3ornqgXopgN+5UqG2ItUK6u2vIZHv3eekMdFZ1Aa7l6u9QAC/fQVK8ixIw6epTa9Qoq8LQVFPVZvMrOQfxLAJEwIeYqrBd+RhGTYtiX4sdCZ48+eXIJhl+o1Fmezl+kwOCxc8Yqrej5KZtvyHciDYhbhvRvFeKmMi+Ck6gvRUQ230lYU3HKBA9Rtz1rxWxbN8PSmOo22R9AvB8qPg+gS9AMHi+sxH3Cz10HcwO1dQeCgckn+U0ns7hM0I+us2DFF6ad1FL4iSPYc4yNDnt+IouH1QXM/kCggEBAJljVoiYPxmbqvQH122rHzudFt7uTf3Xhu478fc8I06BlGr1G8YC3TWlh9kQTR6yjBu/w+4reB4mTPq2PgGm2tavHmxYR2DiFZWvmPCztYxi/tkQ9o36/fTQ/BOPfPJxugcQrZQ+zeiY7U6CbckOW4V5WUjRvZzXMWnqNFyh1gJc1zv/6LkyqwVPZmlN0m3uWYHAnh1e7/1TcPkCpUfL7pjzoXM+IJQ0ZsLZv9jfCbWwexwewRcA5/ZRl5oCnyOW/ZNXKqsDHOYfuaz1j4/N5oYTigpOZFGD8SOM1DymeyI3Oeeingvg+r115h64pFB4lqTCCnXjcA2n2urz3crb7YkCggEAYDq3nvygELFxGkK29jKjG+A9GFsggmuLgtXUU8KOKOpJn213fVTDhCx5Y0jNAtYq2eAPdzVvk/v1OIsBaZn3enPOneWTKGEaReazQ6jZoV9LEXa1UqjHcvZL2WhefJOyo8D4X86bENwEKSX107C7woIZ/y5o9LLmT9fYWqVWq20kkyeynsFNyRHOa0v5y+8I4CUIZ5Q6R1e4l9dghFhckEQ5BhdSx8CPn+QyUJ7kUuNVnx9q9389eTwYJQqG/879eV9r4IeH+SV9H0kg66uvhAhNQc0u7F/A4T3ZgpeSRbiBZ9o5AEc1e4Ziy2JM+4ztypNZoT44+/sJFlfHSn5mEA==");
//
//    digitalSignature.loadPublicKey(
//        "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAkqswY/JOlcx8Ubaht7Z6MUPt4SKy/MVyd50EBqv9CTzMPKPR7uK0I3vETO6po9Wq3RygIycOn7UQ6Ycb1k9SdqcUBmap8vNaGf78EgCtiYeh+hF/NoMIBpvadT3JQ2ZgNrPvBlMG8d3cD1saHuLfReFWq8x59WLPOPa2HjKd82u/Mmlc+wM3lTIFVH0NWXaGHOniWEbixaJXivaHPRpMMI/q741tsrGwwKsWYeZUX9BBEGkOhePbUXCwIUImSfeD/VQUDwWn3fg6EWg0nVBVMoyEjAfOK9F+UAjApX4PgXcHN/F7AYmp0Cp83Td7SHz/rpmIunCYjAjugYIP1MxzwnbUWjRJYVLIz+wTtPjvNC7I7ccCW7A03s8pGIy+ItlLOmKKiHaq2RtzNXbEmaTGc0LrEE/96PjEcEypSIRqOT3ku0pB7dlL4QanmMyQzQkXlYFFbLD/wHG43imU7UhgOShp0tcb/4V5kOtwDvXNC/mpDAgOl2GqqewtwTCcUyOdfOBTqrIsaIcPgpbgN9VVd8PqBJMRTC7PQmDN5T2rAPS+leH0JVHJ1s2gwI0P9t9Jd3UDADft0yotBev4M03KocpO3mZ2s0TFaf27AM+S/2vF94kV+T6xjjeywnE31zdZdIcFzP3aGSeARyie+sdYPhdmAS/fB7uz2m4geoQ5SbkCAwEAAQ==");
////    System.out.println("Private key: " + asymmetric.getPrivateKeyAsString());
////    System.out.println("Public key: " + asymmetric.getPublicKeyAsString());
////    System.out.println("Signature bill " + digitalSignature.createSignature(billJson));
////    System.out.println();
////    System.out.println(hashBill);
////    System.out.println(bill.getSignature());
//
//    // insert new userKey
//    PublicKey publicKey = PublicKey
//        .builder()
//        .key(asymmetric.getPublicKeyAsString())
//        .createDate(LocalDateTime.now())
//        .build();
//
//    int publicKeyId = publicKeyDAO.insertPublicKey(publicKey);
//    System.out.println("public key sau khi insert: " + publicKey);
//    publicKey.setId(publicKeyId);
//
//    // after insert write log
//    RequestInfo requestInfo = new RequestInfo(request.getRemoteAddr(), "HCM", "VietNam");
//    Log<PublicKey> publicKeyLog = new Log<>();
//    publicKeyLog.setNote(String.valueOf(LogNote.INSERT_PUBLIC_KEY.getLevel()));
//    publicKeyLog.setLevel(LogLevels.INFO);
//    publicKeyLog.setPreValue("");
//    publicKeyLog.setCurrentValue(MyUtils.convertToJson(publicKey));
//    publicKeyLog.setCreateAt(LocalDateTime.now());
//    publicKeyLog.setUpdateAt(null);
//    publicKeyLog.setIp(requestInfo.getIp());
//    publicKeyLog.setAddress(requestInfo.getAddress());
//    publicKeyLog.setNational(requestInfo.getNation());
//    super.insert(publicKeyLog);
//
//    // insert userPublic key
//    UserPublicKey userPublicKey = new UserPublicKey();
//    userPublicKey.setIdPublicKey(publicKeyId);
//    userPublicKey.setIdUser(bill.getUserId());
//    userPublicKey.setStatus(PublicKeyStatus.IN_USE);
//    int userPublicKeyId = userPublicKeyDAO.insertUserPublicKey(userPublicKey);
//    userPublicKey.setId(userPublicKeyId);
//
//    System.out.println("user public key sau khi insert");
//
//    // after insert write log
//    Log<UserPublicKey> userPublicKeyLog = new Log<>();
//    userPublicKeyLog.setNote(String.valueOf(LogNote.USER_CREATE_PUBLIC_KEY.getLevel()));
//    userPublicKeyLog.setLevel(LogLevels.INFO);
//    userPublicKeyLog.setPreValue("");
//    userPublicKeyLog.setCurrentValue(MyUtils.convertToJson(userPublicKey));
//    userPublicKeyLog.setCreateAt(LocalDateTime.now());
//    userPublicKeyLog.setUpdateAt(null);
//    userPublicKeyLog.setIp(requestInfo.getIp());
//    userPublicKeyLog.setAddress(requestInfo.getAddress());
//    userPublicKeyLog.setNational(requestInfo.getNation());
//    super.insert(userPublicKeyLog);
//    System.out.println();
//
//    // update the signature
//    System.out.println("bill sau khi update");
//    billDao.updateSignatureABill(bill.getId(), digitalSignature.createSignature(billJson));
//    System.out.println(billDao.getABill(bill.getId()));
  }

}
