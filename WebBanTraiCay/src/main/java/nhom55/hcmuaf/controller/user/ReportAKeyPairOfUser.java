package nhom55.hcmuaf.controller.user;


import nhom55.hcmuaf.beans.PublicKey;
import nhom55.hcmuaf.beans.UserPublicKey;
import nhom55.hcmuaf.beans.Users;
import nhom55.hcmuaf.dao.UserPublicKeyDAO;
import nhom55.hcmuaf.dao.daoimpl.UserPublicKeyDAOImpl;
import nhom55.hcmuaf.encrypt.Asymmetric;
import nhom55.hcmuaf.encrypt.AsymmetricImpl;
import nhom55.hcmuaf.enums.LogLevels;
import nhom55.hcmuaf.enums.LogNote;
import nhom55.hcmuaf.enums.PublicKeyStatus;
import nhom55.hcmuaf.log.AbsDAO;
import nhom55.hcmuaf.log.Log;
import nhom55.hcmuaf.log.RequestInfo;
import nhom55.hcmuaf.util.MyUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@WebServlet(name = "reportAKeyPairOfUser", value = "/page/user/report-a-key-pair-of-user")
public class ReportAKeyPairOfUser extends HttpServlet {
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
                RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/user/report-key.jsp");
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
            UserPublicKeyDAO userPublicKeyDAO = new UserPublicKeyDAOImpl();
            LocalDateTime timeCreated = LocalDateTime.now();
            RequestInfo requestInfo = new RequestInfo(request.getRemoteAddr(), "HCM", "VietNam");

            // Viết thêm log khi user report key
            UserPublicKey userPublicKeyPreValue = userPublicKeyDAO.getUserPublicKey(user.getId());
            userPublicKeyPreValue.setStatus(PublicKeyStatus.BANNED);
            PublicKey publicKeyPreValue = userPublicKeyDAO.getPublicKey(userPublicKeyPreValue.getIdPublicKey());
            userPublicKeyDAO.setStatusUserPublicKey(user.getId(),userPublicKeyPreValue.getIdPublicKey(),PublicKeyStatus.BANNED.name());



            Log<UserPublicKey> usersPublicKeyLog = new Log<>();
            usersPublicKeyLog.setIp(requestInfo.getIp());
            usersPublicKeyLog.setAddress(requestInfo.getAddress());
            usersPublicKeyLog.setNational(requestInfo.getNation());
            usersPublicKeyLog.setLevel(LogLevels.WARNING);
            usersPublicKeyLog.setNote(LogNote.USER_REPORT_PUBLIC_KEY.name());
            usersPublicKeyLog.setCreateAt(timeCreated);
            usersPublicKeyLog.setCurrentValue(MyUtils.convertToJson(userPublicKeyPreValue));


//               Viết log public Key bị report
            Log<PublicKey> publicKeyLog = new Log<>();
            publicKeyLog.setIp(requestInfo.getIp());
            publicKeyLog.setAddress(requestInfo.getAddress());
            publicKeyLog.setNational(requestInfo.getNation());
            publicKeyLog.setLevel(LogLevels.WARNING);
            publicKeyLog.setNote(LogNote.BAN_PUBLIC_KEY.name());
            publicKeyLog.setCreateAt(timeCreated);
            publicKeyLog.setCurrentValue(MyUtils.convertToJson(publicKeyPreValue));


            AbsDAO<UserPublicKey> absUserDao = new AbsDAO<>();
            AbsDAO<PublicKey> absPublicKeyDao = new AbsDAO<>();
            absUserDao.insert(usersPublicKeyLog);
            absPublicKeyDao.insert(publicKeyLog);


            request.setAttribute("isReportKeySuccess", true);

            doGet(request, response);



        }
    }
}
