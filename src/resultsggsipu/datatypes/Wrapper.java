package resultsggsipu.datatypes;

import java.util.LinkedList;

public class Wrapper {
	private LinkedList<Papers> paper;
	private LinkedList<College> college;
	private LinkedList<Program> program;
	
	public Wrapper(){
		this.paper = new LinkedList<Papers>();
		this.college = new LinkedList<College>();
		this.program = new LinkedList<Program>();
	}

	public Papers getLastPaper() {
		return this.paper.getLast();
	}	

	public void addLastPaper(Papers paper) {
		this.paper.addLast(paper);
	}	

	public LinkedList<Papers> getPapers(){
		return this.paper;
	}

	public void addPaper(Papers paper){
		this.paper.add(paper);
	}

	public void addCollege(College college){
		this.college.add(college);
	}

	public void addProgram(Program program){
		this.program.add(program);
	}
	
	public int getPaperLength(){
		return this.paper.size();
	}

	public Papers getPaper(int i){
		return this.paper.get(i);
	}

	public int getCollegeLength(){
		return this.college.size();
	}

	public College getCollege(int i){
		return this.college.get(i);
	}

	public int getProgramLength(){
		return this.program.size();
	}

	public Program getProgram(int i){
		return this.program.get(i);
	}
	
	public boolean containsSchemeID(long SchemeID) {
		boolean status;
		
		status = false;
		
		for(int i=0;i<paper.size();i++)
			if(paper.get(i).schemeID == SchemeID)
				status = true;
		
		return status;
	}
	
	public boolean containsSchemeID(long SchemeID,int paperid) {
		boolean status;
		
		status = false;
		
		for(int i=0;i<paper.size();i++)
			if(paper.get(i).schemeID == SchemeID)
				for(int j=0;j<paper.get(i).scheme.size();j++)
					if(paper.get(i).scheme.get(j).paperID == paperid)
						status = true;
		
		return status;
	}
	
}
