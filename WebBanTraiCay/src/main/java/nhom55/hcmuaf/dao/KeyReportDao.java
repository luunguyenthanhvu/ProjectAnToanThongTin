package nhom55.hcmuaf.dao;

import nhom55.hcmuaf.beans.KeyReport;

public interface KeyReportDao {

  void addNewKeyReport(KeyReport keyReport);

  KeyReport getKeyReportByPublicKeyId(int id);
}
