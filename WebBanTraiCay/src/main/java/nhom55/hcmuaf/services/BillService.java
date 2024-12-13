package nhom55.hcmuaf.services;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import nhom55.hcmuaf.dao.BillDao;
import nhom55.hcmuaf.dao.daoimpl.BillDaoImpl;
import nhom55.hcmuaf.dto.request.VerifyUserBillRequestDTO;
import nhom55.hcmuaf.dto.response.MessageResponseDTO;
import nhom55.hcmuaf.util.MyUtils;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillService {

  BillDao billDao;

  public BillService() {
    billDao = new BillDaoImpl();
  }

  /**
   * This method use for check user signature in bill
   *
   * @param requestDTO
   * @return
   */
  public MessageResponseDTO checkVerifyUserBill(String requestDTO) {
    // Convert request dto to entity
    VerifyUserBillRequestDTO dto = MyUtils.convertJsonToObject(requestDTO,
        VerifyUserBillRequestDTO.class);

    // get signature by dto id
    var bill = billDao.getABill(dto.getIdBill());
    System.out.println(bill.getSignature());

    return null;
  }
}
