package main;

import parser.CDPParser;
import parser.GHG;
import parser.GRI;
import parser.Parser;
import query.QuerySolr;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

import auxiliary.Settings;
import opennlp.tools.parser.Parse;
import org.apache.log4j.*;

import com.github.peterbencze.serritor.api.CrawlRequest;
import com.github.peterbencze.serritor.api.CrawlerConfiguration;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PrintStream out;
		Date now = new Date();
		String nowStr = now.toLocaleString().replace(":", "-").replace(".", "_");
		try {
			out = new PrintStream(new FileOutputStream("./log/parse"+nowStr+".txt"));
			System.setOut(out);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
//		Settings.token = args[0];
		CDPParser cdpparser = new CDPParser();
		Parser[] parsers = {new GHG(), new GRI()};
		QuerySolr query = new QuerySolr(parsers);
//		GHG ghg = new GHG();
		
		try {
//			CrawlerConfiguration config = new CrawlerConfiguration.CrawlerConfigurationBuilder()
//			        .setOffsiteRequestFilterEnabled(true)
//			        .addAllowedCrawlDomain("example.com")
//			        .addCrawlSeed(CrawlRequest.createDefault("https://global.epson.com/SR/report/archive.html"))
//			        .build();
//
//			MyCrawler crawler = new MyCrawler(config);
//
//			try {
//				crawler.start();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			
//			query.queryAll();
//			query.query("\"GRI Index\"");
//			query.queryGRIIndex();
			
			
			cdpparser.processFile("2020-08-21_Empfehlungsübersicht_Aktien.pdf");
//			cdpparser.processURL("https://www.fcx.com/sites/fcx/files/documents/sustainability/2017_cdp_climate_change_info_req.pdf");			
			
			
			
//			ghg.processURL("https://www.basf.com/global/en/who-we-are/sustainability/we-produce-safely-and-efficiently/energy-and-climate-protection/corporate-carbon-footprint.html","BASF");
//			ghg.processURL("https://www.ergo.com/de/Verantwortung/Reporting-und-Kennzahlen/Umweltkennzahlen","Ergo Group");
//			ghg.processURL("https://www.verizon.com/about/sites/default/files/corporate-responsibility-report/2018/environment/emissions.html", "Verizon");
//			ghg.processURL("https://reports.swissre.com/corporate-responsibility-report/2017/cr-report/footprint/our-greenhouse-neutral-programme/co2-emissions-and-underlying-environmental-data.html", "SwissRe");
//			ghg.processURL("https://www.prudentialplc.com/about-us/esg/performance/greenhouse-gas-emissions", "Prudential");
//			ghg.processURL("https://www.basf.com/global/en/who-we-are/sustainability/we-produce-safely-and-efficiently/energy-and-climate-protection/corporate-carbon-footprint.html","BASF");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
