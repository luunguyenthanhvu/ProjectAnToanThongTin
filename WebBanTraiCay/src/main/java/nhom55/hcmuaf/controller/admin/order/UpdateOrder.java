package nhom55.hcmuaf.controller.admin.order;


import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import nhom55.hcmuaf.beans.BillDetails;
import nhom55.hcmuaf.beans.Bills;
import nhom55.hcmuaf.beans.Users;
import nhom55.hcmuaf.dao.BillDao;
import nhom55.hcmuaf.dao.UsersDao;
import nhom55.hcmuaf.dao.daoimpl.BillDaoImpl;
import nhom55.hcmuaf.dao.daoimpl.UsersDaoImpl;
import nhom55.hcmuaf.services.BillService;
import nhom55.hcmuaf.util.MyUtils;

@WebServlet(name = "UpdateOrder", value = "/admin/provider/updateOrder")
public class UpdateOrder extends HttpServlet {

  BillService billService;

  @Override
  public void init() throws ServletException {
    super.init();
    // Initialize the ProductService here
    this.billService = BillService.getInstance();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    int idBill = Integer.valueOf(request.getParameter("idOrder"));
    HttpSession session = request.getSession();
    Users admin = MyUtils.getLoginedUser(session);
    BillDao orderDao = new BillDaoImpl();
    UsersDao usersDao = new UsersDaoImpl();
    List<BillDetails> detailList = orderDao.getListProductInABill(idBill);
    Bills bill = orderDao.getABill(idBill);
    int idUser = orderDao.getIdUser(idBill);
    Users users = null;
    for (Users u : usersDao.showInfoUser()) {
      if (u.getId() == idUser) {
        users = u;
        break;
      }
    }
    request.setAttribute("user", users);
    request.setAttribute("admin", admin);
    request.setAttribute("bill", bill);
    request.setAttribute("detailList", detailList);
    request.setAttribute("idBill", idBill);
    RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/admin/update-order.jsp");
    dispatcher.forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    int idBill = Integer.valueOf(request.getParameter("idBill"));
    String status = request.getParameter("selectedStatus");

    billService.updateStatusBill(idBill, status);
    response.sendRedirect("/admin/order/order-list");
  }
}
