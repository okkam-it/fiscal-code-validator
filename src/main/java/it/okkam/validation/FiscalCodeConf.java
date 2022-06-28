package it.okkam.validation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * Configuration class.
 */
@Getter
@Setter
public class FiscalCodeConf implements Serializable {

  private static final long serialVersionUID = 1L;
  private final Map<String, List<String>> comuniMap;
  private final String maleValue;
  private final int yearStart;
  private final int yearEnd;
  private final int monthStart;
  private final int monthEnd;
  private final int dayStart;
  private final int dayEnd;

  protected FiscalCodeConf() {
    this(null, null, 8, 10, 3, 5, 0, 2);
  }

  /**
   * Config.
   *
   * @param comuniMap comuniMap
   * @param maleValue male value
   * @param yearStart year start index
   * @param yearEnd year end index
   * @param monthStart month start index
   * @param monthEnd month end index
   * @param dayStart day start index
   * @param dayEnd day end index
   */
  public FiscalCodeConf(Map<String, List<String>> comuniMap, String maleValue, int yearStart,
      int yearEnd, int monthStart, int monthEnd, int dayStart, int dayEnd) {
    this.comuniMap = comuniMap;
    this.maleValue = maleValue;
    this.yearStart = yearStart;
    this.yearEnd = yearEnd;
    this.monthStart = monthStart;
    this.monthEnd = monthEnd;
    this.dayStart = dayStart;
    this.dayEnd = dayEnd;
  }

}
