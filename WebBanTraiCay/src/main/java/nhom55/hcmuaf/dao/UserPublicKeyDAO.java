package nhom55.hcmuaf.dao;

import nhom55.hcmuaf.beans.PublicKey;
import nhom55.hcmuaf.beans.UserPublicKey;
import nhom55.hcmuaf.enums.PublicKeyStatus;

import java.time.LocalDateTime;

public interface UserPublicKeyDAO {
    UserPublicKey getUserPublicKey(int idUser);
     PublicKey getPublicKey(int idPublicKey);
    public String getPublicKeyOfUser(int idUser) ;
    void insertUserPublicKey(int idUser, int idPublicKey, PublicKeyStatus status);
    void insertPublicKey(String key, LocalDateTime createDate);
    int getIdPublicKey(LocalDateTime createDate);
    public boolean setStatusUserPublicKey(int idUser,int idPublicKey, String status);

  /**
   * Insert new userPublicKey
   */
  int insertUserPublicKey(UserPublicKey userPublicKey);

  /**
   * Get UserPublicKey IN_USE
   */
  UserPublicKey userPublicKey(int userId);
}
