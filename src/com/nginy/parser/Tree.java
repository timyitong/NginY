package com.nginy.parser;
import com.nginy.list.ScoreList;
import java.util.LinkedList;
import com.nginy.parser.ParaParser;
import com.nginy.list.InvertedList;
import java.util.Stack;
import java.util.Iterator;
import com.nginy.list.ScoreListOperator;
import com.nginy.list.BooleanScoreListOperator;
public class Tree{
	public LinkedList <Tree> children=null;
	public ScoreList score_list=null;
	public String keyword=null;
    public String tree_type;		/* The values have the following meanings:
    									"TREE"=>entire query tree
    									"COM" => end leaf	
    								{"AND"|"OR"|"NEAR/x"}	=>operator tyoe		*/
    public boolean splitted=false;	//indicated whether this Tree's ScoreList has begun calculation.
    public Token token=null;


	public Tree(String query,String type){  // 2 types: node: refers to a node and tree refers to an entire tree
		if (!type.equals("TREE")){
			initNode(query,type);
		}else{
			Stack <Tree> parent_stack=new Stack <Tree>();
			ParaParser p=new ParaParser(query);
			Token token=p.nextToken();
			this.token=token;
			String op_name=null;
			char check='0';
			if (!p.hasMoreTokens()){ // if there is # in query, then at least has two tokens, #XXX and )
				initNode(token.key,"COM");
			}else{
				op_name=token.key.substring(1,token.key.length());
				initNode(op_name,op_name);
				Tree parent=this;
				while (p.hasMoreTokens()){
					token=p.nextToken();
					check=token.key.charAt(0);
					if (check=='#'){
						op_name=token.key.substring(1,token.key.length());
						Tree new_parent=new Tree(op_name,op_name);//Generate new tree
						new_parent.token=token;   //give the token to the tree,this is for more complicated queries with weight and qtf
						parent.addChild(new_parent);//add this tree to parent
						parent_stack.push(parent);//save current parent in stack
						parent=new_parent;//the new tree becomes the new parent
					}else if (check==')'){
						if (!parent_stack.empty())
							parent=parent_stack.pop();//all leaves ready, back to previous parent
					}else{
						Tree new_leaf=new Tree(token.key,"COM");
						new_leaf.token=token;  //give the token to the tree,this is for more complicated queries with weight and qtf
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
				query=query+".inv";
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