package nhom55.hcmuaf.download_tool;

import nhom55.hcmuaf.beans.Users;
import nhom55.hcmuaf.util.MyUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "DownloadController", value = "/download/*")
public class DownloadController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private void doDownloadPrivateKey(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        var session = request.getSession(); {
//            session.setAttribute("privateKey_123", "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");
//        }
        HttpSession session = request.getSession();
        Users user = MyUtils.getLoginedUser(session);
        // lấy privateKey string trong session
        // giả sử user đang đăng nhập hiện tại có id là 123
        String privateKey = String.format("Private key:\n%s", session.getAttribute("privateKey_"+user.getId()).toString());

        // Chuyển chuỗi thành byte array (nhị phân)
        byte[] binaryData = privateKey.getBytes(StandardCharsets.UTF_8);

        // Thiết lập thông tin response để tải về file .pem
        response.setContentType("application/x-pem-file");
        response.setHeader("Content-Disposition", "attachment; filename=\"private_key.pem\"");

        // Ghi nội dung nhị phân vào response
        response.getOutputStream().write(binaryData);
        response.getOutputStream().flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        switch (pathInfo) {
            case "/cipher-tool": {
                doDownloadTool(request, response);
            }
            case "/private-key": {
                doDownloadPrivateKey(request, response);
            }
        }
    }

    public void doDownloadTool(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Đường dẫn tới file
        File file = new File("C:\\Users\\Yukihira Souma\\Desktop\\Project_Cuoi_Ky_ATBM\\ProjectAnToanThongTin\\cipher-tools-rework\\cipher-tool.exe");
        if (!file.exists()) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"File not found\"}");
            return;
        }

        // Thiết lập header response để tải file
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        response.setContentLengthLong(file.length());

        // Gửi nội dung file về client
        try (FileInputStream fileInputStream = new FileInputStream(file);
             OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    public static void main(String[] args) {
        String path = DownloadController.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        System.out.println("File đang chạy từ: " + path);
    }
}
