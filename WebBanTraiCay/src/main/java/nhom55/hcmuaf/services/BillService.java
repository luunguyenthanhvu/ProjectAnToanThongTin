package nhom55.hcmuaf.services;

import java.security.NoSuchAlgorithmException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import nhom55.hcmuaf.dao.BillDao;
import nhom55.hcmuaf.dao.daoimpl.BillDaoImpl;
import nhom55.hcmuaf.dto.request.VerifyUserBillRequestDTO;
import nhom55.hcmuaf.dto.response.MessageResponseDTO;
import nhom55.hcmuaf.encrypt.Hash;
import nhom55.hcmuaf.encrypt.HashImpl;
import nhom55.hcmuaf.util.MyUtils;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillService {

  BillDao billDao;
  Hash hash;

  public BillService() {
    billDao = new BillDaoImpl();
    hash = new HashImpl();
  }

  /**
   * This method use for check user signature in bill
   *
   * @param requestDTO
   * @return
   */
  public MessageResponseDTO checkVerifyUserBill(String requestDTO) throws NoSuchAlgorithmException {
    // Convert request dto to entity
    VerifyUserBillRequestDTO dto = MyUtils.convertJsonToObject(requestDTO,
        VerifyUserBillRequestDTO.class);

    // get signature by dto id
    var bill = billDao.getABill(dto.getIdBill());

    // the bill json
    String billJson = MyUtils.convertBillsJson(bill);
    String hashBill = hash.hashText(billJson);
    System.out.println("thử hash");
    System.out.println("billl id:" + bill.getId());
    System.out.println();
    System.out.println(hashBill);
    System.out.println(bill.getSignature());

    return MessageResponseDTO.builder().message("Verify Success!").build();
  }
}
