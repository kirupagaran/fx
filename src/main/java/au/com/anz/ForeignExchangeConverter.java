package au.com.anz;

import java.io.*;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by kirupa on 06/07/17.
 */
public class ForeignExchangeConverter {
    public static void main(String[] args) {
        getInput();
    }

    public static void calculateExchangeValue(){
        InputStream csvFile = ForeignExchangeConverter.class.getResourceAsStream("/cross-via.csv");
        //System.out.println("PATH: "+csvFile.toURI());
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {

            br = new BufferedReader(new InputStreamReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] country = line.split(cvsSplitBy);

                System.out.println("Country [code= " + country[0] + " , name=" + country[1] + "]");

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
        getInput();
    }

    public static void getInput(){
        // create a scanner so we can read the command-line input
        System.out.println("\nEnter the input: ");
        Scanner scanner = new Scanner(System.in);


        // get their input as a String
        String input = scanner.nextLine();
        String[] inputValues = input.split(" ");


        if(inputValues.length!=4 && !inputValues[0].equals("quit")){
            System.out.println("Incorrect Arguments: Try Again");
            getInput();
        }
        else{
            if(inputValues[0].equals("quit")){
                System.exit(1);
            }
            else{
                calculateExchangeValue();
                getInput();
            }
        }
    }

}
