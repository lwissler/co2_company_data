package query;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

public class QuerySolr {
	

	
	  public void query() throws SolrServerException, IOException {
//	    HttpSolrServer solr = new HttpSolrServer("url");
		  HttpSolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/cdp").build();

		  SolrQuery query = new SolrQuery();
		  
	        query.setQuery("cdp");
//	        query.addFilterQuery("cat:electronics","store:amazon.com");
	        query.setFields("id","title","content");
	        query.setStart(0);
//	        query.set("defType", "edismax");

	        QueryResponse response = client.query(query);
	        SolrDocumentList results = response.getResults();
	        for (int i = 0; i < results.size(); i++) {
	            System.out.println(results.get(i));
	        }
	  }
}
