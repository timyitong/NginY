/**	Notice:
	The ScoreList is stored as an int [],
		in format as follows:
			int[0]={doc_id,term_freq,doc_length,term_loc1,term_loc2,.....}
			int[1]={doc_id,term_freq,doc_length,term_loc1,term_loc2,.....}
					...
			int[n]={doc_id,term_freq,doc_length,term_loc1,term_loc2,.....}
		There is no length attributes for it before int [] can simply tell itself.
*/
package com.nginy.list;
import java.util.HashMap;
import java.util.Stack;
import com.nginy.parser.Token;
public class ScoreList{
	public static boolean ranked=false;
	public double [] list;
	public double default_score;
	public Token token;
	public ScoreList(int doc_count){
		list=new double[doc_count*2];
	}
	public ScoreList(String keyword){
		int [] inv_list=InvertedList.getInvList(keyword);
		initScore(inv_list);
	}
	public ScoreList(double[] score_list){
		this.list=score_list;
	}
	private void initScore(int [] inv_list){
		int doc_count=inv_list[2];  //get Inv_list doc count
		this.list=new double[doc_count*2];
		int tf=0; 
		int pointer=0;
		for(int i=3;i<inv_list.length;i=i+tf+3){
			this.list[pointer]=inv_list[i];
			if (this.ranked)			  // here is the ranked score equals to term_frequency
				this.list[pointer+1]=inv_list[i+1];
			else
				this.list[pointer+1]=1;   // here is the unranked score always 1
			pointer+=2;
			tf=(int)inv_list[i+1];			//the pace of the i, first path should be the first record
		}
	}
	
	public String toString(){
		String s="";
		for(int i=0;i<list.length;i++){
			s+=list[i]+" ";
		}
		return s;
	}
	public void sort(){	//This is the entrance of sorting the Score List Result
		if (list.length!=0)
			sort(0,list.length-2);
	}
	//Quick sort Implementation:
	private void sort(int left, int right){
		Stack <int []> op_stack=new Stack <int []>();
		int [] op=new int[2];
		int [] arr=new int[2];
		int checkIndex=0;
		arr[0]=left;
		arr[1]=right;
		op_stack.push(arr);
		while(!op_stack.empty()){
			op=op_stack.pop();
			left=op[0];
			right=op[1];
			checkIndex=(left/2+right/2)/2*2;	/*Use the middle value to do partition*/
			checkIndex=partition(left,right,checkIndex);
			
			if (checkIndex-2>left){
				int [] pointers=new int[2];
				pointers[0]=left;
				pointers[1]=checkIndex-2;
				op_stack.push(pointers);
			}
			if (checkIndex+2<right){
				int [] pointers2=new int[2];
				pointers2[0]=checkIndex+2;
				pointers2[1]=right;
				op_stack.push(pointers2);
			}
		}
	}
	//partition method implmentation for quick sort
	private int partition(int left, int right, int checkIndex){
		double checkValue=this.list[checkIndex+1];
		swap(right,checkIndex);
		int index=left;
		for (int i=left;i<right;i=i+2){
			if (list[i+1]>checkValue)
				{
				 swap(i,index);
				 index+=2;
			
				}
				else if (list[i+1]==checkValue){
					if (list[i]>list[right]){
						swap(i,index);
						index+=2;
					}
				}
		}
		swap(index,right);
		return index;
	}
	private void swap(int front, int rear){   //Swap two pair of DOC_ID SCORE
		double tmp_doc=0;
		double tmp_score=0;
		tmp_doc=this.list[front];
		tmp_score=this.list[front+1];
		this.list[front]=this.list[rear];
		this.list[front+1]=this.list[rear+1];
		this.list[rear]=tmp_doc;
		this.list[rear+1]=tmp_score;
	}
}