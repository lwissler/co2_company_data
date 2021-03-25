package auxiliary;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;

import net.bytebuddy.agent.builder.AgentBuilder.InitializationStrategy.SelfInjection.Split;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class NLPAux {
	
	Tools tools = new Tools();
	
	public String anything = "(.*)";
	public String space = "(\\s)";
	
	public String decimalPattern = "\\d+.?\\d+,?\\d|\\d+,?\\d+.?\\d";
	
	public String annotation = "(\\d\\))";
	
	public String units = "(\\s?(€|$))?(\\s?(million|milion|t|kg|gj|t co2 e|mi|ml|%))";
	public String tableNumber = "(\\(?"+units+"?\\s?[+-]?\\d+(\\.\\d+)?"+units+"?\\)?)";
	public String tableWordStrict = "([a-zA-Z€$\\(\\)]+\\d?\\s?[a-zA-Z€$\\(\\)]+)";
	public static String yearNumber = "20[0-3][0-9]";
	public String shortenedYearNumber = "(20)?[0-3][0-9]";
		  
		   public String[] sentence(String text) throws Exception { 
		   
		      InputStream inputStream = new FileInputStream("en-sent.bin"); 
		      SentenceModel model = new SentenceModel(inputStream); 
		       
		      SentenceDetectorME detector = new SentenceDetectorME(model);  
		    
		      String[] sentences = detector.sentDetect(text);
		      
		      for(int i = 0; i < sentences.length; i++) {
		    	  	String sentence = sentences[i];
			    	if(sentence.endsWith(".")||sentence.endsWith("!")||sentence.endsWith("?")) {
			    		sentences[i] = sentence.substring(0,sentence.length()-1);
			    	}
		      }
		      
		      return sentences;
		}
		   

			public Boolean isIntention(String sentence) {
				if((sentence.contains("want")||sentence.contains("intend")||sentence.contains("intent")||sentence.contains("project")
						||sentence.contains("target")||sentence.contains("commit"))
						&&
					(sentence.contains("to"))){
					return true;
				}
				return false;
			}

			public boolean isPercent(String[] words, int j) {
				if(words.length>j+2) {
					if(words[j].contains("%")||words[j+1].trim().equals("percent")||(words[j+1].trim().equals("per")&&words[j+2].trim().equals("cent"))) {
						return true;
					}
				}
				if(words.length>j+1) {
					if(words[j].contains("%")||words[j+1].trim().equals("percent")) {
						return true;
					}else if(words[j+1].equals("%")) {
						return true;
					}
				}
				if(words[j].contains("%")) {
					return true;
				}
				
				return false;
			}


			public Tuple<String[][],String[]> detectTableYearColumns(String text, String[] keywords, int numTable) {
				int tableNum = 0;
				String tableLineExp = anything+space+tableNumber+"("+space+tableNumber+")+";
				 Pattern tableLinePattern = Pattern.compile(tableLineExp);
				String headlineExp = "(.*\\s)?"+shortenedYearNumber+"\\s"+shortenedYearNumber+"(\\s.*)?";
				Pattern headlinePattern = Pattern.compile(headlineExp);
				
				String[] lines = text.split("\r\n");
				JSONArray headlines = null;
				ArrayList<String> tableLines = new ArrayList<String>();
				String[][] table = null;
				int pos = 0;
				
				for(int i = 0; i < lines.length; i++) {
					int falseCount = 0;
					String line = tools.cleanText((lines[i].replace("%", "").replace(",", "")),true);
					line = annotationCleaner(line);
					if(tableLinePattern.matcher(line).matches()) {
						tools.print(line);
						if(line.contains("total waste volume t 448563 479795 485828 485828")) {
							tools.print("");
						}
						if(!headlinePattern.matcher(line).matches()) {
							headlines = searchHeadlines(lines,i,headlinePattern);
						}else {
							headlines = new JSONArray();
							headlines.add(makeHeadline(line, headlinePattern));
						}
						
						if(headlines!=null) {
							pos = i - headlines.size();
							for(int ii = headlines.size()-1; ii > -1; ii--) {
								tableLines.add((String)headlines.get(ii));
							}
							
							int j = i;
							while(falseCount<3&&j<lines.length-1) {
								if(lines[j].contains("co emissions")) {
									tools.print("");
								}
								j++;
								if(!lines[j].equals("")) {
									line = tools.cleanText((lines[j].replace(",", "")),true);
									tableLines.add(tools.cleanText((line.replace("%", "").replace(",", "")),true));
									tools.print(line);

									if(tableLinePattern.matcher(line).matches()) {
										falseCount = 0;
									}else {
										falseCount++;
									}
								}
							}
							
							if(tableLines.size()>0)
								tableLines.remove(tableLines.size()-1);
							if(tableLines.size()>0)
								tableLines.remove(tableLines.size()-1);
							if(tableLines.size()>0)
								tableLines.remove(tableLines.size()-1);
							
							if(tableLines.size()>1&&tableMatcher(keywords,tableLines)){
								table = makeTableArray(tableLines);
								if(tools.getYears(table).size()>0) {
									for(String[] t : table) {
										tools.printArgs("", t, false);
									}
									if(numTable == tableNum) {
										break;
									}else {
										tableNum++;
									}
								}else {
									tools.print("rejected table");
									i = j;
									tableLines = new ArrayList<String>();
									headlines = null;
									table = null;
								}
							}else {
								tools.print("rejected table");
								i = j;
								tableLines = new ArrayList<String>();
								headlines = null;
							}
						}else {
							tools.print("no headline");
						}
					}
				}
				
				
				String[] additionalLines = {tools.cleanText(lines[pos-1].replace(",", ""),true),tools.cleanText(lines[pos].replace(",", ""),true)};
				Tuple<String[][], String[]> res = new Tuple<String[][], String[]>(table, additionalLines);
				
				return res;
			}
			
			private boolean tableMatcher(String[] keywords, ArrayList<String> tableLines) {
				if(tools.contained(keywords, tableLines)) {
					for(int i = 0; i < tableLines.size(); i++) {
						if(tableLines.get(i).matches(anything+yearNumber+anything)) {
							return true;
						}
					}
				}
				return false;
			}


			public String annotationCleaner(String line) {
				String newLine = line;
				if(line.matches(anything+annotation+anything)&&line.split("\\(").length<line.split("\\)").length) {
					newLine = "";
					String[] split = line.split("\\(");
					for(int i = 0; i < split.length; i++) {
						if(tools.charsInString("\\)",split[i])>1||i==0) {
							split[i] = split[i].replaceAll(annotation, "");
						}
						newLine += split[i];
					}
				}
				
				return newLine;
			}


			private String[][] makeTableArray(ArrayList<String> tableLines) {
				int maxLength = 0;
				tableLines.set(0, tableLines.get(0).replace(": ", "")); 
				for(int i = 0; i < tableLines.size(); i++) {
					String[] split = tableLines.get(i).split(" ");
					split = mergeText(split);
					int length = split.length;
					int descriptionLength = 0;
					for(int j = 0; j < split.length; j++) {
						if(j < split.length-1&&split[j+1].matches(tableWordStrict)) {
							descriptionLength++;
						}else if(!split[j].matches(tableNumber)) {
							descriptionLength++;
						}else {
							break;
						}
					}
					length = length-descriptionLength+1;
					if(length>maxLength) {
						maxLength = length;
					}
				}
				
				String[][] table = new String[tableLines.size()][maxLength];
				for(int i = 0; i < tableLines.size(); i++) {
					table[i] = fitTableLine(annotationCleaner(tableLines.get(i)), maxLength);
				}
				table = consolidateTable(table);
				table = cleanTable(table);
				return table;
			}


			private String[][] cleanTable(String[][] table) {
				for(String[] line : table) {
					for(String entry : line) {
						if(entry!=null) {
							if(entry.equals("n.a.")) {
								entry = "";
							}
						}
					}
				}
				return null;
			}


			private String[][] consolidateTable(String[][] table) {
				String[][] tableReduced = table;
				
				int reducedLength = 0;
				Boolean allNullFound = true;
				while(allNullFound) {
					table = tableReduced;
					reducedLength = 0;
					allNullFound = false;
					for(int i = 0; i < table.length-1; i++) {
						int nullCount = 0;
						for(int j = 1; j < table[0].length; j++) {
							if(table[i][j]==null) {
								nullCount++;
							}
						}
						if(nullCount/((double)table[0].length-1)>0.7) {
							allNullFound = true;
							table[i+1][0] = table[i][0]+table[i+1][0];
							for(int j = i; j < table.length-1; j++) {
								for(int k = 0; k < table[0].length; k++) {
									table[j][k] = table[j+1][k];
								}
							}
							reducedLength++;
						}
					}
					if(reducedLength>0){
						tableReduced = new String[table.length-reducedLength][table[0].length];
						for(int i = 0; i < tableReduced.length; i++) {
							tableReduced[i] = table[i];
						}
					}
				}
				
				
				
				String[][] tableReduced2 = tableReduced;
				reducedLength = 0;
				allNullFound = true;
				while(allNullFound) {
					tableReduced = tableReduced2;
					reducedLength = 0;
					allNullFound = false;
					for(int i = 1; i < tableReduced[0].length; i++) {
						int nullCount = 0;
						if(table[0][i]==null) {
							nullCount++;
						}
						for(int j = 0; j < tableReduced.length; j++) {
							if(table[j][i]==null) {
								nullCount++;
							}
						}
						try {
							if(nullCount/(double)tableReduced.length>0.7||!tableReduced[0][i].matches(anything+shortenedYearNumber+anything)) {
								allNullFound = true;
								for(int j = i; j < table[0].length-1; j++) {
									for(int k = 0; k < table.length; k++) {
										table[k][j] = table[k][j+1];
									}
								}
								reducedLength++;
							}
						} catch (NullPointerException e) {
							allNullFound = true;
							for(int j = i; j < table[0].length-1; j++) {
								for(int k = 0; k < table.length; k++) {
									table[k][j] = table[k][j+1];
								}
							}
							reducedLength++;
						}
					}
					
					if(reducedLength>0){
						tableReduced2 = new String[tableReduced.length][tableReduced[0].length-reducedLength];
						for(int i = 0; i < tableReduced2.length; i++) {
							for(int j = 0; j < tableReduced2[0].length; j++) {
								tableReduced2[i][j] = tableReduced[i][j];
							}
						}
					}
				}
				
				return tableReduced2;

			}


			private String[] fitTableLine(String line, int maxLength) {
				String[] tableLine = new String[maxLength];
				String[] split = line.split(" ");
				split = mergeText(split);
				String description = "";
				int descriptionLength = 0;
				for(int i = 0; i < split.length; i++) {
					if(i < split.length-1&&split[i+1].matches(tableWordStrict)) {
						description = description +" "+ split[i];
						descriptionLength++;
					}else if(!split[i].matches(tableNumber)) {
						description = description +" "+ split[i];
						descriptionLength++;
					}else {
						break;
					}
				}
				int k = 1;
				for(int i = split.length-1; i >= descriptionLength; i--) {
					tableLine[maxLength-k] = split[i].replace("(", "").replace(")","");
					k++;
				}
				tableLine[0] = description;
				return tableLine;
			}


			private String[] mergeText(String[] split) {
				split = tools.cleanSplit(split);
				for(int i = 0; i < split.length-2; i++) {
					if(split[i]!=null) {
						if(((!split[i+1].matches(anything+"\\d(\\.|\\,)?\\d"+anything)||!split[i+2].matches(anything+"\\d(\\.|\\,)?\\d"+anything))&&!split[i+2].contains("change")&&i==0)
//								||
//							(!split[i].matches(anything+"\\d(\\.|\\,)?\\d"+anything)&&!split[i+1].matches(anything+"\\d(\\.|\\,)?\\d"+anything))
								) {
							split[i] = split[i] + " " + split[i+1];
							split[i+1] = null;
							split = tools.cleanSplit(split);
							i--;
						}
					}
				}
				return tools.cleanSplit(split);
			}


			private JSONArray searchHeadlines(String[] lines, int i, Pattern headlinePattern) {

				JSONArray newLines = new JSONArray(); 
				for(int j = i; j > Math.max(-1,i-7); j--) {
					String line = tools.cleanText((lines[j].replace("%", "").replace(",", "")),true);
					if(line.contains("tonnes of")||line.contains("goods sold")||line.contains("total per 1000")||line.equals("")) {
						i--;
					}else if(headlinePattern.matcher(makeHeadline(line, headlinePattern)).matches()){
						String prevLine = tools.cleanText((lines[j-1].replace("%", "").replace(",", "")),true);
						while(prevLine.equals("")||prevLine.matches(yearNumber)) {
							j--;
							if(prevLine.matches(yearNumber)) {
								line = prevLine + " " + line;
							}
							prevLine = tools.cleanText((lines[j-1].replace("%", "").replace(",", "")),true);
						}
						newLines.add(makeHeadline(line, headlinePattern));
						return newLines;
					}else {
						newLines.add(line);
					}
						
				}
				return null;
			}


			private String makeHeadline(String line, Pattern headlinePattern) {
				if(line.matches(anything+yearNumber+anything)) {
					line = line.replaceAll("\\d\\)", "");
				}
				
				return tools.cleanText(line.replace("%", "").replace(",", ""),true).replace("base year", "");
			}
}
