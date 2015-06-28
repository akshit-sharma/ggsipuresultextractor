package resultsggsipu.subprogram;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.mysql.jdbc.MysqlDataTruncation;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;

public class DatabaseInteraction {
	
	   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   public static String URL = "jdbc:mysql://";
	   static final String DB = "akshitsh_ggsdb";
	   //  Database credentials
	   static final String USER = "akshitsh_resggs";
	   static final String PASS = "@k$h!t_resggs";
	   
	   private Connection conn;
	   private Statement stmt;
	   
	   private static boolean changed = false;	   
	   
	static LinkedHashSet<String> query = new LinkedHashSet<String>();
	
		private int checking=100;

	static synchronized void addQuery(String q){
		query.add(q);
	}
	
	public synchronized void execLastQuery(){
		this.start();
		this.send("INSERT INTO vsion(`uplddate`) VALUES('"+new Date().toString()+"')");
		this.close();
		changed = false;
	}
	
	public boolean isNewData(){
		return changed;
	}
	
	public void sendData(){
		System.out.println("Sending data");
		this.Register();
		this.start();
		Iterator<String> itr = query.iterator();
		int length = query.size();
		int sent =0;
		while(itr.hasNext()){
			if(sent%1000==0)
				System.out.println(sent+" of "+length);
			sent++;
			if(!this.send(itr.next()))
				this.start();
		}
		this.close();
		System.out.println("data sent");
		
		/*
		LinkedList<StringBuilder>allQueries = new LinkedList<StringBuilder>();
		StringBuilder oneQuery = new StringBuilder();
		Iterator<String> itr = query.iterator();
		int length = 0;
		int now = 1;
		String [] singleQuery;
		while(itr.hasNext()){
			singleQuery = itr.next().split(";");
			for(String qury: singleQuery){
				oneQuery.append(qury+";");
				if(now%checking==0){
					allQueries.add(oneQuery);		
					try {
						sendPost(oneQuery.toString()+"///DONE");
						now = 1;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					oneQuery = new StringBuilder();
				}
				now++;length++;	
			}
		}	
		System.out.println("Length is "+length);
		*/
	}
	
	private void Register(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
		
	public boolean start(){

		try {
			conn = DriverManager.getConnection(URL+"/"+DB,USER,PASS);
		}catch(MySQLIntegrityConstraintViolationException e){
			  e.printStackTrace();
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			return false;
		}
				
		return true;
	}
		
		public boolean send(String query){
			
			String [] singleQuery;
			//String qry = null;
			String tmpqry = null;
			try {
				
				//this.start();
			
				stmt = conn.createStatement();
				singleQuery = query.split(";");
				for(String qury: singleQuery){
					try{
						if(!qury.trim().equals(new String(""))){
							tmpqry = qury;
							stmt.executeUpdate(qury+";");
							if(!qury.contains("CREATE TABLE"))
								changed = true;
						}
					}catch(MySQLSyntaxErrorException e){
						if(e.getMessage().contains("Query was empty")){
							System.err.println(qury);
							e.printStackTrace();
							System.exit(2);
						}
						else{
							System.err.println(qury);
							e.printStackTrace();
						}
					}catch (MysqlDataTruncation e) {
						if(e.getMessage().contains("Data too long for column")){
							System.err.println(qury);
							e.printStackTrace();
							System.exit(1);
						}
						else{
							System.err.println(qury);
							e.printStackTrace();
						}
					}
			}
			//stmt.executeQuery(query);
			
			stmt.close();
			//this.close();
			
		} catch (MySQLIntegrityConstraintViolationException e) {
			//this.tryAgain(tooManyConnections);
			if(!e.toString().contains("Duplicate")){
				e.printStackTrace();
				return false;
			}/*else{
				if(qry!=null){
					LinkedList<String> column;
					LinkedList<String> value;
					String tablename;
					String tempry;
					int temp;
					column = new LinkedList<String>();
					value = new LinkedList<String>();
					temp =qry.indexOf("INTO")+"INTO".length();
					tablename = qry.substring(temp,(qry.indexOf("(")-temp));
					temp = qry.indexOf("(")+"(".length();
					tempry = qry.substring(temp,qry.indexOf(")",temp)-temp);
					tempry = tempry.replace("`", "");
					for(String smaller:tempry.split(","))
						column.add(smaller);
					temp = qry.indexOf(")");
					temp = qry.indexOf("(")+"(".length();
					tempry = qry.substring(temp,qry.indexOf(")",temp)-temp);
					for(String smaller:tempry.split(","))
						value.add(smaller);
				}
			}
			*/
		} catch (SQLException e) {
			System.out.println(tmpqry);
			e.printStackTrace();
			this.close();
			return false;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			this.close();
			return false;
		}
			
		return true;
	}

	public boolean close(){
	
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
		} catch (NullPointerException e) {
		}
			
			return true;
		}

	
	//not using
	private void sendPost(String urlParameters) throws Exception {
		 
		String webURL = "http://akshit.xyz/resultsggsipu/queryupload.php";

		URL url = new URL(webURL);

	    URLConnection conn = url.openConnection();
	    conn.setRequestProperty("User-Agent", "PrivateComputation");
	    conn.setDoOutput(true);
	    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

	    writer.write("q="+urlParameters);
	    writer.flush();
	    String line;
	    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    while ((line = reader.readLine()) != null) {
	      System.out.println("At "+checking+" "+line+" length "+urlParameters.length()+2);
	      if(line.contains("FOUND"))
	    	  checking++;
	      else
	    	  checking--;
	    }
	    writer.close();
	    reader.close();

	    Thread.sleep(2000);
	    
	}

}
