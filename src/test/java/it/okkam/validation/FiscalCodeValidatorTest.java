package it.okkam.validation;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FiscalCodeValidatorTest {

  private static final String CODICE_ISTAT_COMUNI_CSV = "codice-istat-comuni.csv";
  private static FiscalCodeConf conf1;
  private static FiscalCodeConf conf2;
  private static FiscalCodeConf conf3;
  private int maxComuneNameLength = 25;

  private String readLocalFile(String filePath) throws IOException {
    try {
      ClassLoader classLoader = this.getClass().getClassLoader();
      return IOUtils.toString(classLoader.getResource(filePath), Charset.forName("UTF-8"));
    } catch (IOException ex) {
      throw ex;
    }
  }

  private static boolean arrayContains(String[] array, final String expected) {
    boolean found = false;
    for (String val : array) {
      if (expected.equals(val)) {
        found = true;
        break;
      }
    }
    return found;
  }

  @Test
  public void testComuniMapInitialization() throws IOException {
    String codiciIstatStr = readLocalFile(CODICE_ISTAT_COMUNI_CSV);
    Map<String, List<String>> comuniMap =
        FiscalCodeValidator.getComuniMap(codiciIstatStr, maxComuneNameLength);
    Assert.assertEquals("F205", comuniMap.get("MILANO").get(0));
  }

  /**
   * Init method.
   * 
   * @throws IOException
   */
  @Before
  public void init() throws IOException {
    String codiciIstatStr = readLocalFile(CODICE_ISTAT_COMUNI_CSV);
    conf1 = FiscalCodeValidator.getFiscalCodeConf(codiciIstatStr, maxComuneNameLength, "M", 8, 10,
        3, 5, 0, 2);
    conf2 = FiscalCodeValidator.getFiscalCodeConf(codiciIstatStr, maxComuneNameLength, "M", 2, 4, 5,
        7, 8, 10);
    conf3 = FiscalCodeValidator.getFiscalCodeConf(codiciIstatStr, 0, "M", 2, 4, 5, 7, 8, 10);
  }

  @Test
  public void testEmptyValues() {
    String[] codes = FiscalCodeValidator.calcoloCodiceFiscale(conf1, "XX", null, null, null, "M");
    Assert.assertNull(codes);
  }

  @Test
  public void testTwoLetterName() {
    String[] codes = FiscalCodeValidator.calcoloCodiceFiscale(conf1, "PARKASH", "MO", "01/04/1965", "INDIA", "M");
    Assert.assertTrue(arrayContains(codes, "PRKMOX65D01Z222C"));
  }

  @Test
  public void testDarioFo() {
    String[] codes = FiscalCodeValidator.calcoloCodiceFiscale(conf1, "FO", "DARIO", "24/03/1926",
        "SANGIANO", "M");
    Assert.assertTrue(arrayContains(codes, "FOXDRA26C24H872Y"));
  }

  @Test
  public void testDateTimePattern() {
    String[] codes = FiscalCodeValidator.calcoloCodiceFiscale(conf2, "FO", "DARIO",
        "1926-03-24T00:00:00", "SANGIANO", "M");
    Assert.assertTrue(arrayContains(codes, "FOXDRA26C24H872Y"));
  }

  @Test
  public void testDot() {
    String[] codes = FiscalCodeValidator.calcoloCodiceFiscale(conf2, "FO", "DAR.IO",
        "1926-03-24T00:00:00", "SANGIANO", "M");
    Assert.assertTrue(arrayContains(codes, "FOXDRA26C24H872Y"));
  }

  @Test
  public void testBacktick() {
    String[] codes = FiscalCodeValidator.calcoloCodiceFiscale(conf2, "D`AMICO", "ILARIA",
        "1973-08-30T00:00:00", "ROMA", "F");
    Assert.assertTrue(ArrayUtils.contains(codes, "DMCLRI73M70H501N"));
  }

  @Test
  public void testAutogenerationOfComuniNamesWhenAccented() {
    String[] codes = FiscalCodeValidator.calcoloCodiceFiscale(conf2, "D'AMICO", "ILARIA",
        "1973-08-30T00:00:00", "ROVERÈ DELLA LUNA", "F");
    Assert.assertTrue(ArrayUtils.contains(codes, "DMCLRI73M70H607G"));

    codes = FiscalCodeValidator.calcoloCodiceFiscale(conf2, "D'AMICO", "ILARIA",
        "1973-08-30T00:00:00", "ROVERE' DELLA LUNA", "F");
    Assert.assertTrue(ArrayUtils.contains(codes, "DMCLRI73M70H607G"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTruncatedComuneNameNotConfigured() {
    String[] codes = FiscalCodeValidator.calcoloCodiceFiscale(conf3, "D'AMICO", "ILARIA",
        "1973-08-30T00:00:00", "MAGRÈ SULLA STRADA DEL VI", "F");
    Assert.assertNotNull(codes);
  }

  @Test
  public void testTruncatedComuneName() {
    String[] codes = FiscalCodeValidator.calcoloCodiceFiscale(conf2, "D'AMICO", "ILARIA",
        "1973-08-30T00:00:00", "MAGRÈ SULLA STRADA DEL VINO", "F");
    Assert.assertTrue(ArrayUtils.contains(codes, "DMCLRI73M70E829N"));
    // this comune name has lenght 25
    codes = FiscalCodeValidator.calcoloCodiceFiscale(conf2, "D'AMICO", "ILARIA",
        "1973-08-30T00:00:00", "MAGRÈ SULLA STRADA DEL VI", "F");
    Assert.assertTrue(ArrayUtils.contains(codes, "DMCLRI73M70E829N"));
    // validate also normalized version
    codes = FiscalCodeValidator.calcoloCodiceFiscale(conf2, "D'AMICO", "ILARIA",
        "1973-08-30T00:00:00", "MAGRE' SULLA STRADA DEL V", "F");
    Assert.assertTrue(ArrayUtils.contains(codes, "DMCLRI73M70E829N"));
  }

  @Test
  public void testTruncatedComuneNameWithTrim() {
    String[] codes = FiscalCodeValidator.calcoloCodiceFiscale(conf2, "D'AMICO", "ILARIA",
        "1973-08-30T00:00:00", "CALDARO SULLA STRADA DEL VINO", "F");
    Assert.assertTrue(ArrayUtils.contains(codes, "DMCLRI73M70B397B"));
    // this comune name has lenght 24 (because of trim)
    codes = FiscalCodeValidator.calcoloCodiceFiscale(conf2, "D'AMICO", "ILARIA",
        "1973-08-30T00:00:00", "CALDARO SULLA STRADA DEL", "F");
    Assert.assertTrue(ArrayUtils.contains(codes, "DMCLRI73M70B397B"));
  }

  @Test
  public void testCircumflexedLetters() {
    String[] codes = FiscalCodeValidator.calcoloCodiceFiscale(conf2, "D'AMICO", "ILARIA",
        "1973-08-30T00:00:00", "CHÂTILLON", "F");
    Assert.assertTrue(ArrayUtils.contains(codes, "DMCLRI73M70C294S"));
    codes = FiscalCodeValidator.calcoloCodiceFiscale(conf2, "D'AMICO", "ILARIA",
        "1973-08-30T00:00:00", "CHATILLON", "F");
    Assert.assertTrue(ArrayUtils.contains(codes, "DMCLRI73M70C294S"));
  }

}
