package nhom55.hcmuaf.dao;

import nhom55.hcmuaf.beans.UserPublicKey;
import nhom55.hcmuaf.enums.PublicKeyStatus;

import java.time.LocalDateTime;

public interface UserPublicKeyDAO {
    UserPublicKey getUserPublicKey(int idUser);
    public String getPublicKeyOfUser(int idUser) ;
    void insertUserPublicKey(int idUser, int idPublicKey, PublicKeyStatus status);
    void insertPublicKey(String key, LocalDateTime createDate);
    int getIdPublicKey(LocalDateTime createDate);

}
