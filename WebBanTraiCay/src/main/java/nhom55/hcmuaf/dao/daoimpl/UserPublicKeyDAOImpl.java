package nhom55.hcmuaf.dao.daoimpl;

import nhom55.hcmuaf.beans.PublicKey;
import nhom55.hcmuaf.beans.UserPublicKey;
import nhom55.hcmuaf.dao.UserPublicKeyDAO;
import nhom55.hcmuaf.database.JDBIConnector;
import nhom55.hcmuaf.enums.PublicKeyStatus;

import java.time.LocalDateTime;

public class UserPublicKeyDAOImpl implements UserPublicKeyDAO {

    @Override
    public UserPublicKey getUserPublicKey(int idUser) {
        try {
            return JDBIConnector.get().withHandle(
                    h -> h.createQuery("SELECT * FROM user_public_key WHERE idUser = :idUser and status = 'IN_USE'")
                            .bind("idUser", idUser)
                            .mapToBean(UserPublicKey.class)
                            .one()
            );
        } catch (Exception e) {
            System.err.println("Error fetching UserPublicKey for idUser=" + idUser + ": " + e.getMessage());
            return null;
        }
    }
    @Override
    public PublicKey getPublicKey(int idPublicKey){
        try {
            return JDBIConnector.get().withHandle(
                    h -> h.createQuery("SELECT * FROM public_key where id = :idPublicKey")
                            .bind("idPublicKey", idPublicKey)
                            .mapToBean(PublicKey.class)
                            .one()
            );
        } catch (Exception e) {
            System.err.println("Error fetching PublicKey for idPublicKey=" + idPublicKey + ": " + e.getMessage());
            return null;
        }
    }
    @Override
    public String getPublicKeyOfUser(int idUser) {
        String key ="";
       try{
           key = JDBIConnector.get().withHandle(h ->
                   h.createQuery("SELECT p.key FROM user_public_key u join public_key p on u.idPublicKey = p.id WHERE idUser = :idUser and status = 'IN_USE'")
                           .bind("idUser", idUser)
                           .mapTo(String.class)
                           .one()
           );
           return key;
       }catch (Exception e) {
           System.err.println("Error fetching PublicKey for idUser=" + idUser + ": " + e.getMessage());
           return null;
       }
    }

    @Override
    public void insertUserPublicKey(int idUser, int idPublicKey, PublicKeyStatus status) {
        try {
            JDBIConnector.get().withHandle(h -> {
                // Update trạng thái từ IN_USE sang BANNED
                h.createUpdate("UPDATE user_public_key SET status = :bannedStatus WHERE idUser = :idUser AND status = :inUseStatus")
                        .bind("idUser", idUser)
                        .bind("bannedStatus", PublicKeyStatus.BANNED)
                        .bind("inUseStatus", PublicKeyStatus.IN_USE)
                        .execute();

                // Insert bản ghi mới
                return h.createUpdate("INSERT INTO user_public_key (idUser, idPublicKey, status) VALUES (:idUser, :idPublicKey, :status)")
                        .bind("idUser", idUser)
                        .bind("idPublicKey", idPublicKey)
                        .bind("status", status)
                        .execute();
            });
        } catch (Exception e) {
            System.err.println("Error inserting UserPublicKey (idUser=" + idUser + ", idPublicKey=" + idPublicKey + "): " + e.getMessage());
        }
    }

    @Override
    public void insertPublicKey(String key, LocalDateTime createDate) {
        try {
            JDBIConnector.get().withHandle(h ->
                    h.createUpdate("INSERT INTO public_key (`key`, createDate) VALUES (:key, :createDate)")
                            .bind("key", key)
                            .bind("createDate", createDate)
                            .execute()
            );
        } catch (Exception e) {
            System.err.println("Error inserting PublicKey (key=" + key + "): " + e.getMessage());
        }
    }

    @Override
    public int getIdPublicKey(LocalDateTime createDate) {
        try {
            return JDBIConnector.get().withHandle(h ->
                    h.createQuery("SELECT id FROM public_key WHERE createDate = :createDate")
                            .bind("createDate", createDate)
                            .mapTo(Integer.class)
                            .one()
            );
        } catch (Exception e) {
            System.err.println("Error fetching PublicKey ID for createDate=" + createDate + ": " + e.getMessage());
            return -1; // Giá trị mặc định khi xảy ra lỗi
        }
    }
    @Override
    public boolean setStatusUserPublicKey(int idUser,int idPublicKey, String status) {
        try {
            JDBIConnector.get().withHandle(h ->
                    h.createUpdate("UPDATE user_public_key SET status = :status WHERE idUser = :idUser AND idPublicKey = :idPublicKey and status = 'IN_USE'")
                            .bind("idUser", idUser)
                            .bind("idPublicKey", idPublicKey)
                            .bind("status", status)
                            .execute()
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error updating UserPublicKey (idUser=" + idUser + ", idPublicKey=" + idPublicKey + "): " + e.getMessage());
            return false;
        }
    }
}
