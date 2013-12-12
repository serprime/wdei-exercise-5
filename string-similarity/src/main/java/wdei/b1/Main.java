
package wdei.b1;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.common.Levenshtein;

import java.io.*;
import java.util.*;

public class Main {

    private static XSSFWorkbook workbook = new XSSFWorkbook();
    private static XSSFSheet jaroSheet = workbook.createSheet("Jaro similarity data");
    private static XSSFSheet levSheet = workbook.createSheet("Levenshtein similarity data");

    private static Map<String, Object[]> jaroData = new TreeMap<String, Object[]>();
    private static Map<String, Object[]> levData = new TreeMap<String, Object[]>();

    private static int jTS75, jTS90, lTS75, lTS90;
    private static int jTD75, jTD90, lTD75, lTD90;
    private static int jFS75, jFS90, lFS75, lFS90;
    private static int jFD75, jFD90, lFD75, lFD90;

    private static File getResourceFile(String filename) {
        return new File("src/main/resources/" + filename);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("base path: " + new File(".").getAbsolutePath());

        String inputFile = "string_input.txt";

        List<String> lines = org.apache.commons.io.FileUtils.readLines(getResourceFile(inputFile));

        int count = 1;
        jaroData.put(String.valueOf(count), new Object[]{"string1", "string2", "actual same/different", "similarity",
                "thresh1 (> 0.75)", "thresh2 (> 0.9)"});
        levData.put(String.valueOf(count), new Object[]{"string1", "string2", "actual same/different", "distance", "similarity",
                "thresh1 (> 0.75)", "thresh2 (> 0.9)"});

        double jaroDistance, levDistance;
        double thresh1 = 0.75;
        double thresh2 = 0.9;
        String actual = null;

        JaroDistance jd = new JaroDistance();
        uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein lev = new uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein();
        

        //Scanner sc = new Scanner(System.in);
        for (String line : lines) {
            count++;
            String[] strings = line.split("[|]");

            String answer = strings[2];
            if (answer.toLowerCase().equals("yes")) {
                actual = "same";
            } else if (answer.toLowerCase().equals("no")) {
                actual = "different";
            }

            //calculate the jaro and levenshtein distances
            jaroDistance = jd.calculate(strings[0], strings[1]);
            levDistance = Levenshtein.distance(strings[0], strings[1]);
            double levSimilarity = lev.getSimilarity(strings[0], strings[1]);
            
            System.out.println(String.format("Jaro distance (%s, %s): %f", strings[0], strings[1], jaroDistance));
            System.out.println(String.format("Levenshtein (%s, %s): %f", strings[0], strings[1], levDistance));
            System.out.println();

            //determine the result of similarity in relation to the actual difference
            String jaroResult75 = determineResult(jaroDistance, thresh1, actual, "jaro75");
            String jaroResult90 = determineResult(jaroDistance, thresh2, actual, "jaro90");
            String levResult75 = determineResult(levSimilarity, thresh1, actual, "lev75");
            String levResult90 = determineResult(levSimilarity, thresh2, actual, "lev90");

            //put the data in a map
            jaroData.put(String.valueOf(count), new Object[]{
                    strings[0], strings[1], actual, jaroDistance,
                    jaroResult75, jaroResult90
            });

            levData.put(String.valueOf(count), new Object[]{
                    strings[0], strings[1], actual, levDistance, levSimilarity,
                    levResult75, levResult90
            });
        } //end while

        //calculate the precision and recall and put in map
        getPrecisionAndRecall(count);

        //write map data to sheet
        writeToSheet(jaroData, jaroSheet);
        writeToSheet(levData, levSheet);

        //write to file
        try {
            FileOutputStream out = new FileOutputStream(new File("result/spreadsheet.xlsx"));
            workbook.write(out);
            out.close();
            System.out.println("Spreadsheet successfully created.");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String determineResult(double distance, double threshold, String actual, String flag) {

        String result = null;
        if (distance >= threshold && actual.equals("same")) {
            if (flag.equals("jaro75")) jTS75++;
            if (flag.equals("jaro90")) jTS90++;
            if (flag.equals("lev75")) lTS75++;
            if (flag.equals("lev90")) lTS90++;
            result = "TS";
        }
        if (distance >= threshold && actual.equals("different")) {
            if (flag.equals("jaro75")) jFS75++;
            if (flag.equals("jaro90")) jFS90++;
            if (flag.equals("lev75")) lFS75++;
            if (flag.equals("lev90")) lFS90++;
            result = "FS";
        }
        if (distance < threshold && actual.equals("same")) {
            if (flag.equals("jaro75")) jFD75++;
            if (flag.equals("jaro90")) jFD90++;
            if (flag.equals("lev75")) lFD75++;
            if (flag.equals("lev90")) lFD90++;
            result = "FD";
        }
        if (distance < threshold && actual.equals("different")) {
            if (flag.equals("jaro75")) jTD75++;
            if (flag.equals("jaro90")) jTD90++;
            if (flag.equals("lev75")) lTD75++;
            if (flag.equals("lev90")) lTD90++;
            result = "TD";
        }

        return result;
    }

    public static void writeToSheet(Map<String, Object[]> data, XSSFSheet sheet) {

        Set<String> keySet = data.keySet();
        int rowNum = 0;

        for (String key : keySet) {
            Row row = sheet.createRow(rowNum++);
            Object[] objArr = data.get(key);
            int cellNum = 0;

            for (Object obj : objArr) {
                Cell cell = row.createCell(cellNum++);

                if (obj instanceof String) {
                    cell.setCellValue((String) obj);
                } else if (obj instanceof Integer) {
                    cell.setCellValue((Integer) obj);
                } else {
                    cell.setCellValue((Double) obj);
                }
            }
        }
    }

    public static void getPrecisionAndRecall(int count) {

        jaroData.put(String.valueOf(++count), new Object[]{"", "", "", "", "", "", "", "",
                "TS", "FS", "FD", "TD", "precision", "recall"});
        levData.put(String.valueOf(++count), new Object[]{"", "", "", "", "", "", "", "", "",
                "TS", "FS", "FD", "TD", "precision", "recall"});

        double jaroPrecision75 = jTS75 / (double) (jTS75 + jFS75);
        double jaroPrecision90 = jTS90 / (double) (jTS90 + jFS90);
        double levPrecision75 = lTS75 / (double) (lTS75 + lFS75);
        double levPrecision90 = lTS90 / (double) (lTS90 + lFS90);

        double jaroRecall75 = jTS75 / (double) (jTS75 + jFD75);
        double jaroRecall90 = jTS90 / (double) (jTS90 + jFD90);
        double levRecall75 = lTS75 / (double) (lTS75 + lFD75);
        double levRecall90 = lTS90 / (double) (lTS90 + lFD90);

        System.out.println(jaroRecall75 + " " + jaroRecall90 + " " + levRecall75 + " " + levRecall90);

        jaroData.put(String.valueOf(++count), new Object[]{"", "", "", "", "", "", "", "",
                jTS75, jFS75, jFD75, jTD75, jaroPrecision75, jaroRecall75});
        jaroData.put(String.valueOf(++count), new Object[]{"", "", "", "", "", "", "", "",
                jTS90, jFS90, jFD90, jTD90, jaroPrecision90, jaroRecall90});
        levData.put(String.valueOf(++count), new Object[]{"", "", "", "", "", "", "", "", "",
                lTS75, lFS75, lFD75, lTD75, levPrecision75, levRecall75});
        levData.put(String.valueOf(++count), new Object[]{"", "", "", "", "", "", "", "", "",
                lTS90, lFS90, lFD90, lTD90, levPrecision90, levRecall90});
    }


}
