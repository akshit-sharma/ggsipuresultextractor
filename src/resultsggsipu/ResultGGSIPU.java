package resultsggsipu;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.JDKDSASigner.stdDSA;

import resultsggsipu.subprogram.DatabaseInteraction;
import resultsggsipu.subprogram.FileInteraction;
import resultsggsipu.subprogram.Logging;
import resultsggsipu.subprogram.WebsiteCommunication;
import resultsggsipu.subprogram.WebsiteInteraction;


public class ResultGGSIPU {
	
	public static LinkedList<String> errorList = new LinkedList<String>();
	public static String [] list = {""};
	public static String [] blocklist = {"Result of BTech(IT), USS, Dec 2013"};
	
	static private void extracting(File file,boolean multiThreaded) throws Exception{
		
		if(file.isFile()){
			if(file.toString().endsWith(".txt")){
				for(String li : list){
					for(String bl : blocklist){
						if(!file.getName().contains(bl)){
							if(file.getName().trim().startsWith(li.trim())){
								String fileName = file.getAbsolutePath();
								if(fileName.contains("Result of BTech(IT), USS, Dec 2013"))
									System.out.println("start here--------------");
								System.out.println(fileName);
								try{
									FileInteraction fi = new FileInteraction(fileName,multiThreaded);
									if(multiThreaded)
										fi.start();
									else
										fi.startReading(fileName);

									DatabaseInteraction di = new DatabaseInteraction();
									di.sendData();
									if(di.isNewData()){
										di.execLastQuery();
									}
	
									if(fileName.contains("Result of BTech(IT), USS, Dec 2013"))
										System.out.println("-----------------end here ");
								}catch(Exception ex){
									ex.printStackTrace();
								}finally {
									System.out.println("Deleting "+file.toString());
									file.delete();
								}
							}
						}
					}
				}
			}
		 }
		
	}
	
	static public void extractData(File files,boolean multiThreaded) throws Exception{
		if(files.isFile())
			extracting(files,multiThreaded);
		else
			for(File file:files.listFiles()){
				if(file.isFile())
					extracting(files,multiThreaded);
				else if(file.isDirectory())
					extractData(file, multiThreaded);
			}
	}
	
	/*
	public static void main(String [] args){

		DatabaseInteraction.URL += args[0];	
		
		WebsiteInteraction wi = new WebsiteInteraction(Integer.parseInt(args[1]));
		wi.sendRequest();
		
	}
	*/
	public static void main(String [] args){
		
		boolean multiThreaded = false;

		new File(Logging.mainfolder).mkdirs();
		System.out.println(Logging.mainfolder);

		DatabaseInteraction.URL += args[0];	
		
		try{
			WebsiteCommunication wc =  new WebsiteCommunication(multiThreaded,args[1]);
			
			wc.getContent();
			wc.getList();
			if(!wc.THREADED){
				wc.getPDF();
				wc.extractText();
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		/*	
		try{
			
		DatabaseInteraction.URL += args[0];	
		
		File allFiles = new File(WebsiteCommunication.pdfFolder);
		
		extractData(allFiles,multiThreaded);
		
		
		DatabaseInteraction di = new DatabaseInteraction();
		
		di.sendData();
		
		if(di.isNewData()){
			di.execLastQuery();
		}
		*/
/*
		File file = new File(Logging.mainfolder+File.separator+"QueryFile.txt");

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		
		LinkedHashSet<String> query = DatabaseInteraction.debugTesting();

		for(String q:query){
			bw.write(q);
		}
		bw.close();
*/
/*		
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
*/		
		System.out.println("Done");
		System.exit(0);
	
	}
	
	
	
}
