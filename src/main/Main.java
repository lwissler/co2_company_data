package main;

import parser.CDPParser;
import parser.GHG;
import auxiliary.Settings;
import opennlp.tools.parser.Parse;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		Settings.token = args[0];
		CDPParser cdpparser = new CDPParser();
//		GHG ghg = new GHG();
		
		try {
			cdpparser.parsePDF();			
			
			
			
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
