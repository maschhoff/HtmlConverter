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
import gui.ava.html.parser.HtmlParser;
import gui.ava.html.parser.HtmlParserImpl;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

/**
 *
 * @author Mathias Aschhoff ZTG 2016 m.aschhoff@ztg-nrw.de
 */
public class HtmlConverter {



    /**
     * Main method for running on CommandLine File2PDF
     * Needs 2/3 args
     * 2 args without attachment html and output file
     * 3 with an attachment
     * 
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
       
       
      // Probleme mit dem Pfad lösen über file:///
     //   fromStringToPDF("<img src=\"./bild.jpg\"/>", "file:///C:/Users/maschhoff/Pictures/", "testss", "");
        
       

    }

    /**
     * Convert HTML File to Image
     * @param file_uri
     * @param output
     * @throws MalformedURLException 
     */
    public static void fromFile(String file_uri, String output) throws MalformedURLException {

        final Html2Image html2Image = Html2Image.fromFile(new File(file_uri), null);
        html2Image.getImageRenderer().saveImage(output + ".png");

    }

    /**
     * Convert HTML String to Image
     * @param html
     * @param output 
     */
    public static void fromString(String html, String output) {
        final Html2Image html2Image = Html2Image.fromHtml(html, null);
        html2Image.getImageRenderer().saveImage(output + ".png");
    }

    /**
     * Attachment to PDF File
     * 
     * @param src
     * @param output
     * @param attachment_uri
     * @throws IOException
     * @throws DocumentException 
     */
    public static void addFileToPDF(String src, String output, String attachment_uri) throws IOException, DocumentException {

        PdfReader reader = new PdfReader(src);
        addFile(reader, output, attachment_uri);

    }

    /**
     * Attachment to PDFByteArray
     * @param fos
     * @param output
     * @param attachment_uri
     * @throws IOException
     * @throws DocumentException 
     */
    public static void addFileToByteArray(ByteArrayOutputStream fos, String output, String attachment_uri) throws IOException, DocumentException {

        PdfReader reader = new PdfReader(fos.toByteArray());
        addFile(reader, output, attachment_uri);

    }

    /**
     * Method for attachment. Adds a File to a PDF as attachment
     * 
     * @param reader
     * @param output
     * @param attachment_uri
     * @throws IOException
     * @throws DocumentException 
     */
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

    /**
     * From HTML file to PDF
     * 
     * @param file_uri
     * @param output
     * @param attachment_uri 
     */
    public static void fromFileToPDF(String file_uri, String output, String attachment_uri)  {

        HtmlParser parser = new HtmlParserImpl();
        parser.load(new File(file_uri).toURI(), null);

        Document document = parser.getDocument();

        ITextRenderer renderer = new ITextRenderer();

        renderer.setDocument(document, document.getBaseURI());
        renderer.layout();

        toPDF(renderer, output, attachment_uri);

    }

    /**
     * From HTML String to PDF File
     * 
     * @param html
     * @param path - Path to related documents (css, images, etc.)
     * @param output
     * @param attachment_uri 
     */
    public static void fromStringToPDF(String html, String path, String output, String attachment_uri)  {

        HtmlParser parser = new HtmlParserImpl();
        parser.loadHtml(html, null);

        Document document = parser.getDocument();
        
        ITextRenderer renderer = new ITextRenderer();

        renderer.setDocument(document, path);
        renderer.layout();

        toPDF(renderer, output, attachment_uri);

    }

    /**
     * Method to convert. If attachment is given it will be added here
     * 
     * @param renderer
     * @param output
     * @param attachment_uri 
     */
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
