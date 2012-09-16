package com.nginy.parser;
import com.nginy.list.ScoreList;
import java.util.LinkedList;
import com.nginy.parser.ParaParser;
import com.nginy.list.InvertedList;
import java.util.Stack;
import java.util.Iterator;
public class Tree{
	public LinkedList <Tree> children=null;
	public ScoreList score_list=null;
	public String keyword=null;
    public String tree_type;
    public boolean splitted=false;
	public Tree(String query){
		if (query.charAt(0)=='#'){
			children=new LinkedList <Tree>();
			int i=query.indexOf('(');
			tree_type=query.substring(1,i);
			ParaParser parser=new ParaParser(query.substring(i+1,query.length()-1));
			while(parser.hasMoreTokens()){
				children.add(new Tree(parser.nextToken()));
			}

		}else{
			query=query.toLowerCase();
			if (query.indexOf(".")==-1)
				query=query+".body";
			//score_list=new ScoreList(query);
			keyword=query;
			tree_type="COM";
		}
	}
	public void AND(){
		
	}
	public ScoreList getScoreList_norecur(){
		Stack <Tree> op_stack=new Stack <Tree>();
		ScoreList result=null;
		Tree t=null;
		Tree tmp=null;
		Tree tmp2=null;
		op_stack.push(this);
		while (!op_stack.empty()){
			t=op_stack.pop();
			if (t.score_list==null){
			  if (t.tree_type.equals("COM"))
			  	t.score_list=new ScoreList(t.keyword);
			  else{
			  	if (t.splitted){ //whether already be splitted
			  		if (t.children.size()==1){ //if only gets 1, we do not need to calculate
			  			t.score_list=t.children.pollFirst().score_list;
			  		}else{
			  			if (t.tree_type.matches("^NEAR.*")){
							int width=Integer.parseInt(tree_type.substring(5,tree_type.length()));
							t.score_list=InvertedList.NEAR(children,width);
			  			}else{
			  				ScoreList [] sl=new ScoreList[t.children.size()];
			  				int i=0;
				  				while (t.children.size()!=0){
				  					tmp=t.children.pollFirst();
				  					sl[i]=tmp.score_list;
				  					i++;
				  				}
				  			if (t.tree_type.equals("AND"))
				  				t.score_list=ScoreList.AND(sl);
				  			else if (t.tree_type.equals("OR"))
				  				t.score_list=ScoreList.OR(sl);
			  			}
			  		} 
			  	}else{//if not splitted yet, we need to split it
			  		Iterator <Tree> it=t.children.descendingIterator();
			  		while(it.hasNext()){
			  			t=it.next();
			  			op_stack.push(t);
			  		}
			  		t.splitted=true;
			  		op_stack.push(t);
			  	}
			  }
			}
		}
		result=t.score_list;
		return result;
	}
	public ScoreList getScoreList(){
		if (tree_type.equals("COM"))
			return new ScoreList(keyword);
		if (score_list==null){
			ScoreList [] arr=new ScoreList[children.size()];
			Tree t=null;
			int i=0;

			//handle Inv Operation:
			if ( tree_type.matches("^NEAR.*"))
				// The syntax check is left to InvertedList
			{
				int width=Integer.parseInt(tree_type.substring(5,tree_type.length()));
				return InvertedList.NEAR(children,width);
			}

			//handle Score Operation

			while(	(t=children.pollFirst())!=null	){
				arr[i]=t.getScoreList();
				i++;
				
			}
			if (tree_type.equals("AND"))
				return ScoreList.AND(arr);
			else if (tree_type.equals("OR"))
				 return ScoreList.OR(arr);
			else
				return null;
		}
		else
			return score_list;
	}
	public String toString(){
		String s=tree_type+"{";
		if (children!=null){
			int i=0;
			while (i<children.size()){
				s+=children.get(i).toString()+" ";
				i++;
			}
		}else{
			s+=keyword+" ";
		}
		s+="}";
		return s;
	}
}