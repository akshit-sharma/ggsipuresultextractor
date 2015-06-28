package resultsggsipu.subprogram;

import java.util.LinkedList;

import resultsggsipu.datatypes.Common;
import resultsggsipu.datatypes.Marks;
import resultsggsipu.datatypes.Papers;
import resultsggsipu.datatypes.Scheme;
import resultsggsipu.datatypes.Student;

public class ResultTabulationSheet extends Thread{

	LinkedList<Student> students;
	LinkedList<Papers> paper;
	StringBuilder sb;
	Common common;
	DatabaseQuery di;
	
	public ResultTabulationSheet(LinkedList<Student> students) {
		this.students = students;
		try {
			di = new DatabaseQuery();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*  for interaction with database  */
	private synchronized void insertIntoDatabase(Student student){
			

			/* rollnumber, name, sid and schemeid all are contained in student */
			
			/* Common contains details like 
			 *  program code,batch year,appearing year,
			 *  college code and sem */
				
			di.addStudentDetails(student);
			
			/* contains all marks of particular student */
			di.addMarks(student);
		
				
	}

	public boolean setContent(StringBuilder content) {
		try{
			this.sb = new StringBuilder(content.substring(0, content.indexOf("</div></div>")));
		}catch(Exception ex){
			return false;
		}
		return true;
	}
	
	private Marks getMarks(int paperid, float credit, String internal, String external, String total){
		
		Marks mark = new Marks(paperid, credit);
		
		mark.setMarks(internal, external, total);
		
		return mark;
	
	}
	
	private void setMarks (Marks mark, Student student) {
		
		student.addMarks(mark);
		
	}

	private void addStudent(Student student){
		this.students.add(student);
	}
	
	private boolean containsMarks(StringBuilder sb){
		
		boolean status = true;
		String str;
		int start,end;

		str = "<p>";
		if(sb.toString().contains("<p><b>"))
			str = "<p><b>";
		
		start = sb.indexOf(str)+str.length();
		end = sb.indexOf("(",start);
		
		try{
			Integer.parseInt(sb.substring(start, end));
		}catch(Exception ex){
			status = false;
		}
		
		return status;
		
	}
	
	private Student createStudent(String rollnumber, String name, long sid, long schemeid){
		Student student = new Student(rollnumber, name, sid, schemeid);
		student.setCommon(common);
		return student;
	}

	private boolean containsStudent(StringBuilder sb){
		boolean status = false;
		
		if(sb.indexOf("SID:")!=-1)
			status = true;
			
		return status;
	}
	
	private boolean containsCommon(){
		int contain,end;
		
		contain = sb.indexOf("Result of Programme Code: ");
		
		if(contain!=-1){
			return true;
		}else{
			contain = sb.indexOf("Result Summary of Programme Code: ");
			
			if(contain!=-1)
				return true;
		}
		
		return false;
	}
	
	private void setCommon(){
		
		int start,end;
		String str;
		Common common = new Common();
		
		str = "Result of Programme Code:";
		
		if(sb.indexOf(str)==-1)
			str = "Result Summary of Programme Code:";
		
		start = sb.indexOf(str)+str.length();
		end = sb.indexOf("Programme Name:");
		
		str = sb.substring(start, end).trim();		//contains program code
		common.setProgramCode(Integer.parseInt(str));
		
		
		str = "Sem./Year:";
		start = sb.indexOf(str)+str.length();
		end = sb.indexOf("Batch:");
		
		str = sb.substring(start, end).trim();		//contains sem

		common.setSem(str);
		
		str = "Batch:";
		start = sb.indexOf(str)+str.length();
		end = sb.indexOf("Examination:");
		
		str = sb.substring(start, end).trim();		//contains batch
		common.setBatchYear(Integer.parseInt(str));
		
		
		str = "Examination:";
		start = sb.indexOf(str,end)+str.length();

		if((sb.indexOf("Institution Code:",start)<sb.indexOf("</p> <p>",start))||(sb.indexOf("</p> <p>",start)==-1))
			end = sb.indexOf("Institution Code:",start);
		if((sb.indexOf("</p> <p>",start)<sb.indexOf("Institution Code:",start))||(sb.indexOf("Institution Code:",start)==-1))
			end = sb.indexOf("</p> <p>",start);
		
		str = sb.substring(start, end).trim().replace("</b>","");
		
		if(str.contains("S.No."))
			str = str.substring(0,str.indexOf("S.No.")).trim();
		
		if(str.contains("PROVISIONAL")){
			str = str.replace("PROVISIONAL"," ").replace("ADMIT"," ").replace("CARD"," ").trim();
			if(str.endsWith(",")){
				str = str.trim()+",,,";
				str.replace(",,,,","").trim();
			}
		}
		
		
		common.setAppearYear(str);
		
		
		/*
		if((sb.indexOf("-",end)!=-1)||(sb.indexOf("-",end)>sb.indexOf(",",end)))
			str = ",";
		else if((sb.indexOf(",",end)!=-1)||(sb.indexOf("-",end)<sb.indexOf(",",end)))
			str = "-";
		
		str = sb.substring(start, end).trim();		//contains AppearYear
		
		
		if(str.contains("S.No."))
			if(end>str.indexOf("S.No."))
				str = str.substring(start,str.indexOf("S.No.")-start);
		common.setAppearYear(Integer.parseInt(str));
		*/

		str = "Institution Code:";
		start = sb.indexOf(str)+str.length();
		end = sb.indexOf("Institution:");
		
		str = sb.substring(start, end).trim();		//contains CollegeCode
		common.setCollegeCode(Integer.parseInt(str));
		
		this.common = common;
		
	}
	
	
	private Marks extractMarks(Student student){
		int paperid;
		float credit;
		String internal,external,total;
		int start,end;
		String str;
		int mode = 0;
		if(this.sb.toString().contains("<p><b>"))
			mode = 1;
		/*
		<p>99152(1)
		</p>
		<p> 33  44 
		</p>
		<p> 77 
		</p>
		*/
		str = null;
		if(mode==0)
		    str = "<p>";
		else if(mode==1)
			str = "<p><b>";
		start = this.sb.indexOf(str)+str.length();
		end = this.sb.indexOf("(");
		try{
			paperid = Integer.parseInt(sb.substring(start, end));
		}catch(Exception ex){
			return null;
		}
		start = end + 1;
		end = this.sb.indexOf(")");
		credit = Float.parseFloat(sb.substring(start, end));
		
		start = this.sb.indexOf(str,end)+str.length();
		str = null;
		if(mode==0)
		    str = "<p>";
		else if(mode==1)
			str = "<p><b>";
		end = sb.indexOf(str,start);
		
		String tmpdata;
		tmpdata = sb.substring(start, end).trim();
		int tmploc=0;
		
		if(tmpdata.contains("  "))
			tmploc = tmpdata.indexOf("  ");
		else if(tmpdata.contains(" "))
			tmploc = tmpdata.indexOf(" ");
		
		if(tmpdata.substring(0,tmploc).trim().contains(" "))
			tmploc = tmpdata.substring(0,tmploc).trim().indexOf(" ");
		
		internal = tmpdata.substring(0,tmploc);
		external = tmpdata.substring(tmploc,tmpdata.length()).trim();
		
		if(mode==0)
			if(external.contains("</p>"))
				external = external.substring(0, external.indexOf("</p>")).trim();

		if(mode==1)
			if(external.contains("</b></p>"))
				external = external.substring(0,external.indexOf("</b></p>")).trim();
			else if(external.contains("</b>"))
				external = external.substring(0,external.indexOf("</b>")).trim();				

		if(external.trim().indexOf("  ")>-1){
			total = external.split("  ")[1].trim();
			external = external.split("  ")[0].trim();
		}else if(external.trim().indexOf(" ")>-1){
			total = external.split(" ")[1].trim();
			external = external.split(" ")[0].trim();
		}else{
			start = this.sb.indexOf(str,end)+str.length();
			str = null;
			if(mode==0)
			    str = "</p>";
			else if(mode==1)
				str = "</b></p>";
			end = sb.indexOf(str,start);
			
			total = this.sb.substring(start, end).trim();
		}

		str = null;
		if(mode==0)
		    str = "<p>";
		else if(mode==1)
			str = "<p><b>";
		
		if(sb.indexOf(str,end)!=-1)
			sb = new StringBuilder(sb.substring(sb.indexOf(str,end)));
		//else
		//	sb = new StringBuilder(sb.substring(sb.indexOf(str,end)));
			

		for(int i=0;i<paper.size();i++){
			Papers pap = paper.get(i);
			if(student.getSchemeID()==pap.getSchemeID()){
				for(int j=0;j<pap.getSchemeLength();j++){
					Scheme schme = pap.getScheme(j);
					if(schme.getPaperID()==paperid){
						try{
							if(Integer.parseInt(total.replace("*",""))>=schme.getPassingMarks()){
								i = paper.size();
								j = pap.getSchemeLength();
								//break;
							}else{
								credit = 0;
								i = paper.size();
								j = pap.getSchemeLength();
								//break;
							}
						}catch(NumberFormatException ex){
							credit = 0;
							i = paper.size();
							j = pap.getSchemeLength();
						}
					}
				}
			}
		}
		
		return new Marks(paperid, credit).setMarks(internal, external, total);
	}

	private Student extractStudent() throws Exception{
		int start,end,tempstart;
		int startstudent;
		int endstudent;
		String rollnumber;
		long sid,schemeID;
		String name;
		
		end = sb.indexOf("RTSID:");

		if(end==-1)
			end = 0;
		else
			end+="RTSID:".length();
		
		start = end;
		end = sb.indexOf("SID:",end);
		
		if(end==-1)
			end = sb.length();

		int TIMEOUT = 300;
		do{			
			tempstart = start;
			if(sb.indexOf("<p><b>")>-1){
				start = sb.indexOf("<p><b>",start)+"<p><b>".length();
			}else{
				start = sb.indexOf("<p>",start)+"<p>".length();
			}
			TIMEOUT--;
			if(TIMEOUT==0){
				throw new Exception("I guess this has error");
			}
		}while((start<end)&&((sb.indexOf("<p><b>",start+1)<end)||(sb.indexOf("<p>",start+1)<end)));

		if((tempstart!=start)&&(start>end)&&(tempstart<end))
			start = tempstart;
		
		startstudent = start;
		
		
		if(sb.substring(end,end+2).equals(new String("RT"))){
			end++;
			end = sb.indexOf("SID:",end);
		}
		
		if(end!=-1){
			endstudent = start;
			do{
				if(sb.indexOf("<p><b>")>-1){
					endstudent = sb.indexOf("<p><b>",endstudent+1);
				}else{
					endstudent = sb.indexOf("<p>",endstudent+1);
				}
			}while((endstudent<end)&&(((sb.indexOf("<p><b>",endstudent+1)>end))||(sb.indexOf("<p>",endstudent+1)>end)));
		}
		else {
			endstudent = sb.length();
		}
						
		start = startstudent;
		end = sb.indexOf(" ",start);
		
		rollnumber = sb.substring(start, end).replace("<b>"," ").trim();		//contains rollnumber
		
		start = end;
		end = sb.indexOf("SID:",start);
		
		name = sb.substring(start, end).trim();		//contains Name
		
		if(name.length()>100)
			System.out.println(name);
		
		start = end+"SID:".length();
		end = sb.indexOf("SchemeID:", start);
		
		sid = Long.parseLong(sb.substring(start, end).trim());			//contains SID
		
		start = end+"SchemeID:".length();
		end = sb.indexOf("</p>", start);
		
		schemeID = Long.parseLong(sb.substring(start, end).replace("</b>","").trim());			//contains SchemeID
		
		sb = new StringBuilder(sb.substring(sb.indexOf("<p>", end)));
	
		return this.createStudent(rollnumber, name, sid, schemeID);
		
	}
	
	public void extract(LinkedList<Papers> paper) throws Exception{
		Student student;
		Marks mark;
		this.paper = paper;
		do{
			if(this.containsCommon()){
				this.setCommon();
			}
			student = extractStudent();
			student.setCommon(common);
			while(this.containsMarks(sb)){
				mark = extractMarks(student);
				if(mark!=null){
					this.setMarks(mark, student);
				}
			}
			this.addStudent(student);				//not required
			this.insertIntoDatabase(student);
		}while (this.containsStudent(sb));
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		//this.extract();
	}
	
}
