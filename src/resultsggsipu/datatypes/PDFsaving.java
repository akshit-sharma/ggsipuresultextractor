package resultsggsipu.datatypes;

public class PDFsaving {
	
	String name;
	String url;
	String date;

	public PDFsaving(String name,String url,String date) {
		this.name = name;
		this.url = url;
		this.date = date;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getURL() {
		return this.url;
	}
	
	public String getDate() {
		return this.date;
	}
	
}
