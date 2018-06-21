package it.okkam.validation;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class FiscalCodeNormalizer {

  private FiscalCodeNormalizer() {
    throw new IllegalStateException("Utility class");
  }

  private static final Pattern NOISY_CHARS = Pattern.compile("[|{}(),.;\\\\<>\"\'`#°\n\r\t]");
  private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");

  // see issue-11 (https://github.com/okkam-it/fiscal-code-validator/issues/11)
  private static final String[] LETTERS_TO_REPLACE = new String[] { //
      "À", "Á", "Â", "Ã", "Ä", "Æ", //
      "È", "É", "Ê", "Ë", "&", //
      "Ì", "Í", "Î", "Ï", //
      "Ò", "Ó", "Ô", "Ö", "Œ", //
      "Ù", "Ú", "Û", "Ü", //
      "Ç", "Č", //
      "Ñ", //
      "Š", "ß", //
      "Ž", //
      "+", "-"};

  private static final String[] LETTERS_REPLACEMENT = new String[] { //
      "A", "A", "A", "A", "AE", "AE", //
      "E", "E", "E", "E", "E", //
      "I", "I", "I", "I", //
      "O", "O", "O", "OE", "OE", //
      "U", "U", "U", "UE", //
      "C", "C", //
      "N", //
      "S", "SS", //
      "Z", //
      " ", " "//
  };

  /**
   * Normalize the passed name.
   * 
   * @param name the name to normalize
   * @param stripSpaces to remove all spaces, otherwise just remove duplicated spaces and trim
   * @return the normalized name
   */
  public static String normalizeName(String name, boolean stripSpaces) {
    if (name == null) {
      return "";
    }
    name = name.toUpperCase();
    name = NOISY_CHARS.matcher(name).replaceAll("");
    if (StringUtils.indexOfAny(name, LETTERS_TO_REPLACE) >= 0) {
      name = StringUtils.replaceEach(name, LETTERS_TO_REPLACE, LETTERS_REPLACEMENT);
    }
    // last step: handle white spaces
    if (!name.contains(" ")) {
      return name;
    }
    if (stripSpaces) {
      return StringUtils.remove(name, ' ');
    }
    // just remove multiple spaces
    return MULTIPLE_SPACES.matcher(name).replaceAll(" ").trim();
  }
}
