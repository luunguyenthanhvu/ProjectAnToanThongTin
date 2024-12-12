package nhom55.hcmuaf.beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import nhom55.hcmuaf.log.IModel;
import nhom55.hcmuaf.log.Log;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicKey extends Log<PublicKey> implements Serializable, IModel {

  int id;
  String key;
  LocalDateTime createDate;

  @Override
  public String getTable() {
    return "public_key";
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
