package fr.an.test.qr.qrcodetest;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 
 */
public class QRCodeEncoderApp {

    private String inputText;
    
    private int size = 1000;
    private BarcodeFormat qrCodeFormat = BarcodeFormat.QR_CODE;
    private ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.M;
    private String imageFormat = "png";
    
    private String outputMode = "file"; // "swing";
    private String outputFileName;
    
    
    // ------------------------------------------------------------------------

    public QRCodeEncoderApp() {
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            QRCodeEncoderApp app = new QRCodeEncoderApp();
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
                inputText = args[++i];
            } else if (arg.equals("-s") || arg.equals("--size")) {
                size = Integer.parseInt(args[++i]);
            } else if (arg.equals("-o") || arg.equals("--output")) {
                outputFileName = args[++i];
                if (outputFileName.equalsIgnoreCase("swing")) {
                    outputMode = "swing";
                }
            } else {
                throw new RuntimeException("Unrecognized arg " + arg);
            }
        }
        if (outputFileName == null) {
            outputFileName = "qrcode-out." + imageFormat;
        }

        try {
            // encode
            BitMatrix bitMatrix = generateMatrix(inputText, size);
    
            if (outputMode.equalsIgnoreCase("file")) {
                // write in a file
                writeImage(outputFileName, imageFormat, bitMatrix);
            } else if (outputMode.equalsIgnoreCase("swing")) {
                showSwingPanel(bitMatrix);
            } else {
                System.err.println("Unkown output mode ... using default:");
                writeImage(outputFileName, imageFormat, bitMatrix);
            }
            
        } catch(Exception ex) {
            System.err.println("Failed");
            throw new RuntimeException("Failed", ex);
        }
    }



    private BitMatrix generateMatrix(final String data, final int size) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType,Object>();
        if (errorCorrectionLevel != null) {
            hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
        }
        BitMatrix bitMatrix = qrCodeWriter.encode(data, qrCodeFormat, size, size, hints );
        return bitMatrix;
    }

    private void writeImage(String outputFileName, String imageFormat, BitMatrix bitMatrix) throws IOException {
        OutputStream stream = null;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(new File(outputFileName)));
            MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, stream);
        } finally {
            if (stream != null) {
                try { stream.close(); } catch(Exception ex) {}
            }
        }
    }

    private void showSwingPanel(BitMatrix bitMatrix) {
        final BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JPanel panel = new JPanel();
                JLabel picLabel = new JLabel(new ImageIcon(image));
                panel.add(picLabel);
                
                JFrame frame = new JFrame();
                frame.getContentPane().add(panel);
                frame.pack();
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
    
}
