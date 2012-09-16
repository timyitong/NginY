package com.nginy.parser;
import com.nginy.list.ScoreList;
public class Query{
	private String query;
	public Query(String original){
		query=original;
		if (query.charAt(0)!='#'){
			query="#OR("+query+")";
		}
		check();
	}
	public Query(String orignal, String auto_head){
		query=orignal;
		if (query.charAt(0)!='#'){
			if (auto_head.equals("AND")){
				query="#AND("+query+")";
			}else{
				query="#OR("+query+")";
			}
		}
		check();
	}
	public String getQuery(){
		return query;
	}
	private void check(){
		checkHyphen();
	}
	private void checkHyphen(){
		int i=-1;
		while (	(i=query.indexOf('-'))	!=-1){
			if (i!=query.length()-1)
				query=query.substring(0,i)+query.substring(i+1,query.length()-i-1);
			else
				query=query.substring(0,i);
		}
	}

	public ScoreList getScoreList(){
		ScoreList sl=new Tree(query).getScoreList(); 
		if (ScoreList.ranked)
			sl.sort();
		return sl;
	}

}