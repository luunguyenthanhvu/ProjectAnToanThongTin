package nhom55.hcmuaf.util;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpSession;
import nhom55.hcmuaf.beans.Bills;
import nhom55.hcmuaf.beans.Users;
import nhom55.hcmuaf.websocket.entities.CartsEntityWebSocket;
import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;

public class MyUtils {

  public static final String ATT_NAME_USER_NAME = "ATTRIBUTE_FOR_STORE_USER_NAME_IN_COOKIE";
  private static final ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
        false);
  }

  private static final Map<String, HttpSession> userHashSession = new ConcurrentHashMap<>();

  public MyUtils() {
    super();
  }

  public static String encodePass(String pass) {
    return DigestUtils.md5Hex(pass);
  }

  public static String createHash() {
    Random random = new Random();
    random.nextInt(999999);
    return DigestUtils.md5Hex(random.toString());
  }

  /**
   * Store user info in Session.
   *
   * @param session
   * @param loginedUser
   */
  public static void storeLoginedUser(HttpSession session, Users loginedUser) {
    session.setAttribute("loginedUser", loginedUser);
  }

  /**
   * Create a new cart for user
   *
   * @param session
   * @param cart
   */
  public static void storeCart(HttpSession session, CartsEntityWebSocket cart) {
    session.setAttribute("cart", cart);
  }

  /**
   * remove their cart from session
   *
   * @param session
   */
  public static void removeCart(HttpSession session) {
    session.removeAttribute("cart");
  }

  /**
   * get cart from session
   *
   * @param session
   * @return
   */
  public static CartsEntityWebSocket getCart(HttpSession session) {
    CartsEntityWebSocket cart = (CartsEntityWebSocket) session.getAttribute("cart");
    return cart;
  }

  /**
   * Get the user information stored in the session.
   *
   * @param session
   * @return
   */
  public static Users getLoginedUser(HttpSession session) {
    Users loginedUser = (Users) session.getAttribute("loginedUser");
    return loginedUser;
  }

  /**
   * logout user
   *
   * @param session
   */
  public static void removeLoginedUser(HttpSession session) {
    session.removeAttribute("loginedUser");
    removeRole(session);
  }

  /**
   * @param session
   */
  public static void removeRole(HttpSession session) {
    session.removeAttribute("role");
  }

  /**
   * set role for authentication
   *
   * @param session
   * @param role
   */
  public static void setUserRole(HttpSession session, String role) {
    session.setAttribute("role", role);
  }

  public static String getUserRole(HttpSession session) {
    return (String) session.getAttribute("role");
  }

  /**
   * set url for session
   *
   * @param session
   * @param url
   */
  public static void setPreviousURL(HttpSession session, String url) {
    session.setAttribute("previousURL", url);
  }

  public static String convertToJson(Object object) {
    try {
      String json = objectMapper.writeValueAsString(object);
      return json;
    } catch (Exception e) {
      return null;
    }
  }


  public static <T> T convertJsonToObject(String json, Class<T> valueType) {
    try {
      return objectMapper.readValue(json, valueType);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static HttpSession getSessionFromId(String id) {
    return userHashSession.get(id);
  }

  /**
   * This method use for hash bill to verify signature
   *
   * @param bills
   * @return hash bill
   */
  public static String convertBillsJson(Bills bills) {
//    return convertToJson(Bills
//        .builder()
//        .orderedDate(bills.getOrderedDate())
//        .productList(bills.getProductList())
//        .userId(bills.getUserId())
//        .firstName(bills.getFirstName())
//        .lastName(bills.getLastName())
//        .streetAddress(bills.getStreetAddress())
//        .city(bills.getCity())
//        .phoneNumber(bills.getPhoneNumber())
//        .deliveryFee(bills.getDeliveryFee())
//        .email(bills.getEmail())
//        .totalPrice(bills.getTotalPrice())
//        .note(bills.getNote())
//        .build());
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("lastName", bills.getLastName());
    jsonObject.put("firstName", bills.getFirstName());
    jsonObject.put("address", bills.getAddress());
    jsonObject.put("city", bills.getCity());
    jsonObject.put("orderDate", bills.getOrderedDate());
    jsonObject.put("phoneNumber", bills.getPhoneNumber());
    jsonObject.put("email", bills.getEmail());
    jsonObject.put("deliveryFee", bills.getDeliveryFee());
    jsonObject.put("subTotalPrice", bills.getTotalPrice());
    jsonObject.put("productNameList", bills.getProductList());
    return jsonObject.toJSONString();
  }
}
