package wdei.b1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

/**
 * Created with IntelliJ IDEA.
 * User: serprime
 * Date: 12/7/13
 * Time: 11:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class JaroDistance {
	
	private static XSSFWorkbook workbook = new XSSFWorkbook(); 
    private static XSSFSheet jaroSheet = workbook.createSheet("Jaro similarity data");
    private static XSSFSheet levSheet = workbook.createSheet("Levenshtein similarity data");
	
    private static Map<String, Object[]> jaroData = new TreeMap<String, Object[]>();
    private static Map<String, Object[]> levData = new TreeMap<String, Object[]>();
    
    private static int jTS75, jTS90, lTS75, lTS90;
    private static int jTD75, jTD90, lTD75, lTD90;
    private static int jFS75, jFS90, lFS75, lFS90;
    private static int jFD75, jFD90, lFD75, lFD90;

    public double calculate(String left, String right) {

        Vars vars = calculateVars(left, right);
        double m = vars.matches;
        double t = vars.transpositions;

        double distance = 0;
        if (m > 0) {
            double ml = m / (double) left.length();
            double mr = m / (double) right.length();
            double mt = (m - t) / m;
            double mlrt = ml + mr + mt;
            distance = (1f / 3f) * mlrt;
        }
        //System.out.println(String.format("Jaro distance (%s, %s): %f", left, right, distance));

        return distance;
    }

    private Vars calculateVars(String left, String right) {
        int totalMatches = 0;
        int totalTranspositions = 0;
        int matchingRadius = max(left.length(), right.length()) / 2 - 1;


        // --
        boolean[] leftMatches = new boolean[left.length()];
        Arrays.fill(leftMatches, false);
        boolean[] rightMatches = new boolean[right.length()];
        Arrays.fill(rightMatches, false);
        // --

        // check for each character in left if it matches a character inside the radius of right
        for (int l = 0; l < left.length(); l++) {
            int start = max(0, l - matchingRadius);
            int end = min(l + matchingRadius, right.length() - 1);
            // matches
            for (int r = start; r <= end; r++) {
                // we need to skip already matched chars
                if (rightMatches[r]) {
                    continue;
                }
                if (left.charAt(l) == right.charAt(r)) {
                    totalMatches++;
                    leftMatches[l] = true;
                    rightMatches[r] = true;
                    // we have to break if we find the first match,
                    // else we count one character
                    // multiple times and can get a distance greater 1
                    break;
                }
            }
        }
        // calculate transpositions
        int halfTranspositions = 0;
        int r = 0;
        for (int l = 0; l < leftMatches.length; l++) {
            if (!leftMatches[l]) {
                continue;
            }
            while (!rightMatches[r]) {
                r++;
            }
            if (left.charAt(l) != right.charAt(r)) {
                halfTranspositions++;
            }
            r++;
        }
        return new Vars(totalMatches, halfTranspositions/2);
    }

    public class Vars {
        public Vars(int mathes, int transpositions) {
            this.matches = mathes;
            this.transpositions = transpositions;
        }

        public int matches = 0;
        public int transpositions = 0;

        @Override
        public String toString() {
            return "Vars{" +
                    "matches=" + matches +
                    ", transpositions=" + transpositions +
                    '}';
        }
    }

    private int max(int left, int right) {
        return left > right ? left : right;
    }

    private int min(int left, int right) {
        return left < right ? left : right;
    }
    
    public static String determineResult(double distance, double threshold, String actual, String flag) {
    	
    	String result = null;
    	if(distance >= threshold && actual.equals("same")) {
    		if(flag.equals("jaro75")) jTS75++;
    		if(flag.equals("jaro90")) jTS90++;
    		if(flag.equals("lev75")) lTS75++;
    		if(flag.equals("lev90")) lTS90++;
    		result = "TS";
    	}
        if(distance >= threshold && actual.equals("different")) {
        	if(flag.equals("jaro75")) jFS75++;
    		if(flag.equals("jaro90")) jFS90++;
    		if(flag.equals("lev75")) lFS75++;
    		if(flag.equals("lev90")) lFS90++;
        	result = "FS";
        }
        if(distance < threshold && actual.equals("same")) {
        	if(flag.equals("jaro75")) jFD75++;
    		if(flag.equals("jaro90")) jFD90++;
    		if(flag.equals("lev75")) lFD75++;
    		if(flag.equals("lev90")) lFD90++;
        	result = "FD";
        }
        if(distance < threshold && actual.equals("different")) {
        	if(flag.equals("jaro75")) jTD75++;
    		if(flag.equals("jaro90")) jTD90++;
    		if(flag.equals("lev75")) lTD75++;
    		if(flag.equals("lev90")) lTD90++;
        	result = "TD"; 
        }
    	
        return result;
    }
    
    public static void writeToSheet(Map<String, Object[]> data, XSSFSheet sheet) {
    	
    	Set<String> keySet = data.keySet();
         int rowNum = 0;
         
         for (String key : keySet) {	 
             Row row = sheet.createRow(rowNum++);
             Object [] objArr = data.get(key);
             int cellNum = 0;
             
             for (Object obj : objArr) {
                Cell cell = row.createCell(cellNum++);
                
                if(obj instanceof String) {
                     cell.setCellValue((String) obj);
                } else if(obj instanceof Integer) {
                	cell.setCellValue((Integer) obj);
                } else {
                	cell.setCellValue((Double) obj);
                }
             }
         }
    }
    
    public static void getPrecisionAndRecall(int count) {
    	
    	jaroData.put(String.valueOf(++count), new Object[] {"", "", "", "", "", "", "", "",
    		"TS", "FS", "FD", "TD", "precision", "recall"});
    	levData.put(String.valueOf(++count), new Object[] {"", "", "", "", "", "", "", "", 
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
    	
    	jaroData.put(String.valueOf(++count), new Object[] {"", "", "", "", "", "", "", "",
    		jTS75, jFS75, jFD75, jTD75, jaroPrecision75, jaroRecall75});
    	jaroData.put(String.valueOf(++count), new Object[] {"", "", "", "", "", "", "", "",
    		jTS90, jFS90, jFD90, jTD90, jaroPrecision90, jaroRecall90});
    	levData.put(String.valueOf(++count), new Object[] {"", "", "", "", "", "", "", "",
    		lTS75, lFS75, lFD75, lTD75, levPrecision75, levRecall75});
    	levData.put(String.valueOf(++count), new Object[] {"", "", "", "", "", "", "", "",
    		lTS90, lFS90, lFD90, lTD90, levPrecision90, levRecall90});
    }
    
    public static void main(String[] args) {
    	
    	String inputFile = "string_input.txt";
    	
    	BufferedReader br = null;
    	try {
    		br = new BufferedReader(new FileReader(inputFile));
    	} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	String line = null;
    	try {
			line = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        int count = 1;
    	jaroData.put(String.valueOf(count), new Object[] {"string1", "string2", "actual same/diff", "distance", 
    		"thresh1 (> 0.75)", "thresh2 (> 0.9)"});
    	levData.put(String.valueOf(count), new Object[] {"string1", "string2", "actual same/diff", "distance", 
    		"thresh1 (> 0.75)", "thresh2 (> 0.9)"});
    	
        double jaroDistance, levDistance;
        double thresh1 = 0.75;
        double thresh2 = 0.9;
        String actual = null;        
        
    	JaroDistance jd = new JaroDistance();
    	Levenshtein lev = new Levenshtein();
    	
		Scanner sc = new Scanner(System.in);
    	while(line != null) {
    		count++;
    		String[] strings = line.split("[|]");
    		
    		//read strings from the file, acquire manually if the strings should be the same or different
    		System.out.println("Should the strings (" + strings[0] + ", " + strings[1] + ") be classified as same? yes/no");
    		String answer = sc.next();
    		//String answer = "yes";
    		if(answer.toLowerCase().equals("yes")) {
    			actual = "same";
    		} else if(answer.toLowerCase().equals("no")) {
    			actual = "different";
    		}
    		
    		//calculate the jaro and levenshtein distances
    		jaroDistance = jd.calculate(strings[0], strings[1]);
    		levDistance = lev.getSimilarity(strings[0], strings[1]);
    		
            System.out.println(String.format("Jaro distance (%s, %s): %f", strings[0], strings[1], jaroDistance));
            System.out.println(String.format("Levenshtein (%s, %s): %f", strings[0], strings[1], levDistance));
            System.out.println();
            
            //determine the result of similarity in relation to the actual difference
            String jaroResult75 = determineResult(jaroDistance, thresh1, actual, "jaro75");
            String jaroResult90 = determineResult(jaroDistance, thresh2, actual, "jaro90");
            String levResult75 = determineResult(jaroDistance, thresh1, actual, "lev75");
            String levResult90 = determineResult(jaroDistance, thresh2, actual, "lev90");
            
            //put the data in a map
            jaroData.put(String.valueOf(count), new Object[] {
            		strings[0], strings[1], actual, jaroDistance, 
            		jaroResult75, jaroResult90
            	});

            levData.put(String.valueOf(count), new Object[] {
            		strings[0], strings[1], actual, levDistance, 
            		levResult75, levResult90
            	});
            
    		try {
    			line = br.readLine();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	} //end while
    	
    	//calculate the precision and recall and put in map
    	getPrecisionAndRecall(count);

    	//write map data to sheet
    	writeToSheet(jaroData, jaroSheet);
    	writeToSheet(levData, levSheet);
    	
    	//write to file
    	try {
    		FileOutputStream out = new FileOutputStream(new File("spreadsheet.xlsx"));
    		workbook.write(out);
    		out.close();
    		System.out.println("Spreadsheet successfully created.");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	try {
			br.close();
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }

}
