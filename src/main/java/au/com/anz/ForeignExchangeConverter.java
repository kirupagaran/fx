package au.com.anz;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;

/**
 * Created by kirupa on 06/07/17.
 */
public class ForeignExchangeConverter {
    public static void main(String[] args) {
        Map<Object,Map<Object,String>> crossMatrix = buildData();
        getInput(crossMatrix);
    }

    public static void calculateExchangeValue(String baseCurrency,String targetCurrency,Double quantity,Map<Object,Map<Object,String>> crossMatrix){
        //String baseCurrency = "AUD";
        //String targetCurrency = "JPY";
        Map<Object,String> rates = getRates();
        Double exchangeRate = getCrossValue(baseCurrency,targetCurrency,crossMatrix,rates);
        Double totalExchangeValue = exchangeRate*quantity;

        if(!targetCurrency.equals("JPY")){
            BigDecimal truncatedValue=truncateDecimal(totalExchangeValue,2);
            System.out.println(baseCurrency+" "+quantity+" = "+targetCurrency+" "+truncatedValue);
        }
        else{
            BigDecimal truncatedValue=truncateDecimal(totalExchangeValue,0);
            System.out.println(baseCurrency+" "+quantity+" = "+targetCurrency+" "+truncatedValue);
        }


        getInput(crossMatrix);
    }

    public static Double getCrossValue(String baseCurrency,String targetCurrency,Map<Object,Map<Object,String>> crossMatrix,Map<Object,String> rates){

        Map<Object,String> baseMatrix = crossMatrix.get(baseCurrency);
        String crossCurrency = baseMatrix.get(targetCurrency);
        return getCurrencyValue(baseCurrency,targetCurrency,crossCurrency,crossMatrix,rates);

    }

    public static Double getCurrencyValue(String baseCurrency,String targetCurrency,String crossCurrency,Map<Object,Map<Object,String>> crossMatrix,Map<Object,String> rates){
        String currencyRateFlag = "";
        Double exchangeRate = 0.0;
        if(crossCurrency.equals("1:1"))
            return Double.parseDouble("1");
        else if(crossCurrency.equals("D")) {
            currencyRateFlag = baseCurrency+targetCurrency;
            return  Double.parseDouble(rates.get(currencyRateFlag));
        }
        else if(crossCurrency.equals("Inv")) {
            currencyRateFlag = targetCurrency+baseCurrency;
            return  (1/Double.parseDouble(rates.get(currencyRateFlag)));
        }
        else{
            Double crossCurrencyVal1 = getCrossValue(baseCurrency,crossCurrency,crossMatrix,rates);

            Double crossCurrencyVal2 = getCrossValue(crossCurrency,targetCurrency,crossMatrix,rates);
            exchangeRate = (crossCurrencyVal1*crossCurrencyVal2);
            return exchangeRate;

        }

    }




    public static void getInput(Map<Object,Map<Object,String>> crossMatrix){
        // create a scanner so we can read the command-line input
        System.out.println("\nEnter the input: ");
        Scanner scanner = new Scanner(System.in);


        // get their input as a String
        String input = scanner.nextLine();
        String[] inputValues = input.split(" ");


        if(inputValues.length!=4 && !inputValues[0].equals("q")){
            System.out.println("Incorrect Arguments: Try Again");
            getInput(crossMatrix);
        }
        else{
            if(inputValues[0].equals("q")){
                System.exit(1);
            }
            else if(inputValues.length==4){
                Object source = inputValues[0].toUpperCase();
                Object target = inputValues[3].toUpperCase();
                Double quantity = Double.parseDouble(inputValues[1]);
                if(crossMatrix.get(source) == null || crossMatrix.get(target) == null){
                    System.out.println("Unable to find rate for "+inputValues[0].toUpperCase()+"/"+inputValues[3].toUpperCase());
                }
                /*else if(NumberFormat.getInstance().parse(inputValues[1])){
                    System.out.println("Invalid Target Currency");
                }*/
                else{
                    calculateExchangeValue(inputValues[0].toUpperCase(),inputValues[3].toUpperCase(),quantity,crossMatrix);
                }
                getInput(crossMatrix);
            }
        }
    }


    public static Map<Object,Map<Object,String>> buildData(){
        InputStream csvFile = ForeignExchangeConverter.class.getResourceAsStream("/cross-via.csv");
        //String csvFile = ForeignExchangeConverter.class.getResource("/cross-via.csv").getPath();
        //System.out.println("PATH: "+csvFile.toURI());
        Map<Object,Map<Object,String>> maps = new HashMap<Object,Map<Object,String>>();
        BufferedReader br = null;
        //BufferedReader br1 = null;
        FileInputStream fIn = null;
        String line = "";
        String cvsSplitBy = ",";

        try {

            //fIn = new FileInputStream(csvFile);
            br = new BufferedReader(new InputStreamReader(csvFile));

            Boolean header = true;
            int rows=0;
            int columns=0;
            while ((line = br.readLine()) != null) {
                rows++;
                columns = line.split(cvsSplitBy).length;
                // use comma as separator
                /*String[] country = line.split();
                if(header){
                    String[] headerVal = country;
                    header=false;
                }

                System.out.println("Country [code= " + country[0] + " , name=" + country[1] + "]");*/

            }

            String [ ] [ ] scores = new String [ rows ] [ columns ];
            br.close();


            BufferedReader br1 = new BufferedReader(new InputStreamReader(ForeignExchangeConverter.class.getResourceAsStream("/cross-via.csv")));
            String[] columnValues = br1.readLine().split(cvsSplitBy);
            List<String> list = new ArrayList<String>(Arrays.asList(columnValues));
            list.remove(0);

            while ((line = br1.readLine()) != null) {
                String[] rowVal = line.split(cvsSplitBy);
                Map<Object,String> tempMap = new HashMap<Object,String>();
                for(int i=1;i<rowVal.length;i++){


                    Object col = columnValues[i];
                    tempMap.put(col,rowVal[i]);

                }
                Object row =  rowVal[0];
                maps.put(row,tempMap);
                //System.out.println("TEMPMAP: "+tempMap+" MAP: "+maps);
                //System.out.println(" MAP: "+maps);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return maps;
    }

    public static Map<Object,String> getRates(){
        BufferedReader ratesReader = null;
        Map<Object,String> rates = new HashMap<Object,String>();
        try {
            ratesReader = new BufferedReader(new InputStreamReader(ForeignExchangeConverter.class.getResourceAsStream("/rates.dat")));
            String line = "";

            while ((line = ratesReader.readLine()) != null) {
                String[] ratesArray = line.split("=");
                Object currency = ratesArray[0];
                rates.put(currency,ratesArray[1]);

            }
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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

    private static BigDecimal truncateDecimal(double x,int numberofDecimals)
    {
        if ( x > 0) {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
        } else {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);
        }
    }

}
