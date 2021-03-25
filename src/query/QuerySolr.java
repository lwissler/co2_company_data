package query;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import auxiliary.Tools;
import parser.CDPParser;
import parser.GRI;
import parser.Parser;

public class QuerySolr {
	
	CDPParser cdpparser = new CDPParser();
	Parser[] parsers;
	Tools tools = new Tools();
	HashMap<String, Boolean> urlMap = new HashMap<String, Boolean>();
	
	public QuerySolr(Parser[] parsers) {
		this.parsers = parsers;
	}
	
	public void queryGRIIndex() throws SolrServerException, IOException {
		  long numFound = 999999;
//	    HttpSolrServer solr = new HttpSolrServer("url");
		  HttpSolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/cdp").build();

		  SolrQuery query = new SolrQuery();
		  
	        query.setQuery("\"GRI Index\"");
//	        query.addFilterQuery("cat:electronics","store:amazon.com");
	        query.setFields("id","title","content");
	        for(int j = 0; j < numFound; j=j+10) {
	        	
	        	query.setStart(j);
//	 	        query.set("defType", "edismax");

	 	        QueryResponse response = client.query(query);
	 	        SolrDocumentList results = response.getResults();
	 	        numFound = results.getNumFound();
	 	        for (int i = 0; i < results.size(); i++) {
//	 	            System.out.println(results.get(i));
	 	        	String text = (String)results.get(i).get("content");
	 	        	String url = (String)results.get(i).get("id");
	 	        	GRI gri = new GRI();
	 	        	try {
						gri.indexParser(text,url);
					} catch (Exception e) {
						e.printStackTrace();
					}
//	 	        	gri.indexParser("https://www.roche.com/investors/non-financial-reporting/gri-index.htm");
//	 	        	if(!urlMap.containsKey((String)results.get(i).get("id"))) {
//	 	        		urlMap.put((String)results.get(i).get("id"), cdpparser.processText(text,(String)results.get(i).get("id")));
//	 	        	}
//	 	        	try {
//						List<String> urls = tools.getUrls(text);
//						for(String url : urls) {
//							url = url.replace(")", "");
//							if(!url.contains("www.cdp.net")&&!url.contains("guidance.cdp.net")&&!url.endsWith("-")&&!urlMap.containsKey(url)) {
//								tools.print("Also trying "+url);
//								urlMap.put(url, cdpparser.processURL(url));
//							}
//						}
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
	 	        }	
	 	       tools.print("Processed docs "+j+" to "+(j+10)+" of "+ numFound);
	        }
	  }

	
	  public void queryCDP() throws SolrServerException, IOException {
		  long numFound = 999999;
//	    HttpSolrServer solr = new HttpSolrServer("url");
		  HttpSolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/cdp").build();

		  SolrQuery query = new SolrQuery();
		  
	        query.setQuery("cdp");
//	        query.addFilterQuery("cat:electronics","store:amazon.com");
	        query.setFields("id","title","content");
	        for(int j = 0; j < numFound; j=j+10) {
	        	
	        	 query.setStart(j);
//	 	        query.set("defType", "edismax");

	 	        QueryResponse response = client.query(query);
	 	        SolrDocumentList results = response.getResults();
	 	        numFound = results.getNumFound();
	 	        for (int i = 0; i < results.size(); i++) {
//	 	            System.out.println(results.get(i));
	 	        	String text = (String)results.get(i).get("content");
	 	        	if(!urlMap.containsKey((String)results.get(i).get("id"))) {
	 	        		urlMap.put((String)results.get(i).get("id"), cdpparser.processText(text,(String)results.get(i).get("id")));
	 	        	}
	 	        	try {
						List<String> urls = tools.getUrls(text);
						for(String url : urls) {
							url = url.replace(")", "");
							if(!url.contains("www.cdp.net")&&!url.contains("guidance.cdp.net")&&!url.endsWith("-")&&!urlMap.containsKey(url)) {
								tools.print("Also trying "+url);
								urlMap.put(url, cdpparser.processURL(url));
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	 	        }	
	 	       tools.print("Processed docs "+j+" to "+(j+10)+" of "+ numFound);
	        }
	  }
	  
	  public void query(String queryStr) throws Exception {
		  long numFound = 999999;
//	    HttpSolrServer solr = new HttpSolrServer("url");
		  HttpSolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/cdp").build();

		  SolrQuery query = new SolrQuery();
		  
	        query.setQuery(queryStr);
//	        query.addFilterQuery("cat:electronics","store:amazon.com");
	        query.setFields("id","title","content","outlinks");
	        for(int j = 1; j < numFound; j=j+10) {
	        	
	        	 query.setStart(j);
//	 	        query.set("defType", "edismax");

	 	        QueryResponse response = client.query(query);
	 	        SolrDocumentList results = response.getResults();
	 	        numFound = results.getNumFound();
	 	        for (int i = 0; i < results.size(); i++) {
//	 	            System.out.println(results.get(i));
	 	        	SolrDocument result = results.get(i);
	 	        	String text = (String)result.get("content");
	 	        	String url = (String)result.get("id");
	 	        	String fullText;
					try {
						fullText = tools.apiCall(url).parse().toString();
					} catch (IOException e1) {
						fullText = ""; //cannot read website. Trying with only stored data
					}
					if(!urlMap.containsKey(url)) {
//						urlMap.put(url,  cdpparser.processText(text,url);
						processURL(url,fullText);
	 	        	}else if(!urlMap.get(url)) {
//						urlMap.put(url,  cdpparser.processText(text,url);
						processURL(url,fullText);
	 	        	}
	 	        	try {
						List<String> urls = tools.getUrls(fullText);
						for(String url2 : urls) {
							url2 = url2.replace(")", "");
							if(!url2.contains("www.cdp.net")&&!url2.contains("guidance.cdp.net")&&!url2.endsWith("-")&&!urlMap.containsKey(url)) {
								tools.print("Also trying "+url2);
								try {
									String fullText2 = tools.apiCall(url2).parse().toString();
									processURL(url2, fullText2);
								}catch(Exception e) {
									//link cannot be reached
								}
//								cdpparser.processURL(url2);
//								tools.print("Processing "+url2);
//								for(Parser parser : parsers) {
//									parser.processURL(url2, "");
//								}
//								urlMap.put(url2, 1);
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	 	        }	
	 	       tools.print("Processed docs "+j+" to "+(j+10)+" of "+ numFound);
	        }
	  }
	  
	  private void processURL(String url, String text) throws Exception {
		  tools.print("Processing "+url);
		  if(!urlMap.containsKey(url)) {
			 urlMap.put(url, cdpparser.processText(text,url));
		  }
		  if(!urlMap.get(url)) {
				urlMap.put(url, cdpparser.processText(text,url));
				if(!urlMap.get(url)) {
					urlMap.put(url, cdpparser.processURL(url));
					if(!urlMap.get(url)) {
						for(Parser parser : parsers) {
							try {
								urlMap.put(url,parser.processURL(url, ""));
							} catch (Exception e) {
								e.printStackTrace();
							}
				   		}
						if(!urlMap.get(url)) {
							GRI griParser = new GRI();
							try {
								griParser.indexParser(text,url);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}	
					}	
				}
			}
	}

	public void queryAll() {
		  HashMap<String, String> keywords = new HashMap<String, String>();
		  for(Parser parser : parsers) {
			  keywords.putAll(parser.keywords);
		  }
		  
		 try {
			query("sustainability report");
			query("non-financial report");
			query("cdp");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		  Iterator<Entry<String, String>> iter = keywords.entrySet().iterator();
		  while(iter.hasNext()) {
			  try {
				query(iter.next().getKey());
			} catch (Exception e) {
				e.printStackTrace();
			}
		  }
	  }
}
