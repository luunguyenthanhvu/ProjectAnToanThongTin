package nhom55.hcmuaf.controller.api;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nhom55.hcmuaf.dto.response.MessageResponseDTO;
import nhom55.hcmuaf.my_handle_exception.MyHandleException;
import nhom55.hcmuaf.services.BillService;
import nhom55.hcmuaf.services_remaster.ProductService;
import nhom55.hcmuaf.util.MyUtils;

@WebServlet(name = "OrderDetailsAPI", value = "/api/order-details/*")
public class OrderDetailsAPI extends HttpServlet {

  private ProductService productService;
  private BillService billService;
  private final String REQUEST_BODY = "request-body";

  @Override
  public void init() throws ServletException {
    super.init();
    // Initialize the ProductService here
    this.productService = new ProductService();
    this.billService = new BillService();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    String id = request.getPathInfo();
    if (id == null || id.length() <= 1) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID");
      return;
    }

    id = id.substring(1); // Remove leading '/'

    try (PrintWriter out = response.getWriter()) {
      productService.begin();
      out.println(MyUtils.convertToJson(productService.getShipmentDetails(Integer.valueOf(id))));
      productService.save();
    } catch (NumberFormatException e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
    } catch (Exception e) {
      productService.rollback();
      e.printStackTrace(); // Log to file instead of printing stack trace
      throw new MyHandleException("Server error", 500);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = response.getWriter();
    try {
      String context = request.getPathInfo();
      String requestDTO = (String) request.getAttribute(REQUEST_BODY);
      switch (context) {
        case "/check-signature":
          MessageResponseDTO message = billService.checkVerifyUserBill(requestDTO,request);
          out.println(MyUtils.convertToJson(message));
          break;
      }


    } catch (Exception e) {
      e.printStackTrace();
      throw new MyHandleException("Loi server", 500);
    } finally {
      System.out.println("flush và flush buffer");
      out.flush();
      response.flushBuffer();
    }
  }
}
