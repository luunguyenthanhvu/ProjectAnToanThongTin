package nhom55.hcmuaf.services;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
import nhom55.hcmuaf.enums.LogLevels;
import nhom55.hcmuaf.enums.PublicKeyStatus;
import nhom55.hcmuaf.log.AbsDAO;
import nhom55.hcmuaf.log.Log;
import nhom55.hcmuaf.util.MyUtils;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillService {

  BillDao billDao;
  Hash hash;
  Asymmetric asymmetric;
  DigitalSignature digitalSignature;
  UserPublicKeyDAO userPublicKeyDAO;
  PublicKeyDAO publicKeyDAO;
  LogDao logDao;
  AbsDAO absDAO;

  public BillService() {
    billDao = new BillDaoImpl();
    hash = new HashImpl();
    asymmetric = new AsymmetricImpl();
    digitalSignature = new DigitalSignatureImpl();
    userPublicKeyDAO = new UserPublicKeyDAOImpl();
    publicKeyDAO = new PublicKeyDAOImpl();
    logDao = new LogDaoImpl();
    absDAO = AbsDAO.getInstance();
  }

  /**
   * This method use for check user signature in bill
   *
   * @param requestDTO
   * @return
   */
  public MessageResponseDTO checkVerifyUserBill(String requestDTO)
      throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
    // Convert request dto to entity
    VerifyUserBillRequestDTO dto = MyUtils.convertJsonToObject(requestDTO,
        VerifyUserBillRequestDTO.class);

    // get signature by dto id
    var bill = billDao.getABill(dto.getIdBill());

    // check if bill has signature
    if (bill.getSignature() == null) {
      // generate default data
      setUpDefaultData(bill);
    }

    // the bill json
    String billJson = MyUtils.convertBillsJson(bill);
    String hashBill = hash.hashText(billJson);
    System.out.println("thử hash");
    System.out.println("billl id:" + bill.getId());
    KeyPair keyPair = asymmetric.generateKeyPair();
    System.out.println("Private key: " + asymmetric.getPrivateKeyAsString());
    System.out.println("Public key: " + asymmetric.getPublicKeyAsString());
    System.out.println("Signature bill " + digitalSignature.createSignature(billJson));
    System.out.println();
    System.out.println(hashBill);
    System.out.println(bill.getSignature());

    return MessageResponseDTO.builder().message("Verify Success!").build();
  }

  private void setUpDefaultData(Bills bill)
      throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
// the bill json
    String billJson = MyUtils.convertBillsJson(bill);
    String hashBill = hash.hashText(billJson);
    System.out.println("thử hash");
    System.out.println("billl id:" + bill.getId());
    KeyPair keyPair = asymmetric.generateKeyPair();
    System.out.println("Private key: " + asymmetric.getPrivateKeyAsString());
    System.out.println("Public key: " + asymmetric.getPublicKeyAsString());
    System.out.println("Signature bill " + digitalSignature.createSignature(billJson));
    System.out.println();
    System.out.println(hashBill);
    System.out.println(bill.getSignature());

    // insert new userKey
    PublicKey publicKey = PublicKey
        .builder()
        .key(asymmetric.getPublicKeyAsString())
        .build();

    int publicKeyId = publicKeyDAO.insertPublicKey(publicKey);

    // after insert write log
    Log<PublicKey> publicKeyLog = new Log<>();

    // insert userPublic key
    UserPublicKey userPublicKey = new UserPublicKey();
    userPublicKey.setIdPublicKey(publicKeyId);
    userPublicKey.setIdUser(bill.getUserId());
    userPublicKey.setStatus(PublicKeyStatus.IN_USE);

    userPublicKey.setId(1);

    // after insert write log
    Log<UserPublicKey> userPublicKeyLog = new Log<>();
    userPublicKeyLog.setCreateAt(LocalDateTime.now());
    userPublicKeyLog
        .setCurrentValue(MyUtils.convertToJson(userPublicKey));
    userPublicKeyLog.setLevel(LogLevels.ALERT);

    // update the signature
  }

}
