package resultsggsipu.datatypes;

public class Scheme {
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
	
	public Scheme(int paperID,String minor,String major,String total,int passingMarks){
		this.paperID = paperID;
		this.minor = minor;
		this.major = major;
		this.total = total;
		this.passingMarks = passingMarks;
	}


	
	public void setPaperCode(String paperCode){
		this.paperCode = paperCode;
	}

	public void setCredits(float credit){
		this.credit = credit;
	}
	
	public void setPaperName(String paperName){
		this.paperName = paperName;
	}
	
	public void setType(int i){
		if(i==0)
			this.type = "THEORY";
		else if(i==1)
			this.type = "PRACTICAL";
	}	

	public void setMode(int mode){
		if(mode == 0)
			this.mode = "COMPULSORY";
		else if(mode == 1)
			this.mode = "ELECTIVE";
	}

	public void setKind(int i){
		if(i==0)
			this.kind = "MANDATORY";
		else if(i==1)
			this.kind = "DROPPABLE";
	}

	public int getPaperID(){
		return this.paperID;
	}

	public String getPaperCode(){
		return this.paperCode;
	}

	public String getPaperName(){
		return this.paperName;
	}

	public float getCredits(){
		return this.credit;
	}
	
	public String getType(){
		return this.type;
	}

	public String getMode(){
		return this.mode;
	}

	public String getKind(){
		return this.kind;
	}
	
	public String getMinor(){
		if(this.minor.equals(new String("--")))
			return "0";
		else
			return this.minor;
	}
	
	public String getMajor(){
		if(this.major.equals(new String("--")))
			return "0";
		else
			return this.major;
	}
	
	public String getTotal(){
		if(this.total.equals(new String("--")))
			return "0";
		else
			return this.total;
	}
	
	public int getPassingMarks(){
			return this.passingMarks;
	}
	
}
