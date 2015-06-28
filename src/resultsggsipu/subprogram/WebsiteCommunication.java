package resultsggsipu.subprogram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.pdfbox.ExtractText;

import resultsggsipu.ResultGGSIPU;
import resultsggsipu.datatypes.PDFsaving;

public class WebsiteCommunication implements Runnable{

	StringBuilder content;
	LinkedList<PDFsaving> list;
	String PDFconverter;
	static public String pdfFolder;
	static Logger selfLogger;
	static LinkedHashSet<String> alreadyDone;
	static Logger errselfLogger;
	public boolean THREADED;
	String URL;
	String NAME;
	String DATE;
	static int debug = 1;
	int mode;
	static String mainSite = "http://ggsipuresults.nic.in/ipu/results/";
	static String gotoSite = mainSite;
	
	public WebsiteCommunication() {
		content = new StringBuilder();
		list  =  new LinkedList<PDFsaving>();
		pdfFolder = Logging.mainfolder+File.separator+"pdf"+File.separator;
		new File(pdfFolder).mkdir();
		this.THREADED = false;
		this.alreadyDone = new LinkedHashSet<String>();
		this.initialize();
	}

	public WebsiteCommunication(boolean THREADED) {
		this();
		this.THREADED = THREADED;
	}
	
	public WebsiteCommunication(boolean THREADED,String mode) {
		this(THREADED);
		if(mode.contains("1")||mode.contains("3"))
			gotoSite+="results060814main.htm";
		else if(mode.contains("0")||mode.contains("2"))
			gotoSite+="resultsmain.htm";
		this.mode = Integer.parseInt(mode);
	}
	
	public WebsiteCommunication(String url,String name,String date) {
		this.URL = url;
		this.NAME = name;
		this.DATE = date;
		this.THREADED = true;
	}
	
	void initialize(){
		String logfile = Logging.mainfolder+File.separator+"WebsiteCommunication.log";
		String errlogfile = Logging.mainfolder+File.separator+"errWebsiteCommunication.log";
		
	    selfLogger = Logger.getLogger("WebsiteCommunication"); 
	    errselfLogger = Logger.getLogger("WebsiteCommunication err");
	    FileHandler fh,fe;  
	    
	    try {  

	        // This block configure the logger with handler and formatter 
	    	fh = new FileHandler(logfile,true); 
	        selfLogger.addHandler(fh); 	    	
	        fe = new FileHandler(errlogfile,true); 
	        errselfLogger.addHandler(fe); 
	    	
	        SimpleFormatter formatter = new SimpleFormatter();  
		    fh.setFormatter(formatter);  
		    fe.setFormatter(formatter);
		    

	        // the following statement is used to log any messages  
	        selfLogger.info("New Log");  
	        errselfLogger.info("New Err Log");  

	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } 
	    
	    if(!((mode == 3)||(mode == 2)))
	    	this.getDoneInitialse(logfile);
	    
	}
	
	public void getDoneInitialse(String fileName){
		String line;
	    StringBuilder content;
	    String temp;
			    
	    try {
	        // FileReader reads text files in the default encoding.
	        FileReader fileReader = new FileReader(fileName);
	
	        // Always wrap FileReader in BufferedReader.
	        BufferedReader bufferedReader = new BufferedReader(fileReader);
	
	        content = new StringBuilder();
	        
	        while((line = bufferedReader.readLine()) != null) {   	
	        	
	        	if(line.contains("http")){
	        		temp = line.substring(line.indexOf("http"));
	        		alreadyDone.add(temp);
	        	}
	            
	        }

	        // Always close files.
	        bufferedReader.close();		
	        
	    }
	    catch(FileNotFoundException ex) {
	        System.err.println("Unable to open file '" + fileName + "'");
	        ex.printStackTrace();
	    }
	    catch(IOException ex) {
	        System.err.println("Error reading file '" + fileName + "'");					
	        // Or we could just do this: 
	        //ex.printStackTrace();
	    }
	}
	
	private void log(String log){
		 selfLogger.info(log);
	}
	
	private void errlog(String log){
		 errselfLogger.info(log);
	}
	
	public void getContent() throws Exception {

    	URL webpage = new URL(gotoSite);
        BufferedReader in = new BufferedReader(
        new InputStreamReader(webpage.openStream()));

        String inputLine;
        boolean flag;
        flag=false;
        while ((inputLine = in.readLine()) != null){
        	if(flag&&inputLine.contains("<tr bgcolor=")){
        		content.append("</tr>");
        		break;
        	}
        	if(flag)
        		content.append(inputLine);
        	if(inputLine.contains("Updation</b></th></tr>"))
        		flag = true;
        }
        in.close();
        
    }
	
