package fr.an.tools.flipbook2pdf.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SiteHtmlAnalyzer {

    private HttpClientHelper httpHelper;
    
    public SiteHtmlAnalyzer(HttpClientHelper httpHelper) {
        this.httpHelper = httpHelper;
    }

    public List<OrderBookInfo> scanBooksPage(String htmlPage) {
        List<OrderBookInfo> res = new ArrayList<>();
        
        Document htmlDoc = Jsoup.parse(htmlPage);

        // load already seen orderDetailId => bookTitle 
        Map<String,OrderBookInfo> cacheBooks = new LinkedHashMap<>();
        File cacheBooksFile = new File("out/cachedBooks");
        if (cacheBooksFile.exists()) {
            try {
                for(String line : FileUtils.readLines(cacheBooksFile, "UTF-8")) {
                    String[] cols = line.split(":");
                    String orderDetailId = cols[0], label = cols[1], bookTitle = cols[2];
                    cacheBooks.put(orderDetailId, new OrderBookInfo(orderDetailId, label, bookTitle));
                }
            } catch (IOException e) {
            }
        }
        
        Elements flipbookElts = htmlDoc.getElementsByClass("n-flipbook");
        for(Element flipbookElt : flipbookElts) {
            Element flipbookContentElt = flipbookElt.child(1);
            Element flipbookContentAnchorElt = flipbookContentElt.child(0);
            String label = flipbookContentAnchorElt.getElementsByTag("span").first().text();
            
            Element readAnchorElt = flipbookContentElt.getElementsByClass("read-flipbook").first();
            String readHref = readAnchorElt.attr("href");
            String orderDetailId = readHref.split("=")[1];
            OrderBookInfo orderBookInfo = cacheBooks.get(orderDetailId);
            if (orderBookInfo != null) {
                log.info("found Flipbook:" + label + " orderDetailId=" + orderDetailId 
                        // + " href=" + readHref
                        + " (already cached) .. bookTitle=" + orderBookInfo.bookTitle);
            } else {
                
                // download book html info
                String readHtmlContent = httpHelper.downloadHtml(readHref, orderDetailId);
                
                // parse to extract book title
                Document readHtmlDoc = Jsoup.parse(readHtmlContent);
                String bookTitle = readHtmlDoc.head().getElementsByTag("title").first().text();

                orderBookInfo = new OrderBookInfo(orderDetailId, label, bookTitle);
                cacheBooks.put(orderDetailId, orderBookInfo);
                
                log.info("found Flipbook:" + label + " orderDetailId=" + orderDetailId 
                        + " href=" + readHref + " .. download info"
                        + " => bookTitle=" + bookTitle);

                String line = orderDetailId + ":" + label + ":" + bookTitle + "\n";
                try {
                    FileUtils.write(cacheBooksFile, line, "UTF-8", true);
                } catch (IOException e) {
                }
            }
            res.add(orderBookInfo);
        }
        return res;
    }

}
