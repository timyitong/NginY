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
/*split the query string into useful tokens, we consider space, '(' as splitter, 
	but consider ')' also a token to send a message to the tree that one sub-tree is finished.*/
		while(j<s.length()){
			if (status==1){
				if (s.charAt(j)==')'){
					push(s.substring(head,j));
					push(")");
					status=0;
				}
				else if (s.charAt(j)=='('){ 
					push(s.substring(head,j));
					status=0;
				}
				else if (s.charAt(j)==' '){
					push(s.substring(head,j));
					status=0;
				}
			}
			else {
				if (s.charAt(j)==')')
					push(")");
				else if (s.charAt(j)!=' ' && s.charAt(j)!='('){
					head=j;
					status=1;
				}
			}
			j++;
		}
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