package it.okkam.validation;

import org.junit.Assert;
import org.junit.Test;

public class NormalizationTest {
  private static final String EXPECTED_STRIPPED = "DAMICOILARIA";
  private static final String EXPECTED_NOT_STRIPPED = "DAMICO ILARIA";

  @Test
  public void testStripSpaces() {
    String res = FiscalCodeNormalizer.normalizeName("D'AMICO ILARIA", true);
    Assert.assertEquals(EXPECTED_STRIPPED, res);

    res = FiscalCodeNormalizer.normalizeName("D'AMICO ILARIA", false);
    Assert.assertEquals(EXPECTED_NOT_STRIPPED, res);
  }

  @Test
  public void testNoisyChars() {
    String res = FiscalCodeNormalizer.normalizeName("D`AMICO >ILARIA", false);
    Assert.assertEquals(EXPECTED_NOT_STRIPPED, res);

    res = FiscalCodeNormalizer.normalizeName("D`AMICO-ILARI.A", false);
    Assert.assertEquals(EXPECTED_NOT_STRIPPED, res);

  }

  @Test
  public void testPlusAndMinus() {
    String res = FiscalCodeNormalizer.normalizeName("D'AMICO +ILARIA", false);
    Assert.assertEquals(EXPECTED_NOT_STRIPPED, res);

    res = FiscalCodeNormalizer.normalizeName("D'AMICO-ILARIA", false);
    Assert.assertEquals(EXPECTED_NOT_STRIPPED, res);

    res = FiscalCodeNormalizer.normalizeName("D'AMICO +ILARIA", false);
    Assert.assertEquals(EXPECTED_NOT_STRIPPED, res);

  }

  @Test
  public void testTrimAndMultipleSpaces() {
    String res = FiscalCodeNormalizer.normalizeName(" D'AMICO ILARIA ", true);
    Assert.assertEquals(EXPECTED_STRIPPED, res);

    res = FiscalCodeNormalizer.normalizeName("D'AMICO  ILARIA    ", false);
    Assert.assertEquals(EXPECTED_NOT_STRIPPED, res);
  }

  @Test
  public void testTabs() {
    String res = FiscalCodeNormalizer.normalizeName("D'AMICO     ILARIA", false);
    Assert.assertEquals(EXPECTED_NOT_STRIPPED, res);
  }
}
