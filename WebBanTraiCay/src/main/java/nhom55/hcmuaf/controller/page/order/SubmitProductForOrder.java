package nhom55.hcmuaf.controller.page.order;

import java.util.Arrays;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

import nhom55.hcmuaf.beans.Users;
import nhom55.hcmuaf.dao.daoimpl.UserPublicKeyDAOImpl;
import nhom55.hcmuaf.util.MyUtils;
import nhom55.hcmuaf.websocket.entities.CartsEntityWebSocket;
import org.codehaus.jackson.map.ObjectMapper;

@WebServlet(name = "SubmitProductForOrder", value = "/page/order/submit-selected-products")
public class SubmitProductForOrder extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/page/user/general-key-info?notifyCreateKeyPair=true");

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Users user = MyUtils.getLoginedUser(request.getSession());
        // Đọc danh sách ID sản phẩm từ form
        String selectedProductIdsJson = request.getParameter("selectedProductIds");
        if(selectedProductIdsJson != null ) {
            UserPublicKeyDAOImpl userPublicKeyDAO = new UserPublicKeyDAOImpl();
            if(userPublicKeyDAO.getUserPublicKey(user.getId()) == null) {
                // Người dùng chưa tạo khóa để ký
                doGet(request, response);
            }else{
                List<String> selectedProductIds = Arrays.asList(new ObjectMapper().readValue(selectedProductIdsJson, String[].class));

                // Lưu danh sách ID sản phẩm vào session
                HttpSession session = request.getSession();
                session.setAttribute("selectedProductIds", selectedProductIds);
                CartsEntityWebSocket cart = MyUtils.getCart(session);
                double subTotalPrice =0;
                for(CartsEntityWebSocket.CartItem item : cart.getCartItemList()) {
                    subTotalPrice +=item.getPrice()*item.getQuantity();
                }
                session.setAttribute("subTotalPrice", subTotalPrice);

                // chuyển hướng sang trang check out để tiến hành thanh toán
                response.sendRedirect(request.getContextPath() + "/page/order/check-out");
            }
        } else if (MyUtils.getLoginedUser(request.getSession()) == null) {
            // chuyển hướng sang trang check out để tiến hành thanh toán
            response.sendRedirect(request.getContextPath() + "/page/login");
        }
    }
}