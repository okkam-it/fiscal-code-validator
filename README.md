# Italian Fiscal Code Validator

The Italian fiscal code, officially known as Italy’s Codice Fiscale, is an alphanumeric code of 16 characters. The code aims at identifying unambiguously people residing in Italy and it is used for several purposes, e.g. uniquely identifying individuals in the health system, or natural persons who act as parties in private contracts. For natural persons, the fiscal code is made of 16 alphanumeric characters; for legal persons (e.g. corporations) it comprises 11 numeric-only characters (i.e. VAT number that is not taken into account here).

### Description:

The class **it.okkam.validation.FiscalCodeValidator.java** generates all potentially valid fiscal codes for a specific person, given as input the fiscal code configuration and the person's credentials used into the fiscal code generation and validation procedure:

* The class **FiscalCodeConf** contains some information about sensitive parameters like the gender specification, the date format and, optionally, the length of the birth place (Comune) if those values are truncated at some max length. In particular:
    * *codiciIstatStr* : The path to the "codice-istat-comuni.csv" file from which the Italian Comuni codes are extracted;
    * *maxComuneNameLength* : The max length of the birth place (In the case of inputs with partial Comune name or truncated to *maxComuneNameLength* chars);
    * *maleValue* : The value of the male gender ("M", "Male", "male", etc.). All other strings are considered as female;
    * *yearStart* : The position of the third digit of the year (if YYYY), the first otherwise (i.e. YY);
    * *yearEnd* : The position of the last digit of the year;
    * *monthStart* : The position of the first digit of the month (as MM);
    * *monthEnd* : The position of the last digit of the month;
    * *dayStart* : The position of the first digit of the day (as dd);
    * *dayEnd* : The position of the last digit of the day;
* *surname* : The person surname;
* *name* : The person first name;
* *birthDate* : The person birth date;
* *townOfBirth* : The person birth town (i.e. Comune);
* *gender* : The person gender (male/female);

#### Usage Examples:

The class **it.okkam.validation.FiscalCodeValidator.java** is currently used to generate and validate italian fiscal codes.

##### Fiscal Codes Generator

In the following example, all the valid fiscal codes of the famous actor "Dario Fo", born in Sangiano the 24th March 1926 (gender male) are generated.
The configuration specifies that:
 * the male gender is expressed with the string "M"
 * the day of birth is represented by the concatenation of the chars at position 0 and 1 of the *birthDate* string
 * the month by chars at position 3 and 4
 * the last two digits of the birth year are at position 8 and 9.
	
As introduced above, when the specified *gender* is different from the configured *maleValue* string, the person is assumed to be a female. 

```java
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestGenerator {

  public static void main(String[] args) throws IOException, URISyntaxException {
    String localFile = "codice-istat-comuni.csv";
    int maxComuneNameLength = 25;
    String maleValue = "M";
    int yearStart = 8;
    int yearEnd = 10;
    int monthStart = 3;
    int monthEnd = 5;
    int dayStart = 0;
    int dayEnd = 2;
    String codiciIstatStr = readLocalFile(localFile);

    FiscalCodeConf configuration = FiscalCodeValidator.getFiscalCodeConf(codiciIstatStr,
        maxComuneNameLength, maleValue, yearStart, yearEnd, monthStart, monthEnd, dayStart, dayEnd);

    String surname = "FO";
    String name = "DARIO";
    String birthDate = "24/03/1926";
    String townOfBirth = "SANGIANO";
    String gender = "M";

    String[] validFiscalCodes = FiscalCodeValidator.calcoloCodiceFiscale(configuration, surname,
        name, birthDate, townOfBirth, gender);

    for (int i = 0; i < validFiscalCodes.length; i++) {
      System.out.println(validFiscalCodes[i]);
    }
  }

  private static String readLocalFile(String filePath) throws IOException, URISyntaxException {
    return new String(Files.readAllBytes(
        Paths.get(System.getProperty("user.dir") + "/src/test/resources/" + filePath)));
  }

}
```

##### Fiscal Codes Validator

The following example shows how to check the validity of a fiscal code through some trivial JUnit tests. The goal is to check is the fiscal code "FOXDRA26C24H872Y" belongs to the famous actor "Dario Fo", born in Sangiano the 24th March 1926 (gender male) given two different date formats for the *birthDate* value. 
The first configuration (codiciIstatStr, maxComuneNameLength, "M", 8, 10, 3, 5, 0, 2) specifies a date format like "24/03/1926", while the second configuration (codiciIstatStr, maxComuneNameLength, "M", 2, 4, 5, 7, 8, 10) specifies a date format like "1926-03-24T00:00:00". 
In both cases the same person will be recognized.

```java
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;

public class FiscalCodeValidatorTest {

  private static final String CODICE_ISTAT_COMUNI_CSV = "codice-istat-comuni.csv";
  private static FiscalCodeConf configuration1;
  private static FiscalCodeConf configuration2;
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

  @Before
  public void init() throws IOException {
    String codiciIstatStr = readLocalFile(CODICE_ISTAT_COMUNI_CSV);
    configuration1 = FiscalCodeValidator.getFiscalCodeConf(codiciIstatStr, maxComuneNameLength, "M", 8, 10, 3, 5, 0, 2);
    configuration2 = FiscalCodeValidator.getFiscalCodeConf(codiciIstatStr, maxComuneNameLength, "M", 2, 4, 5, 7, 8, 10);
  }

  @Test
  public void testDarioFo() {
    String[] codes = FiscalCodeValidator.calcoloCodiceFiscale(configuration1, "FO", "DARIO", "24/03/1926",
        "SANGIANO", "M");
    Assert.assertTrue(arrayContains(codes, "FOXDRA26C24H872Y"));
  }

  @Test
  public void testDateTimePattern() {
    String[] codes = FiscalCodeValidator.calcoloCodiceFiscale(configuration2, "FO", "DARIO",
        "1926-03-24T00:00:00", "SANGIANO", "M");
    Assert.assertTrue(arrayContains(codes, "FOXDRA26C24H872Y"));
  }
}
```
A minimal set of JUnit tests for fiscal codes validation can be found in the FiscalCodeValidatorTest.java class.