package nhom55.hcmuaf.controller.page.order;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import nhom55.hcmuaf.beans.Bills;
import nhom55.hcmuaf.beans.PublicKey;
import nhom55.hcmuaf.beans.UserPublicKey;
import nhom55.hcmuaf.beans.Users;
import nhom55.hcmuaf.dao.BillDao;
import nhom55.hcmuaf.dao.daoimpl.BillDaoImpl;
import nhom55.hcmuaf.dao.daoimpl.UserPublicKeyDAOImpl;
import nhom55.hcmuaf.encrypt.DigitalSignature;
import nhom55.hcmuaf.encrypt.DigitalSignatureImpl;
import nhom55.hcmuaf.encrypt.HashImpl;
import nhom55.hcmuaf.enums.LogLevels;
import nhom55.hcmuaf.log.AbsDAO;
import nhom55.hcmuaf.log.Log;
import nhom55.hcmuaf.log.RequestInfo;
import nhom55.hcmuaf.my_handle_exception.MyHandleException;
import nhom55.hcmuaf.sendmail.MailProperties;
import nhom55.hcmuaf.util.MyUtils;
import nhom55.hcmuaf.util.OrderValidator;
import nhom55.hcmuaf.websocket.entities.CartsEntityWebSocket;

@WebServlet(name = "CheckOut", value = "/page/order/check-out")
public class CheckOut extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    HttpSession session = request.getSession();

