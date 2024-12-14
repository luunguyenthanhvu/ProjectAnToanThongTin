package nhom55.hcmuaf.dao;

import nhom55.hcmuaf.beans.UserPublicKey;

public interface UserPublicKeyDAO {

  UserPublicKey getUserPublicKey(int idUser);

  /**
   * Insert new userPublicKey
   */
  int insertUserPublicKey(UserPublicKey userPublicKey);

  /**
   * Get UserPublicKey IN_USE
   */
  UserPublicKey userPublicKey(int userId);
}
