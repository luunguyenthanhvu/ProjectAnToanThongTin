package nhom55.hcmuaf.services;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
import nhom55.hcmuaf.sendmail.MailProperties;
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

    // Get bill details by id
    var bill = billDao.getABill(dto.getIdBill());
    System.out.println(bill);

    // Get the public key used to sign the bill
    UserPublicKeyDAOImpl userPublicKeyDAO = new UserPublicKeyDAOImpl();
    UserPublicKey userPublicKey = userPublicKeyDAO.getUserPublicKey(bill.getUserId());
    PublicKey publicKey = userPublicKeyDAO.getPublicKey(userPublicKey.getIdPublicKey());
    System.out.println("Public key: " + publicKey);

    // Generate the hash of the bill JSON
    String billJson = MyUtils.convertBillsJson(bill);
    System.out.println("Bill JSON: " + billJson);
    String hashBill = hash.hashText(billJson);
    System.out.println("Hash after first hashing: " + hashBill);
    String hash2 = hash.hashText(hashBill);
    System.out.println("Hash after second hashing: " + hash2);

    // Initialize DigitalSignature object and attempt to verify the signature
    DigitalSignature digitalSignature = new DigitalSignatureImpl();
    digitalSignature.loadPublicKey(publicKey.getKey());
    System.out.println("Public key being used for verification: " + publicKey.getKey());

    // Declare variable for decrypted signature hash
    String decryptedSignatureHash = null;

    try {
      // Try to get the decrypted signature hash with the current public key
      decryptedSignatureHash = digitalSignature.getHashFromSignature(bill.getSignature());
      System.out.println("Decrypted hash from signature: " + decryptedSignatureHash);

      // If hashes match, verification is successful
      if (hash2.equals(decryptedSignatureHash)) {
        return MessageResponseDTO.builder().message("Verify Success!").build();
      } else {
        updateBillWrongSignature(bill);
        throw new MyHandleException("Signature mismatch with the current public key", 500);
      }
    } catch (BadPaddingException e) {
      // If there's a BadPaddingException (signature doesn't match), try using the previous public key
      System.out.println("BadPaddingException caught: Trying with the previous public key");

      // Attempt to get the previous public key before the bill creation time
      try {
        PublicKey publicKeyBefore = publicKeyDAO.getLatestPublicKeyBefore(bill.getUserId(),
            bill.getCreationTime());
        digitalSignature.loadPublicKey(publicKeyBefore.getKey());
        decryptedSignatureHash = digitalSignature.getHashFromSignature(bill.getSignature());

        // If the decrypted signature matches with the previous public key, verify successfully
        if (hash2.equals(decryptedSignatureHash)) {
          return MessageResponseDTO.builder().message("Verify Success!")
              .build();
        } else {
          updateBillWrongSignature(bill);
          throw new MyHandleException("Invalid signature: Bill is not valid", 500);
        }
      } catch (BadPaddingException ex) {
        // If both public keys fail, throw exception
        updateBillWrongSignature(bill);
        throw new MyHandleException("Unable to verify signature with any public key", 500);
      }
    }
  }

  private void updateBillWrongSignature(Bills bills) {
    // if signature wrong send email + update status
    BillDao billDao = new BillDaoImpl();
    billDao.updateStatusABill(bills.getId(), "Đã hủy");

    Properties properties = MailProperties.getSMTPPro();
    Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(MailProperties.getEmail(), MailProperties.getPassword());
      }
    });

    // send email
    try {
      Message message = new MimeMessage(session);
      message.addHeader("Content-type", "text/HTML; charset= UTF-8");
      message.setFrom(new InternetAddress(MailProperties.getEmail()));
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(bills.getEmail()));
      message.setSubject("Đơn hàng bị hủy");
      message.setText(
          "Vì chính sách bảo mật của công ty. Chúng tôi sẽ hủy đơn hàng của bạn. 🐧🐧🐧🐧🐧");
      Transport.send(message);
    } catch (Exception e) {
      System.out.println("SendEmail File Error " + e);
    }
  }

}
