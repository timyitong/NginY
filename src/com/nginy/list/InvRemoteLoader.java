package com.nginy.list;
import java.net.URLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
public class InvRemoteLoader{
	private String inv_catelog;
	private String url="http://boston.lti.cs.cmu.edu/Services/search/clueweb09_wikipedia_15p/lemur.cgi";
	private String passwordString="11441:11441";

	public InvRemoteLoader(String inv_catelog,String storedir){
		run();
	}
	private void run(){
		try{
		File [] files=new File("data/inv_local").listFiles();
		String [] names=new String[files.length];
		for (int i=0;i<files.length;i++){		
			names[i]=files[i].getName().substring(0,files[i].getName().length()-4);
		}
		files=null;

		for (int j=0;j<names.length;j++){
			loadOneFile(names[j]+".title");
			loadOneFile(names[j]+".body");
		}
		}catch(Exception e){e.printStackTrace();}
	}
	private void loadOneFile(String name){
		try{
		URL theURL=new URL(this.url+"?g=p&V="+name);
		String encodedPassword=new sun.misc.BASE64Encoder().encode(this.passwordString.getBytes());
		URLConnection urlConnection=theURL.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic " + encodedPassword);
		InputStream content = (InputStream)urlConnection.getInputStream();
		BufferedReader in = new BufferedReader (new InputStreamReader (content));
		BufferedWriter out=new BufferedWriter(new FileWriter(new File("data/inv_web/"+name)));
		String line;
		int begin=0;
		//Find the body part of the web Inverted List and read it line by line
		while ((line = in.readLine()) != null) {
			if (begin==0){
				if (line.matches("^<body>.*")){
					begin=1;
					line=in.readLine();
					out.write(line); out.newLine();
					line=in.readLine();
				}
			}else{
				if (line.matches("^</body>.*"))
					begin=0;
				else{
					out.write(line);
					out.newLine();
				}
			}
		  
		}
		in.close();
		out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}