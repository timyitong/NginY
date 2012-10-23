package com.nginy.list;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.HashMap;
public class StopList{
	private static HashMap <String,byte[]> hashmap;
	public static void  readIntoMemory(int num,String filename){
		try{
		BufferedReader br=new BufferedReader(new FileReader(new File("data/"+filename)));
		String s=null;
		hashmap=new HashMap <String,byte[]>(num);  //indicate the length of the hashmap could save rehash time cost
		while ( (s=br.readLine()) != null){
			hashmap.put(s,new byte[1]);	
			//Only use 1 byte to indicate whether the stop word exists, make the space cost as small as possible
		}
		br.close();
		}catch(Exception e){e.printStackTrace();}
	}
	public static boolean hasStopWord(String w){
		if (hashmap.get(w)==null) 
			return false;
		else return true;
	}
}