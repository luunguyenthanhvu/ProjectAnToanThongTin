package nhom55.hcmuaf.dao.daoimpl;

import nhom55.hcmuaf.beans.UserPublicKey;
import nhom55.hcmuaf.dao.UserPublicKeyDAO;
import nhom55.hcmuaf.database.JDBIConnector;

public class UserPublicKeyDAOImpl implements UserPublicKeyDAO {
    @Override
    public UserPublicKey getUserPublicKey(int idUser) {
        return JDBIConnector.get().withHandle(
                h -> h.createQuery("SELECT * FROM user_public_key WHERE idUser = :idUser and status = 'IN_USE'")
                        .bind("idUser", idUser).mapToBean(UserPublicKey.class).one());

    }
}
