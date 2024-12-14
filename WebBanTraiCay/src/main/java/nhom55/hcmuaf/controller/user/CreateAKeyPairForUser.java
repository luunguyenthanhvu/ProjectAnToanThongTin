package nhom55.hcmuaf.controller.user;

import nhom55.hcmuaf.beans.PublicKey;
import nhom55.hcmuaf.beans.UserPublicKey;
import nhom55.hcmuaf.beans.Users;
import nhom55.hcmuaf.dao.UserPublicKeyDAO;
import nhom55.hcmuaf.dao.daoimpl.PublicKeyDAOImpl;
import nhom55.hcmuaf.dao.daoimpl.UserPublicKeyDAOImpl;
import nhom55.hcmuaf.encrypt.Asymmetric;
import nhom55.hcmuaf.encrypt.AsymmetricImpl;
import nhom55.hcmuaf.enums.LogLevels;
import nhom55.hcmuaf.enums.LogNote;
import nhom55.hcmuaf.enums.PublicKeyStatus;
import nhom55.hcmuaf.log.AbsDAO;
import nhom55.hcmuaf.log.Log;
import nhom55.hcmuaf.log.RequestInfo;
import nhom55.hcmuaf.my_handle_exception.MyHandleException;
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
       try{
           HttpSession session = request.getSession();
           Users user = MyUtils.getLoginedUser(session);
           Boolean isVerified = (Boolean) session.getAttribute("isAllowedEditedKeyPair");
           if(!isVerified){
               doGet(request,response);
           }else{
               RequestInfo requestInfo = new RequestInfo(request.getRemoteAddr(), "HCM", "VietNam");
               Asymmetric asymmetric = new AsymmetricImpl();
               LocalDateTime timeCreated = LocalDateTime.now();
               asymmetric.loadKeyPair(asymmetric.generateKeyPair());
               String publicKey = asymmetric.getPublicKeyAsString();
               String privateKey = asymmetric.getPrivateKeyAsString();
               UserPublicKeyDAO userPublicKeyDAO = new UserPublicKeyDAOImpl();
               if(userPublicKeyDAO.getUserPublicKey(user.getId()) != null){
//                   // Ghi log cho public key bị ban
                   UserPublicKey userPublicKeyReadyToBan = userPublicKeyDAO.getUserPublicKey(user.getId());
                   userPublicKeyReadyToBan.setStatus(PublicKeyStatus.BANNED);
                   Log<UserPublicKey> usersPublicKeyLog = new Log<>();
                   usersPublicKeyLog.setIp(requestInfo.getIp());
                   usersPublicKeyLog.setNational(requestInfo.getNation());
                   usersPublicKeyLog.setAddress(requestInfo.getAddress());
                   usersPublicKeyLog.setLevel(LogLevels.WARNING);
                   usersPublicKeyLog.setNote(String.valueOf(LogNote.USER_REPORT_PUBLIC_KEY.getLevel()));
                   usersPublicKeyLog.setCurrentValue(MyUtils.convertToJson(userPublicKeyReadyToBan));
                   usersPublicKeyLog.setCreateAt(timeCreated);
                   AbsDAO<UserPublicKey> absUserDao = new AbsDAO<>();
                   absUserDao.insert(usersPublicKeyLog);

               }


               userPublicKeyDAO.insertPublicKey(publicKey, timeCreated);
               int idPublicKey = userPublicKeyDAO.getIdPublicKey(timeCreated);
               userPublicKeyDAO.insertUserPublicKey(user.getId(), idPublicKey, PublicKeyStatus.IN_USE);
               // Viết thêm log khi user tao key


               UserPublicKey userPublicKeyObj = new UserPublicKey();
               userPublicKeyObj.setIdUser(user.getId());
               userPublicKeyObj.setIdPublicKey(idPublicKey);
               userPublicKeyObj.setStatus(PublicKeyStatus.IN_USE);

               Log<UserPublicKey> usersPublicKeyLog = new Log<>();
               usersPublicKeyLog.setIp(requestInfo.getIp());
               usersPublicKeyLog.setNational(requestInfo.getNation());
               usersPublicKeyLog.setAddress(requestInfo.getAddress());
               usersPublicKeyLog.setLevel(LogLevels.ALERT);
               usersPublicKeyLog.setNote(String.valueOf(LogNote.USER_CREATE_PUBLIC_KEY.getLevel()));
               usersPublicKeyLog.setCurrentValue(MyUtils.convertToJson(userPublicKeyObj));
               usersPublicKeyLog.setCreateAt(timeCreated);

//

               AbsDAO<UserPublicKey> absUserDao = new AbsDAO<>();
               absUserDao.insert(usersPublicKeyLog);


               request.setAttribute("publicKey", publicKey);
               request.setAttribute("privateKey", privateKey);
               request.setAttribute("isCreateKeySuccess", true);

               doGet(request, response);



           }
       } catch (NoSuchAlgorithmException e) {
           throw new RuntimeException(e);
       }
    }
}