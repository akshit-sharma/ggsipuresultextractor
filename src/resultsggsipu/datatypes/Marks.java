package resultsggsipu.datatypes;

public class Marks {
	
	int paperid;
	float credit;
	int internal;
	int external;
	int total;
	
	public Marks(int paperid,float credit) {
		this.paperid = paperid;
		this.credit = credit;
	}
	
	public Marks setMarks(String internal,String external,String total){

		/* 
		 * A --> Absent 
		 * C --> Cancelled
		 * D --> Detained 	 
		 * RL --> Result Later
		 * */
		
		if(internal.contains("A"))
			internal = "-1";
		else if(internal.contains("C"))
			internal = "-2";
		else if(internal.contains("D"))
			internal = "-3";
		else if(internal.contains("RL"))
			internal = "-4";
		else if(internal.contains("-"))
			internal = "0";
		
		if(external.contains("A"))
				external = "-1";
		else if(external.contains("C"))
			external = "-2";
		else if(external.contains("D"))
			external = "-3";
		else if(external.contains("RL"))
			external = "-4";
		else if(external.contains("-"))
			external = "0";
		
		if(total.contains("A"))
				total = "-1";
		else if(total.contains("C"))
			total = "-2";
		else if(total.contains("D"))
			total = "-3";
		else if(total.contains("RL"))
			total = "-4";
		else if(total.contains("-"))
			total = "0";
		
		if(total.contains("*"))
			total = total.replace("*", "");
		
		this.internal = Integer.parseInt(internal.trim());
		this.external = Integer.parseInt(external.trim());
		this.total = Integer.parseInt(total.trim());
		
		return this;
		
	}
	
	public int getpaperid(){
		return this.paperid;
	}
	
	public float getcredits(){
		return this.credit;
	}
	
	public int getinternal(){
		return this.internal;
	}
	
	public int getexternal(){
		return this.external;
	}
	
	public int gettotal(){
		return this.total;
	}
	
}
