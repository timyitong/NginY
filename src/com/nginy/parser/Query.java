package com.nginy.parser;
import com.nginy.list.ScoreList;
public class Query{
	private String query;
	private char [] check_list={'-','\\','_','/'};
	/*If not specially decided, default to wrap an OR to the query String, 
		here we do not check the query String. The reason is the check process is not o(1) but o(n) complexity in time,
		which may not worth it here, since add one more Layer for 1 parameter nodes will not increse real computation.

		The situation like the following, make the check an o(n) process:
		query: #AND(a b) #OR(c d)

	*/
	public Query(String original){
		query=original;
		query="#OR("+query+")";
		check();
	}
	public Query(String orignal, String auto_head,boolean auto_add_header){
		query=orignal;
		if (auto_add_header){
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
		for (int i=0;i<check_list.length;i++){
			if (check_list[i]=='/')
				query=replace_slash(query);
			else
				query=query.replace(check_list[i],' ');
		}
	}
	private String replace_slash(String s){
		char [] res=s.toCharArray();
		char c='0';
		for (int i=0;i<s.length();i++){
			c=s.charAt(i);
			if (c=='/'){
				if (i<5 || !s.substring(i-5,i).equals("#NEAR"))
					res[i]=' ';
			}
		}
		return new String(res);
	}


	public ScoreList getScoreList(){
		ScoreList sl=new Tree(query,"TREE").getScoreList(); 
		/*We only do the sort operation, when all the sub_queries have been finished, 
			 only 1 sort per query could save time.*/
		if (ScoreList.ranked)
			sl.sort();
		return sl;
	}

}