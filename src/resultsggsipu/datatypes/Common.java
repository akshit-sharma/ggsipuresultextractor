package resultsggsipu.datatypes;

public class Common {
	
	private int programCode;
	private int collegeCode;
	private int batchYear;
	private String sem;
	private String appearYear;
	
	
	public void setProgramCode(int programCode){
		this.programCode = programCode;
	}
	
	public void setCollegeCode(int collegeCode){
		this.collegeCode = collegeCode;
	}
	
	public void setBatchYear(int batchyear){
		this.batchYear = batchyear;
	}

	public void setSem(String sem){
		this.sem = sem;
	}

	public void setAppearYear(String appearYear){
		this.appearYear = appearYear;
	}
	
	public int getProgramCode(){
		return this.programCode;
	}
	
	public String getAppearYear(){
		return this.appearYear;
	}
	
	public String getSem(){
		return this.sem;
	}
	
	public int getCollegeCode(){
		return this.collegeCode;
	}
	
	public int getBatchYear(){
		return this.batchYear;
	}
	
}
