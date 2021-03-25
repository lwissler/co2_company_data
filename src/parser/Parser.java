package parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.simple.JSONObject;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import auxiliary.Paths;
import auxiliary.Tools;

public class Parser {
	public HashMap<String, String> keywords;
	Tools tools = new Tools();
	
	public Boolean processURL(String url, String companyName) throws Exception{
//		tools.print("Processing "+companyName+" ("+url+")");
		
		try {
			Response resp = tools.apiCall(url);
			
			if(validate(resp)){
				tools.print("Valid data, initiating parsing...");
				JSONObject data = parse(resp.parse(),url);
				data.put("Company", companyName);
				tools.print(data);
//			tools.postToMongo(data, Paths.API_ADDRESS+"/"+companyName);
				return true;
			}else{
//			tools.print("No carbon data found");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private JSONObject parse(Document doc, String url) {
		tools.print("Parsing URL "+url);
		
		//build JSON structure
		JSONObject res = new JSONObject();
		String currentContext = "";
		HashMap<String, JSONObject> context = new HashMap<String, JSONObject>();
		
		context.put("Scope 1", new JSONObject());
		context.put("Scope 2", new JSONObject());
		context.put("Scope 3", new JSONObject());
		
		JSONObject scope = context.get("Scope 1");

		Iterator<Entry<String, JSONObject>> cIter = context.entrySet().iterator();
		while(cIter.hasNext()){
			Entry<String, JSONObject> e = cIter.next();
			res.put(e.getKey(), e.getValue());
		}
		
		//find year reporting is for
		String[] split = doc.toString().split(" ");
		String date = "";
		for(int i = 0; i < split.length-1; i++){
			if(split[i].startsWith("20")&&split[i].length()==4){
				date = split[i];
				break;
			}
		}
		
		res.put("date", date);
		
		//get tables to parse
		Elements tables = doc.getElementsByTag("table");
		
		//iterate tables
		for(int i = 0; i < tables.size()-1; i++){
			Elements rows = tables.get(i).getElementsByTag("tr");
			
			//iterate rows
			for(int j = 0; j < rows.size()-1; j++){
				Elements fields = rows.get(j).getElementsByTag("td");
				
				//TODO match date with table head and find datarow
				int datarow = fields.size()-1;
				//parse rows by finding the appropiate data key and store corresponding value for
				//relevant context (scope 1/2/3)
				if(fields.size()>1){
					try {
						String infoType = fields.get(0).toString();
//						tools.print(infoType);
						Iterator<Entry<String, String>> iter = keywords.entrySet().iterator();
						while(iter.hasNext()){
							String key = iter.next().getKey();
							if(infoType.contains(key)){
								if(keywords.get(key)!=""){
									if(context.containsKey(keywords.get(key))){
										scope = context.get(keywords.get(key));
									}
									double data = Double.parseDouble(fields.get(datarow).ownText().replace(",", "").replace(".", ""));
									if(!scope.containsKey(keywords.get(key)))
											scope.put(keywords.get(key), data);
									break;
								}
							}
						}
					} catch (NumberFormatException e) {
						tools.print("Invalid Data, Trying again...");
						try {
							String infoType = fields.get(0).toString();
//							tools.print(infoType);
							Iterator<Entry<String, String>> iter = keywords.entrySet().iterator();
							while(iter.hasNext()){
								String key = iter.next().getKey();
								if(infoType.contains(key)){
									if(keywords.get(key)!=""){
										if(context.containsKey(keywords.get(key))){
											scope = context.get(keywords.get(key));
										}
										double data = Double.parseDouble(fields.get(datarow-1).ownText().replace(",", "").replace(".", ""));
										if(!scope.containsKey(keywords.get(key)))
												scope.put(keywords.get(key), data);
										break;
									}
								}
							}
						} catch (NumberFormatException e1) {
							tools.print("Invalid Data");
						}
					}
				}
			}
		}
		
		return res;
	}

	private boolean validate(Response resp) {
//		tools.print("Validating URL");
		String body = resp.body().toLowerCase();
//		tools.print(body);
		double count = 0;
		
		Iterator<Entry<String, String>> iter = keywords.entrySet().iterator();
		while(iter.hasNext()){
			String key = iter.next().getKey().toLowerCase();
			if(body.contains(key)){
				count++;
			}
		}
			
		if(count/keywords.size()>0.2)
			return true;
		else
			return false;
	}
}
