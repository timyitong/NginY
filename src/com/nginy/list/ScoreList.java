package com.nginy.list;
import java.util.HashMap;
import java.util.Stack;
public class ScoreList{
	public static boolean ranked=false;
	public int [] list;
	private static HashMap <String,ScoreList> hashmap=new HashMap<String,ScoreList>();
	public ScoreList(String keyword){
		int [] inv_list=InvertedList.getInvList(keyword);

		initScore(inv_list);
	}
	public ScoreList(int[] score_list){
		this.list=score_list;
	}
	public ScoreList(int[] list,String type){
		if (type.equals("inv"))
			initScore(list);
		else
			this.list=list;
	}
	private void initScore(int [] inv_list){
		int doc_count=inv_list[2];  //get Inv_list doc count
		this.list=new int[doc_count*2];
		int tf=0; 
		int pointer=0;
		for(int i=3;i<inv_list.length;i=i+tf+3){
			this.list[pointer]=inv_list[i];
			if (this.ranked)			  // here is the ranked score equals to term_frequency
				this.list[pointer+1]=inv_list[i+1];
			else
				this.list[pointer+1]=1;   // here is the unranked score always 1
			pointer+=2;
			tf=inv_list[i+1];			//the pace of the i, first path should be the first record
		//		System.out.println(inv_list.length+"pointer:"+pointer);
		//System.out.println("tf:"+tf+"i:"+i);
		}
	}
	
	public static ScoreList AND(ScoreList l){
		return l;
	}

	//Notice here changed on the the ScoreList's value to save int[] space, may cause problem if further codes want to reuse the scorelist
	public static ScoreList AND(ScoreList l1,ScoreList l2){
		if (l1==null)
			return l2;
		int i=0,j=0;
		int writer=0;
		while (i<l1.list.length && j<l2.list.length){
			if (l1.list[i]<l2.list[j])
				i+=2;
			else if (l1.list[i]>l2.list[j])
				j+=2;
			else{
				l1.list[writer]=l1.list[i];
				l1.list[writer+1]= l1.list[i+1] > l2.list[j+1] ? l2.list[j+1] : l1.list[i+1];
					//AND score=min{score1, score2}
				writer+=2;
				i+=2;
				j+=2;
			}
		}
		int [] new_list=new int[writer];
		for (int h=0;h<new_list.length;h++){
			new_list[h]=l1.list[h];
		}
		return new ScoreList(new_list);
	}
	public static ScoreList AND(ScoreList[] arr){
		if (arr.length==1){
			return AND(arr[0]);
		}
		else if (arr.length==2){
			return AND(arr[0],arr[1]);
		}
		else{
			ScoreList result=arr[0];
			for (int i=1;i<arr.length;i++){
				result=ScoreList.AND(result,arr[i]);
			}
			return result;
		}
	}

	public static ScoreList OR(ScoreList s){
		return s;
	}
	public static ScoreList OR(ScoreList s1, ScoreList s2){
		int [] result=new int [s1.list.length+s2.list.length];
		int i=0,j=0,writer=0;
		while(i<s1.list.length && j<s2.list.length){
			if (s1.list[i]>s2.list[j]){
				result[writer]=s2.list[j];
				result[writer+1]=s2.list[j+1];
				writer+=2;
				j+=2;
			}else if (s1.list[i]<s2.list[j]){
				result[writer]=s1.list[i];
				result[writer+1]=s1.list[i+1];
				writer+=2;
				i+=2;

			}else{
				result[writer]=s1.list[i];
				result[writer+1]= s1.list[i+1] > s2.list[j+1] ? s1.list[i+1] : s2.list[j+1];
					//OR score=max{score1,score2}
				writer+=2;
				j+=2;
				i+=2;
			}
			
		}
		if (i>=s1.list.length){
			while(j<s2.list.length){
				result[writer]=s2.list[j];
				result[writer+1]=s2.list[j+1];
				writer+=2;
				j+=2;
			}
		}else if (j>=s2.list.length){
			while (i<s1.list.length){
				result[writer]=s1.list[i];
				result[writer+1]=s1.list[i+1];
				writer+=2;
				i+=2;

			}
		}
		int [] new_result= new int [writer];
		for (i=0;i<new_result.length;i++)
			new_result[i]=result[i];
		return new ScoreList(new_result);
	}
	public static ScoreList OR(ScoreList [] arr){
		if (arr.length==1){
			return OR(arr[0]);
		}else{
			int i=1;
			ScoreList result=arr[0];
			while(i<arr.length){
				result=ScoreList.OR(result,arr[i]);
				i++;
			}
			return result;
		}

	}
	public String toString(){
		String s="";
		for(int i=0;i<list.length;i++){
			s+=list[i]+" ";
		}
		return s;
	}
	public void sort(){

		sort(0,list.length-2);
		/*for (int i=0;i<list.length;i+=2){
			System.out.println(list[i]+"::::"+list[i+1]);
		}*/
	}
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
			checkIndex=(left/2+right/2)/2*2;
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

	private int partition(int left, int right, int checkIndex){
		int checkValue=this.list[checkIndex+1];
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
	private void swap(int front, int rear){
		int tmp_doc=0;
		int tmp_score=0;
		tmp_doc=this.list[front];
		tmp_score=this.list[front+1];
		this.list[front]=this.list[rear];
		this.list[front+1]=this.list[rear+1];
		this.list[rear]=tmp_doc;
		this.list[rear+1]=tmp_score;
	}
}