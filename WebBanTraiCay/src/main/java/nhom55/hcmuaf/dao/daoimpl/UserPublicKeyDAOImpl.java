package nhom55.hcmuaf.dao.daoimpl;

import nhom55.hcmuaf.beans.UserPublicKey;
import nhom55.hcmuaf.dao.UserPublicKeyDAO;
import nhom55.hcmuaf.database.JDBIConnector;

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
}
