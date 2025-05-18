package smsDubai;

import java.text.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Processing {
  String[] data;
  int dayLane;
  String[] lastLane;
  String[] firstLane;
  String ruPref = " ₽";
  String aedPref = " AED";

  public Processing(List<String[]> update, int day) {
      this.dayLane = day + 7;
      this.data = update.get(this.dayLane);
      this.firstLane = update.get(5);
      this.lastLane = update.get(whatLastLane(this.firstLane) + 8);
  }

  private int whatLastLane(String[] lane) {
    String countDayMonth = lane[19];
    return Integer.parseInt(countDayMonth);
  }

  public String printUpdate() {
    String str = "Good evening \uD83C\uDF0C \n \n" +
            "OM/Shvili/RC\n";
    /*System.out.println(Arrays.toString(data));
    System.out.println(Arrays.toString(firstLane));
    System.out.println(Arrays.toString(lastLane));*/
    str += printDate();
    str += "\n";
    str += "Daytime:\n";
    str += "Morning: " + dayTimePrint(this.data[6], this.data[7], this.data[8], this.data[9]);
    str += "Afternoon: " + dayTimePrint(this.data[10], this.data[11], this.data[12], this.data[13]);
    str += "Evening: " + dayTimePrint(this.data[14], this.data[15], this.data[16], this.data[17]);
    str += "Total (gross after discounts): " + this.data[18] + " / " + formatterValue(this.data[27], aedPref) + " / " + formatterValue(this.data[28], ruPref) + "\n";
    str += "Total (net): " + this.data[18] + " / " + formatterValue(this.data[29], aedPref) + " / " + formatterValue(this.data[30], ruPref) + "\n";
    str += "\n";
    str += "Categories: \n";
    str += "In restaurant: " + categoriesPrint(this.data[54], this.data[55], this.data[56]);
    str += "\n";
    str += "Careem: " + categoriesPrint(this.data[57], this.data[58], this.data[59]);
    str += "Deliveroo: " + categoriesPrint(this.data[60], this.data[61], this.data[62]);
    str += "TALABAT: " + categoriesPrint(this.data[63], this.data[64], this.data[65]);
    str += "Noon: " + categoriesPrint(this.data[66], this.data[67], this.data[68]);
    str += "Qlab payment: " + categoriesPrint(this.data[73], this.data[74], this.data[75]);
    str += "\n";
    str += "For Restaurant Combo:\n";
    str += "Food revenue: " + this.data[76] + this.aedPref + "\n";
    str += "Shvili: " + this.data[77] + this.aedPref + " / " + formatterProc(this.data[86], "#") + "\n";
    str += "OM: " + this.data[78] + this.aedPref + " / " + formatterProc(this.data[85], "#") + "\n";
    str += "\n";
    str += "Finance:\n";
    str += "Average check: " + this.data[31] + this.aedPref + " / " + this.data[32] + this.ruPref+ "\n";
    str += "Average check per guest: " + this.data[33] + this.aedPref + " / " + this.data[34] + this.ruPref+ "\n";
    str += "\n";
    str += "SSS:\n";
    str += "SSS DAY (2024): " + formatterProc(this.data[38], "#") + "\n";
    str += "SSS WEEK (2024): " + formatterProc(this.data[39], "#") + "\n";
    str += "SSS MONTH  (2024): " + formatterProc(this.data[40], "#") + "\n";
    str += "\n";
    str += "Delta BC: " + formatterProc(this.data[41], "#") + "\n";
    str += "\n";
    str += "Plan vs forecast for the end of the month: " + this.firstLane[6] + "/ " + this.lastLane[35] + " / " + formatterProc(this.lastLane[37], "#") + "\n";
    str += "\n";
    str += "Best regards, ";

    return str.substring(0, Math.min(str.length(), 4000));
  }

  private String printDate() {
    String str = "Revenue ";
    DateFormatSymbols dfs = new DateFormatSymbols(new Locale("ru"));
    dfs.setShortMonths(new String[]{
            "янв", "февр", "мар", "апр", "мая", "июн",
            "июл", "авг", "сент", "окт", "нояб", "дек", ""
    });
    String dayMonthYear = this.data[2].replace(".", "").trim();
    SimpleDateFormat parser = new SimpleDateFormat("dd MMM yy", dfs);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    SimpleDateFormat dateWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH);
    try {
      Date date = parser.parse(dayMonthYear);
      str += dateFormat.format(date) + " / " + dateWeek.format(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return str + "\n";
  }

  private String afterSimbol(String value) {
    value = value.replaceAll("[\\s\\u00A0\\u2007\\u202F]", "");
    value = value.replaceAll("%", "");
    value = value.replaceAll("\\u0022", "");
    value = value.replace(",", ".");
    return value;
  }

  private String formatterValue(String value, String pref) {
    value = afterSimbol(value);
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
    symbols.setGroupingSeparator(' ');
    DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
    return decimalFormat.format(Integer.parseInt(value)) + pref;
  }

  private String formatterValue(String value) {
    return formatterValue(value, "");
  }

  private String formatterProc(String value, String pattern) {
    DecimalFormat decimalFormat = new DecimalFormat("#." + pattern);
    //System.out.println("v1" + value);
    value = afterSimbol(value);
    //System.out.println("v2" + value);
    String str = decimalFormat.format(Double.parseDouble(value));
    return str.replace(".", ",") + " %";
  }

  private String dayTimePrint(String chek, String aed, String rub, String proc) {
    String str = chek + " / " + formatterValue(aed, this.aedPref ) + " / ";
    str += formatterValue(rub, this.ruPref) + " / ";
    str += formatterProc(proc, "##");
    str += "\n";
    return str;
  }

  private String categoriesPrint(String chek, String aed, String proc) {
    String str = chek + " / " + formatterValue(aed, this.aedPref) + " / ";
    str += formatterProc(proc, "##");
    str += "\n";
    return str;
  }


}
