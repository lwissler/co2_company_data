package auxiliary;

import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.SimilarityScoreFrom;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.peterbencze.serritor.api.CrawlerState;


public class Tools {
	
	public void saveState(CrawlerState state) throws Exception {
		 FileOutputStream fos = new FileOutputStream("state");
	        ObjectOutputStream oos = new ObjectOutputStream(fos);
	        oos.writeObject(state);
	        oos.close();
	}
	
	public CrawlerState loadSate() throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream("state");
	     ObjectInputStream ois = new ObjectInputStream(fis);
	     CrawlerState state = (CrawlerState) ois.readObject();
	     ois.close();
	     return state;
	}
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
		if(resp.equals("Not found")) {
			return null;
		}
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
		print("Target: "+call);
		
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
//		 	Document doc = SSLHelper.getConnection(url).userAgent(USER_AGENT).get();
		 	org.jsoup.Connection con = SSLHelper.getConnection(url).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21").ignoreContentType(true);
//		 	org.jsoup.Connection con = Jsoup.connect(url).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21").ignoreContentType(true);
			con.timeout(300000).ignoreHttpErrors(true).followRedirects(true);
			org.jsoup.Connection.Response resp = con.execute();
			
			return resp;
	 }
	 
	 public ByteArrayInputStream apiCallHTTP(String path) {
		 InputStream input = null;
		    OutputStream output = null;
		    HttpURLConnection connection = null;
		    try {
		        URL url = new URL(path);
//		    	Connection con = SSLHelper.getConnection(path);
//		    	con.execute()
		    	
		        connection = (HttpURLConnection) url.openConnection();
		        connection.connect();

		        // expect HTTP 200 OK, so we don't mistakenly save error report
		        // instead of the file
//		        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
//		            return "Server returned HTTP " + connection.getResponseCode()
//		                    + " " + connection.getResponseMessage();
//		        }

		        // this will be useful to display download percentage
		        // might be -1: server did not report the length
		        int fileLength = connection.getContentLength();

		        // download the file
		        input = connection.getInputStream();
		        
		        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                int c;
                while ((c = connection.getInputStream().read()) != -1) {
                    byteArrayOutputStream.write(c);
                }
                
	                input.close();
		            connection.disconnect();

                return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
//		        output = new FileOutputStream("/sdcard/file_name.extension");

//		        byte data[] = new byte[4096];
//		        int count;
//		        while ((count = input.read(data)) != -1) {
//		            output.write(data, 0, count);
//		        }
		    } catch (Exception e) {
		    	print(e.toString());
		    	return null;
		    }
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
//		print("Calculating String Similarity");
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
	
	public String formatAllDates(String input) throws ParseException {
		try {
			return formatCDPDate(input);
		} catch (ParseException e) {
			try {
				if(input.contains("/")) {
					int day = Integer.parseInt(input.split("/")[0]);
					if(day>12)
						return formatCDPDate4(input);
					else
						return formatCDPDate2(input);
						
				}
				return formatCDPDate2(input);
			} catch (ParseException e1) {
				try {
					return formatCDPDate3(input);
				} catch (ParseException e2) {
					try {
						return formatCDPDate5(input);
					} catch (ParseException e3) {
						try {
							return formatCDPDate6(input);
						} catch (ParseException e4) {
							return formatCDPDate7(input);
						}
					}
				}
			}
		}
	}
	
	public String formatCDPDate(String input) throws ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat("MMMM dd yyyy", Locale.US);
		Date d = fmt.parse(input);
		return format(d);
	}
	
	public String formatCDPDate3(String input) throws ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
		Date d = fmt.parse(input);
		return format(d);
	}

	public String formatCDPDate2(String input) throws ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		Date d = fmt.parse(input);
		return format(d);
	}

	public String formatCDPDate4(String input) throws ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
		Date d = fmt.parse(input);
		return format(d);
	}
	
	public String formatCDPDate5(String input) throws ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
		Date d = fmt.parse(input);
		return format(d);
	}
	
	private String format(Date d) {
		SimpleDateFormat ourFormat = new SimpleDateFormat ("yyyy-MM-dd");
		return ourFormat.format(d);
	}
	
	public String formatCDPDate6(String input) throws ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat("MMMM dd yyyy", Locale.FRANCE);
		Date d = fmt.parse(input);
		return format(d);
	}
	
	public String formatCDPDate7(String input) throws ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat("EEE dd MMM yyyy", Locale.US);
		Date d = fmt.parse(input);
		return format(d);
	}


	public Long parseNumber(String number) {
		return Long.parseLong(number.replace(",", "").split(Pattern.quote("."))[0].split(" ")[0]);
	}
	
	public List<String> getUrls(String text)
	{
	    List<String> containedUrls = new ArrayList<String>();
	    String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
	    Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
	    Matcher urlMatcher = pattern.matcher(text);

	    while (urlMatcher.find())
	    {
	        containedUrls.add(text.substring(urlMatcher.start(0), urlMatcher.end(0)));
	    }

	    return containedUrls;
	}
	


	public static int getRandom(int[] array) {
    int rnd = new Random().nextInt(array.length);
    return array[rnd];
}

	public String bytify(String source) {
		 byte[] bytes = source.getBytes();
		  StringBuilder binary = new StringBuilder();
		  for (byte b : bytes)
		  {
		     int val = b;
		     for (int i = 0; i < 8; i++)
		     {
		        binary.append((val & 128) == 0 ? 0 : 1);
		        val <<= 1;
		     }
//		     binary.append(' ');
		  }
		  return binary.toString();
	}

	public String[] cleanSplit(String[] s) {
		int l = 0;
		for(int i = 0; i < s.length; i++) {
			if(s[i]==null) {
				
			}else if(!s[i].equals("")&&!s[i].equals(" "))
				l++;
		}
		
		String[] clean = new String[l];
		
		l=0;
		for(int i = 0; i < s.length; i++) {
			if(s[i]==null) {
				
			}else if(!s[i].equals("")&&!s[i].equals(" ")) {
				clean[l] = s[i];
				l++;
			}
		}
		
		return clean;
	}

	public String replaceSpecialChars(String s) {
		return s.replace("İ", "I").replace("ş", "s").replace("é", "e").replace("´s", "").replace("ü", "ue").replace("ö", "oe").replace("ä", "ae");
	}
	
	public String readTextUrl(String urlPath) throws IOException {
		URL url = new URL(urlPath);
		InputStream input = apiCallHTTP(urlPath);
		String text = "";
		PDDocument doc = null;
		 
		try {
			doc = PDDocument.load(input);
			OverriddenPDFTextStripper stripper = new OverriddenPDFTextStripper();
			text = stripper.getText(doc);
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
			doc.close();
			e.printStackTrace();
			StringBuilder sb = new StringBuilder();
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			 String line;
			while ((line = in.readLine()) != null) {
                sb.append(line).append("\n");
            }
			text = sb.toString();
		} finally {
			if(doc!=null)
				doc.close();
		}
		
		if(doc!=null) {
			doc.close();
		}
		
		NLPAux nlp = new NLPAux();
		return text;//.replaceAll("<sup>"+nlp.anything+"</sup>", "");
	}

	public String readTextFile(String path) throws IOException {
		File file = new File(path);
		PDDocument doc = PDDocument.load(file);
		PDFTextStripper stripper = new PDFTextStripper();
		String text = stripper.getText(doc);
		doc.close();
		return text;
	}
	
	
	public String getTextByRegionUrl(String urlPath, Boolean header, Boolean footer) throws IOException {
		String text = "";
		File file = new File(urlPath);
		URL url = new URL(urlPath);
		InputStream input = apiCall(urlPath).bodyStream();
		PDDocument document = PDDocument.load(input);
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

	public String cleanText(String text, Boolean signifyKeywords) {
		text = replaceSpecialChars(text).replaceAll("\\s\\s+", " ")/*.replace("- ", "")*/.replace("-", "").replace("\r", "").replace("\n", "").replace("–", "").replace("– ", "")
				.replace(" )","").replace("( ","").replace("+", "").replace("–", "").replace("&", "")
				.replace("  ", " ").replace("  ", " ").replace("\u2022", "").replaceAll("fy([0-3][0-9])", "20$1").replace("�", ".")
				.trim();
		
		while(text.contains("\\s\\s")) {
			text = text.replaceAll("\\s\\s", " ");
		}
		
		if(signifyKeywords) {
			text = text.replace("scope 1 2 3", "scope123").replace("scope 1 2", "scope12").replace("scope 2 3", "scope23").replace("scope 1", "scope1").replace("scope 2", "scope2").replace("scope 3", "scope3");
		}
		
//		NLPAux nlp = new NLPAux();
//		text = nlp.annotationCleaner(text);
		
		return text;
	}

	
	public JSONArray getYears(String[][] table) {
		JSONArray years = new JSONArray();
		NLPAux nlp = new NLPAux();
		
		for(int i = 1; i < table[0].length; i++) {
			if(table[0][i]!=null) {
				if(table[0][i].matches(nlp.anything+nlp.yearNumber+nlp.anything)) {
					years.add(table[0][i]);
				}
			}
		}
		return years;
	}
	
	public boolean contained(String[] arr, ArrayList<String> lines) {
		for(int i = 0; i < arr.length; i++ ) {
			Boolean match = false;
			for(int j = 0; j < lines.size(); j++) {
				String[] expr = arr[i].split("-or-");
				if(containsOne(lines.get(j),expr)) {
					match = true;
				}
			}
			if(match==false) {
				return false;
			}
		}
		return true;
	}
	
	private boolean containsOne(String s, String[] expr) {
		for(int i = 0; i < expr.length; i++) {
			if(s.contains(expr[i])) {
				return true;
			}
		}
		return false;
	}

	public JSONArray getCompanies(String country) {
		print("Getting companies from Mongo..");
		JSONArray companies = new JSONArray();
		try {
			JSONArray comps;
			if(country!=null)
				comps = getJSONArrayFromMongo(Paths.COMPANY_CALL+country);
			else
				comps = getJSONArrayFromMongo(Paths.COMPANY_CALL);
			for(int i = 0; i < comps.size(); i++) {
				JSONObject c = (JSONObject) comps.get(i);
				JSONObject general = (JSONObject) c.get("General");
				JSONObject comp = new JSONObject();
				
//				String name = filterStopWords((String)general.get("Name"), "en");
				String name = ((String)general.get("Name")).trim();
				if(name!=null) {
					String cleanedName = cleanText(filterStopWords(name.toLowerCase().replace("company", "").replace("compagnie de", "").replace("aktiengesellschaft", "")
							.replace("technology", "").replace("energy", "").replace("compagnie generale des etablissements ", "").replace("the ", "")
							.replace("royal", "").replace("dutch", "").replace("&", "").replace("group", "").replace("incorporated", "").replace("\\bfinancial\\b", "")
							.replace("services", "").replace("industries", "").replace("GROUP", "").replace("AG", "").replace("technologies", "").replace("borse", "boerse")
							.replace("industries", "").replace("international", "").replace("invesment", "").replace("&", "").replace("kgaa", "").replace("ability", "")
							.replace("llc", "").replace(",", "").replace("Anheuser-Busch", "").replace("inc", "").replace("Oyj", "").replace("NV", "")
							.replace("SE & Co. KGaA", "").replaceAll("\\bag\\b", "").replaceAll("\\bco.\\b", "").replace("co.", "")
							.trim(),"en"),false);
					if(!cleanedName.equals("")&&!cleanedName.equals("the")&&!cleanedName.equals("change")&&!cleanedName.equals("investor")&&!cleanedName.equals("com")&&!cleanedName.equals("major")
							&&!cleanedName.equals("jp")&&!cleanedName.equals("future")&&!cleanedName.equals("world")&&!cleanedName.equals("development")&&!cleanedName.equals("development")
							&&!cleanedName.equals("stream")&&!cleanedName.equals("metals")&&!cleanedName.equals("creative")&&!cleanedName.equals("nasdaq")&&!cleanedName.equals("financial institutions")
							&&!cleanedName.equals("royal")&&!cleanedName.equals("global")&&!cleanedName.equals("technologies")&&!cleanedName.equals("century")
							&&!cleanedName.equals("group")&&!cleanedName.equals("ca")&&!cleanedName.equals("investment")&&!cleanedName.equals("change")
							&&!cleanedName.equals("data")&&!cleanedName.equals("products")&&!cleanedName.equals("information")&&!cleanedName.equals("public")
							&&!cleanedName.equals("apc")&&!cleanedName.equals("br")&&!cleanedName.equals("fortune")&&!cleanedName.equals("healthcare") &&!cleanedName.equals("integrated")
							&&!cleanedName.equals("fortune")&&!cleanedName.equals("science")&&!cleanedName.equals("enterprise")&&!cleanedName.equals("natural gas")
							&&!cleanedName.equals("international")) {
						comp.put("Company_id", c.get("Company_id"));
						comp.put("Name", name);
						comp.put("CleanName", cleanedName);
						companies.add(comp);
					}
				}
			}
			print("Loaded...");
			return companies;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	

	public String filterStopWords(String text, String language) {
		String[] stopwords = {};
		try {
			stopwords = loadStopWords(language);
		} catch (IOException e) {
			e.printStackTrace();
		} 
//		String stopWordsPattern = String.join("|", stopwords);
//		Pattern pattern = Pattern.compile("\\b(?:" + stopWordsPattern + ")\\b\\s*", Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		return matcher.replaceAll("");
		
		for(int i = 0; i < stopwords.length; i++) {
			text=text.replace(" "+stopwords[i]+" ", " ");
		}
		
		return text;
	}
	
	public boolean isContained(String source, String subItem){
		 subItem = Pattern.quote(subItem);
         String pattern = "\\b"+subItem+"\\b";
         Pattern p=Pattern.compile(pattern);
         Matcher m=p.matcher(source);
         return m.find();
    }
	 
	public Double getDoubleFromJSONPath(JSONObject jsonObject, String[] path) {
		Object p = getObjectFromJSONPath(jsonObject, path);

		if (p == null) {
			return null;
		}

		if (p instanceof String) {
			return Double.parseDouble((String) p);
		} else if (p instanceof Double) {
			return (Double) getObjectFromJSONPath(jsonObject, path);
		} else {
			return ((Long) getObjectFromJSONPath(jsonObject, path)).doubleValue();
		}
	}
	 
	 public String getStringFromJSONPath(JSONObject jsonObject, String[] path){
			return (String)getObjectFromJSONPath(jsonObject, path);
	}
	 
	 public Object getObjectFromJSONPath(JSONObject j, String[] path){
			if(j==null) {
				return null;
			}else {
				if(path.length > 1){
					JSONObject o = (JSONObject)j.get(path[0]);
					for(int i = 1; i < path.length-1; i++){
						if(o != null){
							o = (JSONObject)o.get(path[i]);
						}else{
							break;
						}
					}
					
					if(o != null){
						return o.get(path[path.length-1]);
					}else{ 
						return null;
					}
				}else{
					return j.get(path[path.length-1]);
				}
			}
		}
	 
		public double round(double d, int decimals){
			double mult = 1;
			
			for(int i = 0; i < decimals; i++){
				mult = mult * 10;
			}
			
			return Math.round(d*mult)/mult;
		}

		
		public int charsInString(String c, String string) {
			Pattern pattern = Pattern.compile(c);
			Matcher matcher = pattern.matcher(string);
			int count = 0;
			while (matcher.find()) {
			    count++;
			}
			
			return count;
		}
		
		public JSONArray sortJSONArray(JSONArray data, String[] path) {
			JSONObject t;
			for(int i=1; i<data.size(); i++) {
				for(int j=0; j<data.size()-i; j++) {
					try {
						if(getDoubleFromJSONPath((JSONObject) data.get(j),path)>getDoubleFromJSONPath((JSONObject) data.get(j+1),path)) {
							t=(JSONObject) data.get(j);
							data.set(j,data.get(j+1));
							data.set(j+1,t);
						}
					} catch (Exception e) {
						e.printStackTrace();
						return new JSONArray();
					}
					
				}
			}
			return data;
		}

		public boolean isContained(JSONArray arr, Object object, String[] path) {
			for(int i = 0; i < arr.size(); i++) {
				if(getObjectFromJSONPath((JSONObject)arr.get(i), path).equals(object)) {
					return true;
				}
			}
			return false;
		}

		public  ArrayList<String[]> readCSV(String path) throws IOException {
			BufferedReader br = new BufferedReader(new FileReader(path));
			
			ArrayList<String[]> data = new ArrayList<String[]>();
			
			String line = br.readLine();
			while (line != null) {
				if(line != null&&!line.contains("null")){
					String[] splitLine = line.split(",");
					for(String cell : splitLine) {
						cell.replace("%", "");
					}

					data.add(splitLine);
				}
				line = br.readLine();	
			}
			
			br.close();
			return data;
		}
		
		public  ArrayList<String[]> readCSV(String path, String delimiter) throws IOException {
			BufferedReader br = new BufferedReader(new FileReader(path));
			
			ArrayList<String[]> data = new ArrayList<String[]>();
			
			String line = br.readLine();
			while (line != null) {
				if(line != null&&!line.contains("null")){
					String[] splitLine = line.split(delimiter);
					for(String cell : splitLine) {
						cell.replace("%", "");
					}

					data.add(splitLine);
				}
				line = br.readLine();	
			}
			
			br.close();
			return data;
		}
}
