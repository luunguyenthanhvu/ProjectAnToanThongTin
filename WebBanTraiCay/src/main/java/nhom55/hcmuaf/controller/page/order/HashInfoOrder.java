package nhom55.hcmuaf.controller.page.order;

import nhom55.hcmuaf.beans.Users;
import nhom55.hcmuaf.util.MyUtils;
import nhom55.hcmuaf.websocket.entities.CartsEntityWebSocket;
import org.json.simple.JSONObject;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "HashInfoOrder", value = "/HashInfoOrder")
public class HashInfoOrder extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users users = MyUtils.getLoginedUser(session);
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
        double subTotalPrice = 0;
        String productNameList = "";
        List<String> selectedProductIds = (List<String>) session.getAttribute("selectedProductIds");
        CartsEntityWebSocket cart = MyUtils.getCart(session);
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
            address += address + ", quận " + district + ", tỉnh " + city;
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

        }else{

        }
    }
}