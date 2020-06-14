package fr.an.testhttpurlconn;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
	try {
	    new Main().run(args);
	    System.out.println("Finished");
	} catch (Exception e) {
	    e.printStackTrace();
	    System.err.println("Failed ..exiting");
	}
    }
    
    public void run(String[] args) throws IOException {
	List<URL> urls = new ArrayList<>();
	for(int i = 0; i < args.length; i++) {
	    String a = args[i];
	    if (a.equals("--url")) {
		URL url = toURL(args[++i]);
		urls.add(url);
	    } else {
		System.err.println("Unrecognised argument '" + a + "'");
	    }
	}

	Map<String,List<URL>> urlByAddr = new HashMap<>();
	for(URL u : urls) {
	    String baseURL = u.getProtocol() + ":";
	    String authority = u.getAuthority(); // host(:port)?
	    if (authority != null && authority.length() > 0) {
		baseURL += "//";
		String userInfo = u.getUserInfo();
		if (userInfo != null) {
		    baseURL += userInfo + "@";
		}
		baseURL += authority;
	    }
	    List<URL> ls = urlByAddr.get(baseURL);
	    if (ls == null) {
		ls = new ArrayList<URL>();
		urlByAddr.put(baseURL, ls);
	    }
	    ls.add(u);
	}
	
	for(Map.Entry<String, List<URL>> e : urlByAddr.entrySet()) {
	    URL baseURL = toURL(e.getKey());
	    List<URL> urlList = e.getValue();
	    
	    URLConnection conn = baseURL.openConnection();
	    conn.connect();
	    conn.setDefaultUseCaches(true);
	    conn.getInputStream();
	}
    }
    
    public static URL toURL(String url) {
	try {
	    return new URL(url);
	} catch (MalformedURLException ex) {
	    throw new RuntimeException("Bad url '" + url +"'", ex);
	}
    }
}
