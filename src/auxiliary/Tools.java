package auxiliary;

import java.io.IOException;
import java.util.Date;

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
}
