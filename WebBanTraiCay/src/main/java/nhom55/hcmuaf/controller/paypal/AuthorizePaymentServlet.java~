package nhom55.hcmuaf.controller.paypal;

import com.paypal.base.rest.PayPalRESTException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
import nhom55.hcmuaf.beans.cart.Cart;
import nhom55.hcmuaf.beans.cart.CartProduct;
import nhom55.hcmuaf.dao.BillDao;
import nhom55.hcmuaf.dao.daoimpl.BillDaoImpl;
import nhom55.hcmuaf.dao.daoimpl.UserPublicKeyDAOImpl;
import nhom55.hcmuaf.encrypt.DigitalSignature;
import nhom55.hcmuaf.encrypt.DigitalSignatureImpl;
import nhom55.hcmuaf.encrypt.HashImpl;
import nhom55.hcmuaf.util.MyUtils;
import nhom55.hcmuaf.websocket.entities.CartsEntityWebSocket;

@WebServlet(name = "AuthorizePayPal", value = "/paypal/authorize-payment")
public class AuthorizePaymentServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  public AuthorizePaymentServlet() {
  }


  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    HttpSession session = request.getSession();
    Users users = MyUtils.getLoginedUser(session);
    HashImpl hash = (HashImpl) session.getAttribute("hash");
    double subTotalPrice = 0;
    String productNameList = "";
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
      LocalDateTime timeNow = LocalDateTime.now();
      for (CartsEntityWebSocket.CartItem itemProduct : cartItem) {
        productNameList += itemProduct.getProductName() + ", ";
      }
      // Kiểm tra xem có bất kỳ tên sản phẩm nào không
      if (productNameList.length() > 0) {
        // Xóa dấu phẩy và khoảng trắng cuối cùng
        productNameList = productNameList.substring(0, productNameList.length() - 2);
      }
    }
    String signatureFromUser = request.getParameter("signature");
    String lastName = request.getParameter("ho_nguoi-dung");
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
    //       Mặc định cho thanh toán Paypal có idPayment = 2
    int idPayment = 2;
    address +=", quận " + district + ", tỉnh " + city;
    LocalDateTime timeNow = LocalDateTime.now();
//    Bills bills = new Bills();
//    bills.setProductList(productNameList);
//    bills.setStatus("Đang giao");
//    bills.setUserId(users.getId());
//    bills.setPayment(idPayment);
//    bills.setFirstName(firstName);
//    bills.setLastName(lastName);
//    bills.setStreetAddress(address);
//    bills.setCity(city);
//    bills.setPhoneNumber(phoneNumber);
//    bills.setEmail(email);
//    bills.setTotalPrice(subTotalPrice);
//    bills.setDeliveryFee(deliveryFeeDouble);
//    bills.setNote(note);
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
    UserPublicKeyDAOImpl userPublicKeyDAO = new UserPublicKeyDAOImpl();
    UserPublicKey userPublicKey = userPublicKeyDAO.getUserPublicKey(users.getId());
    PublicKey publicKey = userPublicKeyDAO.getPublicKey(userPublicKey.getIdPublicKey());
    String publicKeyString = publicKey.getKey();
    boolean result = checkSingnatureOfUser(hash, jsonString, signatureFromUser, publicKeyString);

    try {
      PaymentServices paymentServices = new PaymentServices();
      String approvalLink = paymentServices.authorizePayment(bills);
      session.setAttribute("lastName", lastName);
      session.setAttribute("firstName", firstName);
      session.setAttribute("address", address);
      session.setAttribute("city", city);
      session.setAttribute("phoneNumber", phoneNumber);
      session.setAttribute("email", email);
      session.setAttribute("subtotal", subTotalPrice);
      session.setAttribute("deliveryFee", deliveryFeeDouble);
      session.setAttribute("note", note);
      session.setAttribute("signature", signatureFromUser);
      response.sendRedirect(approvalLink);


    } catch (PayPalRESTException ex) {
      request.setAttribute("errorMessage", ex.getMessage());
      ex.printStackTrace();
//            request.getRequestDispatcher("error.jsp").forward(request, response);
    }
  }
  public boolean checkSingnatureOfUser(HashImpl anotherHash, String jsonOrder, String signature,
                                       String publicKeyFromUser) {
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
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    } catch (InvalidKeySpecException e) {
      throw new RuntimeException(e);
    } catch (NoSuchPaddingException e) {
      throw new RuntimeException(e);
    } catch (IllegalBlockSizeException e) {
      throw new RuntimeException(e);
    } catch (BadPaddingException e) {
      throw new RuntimeException(e);
    } catch (InvalidKeyException e) {
      throw new RuntimeException(e);
    }
  }
}
