package resultsggsipu.subprogram;

import java.util.LinkedHashSet;

import resultsggsipu.datatypes.College;
import resultsggsipu.datatypes.Common;
import resultsggsipu.datatypes.Marks;
import resultsggsipu.datatypes.Papers;
import resultsggsipu.datatypes.Program;
import resultsggsipu.datatypes.Scheme;
import resultsggsipu.datatypes.Student;

public class DatabaseQuery {
	
	public boolean last;
	
	public DatabaseQuery() throws ClassNotFoundException{
		this.last = false;					//not used currently
	}
	
	private void send(String query){
		query = query.replaceAll("&amp;", "and");
		if(!query.trim().equals(new String("")))
			for(String oneQuery : query.split(";")){
				oneQuery+=";";
				DatabaseInteraction.addQuery(oneQuery);
			}
	}
	
	private void createTable(String newTable,String oldTable){
		String query;
		
		query = "CREATE TABLE IF NOT EXISTS "+newTable+" LIKE "+oldTable+";";
		
		this.send(query.toString());
	
	}
	
	
	/* table name for marks schemeID_______ 
	 * where ________ represents schemeID number
	 * 
	 * columns : paperid, papercode, papername, credit, type, mode, kind
	 * 				internal, external , total, passingmarks
	 * 
	 * 		internal , external and total should be varchar type
	 * 		credit maybe float (decimal length 2 and decimal 1) or varchar
	 * 	
	 * 
	 * plz have same column name for compatiblity with website
	 * */	
	public void addPaper(Papers paper){
		String table;
		int paperID;
		String paperCode;
		String paperName;
		float credit;
		String type;
		String mode;
		String kind;
		String minor;
		String major;
		String total;
		int passingMarks;
		Scheme scheme;
		String temp;
		StringBuilder query = new StringBuilder();
		
		table = "schemeID"+paper.getSchemeID();
		//table = "scheme";
		
		for(int i=0;i<paper.getSchemeLength();i++){
		
			
			if(i==0){
				
				this.createTable(table, "scheme");
		
			}
			
			scheme = paper.getScheme(i);

			paperID = scheme.getPaperID();
			paperCode = scheme.getPaperCode();
			paperName = scheme.getPaperName().replace("'", "`");
			credit = scheme.getCredits();
			type = scheme.getType();
			mode = scheme.getMode();
			kind = scheme.getKind();
			minor = scheme.getMinor();
			major = scheme.getMajor();
			total = scheme.getTotal();
			passingMarks = scheme.getPassingMarks();
			
			temp = " Insert into "+table+"(`paperid`, `papercode`, `papername`, `credit`, `type`, `mode`, `kind`,`internal`,`external` , `total`, `passingmarks`)	Values('"+paperID+"'"
					+ ",'"+paperCode+"','"+paperName+"','"+credit+"','"+type+"','"+mode+"','"+kind+"','"+minor+"','"+major+"','"+total+"','"+passingMarks+"');";
			//temp = " Insert into "+table+"(`paperid`, `papercode`, `papername`, `credit`, `type`, `mode`, `kind`,`internal`,`external` , `total`, `passingmarks`,`schemeid`)	Values('"+paperID+"'"
			//			+ ",'"+paperCode+"','"+paperName+"','"+credit+"','"+type+"','"+mode+"','"+kind+"','"+minor+"','"+major+"','"+total+"','"+passingMarks+"','"+paper.getSchemeID()+"');";
					
			query.append(temp);
				
		}

		this.send(query.toString());	
		
	}
	
	
	/* table name program
	 * 
	 * columns : programcode, programname
	 * 
	 * 
	 * plz have same column name for compatiblity with website
	 * 
	 * */	
	public void addProgram(Program program){
		String table;
		int code;
		String name;
		
		String temp;
		
		table = "program";
		
		code = program.code;
		name = program.name.replace("'", "`");
		
		temp = " Insert into "+table+"(`programcode`, `programname`, `unicol`) Values('"+code+"','"+name+"','"+code+" "+name+"')";

		this.send(temp.toString());		
		
	}
	
	
	/* table name college
	 * 
	 * columns : collegecode, collegename
	 * 
	 * 
	 * plz have same column name for compatiblity with website
	 * 
	 * */
	public void addCollege(College college){
		String table;
		int code;
		String name;
		
		String temp;
		
		table = "college";
		
		code = college.code;
		name = college.name.replace("'", "`");
		
		temp = " Insert into "+table+"(`collegecode`, `collegename`, `unicol`) Values('"+code+"','"+name+"','"+code+" "+name+"')";
			
		this.send(temp.toString());
		
	}
	
