package nhom55.hcmuaf.enums;

public enum LogNote {
  INSERT_PUBLIC_KEY("Public key mới vừa được tạo!"),
  USER_CREATE_PUBLIC_KEY("User vừa tạo public key!"),
  BAN_PUBLIC_KEY("Public key vừa bị report!"),
  USER_REPORT_PUBLIC_KEY("User vừa report key!");

  private final String note;

  LogNote(String note) {
    this.note = note;
  }

  public String getLevel() {
    return note;
  }
}
