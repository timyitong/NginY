package com.nginy;
/** This class is written only for the convenience of printing out the required  Sample file results in the Homework.
*/
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.util.StringTokenizer;
public class TrecRewriter{
	public TrecRewriter(String in, String out){
		File [] list=new File(in).listFiles();
		for (int i=0;i<list.length;i++){
			System.out.println(list[i].getName());
			rewrite(in+list[i].getName(),out+list[i].getName());
		}
	}
	//Read a trec_result file and rewrite it into the sample result file.
	public static void rewrite(String url, String wurl){
		try{
			BufferedReader br=new BufferedReader(new FileReader(new File(url)));
			BufferedWriter bw=new BufferedWriter(new BufferedWriter(new FileWriter(new File(wurl))));
			String line=null;
			int count=0;
			String rank=null;
			String doc_id=null;
			String score=null;
			String qid=null;
			StringTokenizer st=null;
			while((line=br.readLine())!=null && count!=100){
				st=new StringTokenizer(line);
				qid=st.nextToken();
				if ((qid.equals("1") && count<50) || qid.equals("15")){
					st.nextToken();
					doc_id=st.nextToken();
					rank=st.nextToken();
					score=st.nextToken();
					bw.write(rank+" "+doc_id+" "+score); 
					count++;
					if (count!=100)
						bw.newLine();
				}	
			}
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}