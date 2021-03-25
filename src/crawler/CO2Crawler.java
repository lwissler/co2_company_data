package crawler;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.openqa.selenium.WebDriver;

import com.github.peterbencze.serritor.api.CompleteCrawlResponse;
import com.github.peterbencze.serritor.api.CrawlRequest;
import com.github.peterbencze.serritor.api.Crawler;
import com.github.peterbencze.serritor.api.CrawlerConfiguration;
import com.github.peterbencze.serritor.api.event.ResponseSuccessEvent;
import com.github.peterbencze.serritor.api.helper.UrlFinder;

import auxiliary.Tools;
import parser.CDPParser;
import parser.GHG;
import parser.GRI;
import parser.Parser;

	public class CO2Crawler extends Crawler {

    private final UrlFinder urlFinder;
    
    Tools tools = new Tools();
    int lastStored = 0;
    
//    Parser parser;
    CDPParser cdpparser;
    int depth = 0;
//    Boolean parse = true;
    
    HashMap<String, Boolean> excludedURLs = new HashMap<String, Boolean>(); 
    
    public CO2Crawler(final CrawlerConfiguration config, CDPParser parser, int depth) {
        super(config);
        urlFinder = UrlFinder.createDefault();
        this.cdpparser = parser;
        this.depth = depth;
        
        
        excludedURLs.put("https://www.thehartford.com/sites/the_hartford/files/cdp-project-submission.pdf", true);
        excludedURLs.put("https://www.anytimefitness.com/training/", true);
//        this.parse = parse;
        
//        Parser[] parsers = {new GHG(), new GRI()};
//        parser = new Parser(parsers);
    }

    @Override
    protected void onResponseSuccess(final ResponseSuccessEvent event) {
    	CompleteCrawlResponse res = event.getCompleteCrawlResponse();
    	WebDriver webdriver = res.getWebDriver();
    	String source = webdriver.getPageSource();
    	String currenturl = webdriver.getCurrentUrl();
    	 tools.print("Called: "+currenturl);
    	 
         List<String> urls = urlFinder.findAllInResponse(res);
         for(String url : urls) {
//        	 tools.print(depth+" "+url);
//        	 tools.print(cdpparser.urls.get(url)+" "+(depth>0)+" ("+url.contains("cdp")+" "+url.contains("climate")+" "+url.contains("response")+" "+url.contains("questionnaire")+")");
//        	 tools.print(depth > 0 && (currenturl.contains("cdp")||currenturl.contains("climate")||currenturl.contains("response")||currenturl.contains("questionnaire")));
        	 if(depth > 0 && !cdpparser.urls.containsKey(url) && (url.contains("cdp")||url.contains("climate")||url.contains("response")||url.contains("questionnaire"))) {
//        		 tools.print("Found potential additional document "+url);
//        		 tools.print(cdpparser.urls.get(url)+" ("+url.contains("cdp")+" "+url.contains("climate")+" "+url.contains("response")+" "+url.contains("questionnaire")+")");
        		 try {
     				cdpparser.parsePDF(url);
     			} catch (Exception e) {
     				e.printStackTrace();
     			}
        	 }
        	
//        	 tools.print("Found url "+url);
        	 if(!cdpparser.urls.containsKey(url)&&!url.contains("google.com")&&!url.contains("google.de")&&!url.contains("googleusercontent.com")&&!url.contains("www.cdp.net")) {
//        		 tools.print("Processing url "+url);
        		 try {
        			 
        				 
//					Boolean parsed = cdpparser.parsePDF(url);
//					if(true) {
					 if(depth<1/*&&*/) {
						 tools.print("parsing toplevel");
						 if(url.equals("https://www.foley.com/CDP-Announces-Climate-Change-Reporting-Changes-for-2015-03-05-2015?nomobile=perm"))
							 tools.print("");
						 try {
							cdpparser.parsePDF(url);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						 if(!excludedURLs.containsKey(url)) {
//							 tools.print("Also crawling:"+url);
							 CrawlerConfiguration config = new CrawlerConfiguration.CrawlerConfigurationBuilder()
			          			        .addCrawlSeed(CrawlRequest.createDefault(url))
			          			        .build();

			                 	 CO2Crawler crawler = new CO2Crawler(config,cdpparser,depth+1);

			                 	 
			          			try {
			          				crawler.start();
			          			} catch (Exception e) {
			          				e.printStackTrace();
			          			}

						 }
	            		 	            	 }
				} catch (Exception e) {
					e.printStackTrace();
				}
        		 
        		
        	 }
         }
         
         
//         Stream<CrawlRequest> requests = urls.stream().map(CrawlRequest::createDefault);
//         Iterator<CrawlRequest> iter = requests.iterator();
//         while(iter.hasNext()) {
//        	 CrawlRequest e = iter.next();
//        	 try {
//				this.crawl(e);
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//         }
         
//         Date d = new Date();
//
// 		int hour = d.getHours();
// 		int minute = d.getMinutes();
// 		if(minute==0 && hour!=lastStored) {
// 			lastStored=hour;
// 			try {
//				tools.saveState(this.getState());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
// 		}
    }
}
