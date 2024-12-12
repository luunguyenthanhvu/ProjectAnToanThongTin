package nhom55.hcmuaf.enums;

public enum PublicKeyStatus {
  IN_USE("IN_USE"),
  BANNED("BANNED");


  private final String level;

  PublicKeyStatus(String level) {
    this.level = level;
  }

  public String getLevel() {
    return level;
  }
}
