package resultsggsipu.subprogram;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.pdfbox.ExtractText;

import resultsggsipu.ResultGGSIPU;

public class WebsiteDownload extends Thread {

	String url;
	String name;
	String date;
	public static String [] list = {"Result of BTech"};
	public static String [] blocklist = {"Result of BTech(IT), USS, Dec 2013"};
	
	public WebsiteDownload(String href,String name,String date) {
		this.url = href;
		this.name = name;
		this.date = date;
	}
	
	
	
	public void run() {
		try {
			this.download(url, name);
			this.extractFromFile(new File(name));
			if(name.contains(".pdf")){
				name = name.replace(".pdf",".txt");
				boolean go;
				go = false;
				for(String li:list){
					if(name.contains(li.trim()))
						go = true;
				}	
				for(String blcklst: blocklist){
					if(name.contains(blcklst.trim()))
						go = false;
				}
				if(go){
					FileInteraction fi = new FileInteraction(name);
					try {
						fi.startReading(name);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	 private void download(String fileURL, String destinationDirectory) throws IOException {
	        // File name that is being downloaded
	        String downloadedFileName = fileURL.substring(fileURL.lastIndexOf("/")+1);
	         
	        // Open connection to the file
	        URL url = new URL(fileURL);
	        InputStream is = url.openStream();
	        // Stream to the destionation file
	        FileOutputStream fos = new FileOutputStream(WebsiteInteraction.pdfFolder + File.separator + downloadedFileName);
	  
	        // Read bytes from URL to the local file
	        byte[] buffer = new byte[4096];
	        int bytesRead = 0;
	         
	        System.out.print("Downloading " + downloadedFileName);
	        
	        while ((bytesRead = is.read(buffer)) != -1) {
	            System.out.print(".");  // Progress bar :)
	            fos.write(buffer,0,bytesRead);
	        }
	        System.out.println("done!");
	  
	        // Close destination stream
	        fos.close();
	        // Close URL stream
	        is.close();
	    }  
	 
		private boolean extractFromFile(File component){
			String args[] = new String[3];
			args[0] = "-html";
			if(component.isFile()){
				if(component.getName().endsWith(".pdf")){
					 args[1] = component.getAbsolutePath();
					 args[2] = component.getAbsolutePath().replace(".pdf", ".txt");
					 try {
						ExtractText.main(args);

						//new File(args[1]).delete();
						return true;
					} catch (Exception e) {
						System.err.println("Error in converting "+args[1]+"\n\r"+e.getMessage());
						e.printStackTrace();
					}
				}
			}
			else if(component.isDirectory()){
				for (File inComponent:component.listFiles()){
					return this.extractFromFile(inComponent);
				}
			}
			else{
				System.err.println("unknown file "+component.getName());
			}
			return false;
		}
	
}
