package auxiliary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPURLConnectionImpl {
	
	String token;
	
	public HTTPURLConnectionImpl(String t){
		token = t;
	}
	
	Tools t = new Tools();
	
	private final String USER_AGENT = "Mozilla/5.0";
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public String sendGet(String url) throws Exception {
//		t.print("Sending GET request to URL : " + url);
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

	
		con.setRequestMethod("GET");

		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty ("x-auth-token", token);

		
		int responseCode = con.getResponseCode();
//		System.out.print("Response : " + responseCode +" "+con.getResponseMessage());

		BufferedReader in;
		if (con.getResponseCode() <= 399) {
		    in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		} else {
		    in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		}
		 
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		if(con.getResponseCode()>=400){
			t.print(response.toString());
		}

		return response.toString();

	}
	
/**
 * 
 * @param url
 * @return
 * @throws Exception
 */
	public String sendPost(String url, String content) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty ("x-auth-token", token);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Accept-Charset", "UTF-8");

//		t.print("Sending POST request to URL : " + url);
		t.print("Post parameters : " + content);
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
		writer.write(content);
		writer.flush();
		writer.close();
		wr.close();

		int responseCode = con.getResponseCode();
//		System.out.print("Response Code : " + responseCode);
//		t.print(" : " + con.getResponseMessage());
//		t.print("---------------");
//		System.out.println();

		BufferedReader in;
		if (con.getResponseCode() <= 399) {
		    in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		} else {
		    in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		}
		 
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		if(con.getResponseCode()>=400){
			t.print(response.toString());
		}
		

		return response.toString();
	}
	
	/**
	 * 
	 * @param url
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public String sendPut(String url, String content) throws Exception {
		t.print("Sending PUT to "+url);
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("PUT");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty ("x-auth-token", token);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Accept-Charset", "UTF-8");
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
		writer.write(content);
		writer.flush();
		writer.close();
		wr.close();


		int responseCode = con.getResponseCode();
		System.out.print("Response Code : " + responseCode +" " +con.getResponseMessage());
//		t.print(" : " + );
//		t.print(" : " + con.getContent().toString());
		
		BufferedReader in;
		if (con.getResponseCode() <= 399) {
		    in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		} else {
		    in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		}
		 
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		if(con.getResponseCode()>=400){
			t.print(response.toString());
		}
		

		return response.toString();
	}
}