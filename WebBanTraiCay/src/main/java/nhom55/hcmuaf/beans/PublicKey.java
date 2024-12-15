package nhom55.hcmuaf.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import nhom55.hcmuaf.log.IModel;
import nhom55.hcmuaf.log.Log;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({"id", "ip", "level", "address", "national", "note", "preValue",
    "currentValue", "createAt", "updateAt"})
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
