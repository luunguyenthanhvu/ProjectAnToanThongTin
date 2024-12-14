package nhom55.hcmuaf.dao.daoimpl;

import nhom55.hcmuaf.beans.UserPublicKey;
import nhom55.hcmuaf.dao.UserPublicKeyDAO;
import nhom55.hcmuaf.database.JDBIConnector;
import nhom55.hcmuaf.enums.PublicKeyStatus;

public class UserPublicKeyDAOImpl implements UserPublicKeyDAO {

  @Override
  public UserPublicKey getUserPublicKey(int idUser) {
    return JDBIConnector.get().withHandle(
        h -> h.createQuery(
                "SELECT * FROM user_public_key WHERE idUser = :idUser and status = 'IN_USE'")
            .bind("idUser", idUser).mapToBean(UserPublicKey.class).one());

  }

  @Override
  public int insertUserPublicKey(UserPublicKey userPublicKey) {
    return JDBIConnector.get().withHandle(handle -> {
      return handle.createUpdate("INSERT INTO user_public_key(idUser,idPublicKey, status)"
              + "VALUES (:idUser, :idPublicKey, :status)")
          .bind("idUser", userPublicKey.getIdUser())
          .bind("idPublicKey", userPublicKey.getIdPublicKey())
          .bind("status", userPublicKey.getStatus())
          .executeAndReturnGeneratedKeys("id")
          .mapTo(int.class)
          .one();
    });

  }

  @Override
  public UserPublicKey userPublicKey(int userId) {
    // Lấy public key đang được sử dụng (status = 'IN_USE')
    return JDBIConnector.get().withHandle(handle ->
        handle.createQuery(
                "SELECT * FROM user_public_key WHERE idUser = :userId AND status = :status")
            .bind("userId", userId)
            .bind("status",
                PublicKeyStatus.IN_USE.getLevel()) // Sử dụng enum để đảm bảo tính nhất quán
            .mapToBean(UserPublicKey.class)
            .findOne() // Tìm một kết quả
            .orElse(null) // Nếu không có kết quả, trả về null
    );
  }
}
