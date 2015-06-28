package resultsggsipu.subprogram;

import resultsggsipu.datatypes.College;
import resultsggsipu.datatypes.Papers;
import resultsggsipu.datatypes.Program;
import resultsggsipu.datatypes.Scheme;
import resultsggsipu.datatypes.Wrapper;

public class SchemeOfExaminations extends Thread{
		
	StringBuilder sb;
	Wrapper wrapper;

	Papers paper;// = wrapper.getLastPaper();
	static String prepaper = null;
	
	DatabaseQuery di;
	
	public SchemeOfExaminations(Wrapper wrapper) {
		this.wrapper = wrapper;
		try {
			di = new DatabaseQuery();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* Database Interaction */
	private synchronized void insertIntoDatabase(){

			/* call contructor of Database Interaction class */
			/* pass the object in parameters */
			this.makePaperTable(di);
			this.makeCollegeTable(di);
			this.makeProgramTable(di);		

	}

	/* for making paper Table */
	private void makePaperTable(DatabaseQuery di){
		Papers paper;
		for(int i=0;i<wrapper.getPaperLength();i++){
			/* paper contains further details */
			paper = wrapper.getPaper(i);
			/* call corresponding method */
			di.addPaper(paper);
		}			
	}

	/* for making program Table */
	private void makeProgramTable(DatabaseQuery di){
		Program program;
		for(int i=0;i<wrapper.getProgramLength();i++){
			/* program contains further details */
			program = wrapper.getProgram(i);
			/* call corresponding method */
			di.addProgram(program);
		}			
	}

	/* for making college Table */
	private void makeCollegeTable(DatabaseQuery di){
		College college;
		for(int i=0;i<wrapper.getCollegeLength();i++){
			/* college contains further details */
			college = wrapper.getCollege(i);
			/* call corresponding method */
			di.addCollege(college);
		}			
	}
	
	public boolean setContent(StringBuilder content) {
		try{
			this.sb = new StringBuilder(content.substring(0, content.indexOf("</div></div>")));
		}catch(Exception ex){
			return false;
		}
		return true;
	}
	
	private StringBuilder extractHeaders(StringBuilder sb){
		int start;
		int end;
		String data,str;
		College college = new College();
		Program program = new Program();
		
		str = "Scheme of Programme Code: ";
		start = sb.indexOf(str)+str.length();
		str = "Programme Name: ";
		end = sb.indexOf(str,start);
		
		data = sb.substring(start, end).trim();
		
		if(data.equalsIgnoreCase("191"))
			System.out.println("from here");
		
		program.code = Integer.parseInt(data);

		str = "Programme Name: ";
		start = sb.indexOf(str)+str.length();
		str = "SchemeID:";
		end = sb.indexOf(str,start);
		
		data = sb.substring(start, end).trim();
		program.name = data;

		str = "Institution Code: ";
		start = sb.indexOf(str)+str.length();
		str = "Institution:";
		end = sb.indexOf(str,start);
		
		data = sb.substring(start, end).trim();
		
		college.code = Integer.parseInt(data);

		str = "Institution: ";
		start = sb.indexOf(str)+str.length();
		str = "</p>";
		end = sb.indexOf(str,start);
		
		data = sb.substring(start, end).replace("</b>","").trim();
		
		college.name = data;
		
		wrapper.addCollege(college);
		wrapper.addProgram(program);

		return new StringBuilder(sb.substring(sb.indexOf("<p>",end+2)));
	}
	
	private int hasNextScheme(StringBuilder check){
		String temp;
		int condition;
		int status;
		
		status = 1;
		try{
			temp = "<p><b>";
			if(check.indexOf(temp)!=-1){
				 condition = check.substring(check.indexOf(temp)+temp.length()).indexOf("S.No.");
				if(condition==-1){
					Integer.parseInt(check.substring(check.indexOf(temp)+temp.length(),check.indexOf(temp)+temp.length()+2));
				}
				else{
					return 2;
				}
			}
			else if(check.indexOf("<p>")!=-1){
				temp = "<p>";
				 condition = check.substring(check.indexOf(temp)+temp.length()).indexOf("S.No.");
					if(condition==-1){
						Integer.parseInt(check.substring(check.indexOf(temp)+temp.length(),check.indexOf(temp)+temp.length()+2));
					}
					else{
						return 2;
					}
			}
			else {
				condition = check.indexOf("S.No.");
				if(condition==-1){
					Integer.parseInt(check.substring(check.indexOf(temp)+temp.length(),check.indexOf(temp)+temp.length()+2));
				}
				else{
					return 2;
				}
			}
			
		}catch(NullPointerException ex){
			status = 0;
		}catch (NumberFormatException ex) {
			status = 0;
		}
		return status;
	}
	
	private StringBuilder getNextScheme(StringBuilder sb){		
		if(sb.substring(0,10).contains("<p><b>")){
			sb = new StringBuilder(sb.substring(sb.lastIndexOf("Marks")+5));
		}
		else if(sb.substring(0,10).contains("<p>")){
			sb = new StringBuilder(sb.substring(sb.lastIndexOf("Marks")+5));
		}
		else{ 
			sb = new StringBuilder(sb.substring(sb.lastIndexOf("Marks")+5));
		}
		
		if(sb.indexOf("<p><b>")==-1||((sb.indexOf("<p>")<sb.indexOf("<p><b>"))&&sb.indexOf("<p><b>")!=-1)){
			sb = new StringBuilder(sb.substring(sb.indexOf("<p>")));
		}
		else if(sb.indexOf("<p>")==-1||((sb.indexOf("<p><b>")<sb.indexOf("<p>"))&&sb.indexOf("<p>")!=-1)){
			sb = new StringBuilder(sb.substring(sb.indexOf("<p><b>")));
		}
		
		return sb;
	}
	
	private StringBuilder extractScheme(StringBuilder content){
		StringBuilder data;
		int next;
		int temp;
		int end;
		int start;
		String tmpdata;
				
		int paperID;
		String paperCode;
		String paperName;
		float credits;
		String major,minor,total;
		int passing;
		int type,kind;
		int mode;
		
		
		if(content.substring(0,10).indexOf("</b></p>")!=-1){
			content = new StringBuilder(content.substring(content.substring(0,10).indexOf("</b></p>")+"</b></p>".length()));			
		}
		
		if(content.substring(0,10).indexOf("<p><b>")!=-1){
			next = content.indexOf("<p><b>",2);
			end = content.indexOf("</b></p>");			
		}else{
			next = content.indexOf("<p>",2);
			end = content.indexOf("</p>");
		}
		
		data = new StringBuilder(content.substring(0, end));
		
		start = data.indexOf(" ",2);
		end = data.indexOf(" ",start+1);
	
		String last3digit;
		
		tmpdata = data.substring(start, end).trim();		//contains paperID
		paperID = Integer.parseInt(tmpdata);
		start = end;
		
		last3digit = tmpdata.substring(tmpdata.length()-3);

		end = data.indexOf(" ",start+1);
		
		tmpdata = data.substring(start, end).trim();		//contains paperCode
		
		if(tmpdata.contains(last3digit)){
			tmpdata = tmpdata.substring(0,tmpdata.indexOf(last3digit)+3);
			end = data.indexOf(tmpdata)+tmpdata.length();
		}
		
		paperCode = tmpdata;
		start = end;
		
		/* for starting of credits */
		end = data.indexOf(" UES ",start);
		if(end == -1)
			end = data.indexOf(" NUES ",start);
		
		{
		int tend = end;
		String tmprydata = new StringBuilder(data.substring(0, end)).reverse().substring(0,end-start).trim();
		for(int i=0;i<10;i++){
			if(((tend>tmprydata.length())||(tend>tmprydata.indexOf(i+"")))&&(tmprydata.indexOf(i+"")!=-1)){
				tend = tmprydata.indexOf(i+"");
			}
		}
		tend = tmprydata.indexOf(" ",tend);
		end = end - tend;
		}
		//if(data.toString().contains("LINUX AND WIN32 PROGRAMMING  ")){
			//end = data.indexOf("LINUX AND WIN32 PROGRAMMING  ")+"LINUX AND WIN32 PROGRAMMING  ".length();
		//}else{
		//	for(int i=0;i<10;i++)
		//		if((data.indexOf(i+"",start)<end) && (data.indexOf(i+"",start)!=-1)){
		//			end = data.indexOf(i+"",start);
		//		}
		//}
					
		tmpdata = data.substring(start, end); 			//contains paperName
		paperName = tmpdata.replaceAll("&amp;", "and").trim();
		
		start = end;
		end = data.indexOf(" ",start+1);
		
		tmpdata = data.substring(start, end).trim();		//contains credits
		credits = Float.parseFloat(tmpdata);
		
		mode=0;

		if(data.indexOf("THEORY")==-1)
			type = 1;
		else if(data.indexOf("PRACTICAL")==-1)
			type = 0;
		else if(data.indexOf("THEORY")>data.indexOf("PRACTICAL"))
			type = 1;
		else //if(data.indexOf("THEORY")<data.indexOf("PRACTICAL"))
			type = 0;


		if(data.indexOf("COMPULSORY")==-1){
			mode = 1;
			start = data.indexOf("ELECTIVE")+"ELECTIVE".length();
		}
		else if(data.indexOf("ELECTIVE")==-1){
			mode = 0;
			start = data.indexOf("COMPULSORY")+"COMPULSORY".length();
		}
		else if(data.indexOf("COMPULSORY")>data.indexOf("ELECTIVE")){
			mode = 1;
			start = data.indexOf("ELECTIVE")+"ELECTIVE".length();
		}
		else //if(data.indexOf("COMPULSORY")<data.indexOf("ELECTIVE"))
		{
			mode = 0;
			start = data.indexOf("COMPULSORY")+"COMPULSORY".length();
		}

		if(data.indexOf("MANDATORY")==-1){
			kind = 1;
			start = data.indexOf("DROPPABLE")+"DROPPABLE".length();
		}
		else if(data.indexOf("DROPPABLE")==-1){
			kind = 0;
			start = data.indexOf("MANDATORY")+"MANDATORY".length();
		}
		else if(data.indexOf("MANDATORY")>data.indexOf("DROPPABLE")){
			kind = 1;
			start = data.indexOf("DROPPABLE")+"DROPPABLE".length();
		}
		else //if(data.indexOf("MANDATORY")<data.indexOf("DROPPABLE"))
		{
			kind = 0;
			start = data.indexOf("MANDATORY")+"MANDATORY".length();
		}

		
		/* start from number   minor    */
		start=end;
		end = data.length();
		temp = start;
		for(int i=0;i<10;i++)
			if((data.indexOf(i+"",start)<end) && (data.indexOf(i+"",start)!=-1))
				end = data.indexOf(i+"",start);
		start = end;
		if((data.indexOf("--",temp)<start)&&(data.indexOf("--",temp)!=-1)){
			start = data.indexOf("--",temp);
			end = data.indexOf(" ",start);
		}else{
			end = data.indexOf(" ", start);
		}
		
		tmpdata = data.substring(start,end);
		minor = tmpdata;
		
		/* start from number   major    */
		start=end;
		end = data.length();
		temp = start;
		for(int i=0;i<10;i++)
			if((data.indexOf(i+"",start)<end) && (data.indexOf(i+"",start)!=-1))
				end = data.indexOf(i+"",start);
		start = end;
		if((data.indexOf("--",temp)<start)&&(data.indexOf("--",temp)!=-1)){
			start = data.indexOf("--",temp);
			end = data.indexOf(" ",start);
		}else{
			end = data.indexOf(" ", start);
		}
		
		tmpdata = data.substring(start,end);
		major = tmpdata;
		
		/* start from number   total    */
		start=end;
		end = data.length();
		temp = start;
		for(int i=0;i<10;i++)
			if((data.indexOf(i+"",start)<end) && (data.indexOf(i+"",start)!=-1))
				end = data.indexOf(i+"",start);
		start = end;
		if((data.indexOf("--",temp)<start)&&(data.indexOf("--",temp)!=-1)){
			start = data.indexOf("--",temp);
			end = data.indexOf(" ",start);
		}else{
			end = data.indexOf(" ", start);
		}
		
	
		tmpdata = data.substring(start,end);
		total = tmpdata;
		
		/* start from number   passing    */
		start=end;
		end = data.length();
		temp = start;
		for(int i=0;i<10;i++)
			if((data.indexOf(i+"",start)<end) && (data.indexOf(i+"",start)!=-1))
				end = data.indexOf(i+"",start);
		start = end;
		if((data.indexOf("--",temp)<start)&&(data.indexOf("--",temp)!=-1)){
			start = data.indexOf("--",temp);
			end = data.indexOf(" ",start);
		}else{
			end = data.indexOf(" ", start);
		}
		
		tmpdata = data.substring(start,end);
		if(tmpdata.equals(new String("--")))
			passing = 0;
		else
			passing = Integer.parseInt(tmpdata);

	
		Scheme scheme = new Scheme(paperID, minor, major, total, passing);
		
		scheme.setPaperCode(paperCode);
		scheme.setPaperName(paperName);
		scheme.setCredits(credits);
		scheme.setMode(mode);
		scheme.setKind(kind);
		scheme.setType(type);
		
		paper.addScheme(scheme);
			
		if(next!=-1)
			return new StringBuilder(content.substring(next));
		else
			return null;
	}
	
	private boolean hasDuplicateScheme(StringBuilder sb){
		boolean status = false;
		
		//  increases computation time but should give all results
		//   => not only increasing computation time but also increases resources required
		//	 =>
		//currently having one problem in 
		//Mercy Result of BTech(BT), Dec 2013
		//schemeid followed in next page
		//not expected
		long schemetmp;
		String str,data;
		int start,end;

		str = "SchemeID:";
		start = sb.indexOf(str)+str.length();
		str = "Sem./Year:";
		end = sb.indexOf(str,start);
		
		data = sb.substring(start, end).trim();
		
		schemetmp = Long.parseLong(data);
		
		if(prepaper==(schemetmp+"")){
			status = false;
			
		}else if(prepaper==null){
			prepaper = schemetmp+"";
			paper = new Papers(Long.parseLong(data));
			wrapper.addPaper(paper);	
	}
		else{
			if(wrapper.containsSchemeID(schemetmp))
				status = true;
			else{
				paper = new Papers(Long.parseLong(data));
				wrapper.addPaper(paper);	
			}
		}
		
		return status;
	}
	
	public void extract(){
		boolean duplicateScheme = hasDuplicateScheme(sb);
		sb = extractHeaders(sb);
		if(!duplicateScheme){
			while(this.hasNextScheme(sb)!=0){
				if(this.hasNextScheme(sb)==2){
					sb = this.getNextScheme(sb);
				}
				if(this.hasNextScheme(sb)==1){
					sb = this.extractScheme(sb);
				}
			}
			this.insertIntoDatabase();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.extract();
	}	
	
}
