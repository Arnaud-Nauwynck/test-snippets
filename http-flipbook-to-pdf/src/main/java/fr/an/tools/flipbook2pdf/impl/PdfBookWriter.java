package fr.an.tools.flipbook2pdf.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.FileUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Jpeg;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfBookWriter {


    public void writePdf(BookConfig bookConfig, File imgDir,
            File outputFile) throws Exception {
    	final int width = bookConfig.largePageWidth;
    	final int height = bookConfig.largePageHeight;
        Document document = new Document(PageSize.A4, 
        		// new Rectangle(width, height), 
        		0, 0, 0, 0);
        try {
            PdfWriter.getInstance(document, new BufferedOutputStream(new FileOutputStream(outputFile)));
            document.open();
            document.setPageCount(bookConfig.totalPageCount);
            // document.setPageSize(new Rectangle(width, height));
            document.newPage();
            
            for(int page = 1; page <= bookConfig.totalPageCount; page++) {
                String imageName = page + ".jpg";
                File imageFile = new File(imgDir, imageName);
                byte[] imgBytes = FileUtils.readFileToByteArray(imageFile);
                
                Image img = Image.getInstance(imgBytes);
                		// new Jpeg(imgBytes, width, height);
                img.scaleToFit(PageSize.A4);
                document.add(img);
                
                document.newPage();
            }
            
        } finally {
            document.close();
        }
    }
}
