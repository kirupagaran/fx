package au.com.anz;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by kirupa on 06/07/17.
 */


public class ForeignExchangeConverter {
    //Main method
    public static void main(String[] args) {
        ForeignExchangeConverter fx = new ForeignExchangeConverter();
        Map<Object, Map<Object, String>> crossMatrix = fx.createCrossViaMatrix();
        fx.getInput(crossMatrix);
    }


    /*
    Method Name: createCrossViaMatrix()
    Description: Reads the cross-via values from cross-via file in project's resources folder and creates a map for the cross-via values
    @Return: Returns cross-via values as hashmap of type Map<Object,Map<Object,String>>
    */
    public Map<Object, Map<Object, String>> createCrossViaMatrix() {
        InputStream csvFile = ForeignExchangeConverter.class.getResourceAsStream("/cross-via.csv");
        Map<Object, Map<Object, String>> maps = new HashMap<Object, Map<Object, String>>();
        BufferedReader br = null;
        BufferedReader br1 = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            br = new BufferedReader(new InputStreamReader(csvFile));
            Boolean header = true;

            //get the row counts in the cross-via file
            int rows = 0;
            while ((line = br.readLine()) != null)
                rows++;

            //Read the cross-via file from the project's resources folder
            br1 = new BufferedReader(new InputStreamReader(ForeignExchangeConverter.class.getResourceAsStream("/cross-via.csv")));
            //Read the first line of the file as column
            String[] columnValues = br1.readLine().split(cvsSplitBy);
            List<String> list = new ArrayList<String>(Arrays.asList(columnValues));
            list.remove(0);
            //Read rest of the rows in the file as cross-via values using a hashmap of type Map<Object,Map<Object,String>>
            while ((line = br1.readLine()) != null) {
                String[] rowVal = line.split(cvsSplitBy);
                Map<Object, String> tempMap = new HashMap<Object, String>();
                for (int i = 1; i < rowVal.length; i++) {
                    Object col = columnValues[i];
                    tempMap.put(col, rowVal[i]);
                }
                Object row = rowVal[0];
                maps.put(row, tempMap);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null || br1 != null) {
                try {
                    br.close();
                    br1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return maps;
    }


    /*
    Method Name: getRates()
    Description: Reads the rates from rates file in project's resources folder and creates a map for the rate values
    @Return: Returns the rates as a Hashmap
    */
    public Map<Object, String> getRates() {
        BufferedReader ratesReader = null;
        Map<Object, String> rates = new HashMap<Object, String>();
        try {
            //Read the rates from rates.dat file from project's resource folder
            ratesReader = new BufferedReader(new InputStreamReader(ForeignExchangeConverter.class.getResourceAsStream("/rates.dat")));
            String line = "";

            while ((line = ratesReader.readLine()) != null) {
                String[] ratesArray = line.split("=");
                Object currency = ratesArray[0];
                rates.put(currency, ratesArray[1]);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //Close the file reader
            if (ratesReader != null) {
                try {
                    ratesReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return rates;
    }


    /*
    MEthod Name: getInput
    Description: Method to receive input from the console continuosly until user types 'q'
    @Return: void
    */
    public void getInput(Map<Object, Map<Object, String>> crossMatrix) {
        // create a scanner so we can read the command-line input
        System.out.println("\nEnter the input: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] inputValues = input.split(" ");

        //Check for incorrect arguments
        if (inputValues.length != 4 && !inputValues[0].toLowerCase().equals("q")) {
            System.out.println("Incorrect Arguments: Try Again");
            getInput(crossMatrix);
        } else {
            //Quit the console if user types 'q'
            if (inputValues[0].toLowerCase().equals("q")) {
                System.exit(1);
            } else if (inputValues.length == 4) {
                Object source = inputValues[0].toUpperCase();
                Object target = inputValues[3].toUpperCase();

                //Check user has entered correct currency values in the console
                if (crossMatrix.get(source) == null || crossMatrix.get(target) == null) {
                    System.out.println("Unable to find rate for " + inputValues[0].toUpperCase() + "/" + inputValues[3].toUpperCase());
                } else if (!isNumeric(inputValues[1])) {
                    System.out.println("Invalid quantity");
                } else {
                    Double quantity = Double.parseDouble(inputValues[1]);
                    //If every input is correct calculate exchange rate
                    showExchangeRate(inputValues[0].toUpperCase(), inputValues[3].toUpperCase(), quantity, crossMatrix);
                }
                getInput(crossMatrix);
            }
        }
    }


    /*
    Method name: showExchangeRate
    Description: Displays the final exchange rate in console
    @Return: void
    */
    public void showExchangeRate(String baseCurrency, String targetCurrency, Double quantity, Map<Object, Map<Object, String>> crossMatrix) {
        Map<Object, String> rates = getRates();
        Double exchangeRate = getExchangeRate(baseCurrency, targetCurrency, crossMatrix, rates);
        Double totalExchangeValue = exchangeRate * quantity;

        if (!targetCurrency.equals("JPY")) {
            BigDecimal truncatedValue = truncateDecimal(totalExchangeValue, 2);
            System.out.println(baseCurrency + " " + quantity + " = " + targetCurrency + " " + truncatedValue);
        } else {
            BigDecimal truncatedValue = truncateDecimal(totalExchangeValue, 0);
            System.out.println(baseCurrency + " " + quantity + " = " + targetCurrency + " " + truncatedValue);
        }


        new ForeignExchangeConverter().getInput(crossMatrix);
    }


    /*
    Method: getExchangeRate
    Description: Extracts the cross-via values to be mapped from cross-via matrix table and calls the method that calculates exchange rate
    @Return: Double
    */
    public Double getExchangeRate(String baseCurrency, String targetCurrency, Map<Object, Map<Object, String>> crossMatrix, Map<Object, String> rates) {
        //Extract Cross-via value to calulate exchange rate for target currency
        Map<Object, String> baseMatrix = crossMatrix.get(baseCurrency);
        String crossValue = baseMatrix.get(targetCurrency);

        //Use the extracted cross-via rate to calculate the exchange rate
        return getCurrencyValue(baseCurrency, targetCurrency, crossValue, crossMatrix, rates);

    }

    
    /*
    Method: getCurrencyValue
    Description: Calculates the exchange rate based on the cross-via value and returns the value to getExchangeRate()
    @Return: Double
     */
    public Double getCurrencyValue(String baseCurrency, String targetCurrency, String crossCurrency, Map<Object, Map<Object, String>> crossMatrix, Map<Object, String> rates) {
        String currencyRateFlag = "";
        Double exchangeRate = 0.0;
        if (crossCurrency.equals("1:1"))
            return Double.parseDouble("1");
        else if (crossCurrency.equals("D")) {
            currencyRateFlag = baseCurrency + targetCurrency;
            return Double.parseDouble(rates.get(currencyRateFlag));
        } else if (crossCurrency.equals("Inv")) {
            currencyRateFlag = targetCurrency + baseCurrency;
            return (1 / Double.parseDouble(rates.get(currencyRateFlag)));
        } else {
            Double crossCurrencyVal1 = getExchangeRate(baseCurrency, crossCurrency, crossMatrix, rates);
            Double crossCurrencyVal2 = getExchangeRate(crossCurrency, targetCurrency, crossMatrix, rates);
            exchangeRate = (crossCurrencyVal1 * crossCurrencyVal2);
            return exchangeRate;
        }
    }


    /*
    Method Name: truncateDecimal
    Description: Method to truncate decimal points in exchange rate to 2
    @Returns: Returns the exchange rate with only two decimal points
    */
    public BigDecimal truncateDecimal(double exchangeValue, int numberofDecimals) {
        if (exchangeValue > 0) {
            return new BigDecimal(String.valueOf(exchangeValue)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
        } else {
            return new BigDecimal(String.valueOf(exchangeValue)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);
        }
    }


    /*
    Method Name: isNumeric
    Description: check whether the given string is numberic or not
    @Returns: Returns true if number else false
    */
    public static boolean isNumeric(String s) {
        String regex = "[0-9.]+";
        if (s.matches(regex))
            return true;
        else
            return false;
    }
}
