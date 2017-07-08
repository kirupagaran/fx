package au.com.anz;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Map;

public class ForeignExchangeConverterTest {

    ForeignExchangeConverter fx = new ForeignExchangeConverter();
    Map<Object,String> rates = fx.getRates();
    Map<Object,Map<Object,String>> crossMatrix = fx.createCrossViaMatrix();

    //Unit Test for calculateExchangeRate()
    @Test
    public void calculateExchangeRate() {
        Double actualExchangeRate = fx.getExchangeRate("AUD","USD",crossMatrix,rates);
        Double expectedExchangeRate = 0.8371;
        System.out.println("@Test getExchangeRate(): " + actualExchangeRate + " = " + expectedExchangeRate);
        assertEquals(actualExchangeRate, expectedExchangeRate);
    }

    //Unit Test for truncateDecimal()
    @Test
    public void truncateDecimal() {
        String actualTruncatedValue = fx.truncateDecimal(fx.getExchangeRate("AUD","USD",crossMatrix,rates),2).toString();
        String expectedTruncatedValue= "0.83";
        System.out.println("@Test truncateDecimal(): " + actualTruncatedValue + " = " + expectedTruncatedValue);
        assertEquals(actualTruncatedValue, expectedTruncatedValue);
    }

    //Unit Test for getCurrencyValue()
    @Test
    public void getCurrencyValue() {
        Double actualCrossViaTestValue = fx.getCurrencyValue("GBP","CNY","USD",crossMatrix,rates);
        Double expectedCrossViaTestValue = 9.67876345;
        System.out.println("@Test getCurrencyValue(): " + actualCrossViaTestValue + " = " + expectedCrossViaTestValue);
        assertEquals(actualCrossViaTestValue, expectedCrossViaTestValue);
    }

}