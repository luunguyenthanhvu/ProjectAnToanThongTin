package nhom55.hcmuaf.dao;

import nhom55.hcmuaf.beans.PublicKey;

public interface PublicKeyDAO {

  /**
   * Insert new publicKey
   */
  int insertPublicKey(PublicKey publicKey);

  /**
   * Get public key by id
   */
  PublicKey getPublicKey(int id);
}
