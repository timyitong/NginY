package com.nginy;
import java.util.Date;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import com.nginy.list.ScoreList;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
public class TrecWriter{
	private BufferedWriter bw=null;
	TrecWriter(){
		this("trec");
	}
	TrecWriter(String file_prefix){
		Date date=new Date();
		SimpleDateFormat f=new SimpleDateFormat("MM-dd_HH-mm-ss_SSS");
		try{
		bw=new BufferedWriter(new FileWriter(new File("data/result/"+file_prefix+"_"+f.format(date)+".txt")));
		}catch(IOException e){e.printStackTrace();}
	}

	public void close(){
		try{
		bw.close();
		}catch(IOException e){e.printStackTrace();}
	}

	public void writeScore(ScoreList s,String query_id, int limit_lines){
		int i=0;
		int rank=1;
		// here we mean lines limits, but in the next while loop, the pace is 2
		if (limit_lines==-1)
			limit_lines=s.list.length*2;
		else
			limit_lines=limit_lines*2;
		
		while(i<s.list.length && i<limit_lines){
			double score=s.list[i+1];
			DecimalFormat f=new DecimalFormat("#.0");
			String line=query_id+" Q0 "+(int)s.list[i]+" "+rank+" "+score+" "+"run-1\n";
			try{
			bw.write(line);
			}catch(IOException e){e.printStackTrace();}
			rank+=1;
			i+=2;
		}
	}
}