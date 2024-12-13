package nhom55.hcmuaf.controller.user;

import nhom55.hcmuaf.beans.Users;
import nhom55.hcmuaf.util.MyUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "createAKeyPairForUser", value = "/page/user/create-a-key-pair-for-user")
public class CreateAKeyPairForUser extends HttpServlet {
    private static final long VERIFIED_TIME_LIMIT = 20 * 60 * 1000; // 20 phút tính bằng milliseconds

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = MyUtils.getLoginedUser(session);
        Boolean isVerified = (Boolean) session.getAttribute("isAllowedEditedKeyPair");
        Long verifiedAt = (Long) session.getAttribute("verifiedForKeyPairAt");
        if (isVerified != null && isVerified && verifiedAt != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - verifiedAt <= VERIFIED_TIME_LIMIT) {
                request.setAttribute("user", user);
                // Người dùng đã xác minh trong thời hạn 30 phút
                RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/user/tao-moi-khoa.jsp");
                dispatcher.forward(request, response);
                return;
            } else {
                // Hết thời gian xác minh
                session.removeAttribute("isAllowedEditedKeyPair");
                session.removeAttribute("verifiedForKeyPairAt");
            }
        }
        // Nếu chưa xác minh hoặc hết hạn, điều hướng về trang xác minh
        response.sendRedirect(request.getContextPath() + "/page/user/general-key-info");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = MyUtils.getLoginedUser(session);
        Boolean isVerified = (Boolean) session.getAttribute("isAllowedEditedKeyPair");
        if(!isVerified){
            doGet(request,response);
        }else{

        }
    }
}