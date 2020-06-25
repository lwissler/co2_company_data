package parser;

import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.glassfish.jersey.internal.inject.ExtractorException;
import org.json.simple.JSONObject;

import auxiliary.Tools;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.apache.log4j.*;

public class CDPParser {
	Tools tools = new Tools();
	
	public void parsePDF(String url) throws IOException, ParseException{
//		Date now = new Date();
//		String nowStr = now.toLocaleString().replace(":", "-").replace(".", "_");
//		try {
//			PrintStream out = new PrintStream(new FileOutputStream("./log/"+nowStr+".txt"));
//			System.setOut(out);
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		}

		
//		String coca = "2019-cdp-climate-change-response.pdf";
//		String bayer = "20190712-cdp-climate-report-bag-final.pdf";
//		String conti = "climate-change--data.pdf";
//		String basf = "CDP_Programme_Response_Climate_Change_2019.pdf";
//		String MSCI = "MSCI-CDP-Climate-Change-Questionnaire-2019.pdf";
//		String eastman = "https://www.eastman.com/Company/Sustainability/Documents/CDP-Climate-Change-2019.pdf";//"CDP-Climate-Change-2019.pdf";
//		String saintgobain = "cdp_2019_cc_final_version.pdf";
//		String commbank = "2018-cdp-submission.pdf";
//		String akamai = "cdp-2019.pdf";
//		String enka = "ENKA-CDP-Climate-Change-2018-Answers.pdf";
		
