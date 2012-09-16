package com.nginy.parser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.StringTokenizer;
import com.nginy.list.InvertedList;
public class Val{
	String keyword=null;
	int term_count;  //total_term_count
	int term_freq;   //collection_term_frequence
	String term;
	String stemmed_term;
	public InvertedList list;
	Val(String s){
		keyword=s;

		try {
		BufferedReader br=new BufferedReader(new FileReader(new File("data/inv_local/"+keyword+".inv")));
		String line=br.readLine();
		StringTokenizer st=new StringTokenizer(line);
		term=st.nextToken();
		stemmed_term=st.nextToken();
//		term_freq=st.nextToken();
//		term_count=st.nextToken();
		list=new InvertedList(keyword);

		}catch(Exception e){System.out.println(e.toString());}


	}
	public String getKeyword(){
		return keyword;
	}
}