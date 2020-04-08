package auxiliary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Tools {
	/**
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public JSONArray getJSONArrayFromMongo (String call) throws Exception{
		String url = Paths.API_ADDRESS+call;
		JSONParser jsonParser = new JSONParser();
		HTTPURLConnectionImpl h = new HTTPURLConnectionImpl(Settings.token);
		String resp = h.sendGet(url);
//		print(resp);
		JSONArray jsonObject = (JSONArray)jsonParser.parse(resp);
		return jsonObject;
	}
	
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public JSONObject getJSONFromMongo (String call) throws Exception{
		String url = Paths.API_ADDRESS+call;
		JSONParser jsonParser = new JSONParser();
		HTTPURLConnectionImpl h = new HTTPURLConnectionImpl(Settings.token);
		String resp = h.sendGet(url);
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(resp);
			return jsonObject;
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONObject();
		}
	}
	
	/**
	 * 
	 * @param data
	 * @param call
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void postToMongo(JSONObject data, String call) throws Exception{
		HTTPURLConnectionImpl h = new HTTPURLConnectionImpl(Settings.token);
		String url = Paths.API_ADDRESS+call;
		
		JSONObject json = new JSONObject();
		json.put("data", data);
		System.out.println("Sending: "+ json.toString());
		
		h.sendPost(url, json.toJSONString());
	}
	
	/**
	 * 
	 * @param data
	 * @param call
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void putToMongo(JSONObject data, String call, String id) throws Exception{
		HTTPURLConnectionImpl h = new HTTPURLConnectionImpl(Settings.token);
		String url = Paths.API_ADDRESS+call+"/"+id;
		
		JSONObject json = new JSONObject();
		json.put("data", data);
		System.out.println("Sending: "+ json.toString());
		
		h.sendPut(url, json.toJSONString());
	}
	
	 /**
	  * 
	  * @param url
	  * @return
	  * @throws IOException
	  */
	 
	 public Response apiCall(String url) throws IOException{
		 org.jsoup.Connection con = Jsoup.connect(url).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21").ignoreContentType(true);
			con.timeout(300000).ignoreHttpErrors(true).followRedirects(true);
			org.jsoup.Connection.Response resp = con.execute();
			
			return resp;
	 }
	
	/**
	 * 
	 * @param arg
	 */
	public void print(Object arg) {
		System.out.println(new Date() + ": " + arg.toString());
	}
	
	/*
	 * 
	 */
	public double stringSimilarity(String s1, String s2) {
		print("Calculating String Similarity");
		if(s1!=null&&s2!=null){
			String longer = s1, shorter = s2;
		    if (s1.length() < s2.length()) {
		      longer = s2; shorter = s1;
		    }
		    int longerLength = longer.length();
		    if (longerLength == 0) { return 1.0;}
		    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
		    return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength;
		}
		
	    return 0;
	  }

/*
 * 
 */
	public String[] loadFileToArray(String path) throws IOException {
		FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines.toArray(new String[lines.size()]);
	}

/*
 * 
 */
	public String[] loadStopWords(String language) throws IOException {
		String path = null;
		if(language.equals("de")) {
			path = "stopwords_de";
		}else if(language.equals("en")) {
			path = "stopwords_en";
		}else {
			return null;
		}
		return loadFileToArray(path);
	}
	
	/*
	 * 
	 */
	public void printArgs(String s, Object[] o, Boolean csv){
		if(!csv){
			System.out.print(s + ": ");
			for(Object k:o){
				System.out.print(k + ", ");
			}
			System.out.println();
		}else{
			System.out.print(s.replace(" ", "") + ", ");
			for(Object k:o){
				System.out.print(k.toString().replace(" ", "") + ", ");
			}
			System.out.println();
		}
	}
	
	/*
	 * 
	 */
	
	public String stringArrToString(String[] stringArray) {
	      StringBuffer sb = new StringBuffer();
	      for(int i = 0; i < stringArray.length; i++) {
	         sb.append(stringArray[i]+" ");
	      }
	      return sb.toString();
	}
}
