package nhom55.hcmuaf.controller.page.order;

import nhom55.hcmuaf.beans.Users;
import nhom55.hcmuaf.encrypt.Hash;
import nhom55.hcmuaf.encrypt.HashImpl;
import nhom55.hcmuaf.util.MyUtils;
import nhom55.hcmuaf.util.OrderValidator;
import nhom55.hcmuaf.websocket.entities.CartsEntityWebSocket;
import org.json.simple.JSONObject;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@WebServlet(name = "HashInfoOrder", value = "/page/order/hash-info-order")
public class HashInfoOrder extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       try{
           HttpSession session = request.getSession();
           Users users = MyUtils.getLoginedUser(session);
           String lastName = request.getParameter("ho_nguoi_dung");
           String firstName = request.getParameter("ten_nguoi_dung");
           String address = request.getParameter("dia_chi_nguoi_dung");
           String city = request.getParameter("provinceName");
           String district = request.getParameter("districtName");
           String phoneNumber = request.getParameter("sdt_nguoi_dung");
           String email = request.getParameter("email_nguoi_dung");
           String deliveryFee = request.getParameter("delivery_fee");
           String cleanedString = deliveryFee.replaceAll("[₫\\s]", "");
           cleanedString = cleanedString.replace(".", "");
           double deliveryFeeDouble = Double.parseDouble(cleanedString);
           String note = request.getParameter("note_nguoi_dung");
           double subTotalPrice = 0;
           String productNameList = "";
           List<String> selectedProductIds = (List<String>) session.getAttribute("selectedProductIds");
           CartsEntityWebSocket cart = MyUtils.getCart(session);
           if(!checkValidate(request, response, lastName, firstName, address, city, phoneNumber, email)){
               JSONObject jsonObject = new JSONObject();
               jsonObject.put("invalidInfo", "true");
               PrintWriter out = response.getWriter();
               out.print(jsonObject);
               out.flush();
           }else{
               if(cart != null && selectedProductIds != null){
                   List<CartsEntityWebSocket.CartItem> cartItem = cart.getCartItemList();
                   subTotalPrice = (Double) session.getAttribute("subTotalPrice");
                   for (CartsEntityWebSocket.CartItem itemProduct : cartItem) {
                       productNameList += itemProduct.getProductName() + ", ";
                   }
                   // Kiểm tra xem có bất kỳ tên sản phẩm nào không
                   if (productNameList.length() > 0) {
                       // Xóa dấu phẩy và khoảng trắng cuối cùng
                       productNameList = productNameList.substring(0, productNameList.length() - 2);
                   }
                   address += ", quận " + district + ", tỉnh " + city;
                   // Tạo đối tượng JSON
                   JSONObject jsonObject = new JSONObject();
                   jsonObject.put("lastName", lastName);
                   jsonObject.put("firstName", firstName);
                   jsonObject.put("address", address);
                   jsonObject.put("city", city);
                   jsonObject.put("district", district);
                   jsonObject.put("phoneNumber", phoneNumber);
                   jsonObject.put("email", email);
                   jsonObject.put("deliveryFee", deliveryFeeDouble);
                   jsonObject.put("note", note);
                   jsonObject.put("subTotalPrice", subTotalPrice);
                   jsonObject.put("productNameList", productNameList);

                   // Chuyển đổi đối tượng JSON thành chuỗi
                   String jsonString = jsonObject.toString();
                   System.out.println("Json 1: " + jsonString);
                   PrintWriter out = response.getWriter();
                   HashImpl hash = new HashImpl();
                   session.setAttribute("hash",hash);
                   String hashInfoOrder = hash.hashText(jsonString);
                   JSONObject hashObj = new JSONObject();
                   hashObj.put("invalidInfo", "false");
                   hashObj.put("data", hashInfoOrder);
                   out.print(hashObj);
                   out.flush();
               }else{

               }
           }
       } catch (NoSuchAlgorithmException e) {
           throw new RuntimeException(e);
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
}