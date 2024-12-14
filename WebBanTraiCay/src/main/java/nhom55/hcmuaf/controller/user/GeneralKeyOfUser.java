package nhom55.hcmuaf.controller.user;


import nhom55.hcmuaf.beans.LoginBean;
import nhom55.hcmuaf.beans.PublicKey;
import nhom55.hcmuaf.beans.UserPublicKey;
import nhom55.hcmuaf.beans.Users;
import nhom55.hcmuaf.dao.daoimpl.LoginDao;
import nhom55.hcmuaf.dao.daoimpl.UserPublicKeyDAOImpl;
import nhom55.hcmuaf.util.MyUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 10, maxRequestSize =
        1024 * 1024 * 100)
@WebServlet(name = "generalKeyOfUser", value = "/page/user/general-key-info")
public class GeneralKeyOfUser extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Users user = MyUtils.getLoginedUser(session);
        UserPublicKeyDAOImpl userPublicKeyDAO = new UserPublicKeyDAOImpl();
         if(userPublicKeyDAO.getUserPublicKey(user.getId()) != null){
             UserPublicKey userPublicKey = userPublicKeyDAO.getUserPublicKey(user.getId());
             if(userPublicKeyDAO.getPublicKey(userPublicKey.getIdPublicKey()) != null){
                 PublicKey publicKey =  userPublicKeyDAO.getPublicKey(userPublicKey.getIdPublicKey());
                 req.setAttribute("publicKey", publicKey);
             }
         }

        req.setAttribute("user", user);
        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/user/thong-tin-ve-khoa.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Users user = MyUtils.getLoginedUser(session);
        String passwordFromUser = req.getParameter("password");
        LoginBean loginBean = new LoginBean(user.getEmail(), passwordFromUser, MyUtils.encodePass(passwordFromUser));
        String result = new LoginDao().authorizeLogin(loginBean);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String jsonResponse;
        if(result.equals("FAIL")) {
            jsonResponse = "{\"status\":\"fail\"}";
        }else{
            session.setAttribute("isAllowedEditedKeyPair", true);
            session.setAttribute("verifiedForKeyPairAt", System.currentTimeMillis());
            jsonResponse = "{\"status\":\"success\"}";
        }
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.flush();
        out.close();
    }
}
