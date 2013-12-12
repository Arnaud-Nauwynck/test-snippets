package fr.an.test.qr.qrcodetest;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

public class QRCodeDecoderApp {

    
    private String inputFileName;
    private int size = 1000;
    private int[] decodeHintAllowedLength = new int[] { 1000, 1000 };
    // private BarcodeFormat qrCodeFormat = BarcodeFormat.QR_CODE;
    
    private String outputFileName;
    
    // ------------------------------------------------------------------------

    public QRCodeDecoderApp() {
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            QRCodeDecoderApp app = new QRCodeDecoderApp();
            app.doMain(args);
        }  catch(Exception ex) {
            System.err.println("Failed");
            ex.printStackTrace(System.err);
        }
    }
    
    // ------------------------------------------------------------------------
    
    public void doMain(String[] args) {
        for(int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-i") || arg.equals("--input")) {
                inputFileName = args[++i];
            } else if (arg.equals("-s") || arg.equals("--size")) {
                size = Integer.parseInt(args[++i]);
                decodeHintAllowedLength = new int[] { size, size };
            } else if (arg.equals("-o") || arg.equals("--output")) {
                outputFileName = args[++i];
            } else {
                throw new RuntimeException("Unrecognized arg " + arg);
            }
        }
        if (inputFileName == null) {
            inputFileName = "qrcode-in.png";
        }

        try {
            // read image file
            File inputFile = new File(inputFileName);
            BinaryBitmap bitmap = readAndBinarizeImage(inputFile);
            
            // decode
            Result decodeResult = decode(bitmap);
            String text = decodeResult.getText();
            
            writeOutputResult(text);
            
        } catch(Exception ex) {
            System.err.println("Failed");
            throw new RuntimeException("Failed", ex);
        }
    }


    private void writeOutputResult(String text) throws FileNotFoundException,
            IOException {
        if (outputFileName != null) {
            OutputStream out = null;
            try {
                out = new BufferedOutputStream(new FileOutputStream(outputFileName));
                out.write(text.getBytes());
            } finally {
                try { out.close(); } catch(IOException e) {}
            }
        } else {
            // print to stdout
            System.out.println(text);
        }
    }


    private Result decode(BinaryBitmap bitmap) {
        QRCodeReader qrCodeReader = new QRCodeReader();
        Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType,Object>();
        if (decodeHintAllowedLength != null) {
            hints.put(DecodeHintType.ALLOWED_LENGTHS, decodeHintAllowedLength);
        }

        Result result;
        try {
            result = qrCodeReader.decode(bitmap, hints);
        } catch (ReaderException ex) {
            throw new RuntimeException("Failed to decode", ex);
        }
        return result;
    }
    
    private BinaryBitmap readAndBinarizeImage(File inputFile) {
        BufferedImage image;
        try {
            image = ImageIO.read(inputFile);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read image file " + inputFile, ex);
        }
        
        // image to binary bitmap
        LuminanceSource source = new com.google.zxing.client.j2se.BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        return bitmap;
    }
}
