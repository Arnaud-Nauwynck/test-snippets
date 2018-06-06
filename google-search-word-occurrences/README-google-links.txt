


google: http download java library
=> Apache Commons    HttpClient
http://hc.apache.org/httpclient-3.x/

maven dependency:
example: google: httpclient maven dependency
=> http://mvnrepository.com/artifact/commons-httpclient/commons-httpclient/3.1
search from nexus:
http://search.maven.org
commons-httpclient
=> http://search.maven.org/#search|gav|1|g%3A%22commons-httpclient%22%20AND%20a%3A%22commons-httpclient%22


<dependency>
	<groupId>commons-httpclient</groupId>
	<artifactId>commons-httpclient</artifactId>
	<version>3.1</version>
</dependency>
            


cf link Tutorial: http://hc.apache.org/httpclient-3.x/tutorial.html
=> code sample:

	HttpClient client = new HttpClient();

	// Create a method instance.
	GetMethod method = new GetMethod(url);

	// Provide custom retry handler is necessary
	method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
		new DefaultHttpMethodRetryHandler(3, false));

	try {
	// Execute the method.
	int statusCode = client.executeMethod(method);

	if (statusCode != HttpStatus.SC_OK) {
	System.err.println("Method failed: " + method.getStatusLine());
	}

	// Read the response body.
	byte[] responseBody = method.getResponseBody();

	// Deal with the response.
	// Use caution: ensure correct character encoding and is not binary data
	System.out.println(new String(responseBody));




google : Html parser Java library
=> JSoup

google: jsoup tutorial
=> http://www.mkyong.com/java/jsoup-html-parser-hello-world-examples/
=> maven dependency + code sample

	<dependency>
		<groupId>org.jsoup</groupId>
		<artifactId>jsoup</artifactId>
		<version>1.7.2</version>
	</dependency>

code sample:
	Jsoup.connect("http://yahoo.com").get();
	or Jsoup.parse()  ...
		
	Document doc = Jsoup.parse(html.toString()); 
...then extract:
	String description = doc.select("meta[name=description]").get(0).attr("content");
	System.out.println("Meta description : " + description);


google: google search html parse results java

	https://developers.google.com/custom-search/v1/overview
using JSon instead of Html 

google: google search json java api library 
=> http://stackoverflow.com/questions/3727662/how-can-you-search-google-programmatically-java-api

example of Json query:
	http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=spring

{"responseData": {"results":[{"GsearchResultClass":"GwebSearch","unescapedUrl":"http://spring.io/","url":"http://spring.io/","visibleUrl":"spring.io","cacheUrl":"http://www.google.com/search?q\u003dcache:0fqfBWIG5MEJ:spring.io","title":"\u003cb\u003eSpring\u003c/b\u003e","titleNoFormatting":"Spring","content":"\u003cb\u003eSpring\u003c/b\u003e helps development teams everywhere build simple, portable, fast and   flexible JVM-based systems and applications. Build Anything. Write clean,   testable \u003cb\u003e...\u003c/b\u003e"},{"GsearchResultClass":"GwebSearch","unescapedUrl":"http://w  ... 





    String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
    String search = "stackoverflow";
    String charset = "UTF-8";

    URL url = new URL(google + URLEncoder.encode(search, charset));
    Reader reader = new InputStreamReader(url.openStream(), charset);
    GoogleResults results = new Gson().fromJson(reader, GoogleResults.class);

    // Show title and URL of 1st result.
    System.out.println(results.getResponseData().getResults().get(0).getTitle());
    

	public class GoogleResults {

	    private ResponseData responseData;
	    public ResponseData getResponseData() { return responseData; }
	    public void setResponseData(ResponseData responseData) { this.responseData = responseData; }
	    public String toString() { return "ResponseData[" + responseData + "]"; }

	    static class ResponseData {
		private List<Result> results;
		public List<Result> getResults() { return results; }
		public void setResults(List<Result> results) { this.results = results; }
		public String toString() { return "Results[" + results + "]"; }
	    }

	    static class Result {
		private String url;
		private String title;
		public String getUrl() { return url; }
		public String getTitle() { return title; }
		public void setUrl(String url) { this.url = url; }
		public void setTitle(String title) { this.title = title; }
		public String toString() { return "Result[url:" + url +",title:" + title + "]"; }
	    }

	}

missing pagination in this code....
cf doc for google search api json:
https://developers.google.com/image-search/v1/jsondevguide
=> 
	cursor is an optional property that is present once a search completes successfully. When present, the property specifies how an application can request additional search results for the current query term, the estimated result count, the current page, and the URL for a search results page. The property has the following structure:

	    pages[] supplies an array used by start in order to iterate through all available results. Each entry in the array is an object with the following structure:
		start supplies the value that will be used in the &start URL argument to request a bundle of results
		label supplies a text label associated with the entry (for example, "1", "2", "3", or "4")


extract fragment in json answer:
"cursor":{
	"resultCount":"112 000 000",
	"pages":[{"start":"0","label":1},{"start":"4","label":2},{"start":"8","label":3},{"start":"12","label":4},{"start":"16","label":5},{"start":"20","label":6},{"start":"24","label":7},{"start":"28","label":8}],
	"estimatedResultCount":"112000000",
	"currentPageIndex":0,
	"moreResultsUrl":"http://www.google.com/search?oe\u003dutf8\u0026ie\u003dutf8\u0026source\u003duds\u0026start\u003d0\u0026hl\u003dfr\u0026q\u003dspring","searchResultTime":"0,15"}

try query with &start=4  ... for page 4


http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=spring&start=4
=> ... OK more data ....


google: json to java pojo code generator 
=> http://www.jsonschema2pojo.org/
online converter:

=> copy&paste  google search result into  it ... => got 

	-----------------------------------com.example.Cursor.java-----------------------------------

	package com.example;

	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import javax.annotation.Generated;
	import com.fasterxml.jackson.annotation.JsonAnyGetter;
	import com.fasterxml.jackson.annotation.JsonAnySetter;
	import com.fasterxml.jackson.annotation.JsonInclude;
	import com.fasterxml.jackson.annotation.JsonProperty;
	import com.fasterxml.jackson.annotation.JsonPropertyOrder;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Generated("com.googlecode.jsonschema2pojo")
	@JsonPropertyOrder({
	"resultCount",
	"pages",
	"estimatedResultCount",
	"currentPageIndex",
	"moreResultsUrl",
	"searchResultTime"
	})
	public class Cursor {

	@JsonProperty("resultCount")
	private String resultCount;
	@JsonProperty("pages")
	private List<Page> pages = new ArrayList<Page>();
	@JsonProperty("estimatedResultCount")
	private String estimatedResultCount;
	@JsonProperty("currentPageIndex")
	private Integer currentPageIndex;
	@JsonProperty("moreResultsUrl")
	private String moreResultsUrl;
	@JsonProperty("searchResultTime")
	private String searchResultTime;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("resultCount")
	public String getResultCount() {
	return resultCount;
	}

	@JsonProperty("resultCount")
	public void setResultCount(String resultCount) {
	this.resultCount = resultCount;
	}

	@JsonProperty("pages")
	public List<Page> getPages() {
	return pages;
	}

	@JsonProperty("pages")
	public void setPages(List<Page> pages) {
	this.pages = pages;
	}

	@JsonProperty("estimatedResultCount")
	public String getEstimatedResultCount() {
	return estimatedResultCount;
	}

	@JsonProperty("estimatedResultCount")
	public void setEstimatedResultCount(String estimatedResultCount) {
	this.estimatedResultCount = estimatedResultCount;
	}

	@JsonProperty("currentPageIndex")
	public Integer getCurrentPageIndex() {
	return currentPageIndex;
	}

	@JsonProperty("currentPageIndex")
	public void setCurrentPageIndex(Integer currentPageIndex) {
	this.currentPageIndex = currentPageIndex;
	}

	@JsonProperty("moreResultsUrl")
	public String getMoreResultsUrl() {
	return moreResultsUrl;
	}

	@JsonProperty("moreResultsUrl")
	public void setMoreResultsUrl(String moreResultsUrl) {
	this.moreResultsUrl = moreResultsUrl;
	}

	@JsonProperty("searchResultTime")
	public String getSearchResultTime() {
	return searchResultTime;
	}

	@JsonProperty("searchResultTime")
	public void setSearchResultTime(String searchResultTime) {
	this.searchResultTime = searchResultTime;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
	return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperties(String name, Object value) {
	this.additionalProperties.put(name, value);
	}

	}
	-----------------------------------com.example.Example.java-----------------------------------

	package com.example;

	import java.util.HashMap;
	import java.util.Map;
	import javax.annotation.Generated;
	import com.fasterxml.jackson.annotation.JsonAnyGetter;
	import com.fasterxml.jackson.annotation.JsonAnySetter;
	import com.fasterxml.jackson.annotation.JsonInclude;
	import com.fasterxml.jackson.annotation.JsonProperty;
	import com.fasterxml.jackson.annotation.JsonPropertyOrder;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Generated("com.googlecode.jsonschema2pojo")
	@JsonPropertyOrder({
	"responseData",
	"responseDetails",
	"responseStatus"
	})
	public class Example {

	@JsonProperty("responseData")
	private ResponseData responseData;
	@JsonProperty("responseDetails")
	private Object responseDetails;
	@JsonProperty("responseStatus")
	private Integer responseStatus;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("responseData")
	public ResponseData getResponseData() {
	return responseData;
	}

	@JsonProperty("responseData")
	public void setResponseData(ResponseData responseData) {
	this.responseData = responseData;
	}

	@JsonProperty("responseDetails")
	public Object getResponseDetails() {
	return responseDetails;
	}

	@JsonProperty("responseDetails")
	public void setResponseDetails(Object responseDetails) {
	this.responseDetails = responseDetails;
	}

	@JsonProperty("responseStatus")
	public Integer getResponseStatus() {
	return responseStatus;
	}

	@JsonProperty("responseStatus")
	public void setResponseStatus(Integer responseStatus) {
	this.responseStatus = responseStatus;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
	return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperties(String name, Object value) {
	this.additionalProperties.put(name, value);
	}

	}
	-----------------------------------com.example.Page.java-----------------------------------

	package com.example;

	import java.util.HashMap;
	import java.util.Map;
	import javax.annotation.Generated;
	import com.fasterxml.jackson.annotation.JsonAnyGetter;
	import com.fasterxml.jackson.annotation.JsonAnySetter;
	import com.fasterxml.jackson.annotation.JsonInclude;
	import com.fasterxml.jackson.annotation.JsonProperty;
	import com.fasterxml.jackson.annotation.JsonPropertyOrder;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Generated("com.googlecode.jsonschema2pojo")
	@JsonPropertyOrder({
	"start",
	"label"
	})
	public class Page {

	@JsonProperty("start")
	private String start;
	@JsonProperty("label")
	private Integer label;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("start")
	public String getStart() {
	return start;
	}

	@JsonProperty("start")
	public void setStart(String start) {
	this.start = start;
	}

	@JsonProperty("label")
	public Integer getLabel() {
	return label;
	}

	@JsonProperty("label")
	public void setLabel(Integer label) {
	this.label = label;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
	return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperties(String name, Object value) {
	this.additionalProperties.put(name, value);
	}

	}
	-----------------------------------com.example.ResponseData.java-----------------------------------

	package com.example;

	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import javax.annotation.Generated;
	import com.fasterxml.jackson.annotation.JsonAnyGetter;
	import com.fasterxml.jackson.annotation.JsonAnySetter;
	import com.fasterxml.jackson.annotation.JsonInclude;
	import com.fasterxml.jackson.annotation.JsonProperty;
	import com.fasterxml.jackson.annotation.JsonPropertyOrder;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Generated("com.googlecode.jsonschema2pojo")
	@JsonPropertyOrder({
	"results",
	"cursor"
	})
	public class ResponseData {

	@JsonProperty("results")
	private List<Result> results = new ArrayList<Result>();
	@JsonProperty("cursor")
	private Cursor cursor;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("results")
	public List<Result> getResults() {
	return results;
	}

	@JsonProperty("results")
	public void setResults(List<Result> results) {
	this.results = results;
	}

	@JsonProperty("cursor")
	public Cursor getCursor() {
	return cursor;
	}

	@JsonProperty("cursor")
	public void setCursor(Cursor cursor) {
	this.cursor = cursor;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
	return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperties(String name, Object value) {
	this.additionalProperties.put(name, value);
	}

	}
	-----------------------------------com.example.Result.java-----------------------------------

	package com.example;

	import java.util.HashMap;
	import java.util.Map;
	import javax.annotation.Generated;
	import com.fasterxml.jackson.annotation.JsonAnyGetter;
	import com.fasterxml.jackson.annotation.JsonAnySetter;
	import com.fasterxml.jackson.annotation.JsonInclude;
	import com.fasterxml.jackson.annotation.JsonProperty;
	import com.fasterxml.jackson.annotation.JsonPropertyOrder;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Generated("com.googlecode.jsonschema2pojo")
	@JsonPropertyOrder({
	"GsearchResultClass",
	"unescapedUrl",
	"url",
	"visibleUrl",
	"cacheUrl",
	"title",
	"titleNoFormatting",
	"content"
	})
	public class Result {

	@JsonProperty("GsearchResultClass")
	private String GsearchResultClass;
	@JsonProperty("unescapedUrl")
	private String unescapedUrl;
	@JsonProperty("url")
	private String url;
	@JsonProperty("visibleUrl")
	private String visibleUrl;
	@JsonProperty("cacheUrl")
	private String cacheUrl;
	@JsonProperty("title")
	private String title;
	@JsonProperty("titleNoFormatting")
	private String titleNoFormatting;
	@JsonProperty("content")
	private String content;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("GsearchResultClass")
	public String getGsearchResultClass() {
	return GsearchResultClass;
	}

	@JsonProperty("GsearchResultClass")
	public void setGsearchResultClass(String GsearchResultClass) {
	this.GsearchResultClass = GsearchResultClass;
	}

	@JsonProperty("unescapedUrl")
	public String getUnescapedUrl() {
	return unescapedUrl;
	}

	@JsonProperty("unescapedUrl")
	public void setUnescapedUrl(String unescapedUrl) {
	this.unescapedUrl = unescapedUrl;
	}

	@JsonProperty("url")
	public String getUrl() {
	return url;
	}

	@JsonProperty("url")
	public void setUrl(String url) {
	this.url = url;
	}

	@JsonProperty("visibleUrl")
	public String getVisibleUrl() {
	return visibleUrl;
	}

	@JsonProperty("visibleUrl")
	public void setVisibleUrl(String visibleUrl) {
	this.visibleUrl = visibleUrl;
	}

	@JsonProperty("cacheUrl")
	public String getCacheUrl() {
	return cacheUrl;
	}

	@JsonProperty("cacheUrl")
	public void setCacheUrl(String cacheUrl) {
	this.cacheUrl = cacheUrl;
	}

	@JsonProperty("title")
	public String getTitle() {
	return title;
	}

	@JsonProperty("title")
	public void setTitle(String title) {
	this.title = title;
	}

	@JsonProperty("titleNoFormatting")
	public String getTitleNoFormatting() {
	return titleNoFormatting;
	}

	@JsonProperty("titleNoFormatting")
	public void setTitleNoFormatting(String titleNoFormatting) {
	this.titleNoFormatting = titleNoFormatting;
	}

	@JsonProperty("content")
	public String getContent() {
	return content;
	}

	@JsonProperty("content")
	public void setContent(String content) {
	this.content = content;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
	return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperties(String name, Object value) {
	this.additionalProperties.put(name, value);
	}

	}






google: jackson json java 
=> Jackson
	<dependency>
		<groupId>com.fasterxml.jackson.core</groupId>
		<artifactId>jackson-core</artifactId>
		<version>2.2.3</version>
	</dependency>


Jackson or Gson ??? 

google: Gson json java 
=> Gson
http://code.google.com/p/google-gson/
=> find maven dependency... in Source > Browse > trunk > gson > pom.xml ... 
http://code.google.com/p/google-gson/source/browse/trunk/gson/pom.xml
	<dependency>
	  <groupId>com.google.code.gson</groupId>
	  <artifactId>gson</artifactId>
	  <packaging>jar</packaging>
	  <version>2.2.5-SNAPSHOT</version>
	</dependency>




google:  AutoDectectParser
=> Apache Tika 

maven dependency:
	<dependency>
	    <groupId>org.apache.tika</groupId>
	    <artifactId>tika-parsers</artifactId>
	    <version>1.1</version>
	    <type>jar</type>
	</dependency>


Code sample:
google: Tika example AutoDectectParser text BodyContentHandler
http://massapi.com/class/bo/BodyContentHandler.html

	Parser parser = new AutoDetectParser();
	BodyContentHandler handler = new BodyContentHandler();
	Metadata metadata = new Metadata();
	InputStream content = getClass().getResourceAsStream("/slides.pdf");
	parser.parse(content, handler, metadata, new ParseContext());




MultiThreading 
google: java ThreadPoolExecuter sample Future
=> http://www.journaldev.com/1090/java-callable-future-example

code sample:
public class MyCallable implements Callable<String> {
 
    @Override
    public String call() throws Exception {
        Thread.sleep(1000);
        //return the thread name executing this callable task
        return Thread.currentThread().getName();
    }
     
    public static void main(String args[]){
        //Get ExecutorService from Executors utility class, thread pool size is 10
        ExecutorService executor = Executors.newFixedThreadPool(10);
        //create a list to hold the Future object associated with Callable
        List<Future<String>> list = new ArrayList<Future<String>>();
        //Create MyCallable instance
        Callable<String> callable = new MyCallable();
        for(int i=0; i< 100; i++){
            //submit Callable tasks to be executed by thread pool
            Future<String> future = executor.submit(callable);
            //add Future to the list, we can get return value using Future
            list.add(future);
        }
        for(Future<String> fut : list){
            try {
                //print the return value of Future, notice the output delay in console
                // because Future.get() waits for task to get completed
                System.out.println(new Date()+ "::"+fut.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        //shut down the executor service now
        executor.shutdown();
    }
 
}




google: java fileutils
=> Apache commons-io
http://commons.apache.org/proper/commons-io/
FileUtils 
=> http://commons.apache.org/proper/commons-io/javadocs/api-release/index.html?org/apache/commons/io/package-summary.html

maven dependency
http://commons.apache.org/proper/commons-io/project-summary.html
or from nexus ... http://search.maven.org/#search|gav|1|g%3A%22commons-io%22%20AND%20a%3A%22commons-io%22

<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.4</version>
</dependency>



Eclipse Plugins:
moreunit
eclemma
infinitest
epomodoro
pulse
tdgotchi

http://code.google.com/p/key-mon/



