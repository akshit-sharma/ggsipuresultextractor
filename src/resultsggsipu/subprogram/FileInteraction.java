package resultsggsipu.subprogram;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import resultsggsipu.datatypes.Student;
import resultsggsipu.datatypes.Wrapper;

public class FileInteraction extends Thread{
	
	SchemeOfExaminations soe;
	ResultTabulationSheet rts;
	String fileName;
	LinkedList<Thread> threadsCreated;
	boolean mutlithreaded;
	int mode;
	Wrapper wrapper;
	LinkedList<Student> students;
	
	
	private FileInteraction(){
		this.wrapper = new Wrapper();
		this.students = new LinkedList<Student>();
	}
	
	public FileInteraction(String fileName) {
		this();
		this.fileName = fileName;
		this.threadsCreated = new LinkedList<Thread>();
		this.mutlithreaded = false;
		mode = 0;
	}	
	
	public FileInteraction(String fileName,boolean MultiThreaded) {
		this(fileName);
		this.mutlithreaded = MultiThreaded;
	}
	
	public void run (){
		try {
			this.startReading(fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}
	
	private void sendScheme(StringBuilder content){
		soe = new SchemeOfExaminations(wrapper);
		if(soe.setContent(content)){
			if(this.mutlithreaded){
				Thread soeThread = new Thread(soe);
				soeThread.start();
				threadsCreated.add(soeThread);
			}else{
				soe.extract();
			}
		}
	}
	
	private void sendResult(StringBuilder content) throws Exception{
		rts = new ResultTabulationSheet(students);
		if(rts.setContent(content)){
			if(this.mutlithreaded){
				Thread rtsThread = new Thread(rts);
				rtsThread.start();
				threadsCreated.add(rtsThread);
			}else{
				rts.extract(this.wrapper.getPapers());
			}
		}
	}

	public void startReading(String fileName) throws Exception{
		int SchemeOfExamination;
		int ResultTabulationSheet;
		int schemeresult;
	    // This will reference one line at a time
		String line;
	    StringBuilder content;
		
	    try {
	        // FileReader reads text files in the default encoding.
	        FileReader fileReader = new FileReader(fileName);
	
	        // Always wrap FileReader in BufferedReader.
	        BufferedReader bufferedReader = new BufferedReader(fileReader);
	
	        SchemeOfExamination=0;
	        ResultTabulationSheet=0;
	        schemeresult =0;
	        content = new StringBuilder();
	        
	        while((line = bufferedReader.readLine()) != null) {
	        	if(line.contains(":-")){
	        	 System.out.println("here");
	        	 break;
	        	}
	            line = org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(line);
	           
	        	if(schemeresult==1){
	        		if(line.trim().equals(""))
	        			content.append(" ");
	        		else if(!line.startsWith("<"))
	        			content.append("</p><p>"+line);
	        		else 
	        			content.append(line+" ");
	        	}
	        	else
	        		content.append(line+" ");
	        	
	            if(line.contains("(SCHEME OF EXAMINATIONS)")){
	        		schemeresult = 1;
	        		SchemeOfExamination = content.indexOf("(SCHEME OF EXAMINATIONS)");
	        	}else if(line.contains("RESULT TABULATION SHEET")){
	        		schemeresult = 2;
	        		ResultTabulationSheet = content.indexOf("RESULT TABULATION SHEET");
	        	}else if(line.contains("</div></div>")){
	        		/* is scheme of examinations */
	        		if(schemeresult==1){						
	        			this.sendScheme(content);
	        			content = new StringBuilder();
	        		}
	        		/* is result sheet */
	        		else if(schemeresult==2){
	        			this.sendResult(content);
	        			content = new StringBuilder();
	        		}
	        		schemeresult = 0;
	        	}
	        }

	        // Always close files.
	        bufferedReader.close();			
	        
	        if(this.mutlithreaded){
			       for(Thread thread:threadsCreated){
			    		   try {
							thread.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			       }
	        }
	        
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
}
