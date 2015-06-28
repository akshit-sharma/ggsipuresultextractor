package resultsggsipu.subprogram;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Logging {
	
	static String tempdir = System.getProperty("java.io.tmpdir").replace(";", "");
	public static String mainfolder = tempdir.endsWith(File.separator) ? tempdir+"resultsggspiu" : tempdir+File.separator+"resultsggspiu";
	static String logfile;
	static Logger logger;
	static int debug=0;
	//static String ;
	
	public static void initialize(){
		logfile = mainfolder+File.separator+"InformationExtractor.log";
		
	    Logger logger = Logger.getLogger("MyLog");  
	    FileHandler fh;  

	    try {  

	        // This block configure the logger with handler and formatter  
	        fh = new FileHandler(logfile);  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  

	        // the following statement is used to log any messages  
	        logger.info("My first log");  

	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } 
		
	}
	
	 public static void log(String log){
		 logger.info(log);
	 }
	 
}
