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
		//test();
	}
	private static void init(){
		SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss:SSS");
		System.out.println("===Engine Boot ===    TIME "+format.format(new Date()));
		/*Load Stopword List into Memory*/
		StopList.readIntoMemory(420,"stoplist.txt");	
		
		/*Load InvertedList into Memory*/
		InvertedList.readIntoMemory(0,"inv_local");  //0 means we do not use the hashmap
		//Add the folder directory to load other lists:
			//InvertedList.readIntoMemory(400,"body_local");
			//InvertedList.readIntoMemory(400,"title_local");

		ScoreList.ranked=true;
		System.out.println("===Engine Ready===    TIME "+format.format(new Date()));
	}
	private static void run(){
	/*	ListOperator.setModel("boolean");
		BatchTask task_x=new BatchTask("queries.txt",true,"AND");
		task_x.run();
		BatchTask task_x2=new BatchTask("queries-sdm.txt",true,"AND");
		task_x2.run();

	*/	
	/*	ListOperator.setModel("BM25");
		BatchTask task_y=new BatchTask("uw.txt",true,"SUM");
		task_y.run();
		BatchTask task_y2=new BatchTask("queries-sdm.txt",true,"SUM");
		task_y2.run();	
*/
		ListOperator.setModel("language");
		BatchTask task_z=new BatchTask("queries-sdm.txt",true,"AND");
		task_z.run();
		BatchTask task_z2=new BatchTask("queries-sdm-s.txt",true,"AND");
		task_z2.run();
/*
		ListOperator.setModel("language");
		BatchTask task_w=new BatchTask("test.txt",true,"AND");
		task_w.run();
*/	}
	private static void test(){
		try{
		/*This is used for downloading the Inverted Lists on the WEB*/
			//new InvRemoteLoader("","");	
		/*---------------------------------------------------------*/

		/*This is used for parsing the web inverted lists into lists that are usable  locally.*/
			//new InvWriter("data/inv_web");
		/*--------------------------------------------------------*/

		
		ParaParser para=new ParaParser("#WEIGHT(0.4 #AND(disneyland hotel) 0.2 #AND(disneyland) 0.4 #AND(#UW/80(disneyland hotel)))");
		while(para.hasMoreTokens()){
			Token s=para.nextToken();
			System.out.println(s.key+"=====qtf:weight===="+s.qtf+"::"+s.weight+"   tw:"+s.total_weight+"   total_qtf:"+s.total_qtf);
		}
	
		}catch(Exception e){e.printStackTrace();};
	}
}