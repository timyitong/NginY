package com.nginy.parser;
import java.util.LinkedList;
import com.nginy.list.StopList;
public class ParaParser{
	private int [] sep;
	public String header;
	public LinkedList <String> tokens=null;
	public ParaParser(String s){
		int head=0;
		int j=0;
		int status=0;
		int layers=1;
		int rear=s.length()-1;

		while(j<s.length()){
			if (s.charAt(j)=='(')
				layers++;
			if (s.charAt(j)==')')
				layers--;

			if (s.charAt(j)!=' '){
				if (status==0){
					status=1;
					head=j;
				}
	
			}else{
				if (status==1 && layers==1){
					status=0;
					push(s.substring(head,j));
				}
			}

			j++;
		}
		if (s.charAt(j-1)!=' ')
			push(s.substring(head,j));
	}
	private void push(String s){
		if (tokens==null)
			tokens=new LinkedList <String> ();
		if (!StopList.hasStopWord(s))	//check whether the word is a StopWord
			tokens.add(s);
	}
	public boolean hasMoreTokens(){
		if (tokens.size()!=0)
			return true;
		else 
			return false;
	}
	public String nextToken(){
		return tokens.pollFirst();
	}
}