package lab1.cases;

public class String {
  java.lang.String value;

  public void setValue(java.lang.String string) {
    this.value = string;
  }

  public void setValue(String other) {
    this.value = other.getValue();
  }

  public java.lang.String getValue() {
    return value;
  }
}
