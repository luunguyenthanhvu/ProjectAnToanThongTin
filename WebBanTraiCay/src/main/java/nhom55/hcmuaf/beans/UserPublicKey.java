package nhom55.hcmuaf.beans;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import nhom55.hcmuaf.enums.PublicKeyStatus;
import nhom55.hcmuaf.log.IModel;
import nhom55.hcmuaf.log.Log;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPublicKey extends Log<PublicKey> implements Serializable, IModel {

  int id;
  int idUser;
  int idPublicKey;
  PublicKeyStatus status;

  @Override
  public String getTable() {
    return "user_public_key";
  }

  @Override
  public String getBeforeData() {
    return super.getPreValue();
  }

  @Override
  public String GetAfterData() {
    return super.getCurrentValue();
  }
}