		tools.print(url);
		process(url);
//		tools.print("BASF");
//		process(basf);
//		tools.print("CONTI");
//		process(conti);
//		tools.print("BAYER");
//		process(bayer);
//		tools.print("MSCI");
//		process(MSCI);
//		tools.print("eastman");
//		process(eastman);
//		tools.print("Gobain");
//		process(saintgobain);
//		tools.print("COMMBANK");
//		process(commbank);
//		tools.print("Akamai");
//		process(akamai);
//		tools.print("ENKA");
//		process(enka);
		
		
		
		
//		
		

		
//		String bayertext = getTextByRegion(bayer);
//		String contitext = getTextByRegion(conti);
	
//		String cocatext = getTextByRegion(coca);
//		parse(cocatext);
//		
	
//		parse(contitext);
////		
//		parse(bayertext);
		
//		cocatext=reduce(cocatext,"en");
//		basftext=reduce(basftext,"en");
//		contitext=reduce(contitext,"en");
//		bayertext=reduce(bayertext,"en");
//		tools.print(bayertext);
//		tools.print(basftext);
//		tools.print(contitext);
//		tools.print(cocatext);
//		
//		tools.print(tools.stringSimilarity(cocatext, bayertext));
//		tools.print(tools.stringSimilarity(basftext, bayertext));
//		tools.print(tools.stringSimilarity(cocatext, contitext));
//		tools.print(tools.stringSimilarity(basftext, contitext));
	}
	
	private void process(String url) {
		try {
			parse(readText(url));
		} catch (Exception e) {
			e.printStackTrace();
			try {
				parse(getTextByRegion(url,true,false));
			} catch (Exception e1) {
				try {
					e1.printStackTrace();
					parse(getTextByRegion(url,false,true));
				} catch (Exception e2) {
					try {
						e2.printStackTrace();
						parse(getTextByRegion(url,true,true));
					} catch (Exception e3) {
						e3.printStackTrace();
					} 
				} 
			} 
		} 
	}

	private String readText(String urlPath) throws IOException {
//		File file = new File(urlPath);
		URL url = new URL(urlPath);
		InputStream input = url.openStream();
//		 BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//		InputStream in = new InputStreamReader(url.openStream());
		PDDocument doc = PDDocument.load(input);
		PDFTextStripper stripper = new PDFTextStripper();
		String text = stripper.getText(doc);
		doc.close();
		return text;
	}

	public String getTextByRegion(String path, Boolean header, Boolean footer) throws IOException {
		String text = "";
        File file = new File(path);
        PDDocument document = PDDocument.load(file);
        //Rectangle2D region = new Rectangle2D.Double(x,y,width,height);
        String regionName = "region";
        PDFTextStripperByArea stripper;
        
        PDPage page = document.getPage(0);
        float height = page.getMediaBox().getHeight();
        float width = page.getMediaBox().getWidth();
        int ypos = 0;
        
        if(header&&!footer) {
        	ypos = 100;
        	height = height-100;
        }
        if(!header&&footer) {
        	height = height-100;
        }
        if(header&&footer) {
        	ypos = 100;
        	height = height-100;
        }
        
        Rectangle2D region = new Rectangle2D.Double(0, ypos, width, height);
        
        for(int i = 0; i < document.getPages().getCount(); i++) {
        	page = document.getPage(i);
            stripper = new PDFTextStripperByArea();
            stripper.addRegion(regionName, region);
            stripper.extractRegions(page);
            text = text + stripper.getTextForRegion(regionName);
        }
        
//        tools.print(text);
        return text;
    }
	
	@SuppressWarnings("unchecked")
	private void parse(String text) throws Exception {
		try {
			parseNewStyle(text);
		}catch(Exception e) {
			parseOldStyle(text);
		}
	}
	
	

	private void parseOldStyle(String text) throws ParseException {
JSONObject res = new JSONObject();
		
		res.put("Company", getCompany(text));
		
		text = text.substring(text.indexOf("C7.2"));
		text = text.replace("\n", " ").replace("\r", " ").replace(System.getProperty("line.separator"), " ").replaceAll(" +", " ");;
		
		res.put("ReportingStandard", getStandardOld(text));
		
		JSONObject scope1 = new JSONObject();
		res.put("Scope 1", scope1);
		
		scope1.put("CO2", getScope1CO2Old(text));	
		String startDate = getStartDateOld(text);
		String endDate = getEndDateOld(text);
		
		scope1.put("StartDate", startDate);
		scope1.put("EndDate", endDate);
		
		JSONObject scope2 = new JSONObject();
		res.put("Scope 2", scope2);
		
		scope2.put("CO2", getScope2Location(text));
		scope2.put("CO2 market based", getScope2market(text));
		scope2.put("StartDate", startDate);
		scope2.put("EndDate", endDate);
		
		JSONObject scope3json = new JSONObject();
		res.put("Scope 3", scope3json);
		
		scope3json.put("CO2", getScope3CO2(text));
		
		tools.print(res);
		
	}

	private void parseNewStyle(String text) throws ParseException {
		JSONObject res = new JSONObject();
		
		res.put("Company", getCompany(text));
		
		text = text.substring(text.indexOf("C5.2"));
		text = text.replace("\n", " ").replace("\r", " ").replace(System.getProperty("line.separator"), " ").replaceAll(" +", " ");;
		
		res.put("ReportingStandard", getStandard(text));
		
		JSONObject scope1 = new JSONObject();
		res.put("Scope 1", scope1);
		
		scope1.put("CO2", getScope1CO2(text));		
		scope1.put("StartDate", getScope1StartDate(text));
		scope1.put("EndDate", getScope1EndDate(text));
		
		JSONObject scope2 = new JSONObject();
		res.put("Scope 2", scope2);
		
		scope2.put("CO2", getScope2Location(text));
		scope2.put("CO2 market based", getScope2market(text));
		scope2.put("StartDate", getScope2StartDate(text));
		scope2.put("EndDate", getScope2EndDate(text));
		
		JSONObject scope3json = new JSONObject();
		res.put("Scope 3", scope3json);
		
		scope3json.put("CO2", getScope3CO2(text));
		
		tools.print(res);
		
	}

	private Long getScope3CO2(String text) {
		Long res = null;
		
		try {
			String scope3 = text.split(Pattern.quote("6.5 (C6.5) "))[1].split("Metric tonnes CO2e")[1].split("Emissions calculation methodology")[0].trim();
			res = tools.parseNumber(scope3);
		} catch (Exception e) {
		}
		
		return res;
	}

	private String getScope2EndDate(String text) throws ParseException {
		
		String res = null;
		
		try {
			String scope2EndDate = text.split("End date")[1].split("Comment")[0].trim();
			res = tools.formatCDPDate(scope2EndDate.replace(".", "").replace(",", ""));
		} catch (Exception e) {
			text = text.split("6.3")[1];
			try {
				res = tools.formatCDPDate(text.split(Pattern.quote("Comment"))[1].split(" ")[4].trim());
			} catch (ParseException e1) {
				res = tools.formatCDPDate2(text.split(Pattern.quote("Comment"))[1].split(" ")[4].trim());
			}
		}
		
		return res;
	}

	private String getScope2StartDate(String text) throws ParseException {
		String res = null;
		
		try {
			String scope2StartDate = text.split("Start date")[1].split("End date")[0].trim();
			res = tools.formatCDPDate(scope2StartDate.replace(".", "").replace(",", ""));
		} catch (Exception e) {
			text = text.split("6.3")[1];
			try {
				res = tools.formatCDPDate(text.split(Pattern.quote("Comment"))[1].split(" ")[3].trim());
			} catch (ParseException e1) {
				res = tools.formatCDPDate2(text.split(Pattern.quote("Comment"))[1].split(" ")[3].trim());
			}
		}
		
		return res;
	}

	private Long getScope2marketOld(String text) {
		Long res = null;
		String scope2market;
		try {
			scope2market = text.split(Pattern.quote("Scope 2, market-based (if applicable)"))[1].split("Start date")[0].trim();
			res = tools.parseNumber(scope2market);
		} catch (Exception e) {
			//probably table based reply
			try {
				text = text.split("8.3")[1];
				scope2market = text.split(Pattern.quote("Comment"))[1].split(" ")[2].trim();
				res = tools.parseNumber(scope2market);
			} catch (Exception e1) {
			}
		}
		
		return res;
	}

	private Long getScope2LocationOld(String text) {
		Long res = null;
		
		try {
			String scope2location = text.split(Pattern.quote("CC8.3"))[1].split("Scope 2, location-based")[1].split(Pattern.quote("Scope 2, market-based (if applicable)"))[0].trim();
			res = tools.parseNumber(scope2location);
		} catch (Exception e) {
			//probably table based reply
			try {
				text = text.split("8.3")[1];
				String scope2location = text.split(Pattern.quote("Comment"))[1].split(" ")[1].trim();
				res = tools.parseNumber(scope2location);
			} catch (Exception e1) {
			}
		}
		
		return res;
	}
	
	private Long getScope2market(String text) {
		Long res = null;
		String scope2market;
		try {
			scope2market = text.split(Pattern.quote("Scope 2, market-based (if applicable)"))[1].split("Start date")[0].trim();
			res = tools.parseNumber(scope2market);
		} catch (Exception e) {
			//probably table based reply
			try {
				text = text.split("6.3")[1];
				scope2market = text.split(Pattern.quote("Comment"))[1].split(" ")[2].trim();
				res = tools.parseNumber(scope2market);
			} catch (Exception e1) {
			}
		}
		
		return res;
	}

	private Long getScope2Location(String text) {
		Long res = null;
		
		try {
			String scope2location = text.split(Pattern.quote("6.3 (C6.3)"))[1].split("Scope 2, location-based")[1].split(Pattern.quote("Scope 2, market-based (if applicable)"))[0].trim();
			res = tools.parseNumber(scope2location);
		} catch (Exception e) {
			//probably table based reply
			try {
				text = text.split("6.3")[1];
				String scope2location = text.split(Pattern.quote("Comment"))[1].split(" ")[1].trim();
				res = tools.parseNumber(scope2location);
			} catch (Exception e1) {
			}
		}
		
		return res;
	}

	private String getScope1EndDate(String text) throws ParseException {
		String res = null;
		try {
			String scope1EndDate = text.split("End date")[1].split("Comment")[0].trim();
			res = tools.formatCDPDate(scope1EndDate.replace(",", ""));
		} catch (Exception e) {
			//probably table based reply
			try {
				res = tools.formatCDPDate(text.split(Pattern.quote("Comment"))[1].split(" ")[3].trim());
			} catch (ParseException e1) {
				res = tools.formatCDPDate2(text.split(Pattern.quote("Comment"))[1].split(" ")[3].trim());
			}
		}
		
		return res;
	}

	private String getScope1StartDate(String text) throws ParseException {
		String res = null;
		
		try {
			String scope1StartDate = text.split("Start date")[1].split("End date")[0].trim();
			res = tools.formatCDPDate(scope1StartDate.replace(",", ""));
		} catch (Exception e) {
			//probably table based reply
			try {
				res = tools.formatCDPDate(text.split(Pattern.quote("Comment"))[1].split(" ")[2].trim());
			} catch (ParseException e1) {
				res = tools.formatCDPDate2(text.split(Pattern.quote("Comment"))[1].split(" ")[2].trim());
			}
		}
		
		return res;
	}
	
	private String getEndDateOld(String text) throws ParseException {
		String res = null;
		try {
			String scope1EndDate = text.split("Page: CC8. Emissions Data -(")[1].split("-")[1].split(")")[0].trim();
			res = tools.formatCDPDate(scope1EndDate.replace(",", ""));
		} catch (Exception e) {
			//probably table based reply
			try {
				res = tools.formatCDPDate(text.split(Pattern.quote("Comment"))[1].split(" ")[3].trim());
			} catch (ParseException e1) {
				res = tools.formatCDPDate2(text.split(Pattern.quote("Comment"))[1].split(" ")[3].trim());
			}
		}
		
		return res;
	}

	private String getStartDateOld(String text) throws ParseException {
		String res = null;
		
		try {
			String scope1StartDate = text.split("Page: CC8. Emissions Data -(")[1].split("-")[0].trim();
			res = tools.formatCDPDate(scope1StartDate.replace(",", ""));
		} catch (Exception e) {
			//probably table based reply
			try {
				res = tools.formatCDPDate(text.split(Pattern.quote("Comment"))[1].split(" ")[2].trim());
			} catch (ParseException e1) {
				res = tools.formatCDPDate2(text.split(Pattern.quote("Comment"))[1].split(" ")[2].trim());
			}
		}
		
		return res;
	}

	private String getCompany(String text) {
		String company = null;
		
		try {
			company = text.split(Pattern.quote("(C0.1) Give a general description and introduction to your organization."))[1].split(Pattern.quote("C0.2"))[0].trim();
		} catch (Exception e) {
			company = text.split(Pattern.quote("give a general description and introduction to your organization."))[1].split(Pattern.quote("C0.2"))[0].trim();
		}
		//getCompanyList
		//iterate companies and if contained return
		return company;
	}

	private Object getStandard(String text) {
		String standard;
		try {
			standard = text.split("calculate Scope 1 and Scope 2 emissions.")[1].split("C6. Emissions")[0].trim();
		} catch (Exception e) {
			try {
				standard = text.split("calculate Scope1 and Scope 2 emissions.")[1].split("C6. Emissions")[0].trim();
			} catch (Exception e1) {
				try {
					standard = text.split("calculate Scope 1 and Scope2 emissions.")[1].split("C6. Emissions")[0].trim();
				} catch (Exception e2) {
					standard = text.split("calculate Scope1 and Scope2 emissions.")[1].split("C6. Emissions")[0].trim();
				}
			}
		}
		return standard.split(":")[0];
	}
	
	private Object getStandardOld(String text) {
		String standard;
//		try {
			standard = text.split("Please select the published methodologies that you use")[1].split("C7.3")[0].trim();
//		} catch (Exception e) {
//			try {
//				standard = text.split("calculate Scope1 and Scope 2 emissions.")[1].split("C6. Emissions")[0].trim();
//			} catch (Exception e1) {
//				try {
//					standard = text.split("calculate Scope 1 and Scope2 emissions.")[1].split("C6. Emissions")[0].trim();
//				} catch (Exception e2) {
//					standard = text.split("calculate Scope1 and Scope2 emissions.")[1].split("C6. Emissions")[0].trim();
//				}
//			}
//		}
		return standard.split(":")[0];
	}

	private Long getScope1CO2(String text) {
		String scope1CO2;
		Long res = null; 
		try {
			scope1CO2 = text.split(Pattern.quote("Gross global Scope 1 emissions (metric tons CO2e)"))[1].split("Start date")[0].trim();
		} catch (Exception e) {
			scope1CO2 = text.split(Pattern.quote("Gross global Scope1 emissions (metric tons CO2e)"))[1].split("Start date")[0].trim();
		}
		
		try {
			res = tools.parseNumber(scope1CO2);
		} catch (NumberFormatException e) {
			//probably table based reply
			scope1CO2 = text.split(Pattern.quote("Comment"))[1].split(" ")[1].trim();
			res = tools.parseNumber(scope1CO2);
		}
		
		return res;
	}
	
	private Long getScope1CO2Old(String text) {
		String scope1CO2;
		Long res = null; 
		try {
			scope1CO2 = text.split(Pattern.quote("gross global Scope 1 emissions figures in metric tonnes CO2e"))[1].split("CC8.3")[0].trim();
		} catch (Exception e) {
			scope1CO2 = text.split(Pattern.quote("Gross global Scope1 emissions (metric tons CO2e)"))[1].split("End-year of reporting period")[0].trim();
		}
		
		try {
			res = tools.parseNumber(scope1CO2);
		} catch (NumberFormatException e) {
			//probably table based reply
			scope1CO2 = text.split(Pattern.quote("Comment"))[1].split(" ")[1].trim();
			res = tools.parseNumber(scope1CO2);
		}
		
		return res;
	}

	private String[] stem(String[] tokens) {
		PorterStemmer stemmer = new PorterStemmer();
		for(int i = 0; i < tokens.length; i++) {         
	         tokens[i] = stemmer.stem(tokens[i]);  
	     }
		return tokens;
	}

	public String filterStopWords(String text, String language) {
		String[] stopwords = {};
		try {
			stopwords = tools.loadStopWords(language);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		String stopWordsPattern = String.join("|", stopwords);
		Pattern pattern = Pattern.compile("\\b(?:" + stopWordsPattern + ")\\b\\s*", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		return matcher.replaceAll("");
	}
	
	public String[] tokenize(String s) {
		      SimpleTokenizer simpleTokenizer = SimpleTokenizer.INSTANCE;  
		      return simpleTokenizer.tokenize(s);  
	}
	/*
	 * process String to reduce size with stemming, tokenizing, stopword removal and so on
	 * 
	 */
	public String reduce(String s, String language) {
		s = filterStopWords(s, language);
		String[] tokens = tokenize(s);
		stem(tokens);
		return tools.stringArrToString(tokens);
	}
}
