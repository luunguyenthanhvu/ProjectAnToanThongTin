package nhom55.hcmuaf.controller.user;


import java.io.IOException;
import java.time.LocalDateTime;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import nhom55.hcmuaf.beans.KeyReport;
import nhom55.hcmuaf.beans.UserPublicKey;
import nhom55.hcmuaf.beans.Users;
import nhom55.hcmuaf.dao.KeyReportDao;
import nhom55.hcmuaf.dao.UserPublicKeyDAO;
import nhom55.hcmuaf.dao.daoimpl.KeyReportDaoImpl;
import nhom55.hcmuaf.dao.daoimpl.UserPublicKeyDAOImpl;
import nhom55.hcmuaf.enums.LogLevels;
import nhom55.hcmuaf.enums.LogNote;
import nhom55.hcmuaf.enums.PublicKeyStatus;
import nhom55.hcmuaf.log.AbsDAO;
import nhom55.hcmuaf.log.Log;
import nhom55.hcmuaf.log.RequestInfo;
import nhom55.hcmuaf.util.MyUtils;

@WebServlet(name = "reportAKeyPairOfUser", value = "/page/user/report-a-key-pair-of-user")
public class ReportAKeyPairOfUser extends HttpServlet {

  private static final long VERIFIED_TIME_LIMIT = 20 * 60 * 1000; // 20 phút tính bằng milliseconds

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    HttpSession session = request.getSession();
    Users user = MyUtils.getLoginedUser(session);
    Boolean isVerified = (Boolean) session.getAttribute("isAllowedEditedKeyPair");
    Long verifiedAt = (Long) session.getAttribute("verifiedForKeyPairAt");
    if (isVerified != null && isVerified && verifiedAt != null) {
      long currentTime = System.currentTimeMillis();
      if (currentTime - verifiedAt <= VERIFIED_TIME_LIMIT) {
        request.setAttribute("user", user);
        // Người dùng đã xác minh trong thời hạn 30 phút
        RequestDispatcher dispatcher = this.getServletContext()
            .getRequestDispatcher("/WEB-INF/user/report-key.jsp");
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
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    HttpSession session = request.getSession();
    Users user = MyUtils.getLoginedUser(session);
    Boolean isVerified = (Boolean) session.getAttribute("isAllowedEditedKeyPair");
    if (!isVerified) {
      doGet(request, response);
    } else {
      UserPublicKeyDAO userPublicKeyDAO = new UserPublicKeyDAOImpl();
      KeyReportDao keyReportDao = new KeyReportDaoImpl();

      LocalDateTime timeCreated = LocalDateTime.now();
      RequestInfo requestInfo = new RequestInfo(request.getRemoteAddr(), "HCM", "VietNam");
      // Lấy các giá trị từ form
      String reason = request.getParameter("reason");
      String startDateStr = request.getParameter("startDate");
      String endDateStr = request.getParameter("endDate");

      // Chuyển đổi chuỗi sang LocalDateTime
      LocalDateTime startDate = null;
      LocalDateTime endDate = null;

      try {
        startDate = LocalDateTime.parse(startDateStr + "T00:00:00");
        endDate = LocalDateTime.parse(endDateStr + "T23:59:59");
      } catch (Exception e) {
        request.setAttribute("isReportKeySuccess", false);
        request.setAttribute("errorMessage", "Invalid date format.");
        doGet(request, response);
        return;
      }

      // Viết thêm log khi user report key
      if (userPublicKeyDAO.getUserPublicKey(user.getId()) != null) {
        // khóa key hiện tại
        UserPublicKey userPublicKeyPreValue = userPublicKeyDAO.getUserPublicKey(user.getId());
        userPublicKeyPreValue.setStatus(PublicKeyStatus.BANNED);
        userPublicKeyDAO.setStatusUserPublicKey(user.getId(),
            userPublicKeyPreValue.getIdPublicKey(), PublicKeyStatus.BANNED.name());

        // thêm vào report
        KeyReport keyReport = new KeyReport();
        keyReport.setStartDate(startDate);
        keyReport.setPublicKeyId(userPublicKeyPreValue.getId());
        keyReport.setEndDate(endDate);
        keyReport.setReason(reason);
        System.out.println("long cacwj" + keyReport);
        System.out.println("luuw vaof key report");
        keyReportDao.addNewKeyReport(keyReport);

        Log<UserPublicKey> usersPublicKeyLog = new Log<>();
        usersPublicKeyLog.setIp(requestInfo.getIp());
        usersPublicKeyLog.setAddress(requestInfo.getAddress());
        usersPublicKeyLog.setNational(requestInfo.getNation());
        usersPublicKeyLog.setLevel(LogLevels.WARNING);
        usersPublicKeyLog.setNote(LogNote.USER_REPORT_PUBLIC_KEY.getLevel());
        usersPublicKeyLog.setCreateAt(timeCreated);
        usersPublicKeyLog.setCurrentValue(MyUtils.convertToJson(userPublicKeyPreValue));
        AbsDAO<UserPublicKey> absUserDao = new AbsDAO<>();
        absUserDao.insert(usersPublicKeyLog);
        request.setAttribute("isReportKeySuccess", true);
        doGet(request, response);
      } else {
        request.setAttribute("isReportKeySuccess", false);
        doGet(request, response);
      }


    }
  }
}