	/* table name studentdetails
	 * 
	 * columns : sid, rollnumber, name, emailid, schemeid, collegecode, programcode, batchyear
	 * 
	 * 		rollnumber, sid and schemeid can be varchar or some datatype equilant to long
	 * 		collegecode type must be same with college.collegecode
	 * 		programcode type must be same with program.programcode
	 * 
	 * plz have same column name for compatiblity with website
	 * 
	 * */
	public void addStudentDetails(Student student){
		String rollNumber;
		long sid;
		long schemeid;
		String name;
		int programcode;
		int collegecode;
		int batchyear;
		Common common;

		String temp;
		
		common = student.getCommon();
		
		rollNumber = student.getRollNumber();
		sid = student.getSID();
		schemeid = student.getSchemeID();
		name = student.getName().replace("'", "`");
		programcode = common.getProgramCode();
		collegecode = common.getCollegeCode();
		batchyear = common.getBatchYear();
		
		
		temp = " Insert into studentdetails(`sid`, `rollnumber`, `name`, `schemeid`, `collegecode`,`programcode`, `batchyear`) Values('"+sid+"'"
				+ ",'"+rollNumber+"','"+name+"','"+schemeid+"','"+collegecode+"','"+programcode+"','"+batchyear+"');";
		

		this.send(temp.toString());
		
	}
	
	/* table name for marks sid_______ 
	 * where ________ represents sid number
	 * 
	 * columns : paperid, internal, external , total, appearyear, credit, sem
	 * 
	 * 		internal , external and total should be varchar type
	 * 		credit maybe float (decimal length 2 and decimal 1) or varchar
	 * 		paperid must be same type with schemeid________ . paperid
	 * 
	 * plz have same column name for compatiblity with website
	 * */
	public void addMarks(Student student){
		int paperid;
		float credit;
		String internal;
		String external;
		String total;
		String appearyear;
		String sem;
		int count;
		
		Marks mark;
		
		count = student.getMarksLength();
		
		String temp;
		StringBuilder query;
		
		query = new StringBuilder();
		
		String table = "sid"+student.getSID();
		//String table = "sid";
			
		appearyear = student.getCommon().getAppearYear().replace("'", "`");
		sem = student.getCommon().getSem().replace("'", "`");
		
		for(int i=0;i<count;i++){
			
			if(i==0){
				this.createTable(table, "sid");				
			}
			
			mark = student.getmark(i);
			paperid = mark.getpaperid();
			credit = mark.getcredits();
			internal = this.getmarks(mark.getinternal());
			external = this.getmarks(mark.getexternal());
			total = this.getmarks(mark.gettotal());
			

			temp =  " Insert into "+table+"(`paperid`, `internal`, `external`, `total`, `appearyear`, `credit`, `sem`, `unicol`) Values('"+paperid+"'"
					+ ",'"+internal+"','"+external+"','"+total+"','"+appearyear+"','"+credit+"','"+sem+"','"+paperid+appearyear.replace("REGULAR","").replace("REAPPEAR","").trim()+"');";

			//temp =  " Insert into "+table+"(`paperid`, `internal`, `external`, `total`, `appearyear`, `credit`, `sem`, `unicol`,`sidvalue`) Values('"+paperid+"'"
			//		+ ",'"+internal+"','"+external+"','"+total+"','"+appearyear+"','"+credit+"','"+sem+"','"+paperid+appearyear.replace("REGULAR","").replace("REAPPEAR","").trim()+"','"+student.getSID()+"');";

			query.append(temp);
		}
		

		this.send(query.toString());
		
	}
	
	private String getmarks(int marks){
		if(marks>-1)
			return marks+"";
		else if(marks==-1)
			return "Absent";
		else if(marks==-2)
			return "Cancelled";
		else if(marks==-3)
			return "Detained";
		else if(marks==-4)
			return "Result Later";
		return "";
	}
}
