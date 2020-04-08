package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Date;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import auxiliary.Tools;

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
		System.out.println(cocatext);
		doc.close();
		
		file = new File(bayer);
		 doc = PDDocument.load(file);
		String bayertext = stripper.getText(doc);
		System.out.println(bayertext);
		doc.close();
		
		file = new File(coca);
		 doc = PDDocument.load(file);
		String contitext = stripper.getText(doc);
		System.out.println(contitext);
		doc.close();
		
		file = new File(coca);
		 doc = PDDocument.load(file);
		String basftext = stripper.getText(doc);
		System.out.println(basftext);
		doc.close();
		
		
		
		tools.print(tools.stringSimilarity(cocatext, bayertext));
		tools.print(tools.stringSimilarity(basftext, bayertext));
		tools.print(tools.stringSimilarity(cocatext, contitext));
		tools.print(tools.stringSimilarity(basftext, contitext));
		
	}
	
	public void filterStopWords(String text) {
		
		
	}
}
