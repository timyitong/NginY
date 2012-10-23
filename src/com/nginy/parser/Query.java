package com.nginy.parser;
import com.nginy.list.ScoreList;
import com.nginy.list.InvertedList;
import java.util.Stack;
import java.util.LinkedList;
import com.nginy.list.ListOperator;
import com.nginy.list.ScoreListOperator;
import com.nginy.list.InvertedListOperator;

public class Query{
	private String query;
	private char [] check_list={'-','\\','_','/'};
	private Tree query_tree=null;
	private ScoreListOperator score_operator=ListOperator.getScoreOperator();
	private InvertedListOperator inv_operator=ListOperator.getInvOperator();
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
			query="#"+auto_head+"("+query+")";
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
			if (c=='/'){ //here we need to rewrite to better handle mutiple varifying operators
					res[i]='/';
			}
		}
		return new String(res);
	}
	private void initTree(){
		query_tree=new Tree(query,"TREE");
	}
	public ScoreList getScoreList(){
		if (query_tree==null)
			initTree();

		Tree parent=query_tree;
		LinkedList<ScoreList> score_arr=new LinkedList<ScoreList>();
		Tree child;

		Stack<Tree> parent_stack=new Stack<Tree>();
		Stack<LinkedList>	score_stack=new Stack<LinkedList>();
		while (parent!=null){
			if (inv_operator.matchInvOperation(parent.tree_type)){    // first handle InvertedList calculation
				parent.score_list=inv_operator.invToScore(parent);
				parent.score_list.token=parent.token; //pass token to score list
				//popup
					if (score_stack.empty()){
						parent=null;
					}else{
						score_arr=score_stack.pop();
						score_arr.add(parent.score_list);
						parent=parent_stack.pop();
					}
			}
			else if (parent.children==null || parent.children.size()==0){   // if parent has no more childrens
				parent.score_list=score_operator.CombineScore(score_arr,parent,score_operator,inv_operator);
				parent.score_list.token=parent.token; // pass token to score list
				//popup
					if (score_stack.empty()){
						parent=null;
					}else{
						score_arr=score_stack.pop();
						score_arr.add(parent.score_list);
						parent=parent_stack.pop();
					}
			}else{   // if parent has more children
				child=parent.children.poll();
				parent_stack.push(parent);
				score_stack.push(score_arr);
				parent=child;
				score_arr=new LinkedList<ScoreList>();
			}
		}

		/*We only do the sort operation, when all the sub_queries have been finished, 
			 only 1 sort per query could save time.*/
		if (ScoreList.ranked)
			query_tree.score_list.sort();
		return query_tree.score_list;
	}
}