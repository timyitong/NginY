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
    public String tree_type;		/* The values have the following meanings:
    									"TREE"=>entire query tree
    									"COM" => end leaf	
    								{"AND"|"OR"|"NEAR/x"}	=>operator tyoe		*/
    public boolean splitted=false;	//indicated whether this Tree's ScoreList has begun calculation.
	public Tree(String query,String type){  // 2 types: node: refers to a node and tree refers to an entire tree
		if (!type.equals("TREE")){
			initNode(query,type);
		}else{
			Stack <Tree> parent_stack=new Stack <Tree>();
			ParaParser p=new ParaParser(query);
			String token=p.nextToken();
			String op_name=null;
			char check='0';
			if (!p.hasMoreTokens()){ // if there is # in query, then at least has two tokens, #XXX and )
				initNode(token,"COM");
			}else{
				op_name=token.substring(1,token.length());
				initNode(op_name,op_name);
				Tree parent=this;
				while (p.hasMoreTokens()){
					token=p.nextToken();
					check=token.charAt(0);
					if (check=='#'){
						op_name=token.substring(1,token.length());
						Tree new_parent=new Tree(op_name,op_name);//Generate new tree
						parent.addChild(new_parent);//add this tree to parent
						parent_stack.push(parent);//save current parent in stack
						parent=new_parent;//the new tree becomes the new parent
					}else if (check==')'){
						if (!parent_stack.empty())
							parent=parent_stack.pop();
					}else{
						Tree new_leaf=new Tree(token,"COM");
						parent.addChild(new_leaf);
					}
				}
			}
		}
	}
	private void initNode(String query,String type){ // this is the init method for a leaf end node
		if (type.equals("COM")){
			query=query.toLowerCase();
			if (query.indexOf(".")==-1)
				query=query+".body";
		}
		keyword=query;
		tree_type=type;
	}
	public void addChild(Tree t){
		if (children==null)
			children=new LinkedList<Tree> ();
		children.add(t);	
	}
	// A non-recursive version of getScoreList(), but the performance seems to be no difference
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
							int width=Integer.parseInt(t.tree_type.substring(5,t.tree_type.length()));
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
			  		t.splitted=true;
			  		op_stack.push(t);
			  		while(it.hasNext()){
					//push all its children into stack
			  			tmp=it.next();
			  			op_stack.push(tmp);
			  		}
			  	}
			  }
			}
		}
		result=t.score_list;
		return result;
	}
	public ScoreList getScoreList(){
		if (tree_type.equals("COM")){
			ScoreList l=new ScoreList(keyword);
			return l;
		}
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