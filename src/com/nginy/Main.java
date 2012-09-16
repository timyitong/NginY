/**	Author: Yitong Zhou
	Date: Sep 6, 2012
	Application: Search Engine---NginY
*/
package com.nginy;
import com.nginy.parser.*;
import com.nginy.list.*;
import java.util.Date;
import java.text.SimpleDateFormat;
public class Main{
	public static void main(String argv[]){
		init();
		run();
		test();
	}
	private static void init(){
		SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss:SSS");
		System.out.println("===Engine Boot ===    TIME "+format.format(new Date()));
		/*Load Stopword List into Memory*/
		StopList.readIntoMemory(1000,"stoplist.txt");	
		/*Load InvertedList into Memory*/
		InvertedList.readIntoMemory(1000,"inv_local");
		InvertedList.readIntoMemory(1000,"body_local");
		InvertedList.readIntoMemory(1000,"title_local");
		ScoreList.ranked=true;
		System.out.println("===Engine Ready===   TIME "+format.format(new Date()));
	}
	private static void run(){
	/*Ranked Part:*/	
		/*Unstructured Query Tasks*/
		BatchTask task1=new BatchTask("uns_OR_queries.txt",true,"OR");
		BatchTask task2=new BatchTask("uns_AND_queries.txt",true,"AND");
		task1.run();
		task2.run();
		/*Structure Query Tasks*/
		BatchTask task3=new BatchTask("s_queries.txt",false);
		task3.run();
	/*Unranked Part:*/
		ScoreList.ranked=false;
		/*Unstructured Query Tasks*/
		task1=new BatchTask("uns_OR_queries.txt",true,"OR");
		task2=new BatchTask("uns_AND_queries.txt",true,"AND");
		task1.run();
		task2.run();
		/*Structure Query Tasks*/
		task3=new BatchTask("s_queries.txt",false);
		task3.run();
	}
	private static void test(){
		try{
		/*This is used for downloading the Inverted Lists on the WEB*/
			//new InvRemoteLoader("","");	
		/*---------------------------------------------------------*/

		/*This is used for parsing the web inverted lists into lists that are usable  locally.*/
			//new InvWriter("data/inv_web");
		/*--------------------------------------------------------*/

		
		}catch(Exception e){e.printStackTrace();};
	}
}