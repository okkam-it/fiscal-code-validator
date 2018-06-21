package it.okkam.validation;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FiscalCodeValidator {

  private FiscalCodeValidator() {
    throw new IllegalStateException("Utility class");
  }

  private static final String UNSUPPORTED = " unsupported value";
  private static final String CONTROL = "Control character ";
  private static final char[] VOCALS = new char[] {'A', 'E', 'I', 'O', 'U'};

  private static final String[] ACCENTED_LETTERS = new String[] { //
      "À", "Á", "Ä", "Â", //
      "È", "É", "Ë", "Ê", //
      "Ì", "Í", "Ï", "Î", //
      "Ò", "Ó", "Ö", "Ô", //
      "Ù", "Ú", "Ü", "Û"};

  private static final String[] ACCENTED_LETTERS_REPLACEMENT = new String[] { //
      "A'", "A'", "A", "A", //
      "E'", "E'", "E", "E", //
      "I'", "I'", "I", "I", //
      "O'", "O'", "O", "O", //
      "U'", "U'", "U", "U"};

  private static final Map<Integer, String> monthValues;
  private static final HashMap<String, Integer> oddSumValues;
  private static final HashMap<String, Integer> evenSumValues;
  private static final HashMap<Integer, String> controlCharValues;

  static {
    monthValues = new HashMap<>();
    monthValues.put(1, "A");
    monthValues.put(2, "B");
    monthValues.put(3, "C");
    monthValues.put(4, "D");
    monthValues.put(5, "E");
    monthValues.put(6, "H");
    monthValues.put(7, "L");
    monthValues.put(8, "M");
    monthValues.put(9, "P");
    monthValues.put(10, "R");
    monthValues.put(11, "S");
    monthValues.put(12, "T");

    oddSumValues = new HashMap<>();
    oddSumValues.put("0", 1);
    oddSumValues.put("1", 0);
    oddSumValues.put("2", 5);
    oddSumValues.put("3", 7);
    oddSumValues.put("4", 9);
    oddSumValues.put("5", 13);
    oddSumValues.put("6", 15);
    oddSumValues.put("7", 17);
    oddSumValues.put("8", 19);
    oddSumValues.put("9", 21);
    oddSumValues.put("A", 1);
    oddSumValues.put("B", 0);
    oddSumValues.put("C", 5);
    oddSumValues.put("D", 7);
    oddSumValues.put("E", 9);
    oddSumValues.put("F", 13);
    oddSumValues.put("G", 15);
    oddSumValues.put("H", 17);
    oddSumValues.put("I", 19);
    oddSumValues.put("J", 21);
    oddSumValues.put("K", 2);
    oddSumValues.put("L", 4);
    oddSumValues.put("M", 18);
    oddSumValues.put("N", 20);
    oddSumValues.put("O", 11);
    oddSumValues.put("P", 3);
    oddSumValues.put("Q", 6);
    oddSumValues.put("R", 8);
    oddSumValues.put("S", 12);
    oddSumValues.put("T", 14);
    oddSumValues.put("U", 16);
    oddSumValues.put("V", 10);
    oddSumValues.put("W", 22);
    oddSumValues.put("X", 25);
    oddSumValues.put("Y", 24);
    oddSumValues.put("Z", 23);

    evenSumValues = new HashMap<>();
    evenSumValues.put("0", 0);
    evenSumValues.put("1", 1);
    evenSumValues.put("2", 2);
    evenSumValues.put("3", 3);
    evenSumValues.put("4", 4);
    evenSumValues.put("5", 5);
    evenSumValues.put("6", 6);
    evenSumValues.put("7", 7);
    evenSumValues.put("8", 8);
    evenSumValues.put("9", 9);
    evenSumValues.put("A", 0);
    evenSumValues.put("B", 1);
    evenSumValues.put("C", 2);
    evenSumValues.put("D", 3);
    evenSumValues.put("E", 4);
    evenSumValues.put("F", 5);
    evenSumValues.put("G", 6);
    evenSumValues.put("H", 7);
    evenSumValues.put("I", 8);
    evenSumValues.put("J", 9);
    evenSumValues.put("K", 10);
    evenSumValues.put("L", 11);
    evenSumValues.put("M", 12);
    evenSumValues.put("N", 13);
    evenSumValues.put("O", 14);
    evenSumValues.put("P", 15);
    evenSumValues.put("Q", 16);
    evenSumValues.put("R", 17);
    evenSumValues.put("S", 18);
    evenSumValues.put("T", 19);
    evenSumValues.put("U", 20);
    evenSumValues.put("V", 21);
    evenSumValues.put("W", 22);
    evenSumValues.put("X", 23);
    evenSumValues.put("Y", 24);
    evenSumValues.put("Z", 25);

    controlCharValues = new HashMap<>();
    controlCharValues.put(0, "A");
    controlCharValues.put(1, "B");
    controlCharValues.put(2, "C");
    controlCharValues.put(3, "D");
    controlCharValues.put(4, "E");
    controlCharValues.put(5, "F");
    controlCharValues.put(6, "G");
    controlCharValues.put(7, "H");
    controlCharValues.put(8, "I");
    controlCharValues.put(9, "J");
    controlCharValues.put(10, "K");
    controlCharValues.put(11, "L");
    controlCharValues.put(12, "M");
    controlCharValues.put(13, "N");
    controlCharValues.put(14, "O");
    controlCharValues.put(15, "P");
    controlCharValues.put(16, "Q");
    controlCharValues.put(17, "R");
    controlCharValues.put(18, "S");
    controlCharValues.put(19, "T");
    controlCharValues.put(20, "U");
    controlCharValues.put(21, "V");
    controlCharValues.put(22, "W");
    controlCharValues.put(23, "X");
    controlCharValues.put(24, "Y");
    controlCharValues.put(25, "Z");
  }

  /**
   * Calculate valid italian fiscal codes for given data.
   * 
   * @param conf the FiscalCodeConf
   * @param surname person surname
   * @param name person name
   * @param birthDate person birth date (as dd/MM/yyyy)
   * @param townOfBirth person town of birth
   * @param gender person gender
   * @return the list of valid fiscal codes, null if the fiscal code cannot be computed
   */
  public static String[] calcoloCodiceFiscale(FiscalCodeConf conf, String surname, String name,
      String birthDate, String townOfBirth, String gender) {
    final boolean paramsOk = checkParamsNotEmpty(surname, name, birthDate, townOfBirth, gender);
    if (!paramsOk) {
      return null;
    }
    surname = FiscalCodeNormalizer.normalizeName(surname, true);
    name = FiscalCodeNormalizer.normalizeName(name, true);
    StringBuilder result = new StringBuilder();
    /* calcolo prime 3 lettere */
    int cont = 0;
    /* caso cognome minore di 3 lettere */
    if (surname.length() < 3) {
      result.append(surname);
      while (result.length() < 3) {
        result.append("X");
      }
      cont = 3;
    }
    /* caso normale */
    for (int i = 0; i < surname.length(); i++) {
      if (cont == 3) {
        break;
      }
      if (!isVocal(surname.charAt(i))) {
        result.append(Character.toString(surname.charAt(i)));
        cont++;
      }
    }
    /* nel caso ci siano meno di 3 consonanti */
    while (cont < 3) {
      for (int i = 0; i < surname.length(); i++) {
        if (cont == 3) {
          break;
        }
        if (isVocal(surname.charAt(i))) {
          result.append(Character.toString(surname.charAt(i)));
          cont++;
        }
      }
    }
    /* lettere nome */
    cont = 0;
    /* caso nome minore di 3 lettere */
    if (name.length() < 3) {
      result.append(name);
      while (result.length() < 6) {
        result.append("X");
      }
      cont = 3;
    }
    int consonantCount = 0;
    for (int i = 0; i < name.length(); i++) {
      if (!isVocal(name.charAt(i))) {
        consonantCount++;
      }
    }

    /* caso normale */
    int consonantFound = 0;
    for (int i = 0; i < name.length(); i++) {
      if (cont == 3) {
        break;
      }
      if (!isVocal(name.charAt(i))) {
        if (consonantCount <= 3 || consonantFound != 1) {
          result.append(Character.toString(name.charAt(i)));
          cont++;
        }
        consonantFound++;
      }
    }
    /* nel casoci siano meno di 3 consonanti */
    while (cont < 3) {
      for (int i = 0; i < name.length(); i++) {
        if (cont == 3) {
          break;
        }
        if (isVocal(name.charAt(i))) {
          result.append(Character.toString(name.charAt(i)));
          cont++;
        }
      }
    }
    result.append(birthDate.substring(conf.getYearStart(), conf.getYearEnd()));

    int month = 0;
    if (birthDate.charAt(conf.getMonthStart()) == '0') {
      month = Integer.parseInt(birthDate.substring(conf.getMonthStart() + 1, conf.getMonthEnd()));
    } else {
      month = Integer.parseInt(birthDate.substring(conf.getMonthStart(), conf.getMonthEnd()));
    }
    result.append(monthCode(month));

    int day = Integer.parseInt(birthDate.substring(conf.getDayStart(), conf.getDayEnd()));

    if (gender.equals(conf.getMaleValue())) {
      if (birthDate.charAt(conf.getDayStart()) == '0') {
        result.append("0" + day);
      } else {
        result.append(day);
      }
    } else {
      day += 40;
      result.append(Integer.toString(day));
    }

    /* comune nascita */
    List<String> townCodes = conf.getComuniMap().get(townOfBirth.toUpperCase());
    if (townCodes == null) {
      throw new IllegalArgumentException("Birth town " + townOfBirth + UNSUPPORTED);
    }
    /* Carattere di controllo */
    String[] ret = new String[townCodes.size()];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = result + townCodes.get(i);
      ret[i] += calculateControlChar(ret[i]);
    }
    return ret;
  }

  private static boolean checkParamsNotEmpty(String... params) {
    for (String param : params) {
      if (param == null || param.trim().isEmpty()) {
        return false;
      }
    }
    return true;
  }

  private static String monthCode(int month) {
    String code = monthValues.get(month);
    if (code == null) {
      throw new IllegalArgumentException("Month " + month + UNSUPPORTED);
    }
    return code;
  }

  private static boolean isVocal(char character) {
    for (char vocal : VOCALS) {
      if (character == vocal) {
        return true;
      }
    }
    return false;
  }

  private static String calculateControlChar(String result) {
    int evenSum = 0;
    for (int i = 1; i <= 13; i += 2) {
      Integer valueToSum = evenSumValues.get(String.valueOf(result.charAt(i)));
      if (valueToSum == null) {
        throw new IllegalArgumentException(CONTROL + result.charAt(i) + UNSUPPORTED);
      }
      evenSum += valueToSum;
    }
    int oddSum = 0;
    for (int i = 0; i <= 14; i += 2) {
      Integer valueToSum = oddSumValues.get(String.valueOf(result.charAt(i)));
      if (valueToSum == null) {
        throw new IllegalArgumentException(CONTROL + result.charAt(i) + UNSUPPORTED);
      }
      oddSum += valueToSum;
    }
    int interoControllo = (evenSum + oddSum) % 26;
    String controlChar = controlCharValues.get(interoControllo);
    if (controlChar == null) {
      throw new IllegalArgumentException(CONTROL + interoControllo + UNSUPPORTED);
    }
    return controlChar;
  }

  /**
   * Initialize the FiscalCodeConf.
   * 
   * @param codiciIstatStr the string content of the TSV containing CODICE-ISTAT => TOWN mappings
   * @param maxComuneNameLength the max length of the name of a comune (0 to disable generation of
   *        truncated version of the name)
   * @param maleValue the String denoting a male (e.g. "M", "MALE", etc..)
   * @param yearStart year start index
   * @param yearEnd year end index
   * @param monthStart month start index
   * @param monthEnd month end index
   * @param dayStart day start index
   * @param dayEnd day end index
   * @return the corresponding FiscalCodeConf bean
   */
  public static FiscalCodeConf getFiscalCodeConf(String codiciIstatStr, int maxComuneNameLength,
      String maleValue, int yearStart, int yearEnd, int monthStart, int monthEnd, int dayStart,
      int dayEnd) {
    try {
      return new FiscalCodeConf(getComuniMap(codiciIstatStr, maxComuneNameLength), maleValue,
          yearStart, yearEnd, monthStart, monthEnd, dayStart, dayEnd);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  protected static Map<String, List<String>> getComuniMap(String codiciIstatStr,
      int maxComuneNameLength) throws IOException {
    final char fieldDelim = '\t';// cod-istat-comuni file must be a TSV
    Map<String, List<String>> comuniMap = new HashMap<>();
    try (Scanner scanner = new Scanner(codiciIstatStr)) {
      while (scanner.hasNextLine()) {
        final String line = scanner.nextLine();
        if (line.trim().isEmpty()) {
          continue;
        }
        final int nomeComuneStart = line.indexOf(fieldDelim);
        final String codIstat = line.substring(0, nomeComuneStart);
        String nomeComune = line.substring(nomeComuneStart).trim().toUpperCase();
        addToComuniMap(comuniMap, maxComuneNameLength, nomeComune, codIstat);
        // 1 - add version with apostrophes in place of accented letters
        String normalizedName = null;
        if (StringUtils.indexOfAny(nomeComune, ACCENTED_LETTERS) >= 0) {
          normalizedName =
              StringUtils.replaceEach(nomeComune, ACCENTED_LETTERS, ACCENTED_LETTERS_REPLACEMENT);
          addToComuniMap(comuniMap, maxComuneNameLength, normalizedName, codIstat);
        }
        // 2 - replace '-' in both original and normalized
        addNameWithoutDashes(comuniMap, maxComuneNameLength, nomeComune, codIstat);
        if (normalizedName != null) {
          addNameWithoutDashes(comuniMap, maxComuneNameLength, normalizedName, codIstat);
        }
      }
    }
    return comuniMap;
  }

  private static void addNameWithoutDashes(final Map<String, List<String>> comuniMap,
      final int maxComuneNameLength, final String nomeComune, final String codIstat) {
    if (StringUtils.indexOfAny(nomeComune, '-') >= 0) {
      addToComuniMap(comuniMap, maxComuneNameLength, nomeComune.replace('-', ' '), codIstat);
    }
  }

  private static void addToComuniMap(final Map<String, List<String>> comuniMap,
      final int maxComuneNameLength, final String nomeComune, final String codIstat) {
    if (!comuniMap.containsKey(nomeComune)) {
      comuniMap.put(nomeComune, new ArrayList<>());
    } // else nomeComune has multiple codes;
    comuniMap.get(nomeComune).add(codIstat);

    if (maxComuneNameLength == 0 || nomeComune.length() <= maxComuneNameLength) {
      return;
    }
    // add the truncated version (issue #9)
    addToComuniMap(comuniMap, maxComuneNameLength,
        nomeComune.substring(0, maxComuneNameLength).trim(), codIstat);
  }
}
