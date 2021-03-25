package parser;

import java.io.IOException;
import java.util.HashMap;



public class GRI extends Parser{
	public GRI(){
		keywords = new HashMap<String, String>(); 
		keywords.put("Scope 1","Scope 1");
		keywords.put("Direkte Emissionen","Scope 1");
		keywords.put("CO<sub>2</sub> Direkt","Scope 1");
		keywords.put("Scope 2","Scope 2");
		keywords.put("Indirekte Emissionen","Scope 2");
		keywords.put("CO<sub>2</sub> Indirekt","Scope 2");
		
		keywords.put("carbon dioxide","CO2");
		keywords.put("Treibhausgas","CO2");
		keywords.put("Carbondioxid","CO2");
		keywords.put("CO<sub>2</sub>","CO2");
		
		keywords.put("nitrous oxidee","N2O");
		keywords.put("N<sub>2</sub>O","N2O");
		
		keywords.put("stickoxide", "NO2");
		
		keywords.put("schwefeldioxid", "SO2");
		
		keywords.put("kohlenmonoxid", "CO");
		
		keywords.put("l�semittel", "VOC");
		
		keywords.put("staub", "staub");
		
		keywords.put("methane","CH4");
		keywords.put("CH<sub>4</sub>","CH4");
		keywords.put("HFC","HFC");
		keywords.put("hydrofluorocarbons","HFC");
		
		keywords.put("energiesatz","");
		keywords.put("GRI","");
		keywords.put("emission","");
		keywords.put("<sub>2</sub>","");
	}
	
	public void indexParser(String text, String url) throws IOException {
//		String text = tools.apiCall(url).parse().toString();
		String scope1 = text.split("305-1")[1].split("305-2")[0];
	}
	
}
