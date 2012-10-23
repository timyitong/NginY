package com.nginy.parser;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Stack;
import com.nginy.list.StopList;
public class ParaParser{
	private int [] sep;
	private int [] layer;
	public String header;
	private int max_level;
	private ArrayList <Token> tokens=null;
	private LinkedList <Token> final_tokens=new LinkedList<Token>();
	private boolean cur_op_is_weight=false;
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
					String key=s.substring(head,j);
					push(key);
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

		identify_layer();
		calculate_weight(0,tokens.size());
		combine_same();
		form_final_tokens();
	}
	private void push(String s){
		if (tokens==null)
			tokens=new ArrayList<Token>();
		if (!StopList.hasStopWord(s)){
		Token t=new Token(s,1,1);
		tokens.add(t);
		}
	}
	private void identify_layer(){
		int i=0;
		int level=1;
		max_level=1;
		layer=new int[tokens.size()];
		for (i=0;i<tokens.size();i++){
			layer[i]=level;
			String s=tokens.get(i).key;
			if (s.charAt(0)=='#')
				level++;
			else if (s.equals(")"))
				{level--;layer[i]=0-level;}

			if (level>max_level)
				max_level=level;
		}
	}
	private void calculate_weight(int head, int end){
		boolean stop=false;
		boolean found=false;
		int i=head;
		int w_layer=0;
		int rear=head;
		Token t=null;
		Token score=null;
		int sub_head=0;
		int sub_rear=0;
		int coin=0;
		while(!stop && i<end){
			t=tokens.get(i);
			if (found){
				if (layer[i]==0-w_layer){
					found=false;
					if (i<end-1){
						calculate_weight(i,end);
					}
					stop=true;
				}else if (layer[i]==w_layer+1){
					if (coin==0)
						coin=1;
					else if (coin==1){
						coin=0;
						score=tokens.get(i-1);
						t=tokens.get(i);
						t.weight=Double.parseDouble(score.key);
						tokens.set(i-1,null);
						tokens.set(i,t);

						sub_head=i;
					}
				}else if (layer[i]==0-(w_layer+1)){
					sub_rear=i;
					calculate_weight(sub_head,sub_rear);
				}
			}else{
				if (t.key.equals("#WEIGHT")){
					found=true;
					head=i;
					w_layer=layer[head];
				}
			}
			i++;
		}

	}

	private void combine_same(){
		int i=0;

		int head=0;
		int rear=0;
		int status=0;
		for(i=max_level;i>0;i--){   //i is the current level
			status=0;
			head=0;
			rear=0;
			for(int j=0;j<tokens.size();j++){   //start from begining
				if (status==0 && layer[j]==i){	//enter into current level
					status=1;
					head=j;
				}
				else if (status==1 && (layer[j]==0-(i-1)||j==tokens.size()-1)){  //get out of current level
					status=0;
					rear=j;
					reduce(head,rear,i);
				}
			}
		}
	}
	private void reduce(int head, int rear, int level){
		Token check_token=null;
		Token before_token=null;
		check_token=tokens.get(head);

		String patern=(check_token==null) ? "" : check_token.key;
		double total_weight= (check_token==null) ? 0 : check_token.weight;
		int total_qtf= (check_token==null) ? 0: check_token.qtf;
		for (int h=head+1;h<rear;h++){  
			if (layer[h]==level){	//check each token h from head..rear-1
				check_token=tokens.get(h);
				if (check_token!=null){
					boolean no_duplicate=true;
					for(int k=head;k<h;k++){  //for each token before it:
						before_token=tokens.get(k);
						if (layer[k]==level && before_token!=null){ //if on the same layer
							if (before_token.key.equals(check_token.key)){
								tokens.set(h,null);
								int m=h+1;
								while( m<tokens.size() && ( layer[m]>layer[h] || layer[m]==(0-layer[h]) ) ){
									tokens.set(m,null);
									m++;
								}
									before_token.qtf++;
									before_token.weight=before_token.weight+check_token.weight;
									tokens.set(k,before_token);
									no_duplicate=false;
							}
						}
					}
					if (no_duplicate){
						patern=patern+" "+check_token.key;
					}
					total_weight+=check_token.weight;
					total_qtf+=check_token.qtf;
				}
			}
		}
		if(head!=0){
			check_token=tokens.get(head-1);
			check_token.key=check_token.key+"("+patern+")";
			check_token.total_weight=total_weight;
			check_token.total_qtf=total_qtf;//+1;
			tokens.set(head-1,check_token);
		}
	}
	private void form_final_tokens(){
		Token t=null;
		for (int i=0;i<tokens.size();i++){
			t=tokens.get(i);
			if (t!=null && t.key.indexOf('#')!=-1){
				t.key=t.key.substring(0,t.key.indexOf('('));
			}
			if (t!=null)
				final_tokens.add(t);
		}
	}
	public boolean hasMoreTokens(){
		if (final_tokens.size()!=0)
			return true;
		else 
			return false;
	}
	public Token nextToken(){
		return final_tokens.pollFirst();
	}
}