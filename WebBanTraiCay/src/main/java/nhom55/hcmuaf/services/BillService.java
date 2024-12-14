package nhom55.hcmuaf.services;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
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
import nhom55.hcmuaf.enums.LogLevels;
import nhom55.hcmuaf.enums.LogNote;
import nhom55.hcmuaf.enums.PublicKeyStatus;
import nhom55.hcmuaf.log.AbsDAO;
import nhom55.hcmuaf.log.Log;
import nhom55.hcmuaf.log.RequestInfo;
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
      throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
    // Convert request dto to entity
    VerifyUserBillRequestDTO dto = MyUtils.convertJsonToObject(requestDTO,
        VerifyUserBillRequestDTO.class);

    // get signature by dto id
    var bill = billDao.getABill(dto.getIdBill());

    // check if bill has signature
    if (bill.getSignature() == null || bill.getSignature().isEmpty()) {
      // generate default data
      setUpDefaultData(bill, request);
    }

    // Check bill and check key used When sign the bill
    // Get userPublic key in used
    UserPublicKey userPublicKey = new UserPublicKey();

    // the bill json
    String billJson = MyUtils.convertBillsJson(bill);
    String hashBill = hash.hashText(billJson);

    return MessageResponseDTO.builder().message("Verify Success!").build();
  }

  private void setUpDefaultData(Bills bill, HttpServletRequest request)
      throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
// the bill json
    String billJson = MyUtils.convertBillsJson(bill);
    String hashBill = hash.hashText(billJson);
    System.out.println("thử hash");
    System.out.println("billl id:" + bill.getId());
    KeyPair keyPair = asymmetric.generateKeyPair();
    asymmetric.loadKeyPair(keyPair);
    digitalSignature.loadKeyPair(keyPair);
//    System.out.println("Private key: " + asymmetric.getPrivateKeyAsString());
//    System.out.println("Public key: " + asymmetric.getPublicKeyAsString());
//    System.out.println("Signature bill " + digitalSignature.createSignature(billJson));
//    System.out.println();
//    System.out.println(hashBill);
//    System.out.println(bill.getSignature());

    // insert new userKey
    PublicKey publicKey = PublicKey
        .builder()
        .key(asymmetric.getPublicKeyAsString())
        .createDate(LocalDateTime.now())
        .build();

    int publicKeyId = publicKeyDAO.insertPublicKey(publicKey);
    System.out.println("public key sau khi insert: " + publicKey);
    publicKey.setId(publicKeyId);

    // after insert write log
    RequestInfo requestInfo = new RequestInfo(request.getRemoteAddr(), "HCM", "VietNam");
    Log<PublicKey> publicKeyLog = new Log<>();
    publicKeyLog.setNote(String.valueOf(LogNote.INSERT_PUBLIC_KEY.getLevel()));
    publicKeyLog.setLevel(LogLevels.INFO);
    publicKeyLog.setPreValue("");
    publicKeyLog.setCurrentValue(MyUtils.convertToJson(publicKey));
    publicKeyLog.setCreateAt(LocalDateTime.now());
    publicKeyLog.setUpdateAt(null);
    publicKeyLog.setIp(requestInfo.getIp());
    publicKeyLog.setAddress(requestInfo.getAddress());
    publicKeyLog.setNational(requestInfo.getNation());
    super.insert(publicKeyLog);

    // insert userPublic key
    UserPublicKey userPublicKey = new UserPublicKey();
    userPublicKey.setIdPublicKey(publicKeyId);
    userPublicKey.setIdUser(bill.getUserId());
    userPublicKey.setStatus(PublicKeyStatus.IN_USE);
    int userPublicKeyId = userPublicKeyDAO.insertUserPublicKey(userPublicKey);
    userPublicKey.setId(userPublicKeyId);

    System.out.println("user public key sau khi insert");

    // after insert write log
    Log<UserPublicKey> userPublicKeyLog = new Log<>();
    userPublicKeyLog.setNote(String.valueOf(LogNote.USER_CREATE_PUBLIC_KEY.getLevel()));
    userPublicKeyLog.setLevel(LogLevels.INFO);
    userPublicKeyLog.setPreValue("");
    userPublicKeyLog.setCurrentValue(MyUtils.convertToJson(userPublicKey));
    userPublicKeyLog.setCreateAt(LocalDateTime.now());
    userPublicKeyLog.setUpdateAt(null);
    userPublicKeyLog.setIp(requestInfo.getIp());
    userPublicKeyLog.setAddress(requestInfo.getAddress());
    userPublicKeyLog.setNational(requestInfo.getNation());
    super.insert(userPublicKeyLog);

    // update the signature
    System.out.println("bill sau khi update");
    billDao.updateSignatureABill(bill.getId(), digitalSignature.createSignature(billJson));
    System.out.println(billDao.getABill(bill.getId()));
  }

}
