package nhom55.hcmuaf.dao.daoimpl;

import java.time.LocalDateTime;
import nhom55.hcmuaf.beans.PublicKey;
import nhom55.hcmuaf.dao.PublicKeyDAO;
import nhom55.hcmuaf.database.JDBIConnector;

public class PublicKeyDAOImpl implements PublicKeyDAO {

  @Override
  public int insertPublicKey(PublicKey publicKey) {
    return JDBIConnector.get().withHandle(handle -> {
      return handle.createUpdate("INSERT INTO public_key(`key`,createDate)"
              + "VALUES (:key, :createDate)")
          .bind("key", publicKey.getKey())
          .bind("createDate", publicKey.getCreateDate())
          .executeAndReturnGeneratedKeys("id")
          .mapTo(int.class)
          .one();
    });

  }

  @Override
  public PublicKey getPublicKey(int id) {
    return JDBIConnector.get().withHandle(handle -> {
      return handle.createQuery("SELECT * FROM public_key where id = :id")
          .bind("id", id)
          .mapToBean(PublicKey.class)
          .findOne()
          .orElse(null);
    });
  }

  public PublicKey getLatestPublicKeyBefore(int userID, LocalDateTime orderDate) {
    return JDBIConnector.get().withHandle(handle -> {
      return handle.createQuery(
              "SELECT pk.* " +
                  "FROM public_key pk " +
                  "JOIN user_public_key upk ON pk.id = upk.idPublicKey " +
                  "WHERE upk.idUser = :userID AND pk.createDate <= :orderDate " +
                  "ORDER BY pk.createDate DESC " +
                  "LIMIT 1")
          .bind("userID", userID)
          .bind("orderDate", orderDate)
          .mapToBean(PublicKey.class)
          .findOne()
          .orElse(null);
    });
  }
}
