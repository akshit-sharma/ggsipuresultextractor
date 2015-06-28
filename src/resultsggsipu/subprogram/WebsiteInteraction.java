package resultsggsipu.subprogram;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.pdfbox.ExtractText;

import resultsggsipu.ResultGGSIPU;
import resultsggsipu.datatypes.PDFsaving;

public class WebsiteInteraction {

	/*
	String PDFconverter;
	static public String pdfFolder;
	static Logger selfLogger;
	static LinkedHashSet<String> alreadyDone;
	static Logger errselfLogger;
	public boolean THREADED;
	*/
	LinkedList<PDFsaving> list;
	StringBuilder content;
	String URL;
	String NAME;
	String DATE;
	static int status = 1;  						//0 for production, 1 for debuging, 2 for testing, 3 for testing and debuging
	protected OutputStream loggingFile,errLoggingFile;
	static String mainSite = "http://ggsipuresults.nic.in/ipu/results/";
	static String projectDir;
	static String pdfFolder;
	boolean production,debugging,testing,testinganddebugging = false;
	
	public WebsiteInteraction(){
		projectDir = System.getProperty("java.io.tmpdir")+File.separator+"resultsggspiu";
		pdfFolder = projectDir+File.separator+"pdf";
		WebsiteInteraction.status = 0;
		if(status==0)
			production = true;
		else if(status==1)
			debugging = true;
		else if(status==2)
			testing = true;
		else if(status==3){
			debugging = true;
			testing = true;
		}
		this.makeLogFile();
	}
	
	public WebsiteInteraction(int stats) {
		this();
		WebsiteInteraction.status = stats;
		if(status==0)
			production = true;
		else if(status==1)
			debugging = true;
		else if(status==2)
			testing = true;
		else if(status==3){
			debugging = true;
			testing = true;
		}
	}
	
	public WebsiteInteraction(String href,String name,String date) {
		this.URL = href;
		this.NAME = name;
		this.DATE = date;
	}

	private void makeLogFile(){
		try{
  	      File file1 = new File(projectDir+File.separator+"logging");
	      File file2 = new File(projectDir+File.separator+"errLogging");
	      
	      file1.createNewFile();
	      file2.createNewFile();

          loggingFile = new FileOutputStream(file1.getAbsolutePath());
          errLoggingFile = new FileOutputStream(file2.getAbsolutePath());
		}catch(IOException x){
		    System.err.format("IOException in creating file: %s%n", x);		
		}
	}
	
	protected void writeFile(Path file,String toWrite) {
		Charset charset = Charset.forName("US-ASCII");
		try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
		    writer.write(toWrite, 0, toWrite.length());
		} catch (IOException x) {
		    System.err.format("IOException in writing in file: %s%n", x);
		}
	}
	
	private void closeFile(){
		try {
			loggingFile.close();
			errLoggingFile.close();
		} catch (IOException x) {
		    System.err.format("IOException in closing files: %s%n", x);
		}
	}
	
	public void sendRequest(){
		String page;
		
		if(debugging)
			page = mainSite+"results060814main.htm";
		else
			page = mainSite+"resultsmain.htm";
				
		try{
			URL webpage = new URL(page);
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
		}catch(Exception x){
		    System.err.format("Exception: %s%n", x);
		}
		this.extractList();
	}
	
	private void extractList(){
		
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
					if(name.replace(" ","").startsWith(li.replace(" ",""))){
						ts = temp.indexOf("<a href=",te)+"<a href=".length()+1;
						te = temp.indexOf(">",ts)-1;
						
						href = mainSite+temp.substring(ts, te);
						
						ts = temp.indexOf("<td align=",te);
						ts = temp.indexOf(">",ts)+1;
						te = temp.indexOf("</td>",ts);
						
						date = temp.substring(ts, te);
						
						Runnable r = new WebsiteDownload(href,name,date);
						new Thread(r).start();		
					}
				}
			}
			
			start = temp.indexOf("<tr>",end);

		}
		
	}

	
	
	/*

	public void getContent() throws Exception {

    	URL webpage = new URL(mainSite+"results060814main.htm");//"resultsmain.htm");
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
					if(name.replace(" ","").startsWith(li.replace(" ",""))){
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
	
	public void getPDF(){
		PDFsaving temp;
		
		for(int i=0;i<list.size();i++){
			
			temp = list.get(i);
			
			try{
				URL url = new URL(temp.getURL());
				String pdf = pdfFolder+(temp.getName().replace(File.separator," "));
				File tmpfile = new File(pdf);
				if(alreadyDone.add(temp.getURL())){
					org.apache.commons.io.FileUtils.copyURLToFile(url,tmpfile);
					this.log("Downloading "+temp.getURL());
					if(!new File(pdf).exists()){
						this.errlog("Error in getting "+temp.getURL());
						ResultGGSIPU.errorList.add(temp.getName()+"|"+temp.getURL());
					}
				}
			}catch(Exception ex){
				System.out.println(i);
				ex.printStackTrace();
			}
				
		}
	}
	

	public void getPDF(String URL,String name,String Date){
		
		try{
			URL url = new URL(URL);
			String pdf = pdfFolder+name;
			File tmpfile = new File(pdf);
			if(alreadyDone.add(URL)){
				org.apache.commons.io.FileUtils.copyURLToFile(url,tmpfile);
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
		return false;
	}
	
	//ExtractText [OPTIONS] <inputfile> [Text file]
	public void extractText(){
		String mainDIR = pdfFolder;
		File components = new File(mainDIR);
		for(File component:components.listFiles()){
			this.extractFromFile(component);
		}
	}
	
	public boolean extractText(File component) {
	
		return this.extractFromFile(component);
		
	}

	@Override
	public void run() {
		this.getPDF(this.URL,this.NAME,this.DATE);
	}
	*/
	
}
