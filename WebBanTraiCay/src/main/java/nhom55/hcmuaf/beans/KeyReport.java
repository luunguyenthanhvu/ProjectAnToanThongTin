package nhom55.hcmuaf.beans;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeyReport {

  int id;
  int publicKeyId;
  LocalDateTime startDate;
  LocalDateTime endDate;
  String reason;
}
