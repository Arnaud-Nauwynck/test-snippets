package fr.an.tools.flipbook2pdf.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.FileUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfBookWriter {


    public void writePdf(BookConfig bookConfig, File imgDir,
            File outputFile) throws Exception {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new BufferedOutputStream(new FileOutputStream(outputFile)));
            document.open();
            document.setPageCount(bookConfig.totalPageCount);
            final int width = bookConfig.largePageWidth;
            final int height = bookConfig.largePageHeight;
            document.setPageSize(new Rectangle(width, height));
            
            for(int page = 1; page <= bookConfig.totalPageCount; page++) {
                String imageName = page + ".jpg";
                File imageFile = new File(imgDir, imageName);
                byte[] imgBytes = FileUtils.readFileToByteArray(imageFile);
                
                Image img = Image.getInstance(imgBytes);
                document.add(img);
                
                document.newPage();
            }
            
        } finally {
            document.close();
        }
    }
}
