package resultsggsipu.datatypes;

import java.util.LinkedList;

public class Papers {
	long schemeID;
	LinkedList<Scheme> scheme;
	
	public Papers(long schemeID){
		this.schemeID = schemeID;
		scheme = new LinkedList<Scheme>();
	}
	
	public void setSchemeID(long schemeID){
		this.schemeID = schemeID;
	}
	
	public void addScheme(Scheme scheme){
		this.scheme.add(scheme);
	}
	
	public long getSchemeID(){
		return this.schemeID;
	}
	
	public LinkedList<Scheme> getScheme(){
		return this.scheme;
	}
	
	public int getSchemeLength(){
		return this.scheme.size();
	}

	public Scheme getScheme(int i){
		return this.scheme.get(i);
	}
	
}
