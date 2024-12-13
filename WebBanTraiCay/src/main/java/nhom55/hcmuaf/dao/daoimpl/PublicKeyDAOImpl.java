package nhom55.hcmuaf.dao.daoimpl;

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
}