//    request.setAttribute("subTotalPrice", subTotalPrice);
    RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/checkout.jsp");
    dispatcher.forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    String signatureFromUser = request.getParameter("signature");
    String lastName = request.getParameter("ho_nguoi-dung");
    System.out.println("Ho nguoi dung: " + lastName);
    String firstName = request.getParameter("ten_nguoi-dung");
    String address = request.getParameter("dia-chi_nguoi-dung");
    String city = request.getParameter("provinceName");
    String district = request.getParameter("districtName");
    String phoneNumber = request.getParameter("sdt_nguoi-dung");
    String email = request.getParameter("email_nguoi-dung");
    String deliveryFee = request.getParameter("delivery_fee");
    String cleanedString = deliveryFee.replaceAll("[₫\\s]", "");
    cleanedString = cleanedString.replace(".", "");
    double deliveryFeeDouble = Double.parseDouble(cleanedString);
    String note = request.getParameter("note_nguoi-dung");
    String idVoucherString = request.getParameter("idVoucher");
    int idVoucher = 0;
    if (idVoucherString != null && !idVoucherString.trim().isEmpty()) {
      idVoucher = Integer.valueOf(idVoucherString);
    }
    if (checkValidate(request, response, lastName, firstName, address, city, phoneNumber, email)) {
      HttpSession session = request.getSession();
      Users users = MyUtils.getLoginedUser(session);
      HashImpl hash = (HashImpl) session.getAttribute("hash");
      double subTotalPrice = 0;
      // get selected Product for buy
      List<String> selectedProductIds = (List<String>) session.getAttribute("selectedProductIds");
      CartsEntityWebSocket cart = MyUtils.getCart(session);
      if (cart != null && selectedProductIds != null) {
        // get product list selected from cart
        List<CartsEntityWebSocket.CartItem> cartItem = cart.getCartItemList();
        subTotalPrice = (Double) session.getAttribute("subTotalPrice");
        BillDao billDao = new BillDaoImpl();
        // biến này sẽ lưu tất cả các hóa đơn người dùng đã mua
        List<Bills> listBills = new ArrayList<>();
        LocalDateTime timeNow = (LocalDateTime) session.getAttribute("timeOrder");
        String productNameList = "";
        for (CartsEntityWebSocket.CartItem itemProduct : cartItem) {
          productNameList += itemProduct.getProductName() + ", ";
        }
        // Kiểm tra xem có bất kỳ tên sản phẩm nào không
        if (productNameList.length() > 0) {
          // Xóa dấu phẩy và khoảng trắng cuối cùng
          productNameList = productNameList.substring(0, productNameList.length() - 2);
        }
        int idPayment = 1;
        address += ", quận " + district + ", tỉnh " + city;
        //Tạo object order JSON
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("lastName", lastName);
//        jsonObject.put("firstName", firstName);
//        jsonObject.put("address", address);
//        jsonObject.put("city", city);
//        jsonObject.put("district", district);
//        jsonObject.put("phoneNumber", phoneNumber);
//        jsonObject.put("email", email);
//        jsonObject.put("deliveryFee", deliveryFeeDouble);
//        jsonObject.put("note", note);
//        jsonObject.put("subTotalPrice", subTotalPrice);
//        jsonObject.put("productNameList", productNameList);

        Bills bills = Bills
                .builder()
                .orderedDate(timeNow)
                .productList(productNameList)
                .userId(users.getId())
                .firstName(firstName)
                .lastName(lastName)
                .streetAddress(address)
                .city(city)
                .phoneNumber(phoneNumber)
                .deliveryFee(deliveryFeeDouble)
                .email(email)
                .totalPrice(subTotalPrice)
                .note(note)
                .build();
        MyUtils.convertBillsJson(bills);
        String jsonString = MyUtils.convertBillsJson(bills);
        System.out.println("Json 2: " + jsonString);
        UserPublicKeyDAOImpl userPublicKeyDAO = new UserPublicKeyDAOImpl();
        UserPublicKey userPublicKey = userPublicKeyDAO.getUserPublicKey(users.getId());
        PublicKey publicKey = userPublicKeyDAO.getPublicKey(userPublicKey.getIdPublicKey());
        String publicKeyString = publicKey.getKey();
        boolean result = checkSingnatureOfUser(request,hash, jsonString, signatureFromUser,
                publicKeyString);
        if (result) {
          if (billDao.addAListProductToBills(timeNow, productNameList, "Đang chuẩn bị",
                  users.getId(),
                  idPayment, firstName, lastName, address, city, phoneNumber, email, subTotalPrice,
                  deliveryFeeDouble, note, signatureFromUser, false)) {
            int id_bills = billDao.getIDAListProductFromBills(timeNow, users.getId());
            for (CartsEntityWebSocket.CartItem itemProduct : cartItem) {
              if (billDao.addAProductToBillDetails(itemProduct.getId(), id_bills,
                      itemProduct.getQuantity(), itemProduct.getQuantity() * itemProduct.getPrice())) {
                // billDao.degreeAmountWhenOderingSuccessfully(itemProduct.getId(),itemProduct.getQuantity());
              }
            }

            // xoa san pham sau khi dat hang
            deleteCart(session);

            //          Thông báo người mua đã đặt thành công
            Properties smtpProperties = MailProperties.getSMTPPro();
            Session session1 = Session.getInstance(smtpProperties, new javax.mail.Authenticator() {
              protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MailProperties.getEmail(),
                        MailProperties.getPassword());
              }
            });
            try {
              Message message = new MimeMessage(session1);
              message.addHeader("Content-type", "text/HTML; charset= UTF-8");
              message.setFrom(new InternetAddress(MailProperties.getEmail()));
              message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
              message.setSubject("DAT HANG");
              message.setText(
                      "Don dat hang cua ban thanh cong. Xem don hang ban vua moi dat tai day : "
                              + "http://localhost:8080/page/bill/detail?idBills="
                              + id_bills);
              Transport.send(message);
              boolean isOrderSuccessfully = true;
              Log<Bills> log = new Log<>();
              AbsDAO<Bills> absDAO = new AbsDAO<>();
              RequestInfo requestInfo = new RequestInfo(request.getRemoteAddr(), "HCM", "VietNam");
              log.setLevel(LogLevels.INFO);
              log.setIp(requestInfo.getIp());
              log.setAddress(requestInfo.getAddress());
              log.setNational(requestInfo.getNation());
              log.setNote("Người dùng " + users.getUsername() + " vừa đặt hàng thành công");
              log.setCurrentValue(
                      lastName + " " + firstName + ", địa chỉ: " + address + ", số điện thoại: "
                              + phoneNumber + ", email: " + email + ", giá tiền đơn hàng: " + subTotalPrice
                              + ", tiền vận chuyển: " + deliveryFeeDouble + ", ghi chú: " + note
                              + ", kiểu thanh toán: Thẻ tín dụng " + ", ngày đặt hàng: " + timeNow
                              + " ,tổng tiền: " + (subTotalPrice + deliveryFeeDouble));
              log.setCreateAt(timeNow);
              absDAO.insert(log);
              RequestDispatcher dispatcher = request.getRequestDispatcher("/page/bill/list-bill");
//            request.setAttribute("isOrderSuccessfully",isOrderSuccessfully);
              dispatcher.forward(request, response);
            } catch (Exception e) {
              System.out.println("SendEmail File Error " + e);
            }
          }
        } else {
          request.setAttribute("firstName", firstName);
          request.setAttribute("lastName", lastName);
          request.setAttribute("address", address);
          request.setAttribute("phone", phoneNumber);
          request.setAttribute("email", email);
          request.setAttribute("noteFromUser", note);
          request.setAttribute("wrongSignature", true);
          doGet(request, response);
        }


      }

    } else {
      doGet(request, response);
    }

  }


  private static boolean checkValidate(HttpServletRequest request, HttpServletResponse response,
                                       String lastName, String firstName, String address, String city, String phoneNumber,
                                       String email) {
    String checkFirstName = OrderValidator.validateFirstName(firstName);
    String checkLastName = OrderValidator.validateLastName(lastName);
    String checkAddress = OrderValidator.validateAddress(address);
    String checkCity = OrderValidator.validateCity(city);
    String checkPhone = OrderValidator.validatePhoneNumber(phoneNumber);
    String checkEmail = OrderValidator.validateEmail(email);
    int count = 0;
    if (!checkFirstName.isEmpty()) {
      count++;
      request.setAttribute("firstNameError", checkFirstName);
    } else {
      request.setAttribute("firstName", firstName);
    }
    if (!checkLastName.isEmpty()) {
      count++;
      request.setAttribute("lastNameError", checkLastName);
    } else {
      request.setAttribute("lastName", lastName);
    }
    if (!checkAddress.isEmpty()) {
      count++;
      request.setAttribute("addressError", checkAddress);
    } else {
      request.setAttribute("address", address);
    }
    if (!checkCity.isEmpty()) {
      count++;
      request.setAttribute("cityError", checkCity);
    } else {
      request.setAttribute("city", city);
    }
    if (!checkPhone.isEmpty()) {
      count++;
      request.setAttribute("phoneError", checkPhone);
    } else {
      request.setAttribute("phone", phoneNumber);
    }
    if (!checkEmail.isEmpty()) {
      count++;
      request.setAttribute("emailError", checkEmail);
    } else {
      request.setAttribute("email", city);
    }
    if (count > 0) {
      return false;
    } else {
      return true;
    }
  }

  public static void deleteCart(HttpSession session) {
    List<String> selectedProductIds = (List<String>) session.getAttribute("selectedProductIds");
    CartsEntityWebSocket cart = (CartsEntityWebSocket) session.getAttribute("cart");
    List<Integer> productIds = selectedProductIds.stream()
            .map(Integer::valueOf)
            .collect(Collectors.toList());
    cart.deleteItems(productIds);
  }

  public boolean checkSingnatureOfUser(HttpServletRequest request,HashImpl anotherHash, String jsonOrder, String signature,
                                       String publicKeyFromUser) throws MyHandleException {
    try {
      HashImpl hash = anotherHash;
      System.out.println("JsonOrder: " + jsonOrder);
      String hashInfo = hash.hashText(jsonOrder);
      System.out.println("Chuỗi hash lần 1: " + hashInfo);
      hashInfo = hash.hashText(hashInfo);
      System.out.println("Chuỗi hash lần 2: " + hashInfo);
      DigitalSignature digitalSignature = new DigitalSignatureImpl();
      digitalSignature.loadPublicKey(publicKeyFromUser);
      String resultHash = digitalSignature.getHashFromSignature(signature);
      System.out.println("Chuỗi hash của user: " + resultHash);
      System.out.println("Kết quả: mã hóa : " + resultHash + "\n"
              + "Ket qua hash 2: " + hashInfo);
      System.out.println("2 thang nay bang nhau" + resultHash.equals(hashInfo));
      return resultHash.equals(hashInfo);
    } catch (Exception e) {
      e.printStackTrace();
      request.setAttribute("errorProcessSignature",true);
      return false;
    }
  }
}