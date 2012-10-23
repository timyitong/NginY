package com.nginy;
import com.nginy.TrecWriter;
import com.nginy.parser.Tree;
import com.nginy.parser.Query;
import com.nginy.list.ScoreList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.LinkedList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class BatchTask{
private String header="OR";
private String filename;
private boolean auto_add_header=true;
private LinkedList <String []> query_queue=new LinkedList <String []> ();
private SimpleDateFormat simple_format=new SimpleDateFormat("HH:mm:ss:SSS");
	BatchTask(String filename, boolean auto_add_header){
		this.filename=filename;
		this.auto_add_header=auto_add_header;
		load();
	}
	BatchTask(String filename,boolean auto_add_header, String header){
		this.filename=filename;
		this.auto_add_header=auto_add_header;
		this.header=header;
		load();

	}
	//Load a batch of queries
	private void load(){
		try{
			BufferedReader br=new BufferedReader(new FileReader(new File("data/queries/"+filename)));
			String line=null;
			int split_index=0;
			while((line=br.readLine())!=null){
				split_index=line.indexOf(':');
				String [] s=new String[2];
				s[0]=line.substring(0,split_index);
				s[1]=line.substring(split_index+1,line.length());
				query_queue.add(s);
			}
		}catch(Exception e){e.printStackTrace();}
	}

	public void run(){
		System.out.println("TaskName:"+filename);
		System.out.println("Begin:" +simple_format.format(new Date()));
		String [] query=null;
		TrecWriter tw=new TrecWriter(filename.substring(0,filename.indexOf('.')));	//Do not want the '.txt' be included in the result file name
		ResWriter rw=new ResWriter(filename.substring(0,filename.indexOf('.')));
		while((query=query_queue.pollFirst())!=null){
			Query q=new Query(query[1],this.header,auto_add_header);
			ScoreList sl=q.getScoreList();
			tw.writeScore(sl,query[0],100);
			// -1 means not line limits, and according to the homwwork requirements, we typically use 100
			rw.writeScore(sl,query[0],50);
		}
		tw.close();
		rw.close();
		System.out.println("End:" +simple_format.format(new Date()));
	}
}