# HtmlConverter
This is a HTML2Image and HTML2PDF Converter
Its possible to use it via Command Line or use it as a library.
One great addon is to attach a file into the PDF

This Project is based on the Work of flying-saucer and html2image 

#Run it
To run this Project in Command Line simply set your JAVA_HOME and
```
java -jar HtmlConverter.jar [html file] [output filename] [attachment if needed]
```

#Embed it
To embed this into you project simply use the HtmlConverter.jar as classpath (library) and call the static method 
fromStringToPDF(String html, String output_filenameAndPath, String attachment_uri) 
or 
fromFileToPDF(String file_uri, String output_filenameAndPath, String attachment_uri)

Example
```
 //HTML          
 String html="<html><body><h1>Hello HtmlConverter</h1></body></html>"
 //HTML TO PDF without Attachment
 HtmlConverter.fromStringToPDF(html, "" ,"myPDFfile", null);
```

To embed images and css you have too add the Filepath
like: (!Please use / not \ and please end the line with / to make it work)
```
file:///C:/Content/

 HtmlConverter.fromStringToPDF(html, "file:///C:/Content/" ,"myPDFfile", null);
```
