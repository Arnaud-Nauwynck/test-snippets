package fr.an.tools.flipbook2pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import fr.an.tools.flipbook2pdf.impl.BookConfig;
import fr.an.tools.flipbook2pdf.impl.DownloadBookCtx;
import fr.an.tools.flipbook2pdf.impl.Flipbook2PdfParams;
import fr.an.tools.flipbook2pdf.impl.HttpClientHelper;
import fr.an.tools.flipbook2pdf.impl.OrderBookInfo;
import fr.an.tools.flipbook2pdf.impl.PdfBookWriter;
import fr.an.tools.flipbook2pdf.impl.SiteHtmlAnalyzer;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Flipbook2PdfMain {

    private Flipbook2PdfParams params = new Flipbook2PdfParams();
    
    private HttpClientHelper httpHelper = new HttpClientHelper();
    
    public static void main(String[] args) {
        try {
            Flipbook2PdfMain app = new Flipbook2PdfMain();
            app.run(args);
        } catch(Exception ex) {
            ex.printStackTrace(System.err);
            System.err.println("exiting");
        }
    }
    
    public void run(String[] args) throws Exception {
        params.parseArgs(args);
        File baseOutputDir = params.getBaseOutputDir();
        
        httpHelper.initHttpClient(params);
        
        loginAuthenticate();

        String bookTitle = params.getBookTitle();
        if (bookTitle == null) {
            List<OrderBookInfo> orderBooks = scanBooksPage();
            for (val orderBook : orderBooks) {
                checkOrDownloadBook(baseOutputDir, orderBook.bookTitle, orderBook.orderDetailId);
            }
        }
        
        String orderDetail = params.getOrderDetail();
        if (orderDetail != null && bookTitle != null) {
            checkOrDownloadBook(baseOutputDir, bookTitle, orderDetail);
        }
    }

    private void checkOrDownloadBook(File baseOutputDir, String bookTitle, String orderDetail) throws Exception {
        File bookOutputDir = new File(baseOutputDir, bookTitle);
        File outputFile = new File(baseOutputDir, bookTitle + ".pdf");
        if (! outputFile.exists()) {
        	if (! bookOutputDir.exists()) {
        		bookOutputDir.mkdirs();
        	}
            downloadBook(baseOutputDir, bookOutputDir, bookTitle, orderDetail);
        } else {
            log.info("dir for book '" + bookTitle + " already exist => skip");
        }
    }

    private void downloadBook(File baseOutputDir, File bookOutputDir, String bookTitle, String orderDetail)
            throws Exception {
        log.info("download book " + bookTitle);
        BookConfig bookConfig = downloadBookConfigAndParse(bookOutputDir, bookTitle, orderDetail);
        
        DownloadBookCtx downloadCtx = new DownloadBookCtx(
                bookOutputDir, bookTitle, orderDetail, bookConfig);
        
        int totalPageCount = bookConfig.getTotalPageCount();
        log.info("loaded book config: totalPageCount=" + totalPageCount);
        
        for (int page = 1; page <= totalPageCount; page++) {
            downloadPage(downloadCtx, page);
        }
        
        File outputFile = new File(baseOutputDir, bookTitle + ".pdf");
        if (! outputFile.exists()) {
            log.info("write Pdf file '" + outputFile + "'");
            PdfBookWriter pdfWriter = new PdfBookWriter();
            pdfWriter.writePdf(bookConfig, bookOutputDir, outputFile);
        } else {
            log.info("skip already generated Pdf file '" + outputFile + "'");            
        }
    }


    private void loginAuthenticate() throws Exception {
        Properties authProps = new Properties();
        String homeDir = System.getProperty("user.home");
        try (InputStream in = new FileInputStream(new File(homeDir, "linuxmag.properties"))) {
            authProps.load(in);
        } 
        String authEmail = authProps.getProperty("email");
        String authPasswd = authProps.getProperty("passwd");

        httpHelper.httpAuthenticate(authEmail, authPasswd);
    }

    private List<OrderBookInfo> scanBooksPage() {
        String respContent = httpHelper.downloadBooksHtmlPage();
        
        SiteHtmlAnalyzer siteHtmlAnalyzer = new SiteHtmlAnalyzer(httpHelper);
        List<OrderBookInfo> orderBooks = siteHtmlAnalyzer.scanBooksPage(respContent);
        return orderBooks; 
    }


    
    private BookConfig downloadBookConfigAndParse(
            File bookOutputDir,
            String bookTitle,
            String orderDetail
            ) throws Exception {
        String respContent;
        File bookConfigFile = new File(bookOutputDir, "config.js");
        if (!bookConfigFile.exists()) {
            respContent = httpHelper.downloadBookConfig(bookTitle, orderDetail);
            FileUtils.writeStringToFile(bookConfigFile, respContent, "UTF-8");
        } else {
            respContent = FileUtils.readFileToString(bookConfigFile, "UTF-8");
        }
        
        int indexStart = respContent.indexOf(";bookConfig.BookTemplateName=");
        String initContent = respContent.substring(indexStart, respContent.length());

        Pattern pattern = Pattern.compile(
                "bookConfig.totalPageCount=(\\d+);"
                + "bookConfig.largePageWidth=(\\d+);"
                + "bookConfig.largePageHeight=(\\d+);"
                + ";bookConfig.securityType=\"1\";"
                + "bookConfig.CreatedTime =\"(\\d+)\";"
                +".*"
                );
        Matcher matcher = pattern.matcher(initContent);
        if (!matcher.find()) {
            throw new RuntimeException("Failed to parse book config.js");
        }
        BookConfig res = BookConfig.builder()
                .totalPageCount(Integer.parseInt(matcher.group(1)))
                .largePageWidth(Integer.parseInt(matcher.group(2)))
                .largePageHeight(Integer.parseInt(matcher.group(3)))
                .createdTime(Long.parseLong(matcher.group(4)))
                .build();
        return res;
    }

    private void downloadPage(DownloadBookCtx ctx, int page) throws Exception {
        String imageName = page + ".jpg";
        File imageFile = new File(ctx.bookOutputDir, imageName);
        if (! imageFile.exists()) {
            log.info("download page " + page + "/" + ctx.bookConfig.getTotalPageCount());
            byte[] pageImgContent = httpHelper.httpDownloadPageImage(ctx, imageName);
    
            FileUtils.writeByteArrayToFile(imageFile, pageImgContent);
        } else {
            log.info("skip already downloaded page " + page + "/" + ctx.bookConfig.getTotalPageCount());
        }
    }
    
}
