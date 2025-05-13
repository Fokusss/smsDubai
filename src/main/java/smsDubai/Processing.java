package smsDubai;

import java.util.Arrays;
import java.util.List;

public class Processing {
  List<String[]> data;
  int day;

  public Processing(List<String[]> update, int day) {
      this.day = day;
      this.data = update;
  }

  public String printUpdate() {
    String str = "";
    for (String[] row : data) {
      System.out.println(row);
      str += Arrays.toString(row);
    }
    return str.substring(0, Math.min(str.length(), 4000));
  }
}