	public void getList(){
		
		String name;
		String href;
		String date;
		
		StringBuilder temp;
		
		int start,end;
		int ts,te;
		
		temp = content;
		
		start = temp.indexOf("<tr>");
		
		while(start>-1){

			end = temp.indexOf("</tr>",start);
			
			ts = temp.indexOf("<td>",start)+"<td>".length();
			te = temp.indexOf("</td>",ts);
			
			name = temp.substring(ts, te).replace("</p>","").replace("<p>","").replace("<br />", "").replace(":","").replace(".","").replace("&amp;","and");
	
			ts = temp.indexOf("align=",te);//+"<align=\"center\">".length();
			ts = temp.indexOf(">",ts)+1;
			te = temp.indexOf("</td>",ts);
			
			name += temp.substring(ts, te).replace("&nbsp;","").replace("&amp;","and").replace("<br />", "").replace("-","").replace(",","").replace(" \\ ", "\\").trim()+".pdf";
			
			if((name.length()-name.lastIndexOf(File.separator))>255){
				name = name.replace(" ", "");
				name = name.substring(0,name.lastIndexOf(File.separator)+250);
				name+=".pdf";
			}			
			
			if(name.contains("Result")){ 
				for(String li : ResultGGSIPU.list){
					if(name.replace(" ","").contains(li.replace(" ",""))){
						ts = temp.indexOf("<a href=",te)+"<a href=".length()+1;
						te = temp.indexOf(">",ts)-1;
						
						href = mainSite+temp.substring(ts, te);
						
						ts = temp.indexOf("<td align=",te);
						ts = temp.indexOf(">",ts)+1;
						te = temp.indexOf("</td>",ts);
						
						date = temp.substring(ts, te);
						
						if(!THREADED){
								list.add(new PDFsaving(name, href, date));
						}else{
							Runnable r = new WebsiteCommunication(href,name,date);
							new Thread(r).start();				
						}
					}
				}
			}
			
			start = temp.indexOf("<tr>",end);

		}
		
	}
	
	 private void download(String fileURL, String destinationFile) throws IOException {
	        // File name that is being downloaded
	        //String downloadedFileName = fileURL.substring(fileURL.lastIndexOf("/")+1);
	         int first,second;
	        // Open connection to the file
	        URL url = new URL(fileURL);
	        InputStream is = url.openStream();
	        // Stream to the destionation file
	        FileOutputStream fos = new FileOutputStream(destinationFile);//WebsiteInteraction.pdfFolder + File.separator + downloadedFileName);
	        
	        // Read bytes from URL to the local file
	        byte[] buffer = new byte[4096];
	        int bytesRead = 0;
	         
	        System.out.println("Downloaded " + destinationFile);//downloadedFileName);
	        
	        int downtime=0;
	        while ((bytesRead = is.read(buffer)) != -1) {
	        	downtime++;
	        	if(downtime%100==0)
	        		System.out.print(".");
	            fos.write(buffer,0,bytesRead);
	        }
	        System.out.println("done!");
	  
	        // Close destination stream
	        fos.close();
	        // Close URL stream
	        is.close();
	    } 
	
	public void getPDF(){
		PDFsaving temp;
		String convertedFile;
		for(int i=0;i<list.size();i++){
			
			temp = list.get(i);
			convertedFile = null;
			try{
				URL url = new URL(temp.getURL());
				String pdf = pdfFolder+(temp.getName().replace(File.separator," "));
				File tmpfile = new File(pdf);
				if(alreadyDone.add(temp.getURL())){
					this.download(url.toString(),tmpfile.toString());
					this.log("Downloading "+temp.getURL());
					convertedFile = this.extractText(tmpfile);
					if(convertedFile!=null){
						try{
							File allFiles = new File(convertedFile);
							ResultGGSIPU.extractData(allFiles,false);
						}
						catch(Exception ex){
							ex.printStackTrace();
						}
					}
					//org.apache.commons.io.FileUtils.copyURLToFile(url,tmpfile);
//					if(!new File(pdf).exists()){
//						this.errlog("Error in getting "+temp.getURL());
//						ResultGGSIPU.errorList.add(temp.getName()+"|"+temp.getURL());
//					}
				}
			}catch(Exception ex){
				System.out.println(i);
				ex.printStackTrace();
			}
				
		}
	}
	
	
/*
	public void getPDF(String URL,String name,String Date){
		
		try{
			URL url = new URL(URL);
			String pdf = pdfFolder+name;
			File tmpfile = new File(pdf);
			if(alreadyDone.add(URL)){
				this.download(url.toString(),tmpfile.toString());
				//org.apache.commons.io.FileUtils.copyURLToFile(url,tmpfile);
				this.log("Downloading "+URL);
				if(!new File(pdf).exists()){
					this.errlog("Error in getting "+URL);
					ResultGGSIPU.errorList.add(name+"|"+URL);
				}
				this.extractText(tmpfile);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
*/	
	private String extractFromFile(File component){
		String args[] = new String[3];
		args[0] = "-html";
		if(component.isFile()){
			if(component.getName().endsWith(".pdf")){

				 args[1] = component.getAbsolutePath();
				 args[2] = component.getAbsolutePath().replace(".pdf", ".txt");
				 
				 try {
					System.out.println("Extracting "+args[1]);
					ExtractText.main(args);
					System.out.println("Extracted "+args[2]);
					//new File(args[1]).delete();
					return args[2];
				} catch (Exception e) {
					this.errlog("Error in converting "+args[1]+"\n\r"+e.getMessage());
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
			log("unknown file "+component.getName());
			
		}
		return null;
	}
	
	//ExtractText [OPTIONS] <inputfile> [Text file]
	public void extractText(){
		String mainDIR = pdfFolder;
		File components = new File(mainDIR);
		for(File component:components.listFiles()){
			this.extractFromFile(component);
		}
	}
	
	public String extractText(File component) {
	
		return this.extractFromFile(component);
		
	}

	@Override
	public void run() {
	//	this.getPDF(this.URL,this.NAME,this.DATE);
	}

}
