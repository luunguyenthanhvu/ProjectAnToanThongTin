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
          session.setAttribute("productNameList",productNameList);

          response.sendRedirect(approvalLink);


        } catch (PayPalRESTException ex) {
          request.setAttribute("errorMessage", ex.getMessage());
          ex.printStackTrace();
//            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
      }

    }
  }

