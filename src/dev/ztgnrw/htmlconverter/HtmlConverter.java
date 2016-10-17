/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.ztgnrw.htmlconverter;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import gui.ava.html.Html2Image;
import gui.ava.html.image.generator.HtmlImageGenerator;
import gui.ava.html.parser.HtmlParser;
import gui.ava.html.parser.HtmlParserImpl;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.XMLResource;
import org.xml.sax.InputSource;

/**
 *
 * @author web
 */
public class HtmlConverter {

    private static String html = "";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, DocumentException {

        if (args.length < 2) {
            System.err.println("Usage Html2ImageConverter [path to html] [output filename] [attachment]");
            return;
        }

     
         if (args.length==2) {
              //  fromFile(args[0], args[1]);
        fromFileToPDF(args[0], args[1], "");
        }
         
         if (args.length==3) {
             //  fromFile(args[0], args[1]);
        fromFileToPDF(args[0], args[1], args[2]);
        }
        
       

    }

    public static void fromFile(String file_uri, String output) throws MalformedURLException {

        final Html2Image html2Image = Html2Image.fromFile(new File(file_uri), null);
        html2Image.getImageRenderer().saveImage(output + ".png");

    }

    public static void fromString(String html, String output) {
        final Html2Image html2Image = Html2Image.fromHtml(html, null);
        html2Image.getImageRenderer().saveImage(output + ".png");
    }

    public static void addFileToPDF(String src, String output, String attachment_uri) throws IOException, DocumentException {

        PdfReader reader = new PdfReader(src);
        addFile(reader, output, attachment_uri);

    }

    public static void addFileToByteArray(ByteArrayOutputStream fos, String output, String attachment_uri) throws IOException, DocumentException {

        PdfReader reader = new PdfReader(fos.toByteArray());
        addFile(reader, output, attachment_uri);

    }

    private static void addFile(PdfReader reader, String output, String attachment_uri) throws IOException, DocumentException {

        File attachment = new File(attachment_uri);

        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(output + ".pdf"));
        // PdfFileSpecification fs = PdfFileSpecification.fileEmbedded(
        //        stamper.getWriter(), null, "test.txt", "Some test".getBytes());

        PdfFileSpecification fs = PdfFileSpecification.fileEmbedded(
                stamper.getWriter(), null, attachment.getName(), Files.readAllBytes(attachment.toPath()));

        stamper.addFileAttachment("Attachment File", fs);
        stamper.close();
    }

    public static void fromFileToPDF(String file_uri, String output, String attachment_uri)  {

        HtmlParser parser = new HtmlParserImpl();
        parser.load(new File(file_uri).toURI(), null);

        Document document = parser.getDocument();

        ITextRenderer renderer = new ITextRenderer();

        renderer.setDocument(document, null);
        renderer.layout();

        toPDF(renderer, output, attachment_uri);

    }

    public static void fromStringToPDF(String html, String output, String attachment_uri)  {

        HtmlParser parser = new HtmlParserImpl();
        parser.loadHtml(html, null);

        Document document = parser.getDocument();

        ITextRenderer renderer = new ITextRenderer();

        renderer.setDocument(document, null);
        renderer.layout();

        toPDF(renderer, output, attachment_uri);

    }

    private static void toPDF(ITextRenderer renderer, String output, String attachment_uri)  {
        String fileNameWithPath = output + ".pdf";
        if (attachment_uri.isEmpty()) {
            try{
            FileOutputStream fos = new FileOutputStream(fileNameWithPath);
            renderer.createPDF(fos);
            fos.close();
            }catch(FileNotFoundException e){
                System.err.println("FileNotFoundException");
                e.printStackTrace();
            }catch(DocumentException e){
                System.err.println("DocumentException");
                e.printStackTrace();
            }
            catch(IOException e){
                System.err.println("IOException");
                e.printStackTrace();
            }
        } else {

            try {
                ByteArrayOutputStream fos = new ByteArrayOutputStream();
                renderer.createPDF(fos);
                fos.close();
                
                addFileToByteArray(fos, output, attachment_uri);
            } catch (DocumentException e) {
                System.err.println("DocumentException");
                e.printStackTrace();
            }
            catch (IOException e) {
                System.err.println("IOException");
                e.printStackTrace();
            }
        }
    }

}
