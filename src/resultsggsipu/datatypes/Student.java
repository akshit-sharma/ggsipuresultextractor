package resultsggsipu.datatypes;

import java.util.LinkedList;

public class Student {
	
	LinkedList<Marks> marks;
	String rollNumber;
	String name;
	long sid;
	long schemeid;
	Common common;
	
	public Student(String rollnumber2,String name,long sid,long schemeid){
		this.rollNumber = rollnumber2;
		this.name = name;
		this.sid = sid;
		this.schemeid = schemeid;
		this.marks = new LinkedList<Marks>();
	}

	public void setCommon(Common common){
		this.common = common;
	}
	
	public Common getCommon(){
		return this.common;
	}
	
	public void addMarks(Marks mark){
		marks.add(mark);
	} 

	public LinkedList<Marks> getmark(){
		return this.marks;
	}
	
	public Marks getmark(int i){
		return this.marks.get(i);
	}

	public int getMarksLength(){
		return this.marks.size();
	}
	
	public String getName(){
		return this.name;
	}

	public String getRollNumber(){
		return this.rollNumber;
	}
	
	public long getSID(){
		return this.sid;
	}
	
	public long getSchemeID(){
		return this.schemeid;
	}
	
}
