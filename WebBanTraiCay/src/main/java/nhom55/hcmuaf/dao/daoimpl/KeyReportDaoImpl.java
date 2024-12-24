package nhom55.hcmuaf.dao.daoimpl;

import nhom55.hcmuaf.beans.KeyReport;
import nhom55.hcmuaf.dao.KeyReportDao;
import nhom55.hcmuaf.database.JDBIConnector;

public class KeyReportDaoImpl implements KeyReportDao {

  @Override
  public void addNewKeyReport(KeyReport keyReport) {
    try {
      System.out.println("bắt đầu report");
      JDBIConnector.get().useHandle(handle ->
          handle.createUpdate(
                  "INSERT INTO key_report (public_key_id, start_date, end_date, reason) VALUES (:publicKeyId, :startDate, :endDate, :reason)")
              .bind("publicKeyId", keyReport.getPublicKeyId())
              .bind("startDate", keyReport.getStartDate())
              .bind("endDate", keyReport.getEndDate())
              .bind("reason", keyReport.getReason())
              .execute()
      );
    } catch (Exception e) {
      e.printStackTrace();  // In ra thông tin lỗi
    }
  }


  @Override
  public KeyReport getKeyReportByPublicKeyId(int id) {
    return JDBIConnector.get().withHandle(handle ->
        handle.createQuery("SELECT id, public_key_id AS publicKeyId, start_date AS startDate, " +
                "end_date AS endDate, reason FROM key_report WHERE public_key_id = :publicKeyId")
            .bind("publicKeyId", id)
            .mapToBean(KeyReport.class)
            .findOne()
            .orElse(null) // Trả về null nếu không tìm thấy
    );
  }
}