package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.simple.JSONObject;

import auxiliary.Tools;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.SimpleTokenizer;

public class CDPParser {
	Tools tools = new Tools();
	
	public void parsePDF() throws IOException{
		Date now = new Date();
//		String nowStr = now.toLocaleString().replace(":", "-").replace(".", "_");
//		try {
//			PrintStream out = new PrintStream(new FileOutputStream("./log/"+nowStr+".txt"));
//			System.setOut(out);
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		}

		
		String coca = "2019-cdp-climate-change-response.pdf";
		String bayer = "20190712-cdp-climate-report-bag-final.pdf";
		String conti = "climate-change--data.pdf";
		String basf = "CDP_Programme_Response_Climate_Change_2019.pdf";
		
		File file = new File(coca);
		PDDocument doc = PDDocument.load(file);
		PDFTextStripper stripper = new PDFTextStripper();
		String cocatext = stripper.getText(doc);
//		System.out.println(cocatext);
		doc.close();
		
		file = new File(bayer);
		 doc = PDDocument.load(file);
		String bayertext = stripper.getText(doc);
//		System.out.println(bayertext);
		doc.close();
		
		file = new File(conti);
		 doc = PDDocument.load(file);
		String contitext = stripper.getText(doc);
//		System.out.println(contitext);
		doc.close();
		
		file = new File(basf);
		 doc = PDDocument.load(file);
		String basftext = stripper.getText(doc);
//		System.out.println(basftext);
		doc.close();

		parse(cocatext);
		
		cocatext=reduce(cocatext,"en");
		basftext=reduce(basftext,"en");
		contitext=reduce(contitext,"en");
		bayertext=reduce(bayertext,"en");
		tools.print(bayertext);
		tools.print(basftext);
		tools.print(contitext);
		tools.print(cocatext);
		
		tools.print(tools.stringSimilarity(cocatext, bayertext));
		tools.print(tools.stringSimilarity(basftext, bayertext));
		tools.print(tools.stringSimilarity(cocatext, contitext));
		tools.print(tools.stringSimilarity(basftext, contitext));
	}
	
	private void parse(String cocatext) {
		JSONObject res = new JSONObject();
		cocatext = cocatext.substring(cocatext.indexOf("C5.2"));
		String standard = cocatext.split("calculate Scope 1 and Scope 2 emissions.")[1].split("C6. Emissions")[0];
		String scope1 = cocatext.split("Gross global Scope 1 emissions (metric tons CO2e)")[1].split("Start date")[0];
		String scope1StartDate = cocatext.split("Start date")[1].split("End date")[0];
		String scope1EndDate = cocatext.split("End date")[1].split("Comment")[0];
		
		String scope2location = cocatext.split("Scope 2, location-based")[1].split("Scope 2, market-based (if applicable)")[0];
		String scope2market = cocatext.split("Scope 2, market-based (if applicable)")[1].split("Start date")[0];
		String scope2StartDate = cocatext.split("Start date")[1].split("End date")[0];
		String scope2EndDate = cocatext.split("End date")[1].split("Comment")[0];
		
//		String scope3 = cocatext.split("Scope 2, location-based")[1].split("Scope 2, market-based (if applicable)")[0];
		
		
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
