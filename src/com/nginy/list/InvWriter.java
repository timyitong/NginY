package com.nginy.list;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.util.StringTokenizer;
import java.util.LinkedList;
public class InvWriter{
	private BufferedWriter bw;
	private BufferedReader br;
	private String read_url;
	public InvWriter(String read_url){
		this.read_url=read_url;
		readFiles();
	}
	private void readFiles(){
		try{
		File [] file_list=new File(read_url).listFiles();
		String filename=null;
		String field=null;
		String write_url=null;
		String word=null;
		String line;
		StringTokenizer st=null;
		int j=0;
		for (int i=0;i<file_list.length;i++){
			filename=file_list[i].getName();
			/* This is to avoid a special problem in OS X
				 that somtimes the  system automatically generates a .DS_Store hidden file */

			if (!filename.equals(".DS_Store")){
				word=filename.substring(0,filename.indexOf('.'));
				field=filename.substring(filename.indexOf('.'),filename.length());
				br=new BufferedReader(new FileReader(new File(read_url+"/"+filename)));
				line=br.readLine();
				st=new StringTokenizer(line);
				st.nextToken();
				int doc_len=Integer.parseInt(st.nextToken());
				int term_f=0;
				int doc_id=0;
				int term_f_sum=0;
				LinkedList <int []> inv_list=new LinkedList <int []> ();
				while((line=br.readLine())!=null){
					st=new StringTokenizer(line);
					term_f=(st.countTokens()-7)/2+1;
					/*The document sometimes has one more void line, to avoid this, we need to ensure term_f is a positive figure*/
					if (term_f>=1) { 
						int [] chunk=new int[term_f+3];
						term_f_sum+=term_f;
						doc_id=Integer.parseInt(st.nextToken());
						chunk[0]=doc_id;
						chunk[1]=term_f;
						chunk[2]=0; // here we want to be compatible with the XXX.inv lists, even we do not know the doc_len, we add a 0
						for (j=3;j<term_f;j++){
							chunk[j]=Integer.parseInt(st.nextToken());
						}
						inv_list.add(chunk);
					}
				}
				writeOneFile(inv_list,filename,term_f_sum);

			}
		}
		}catch(Exception e){e.printStackTrace();}
	}

	private static void writeOneFile(LinkedList <int []> inv_list,String filename,int term_sum){
		try{
		String word=filename.substring(0,filename.indexOf('.'));
		String field=filename.substring(filename.indexOf('.')+1,filename.length());
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File("data/"+field+"_local/"+filename)));
		bw.write(word+" "+word);
		bw.write(" "+term_sum);
		bw.write(" "+0); //here just want to be consistent with the .inv lists
		bw.newLine();
		int [] chunk=null;
		while ((chunk=inv_list.pollFirst())!=null){
			bw.write(chunk[0]+" ");
			bw.write(chunk[1]+" ");
			bw.write(chunk[2]+" ");
			for (int j=0;j<chunk[1];j++){
				bw.write(Integer.toString(chunk[j+2])); // if not converted to String, it will write bytes
				if (j!=chunk[1]-1)
					bw.write(" ");
			}
			if (inv_list.size()!=0)
				bw.newLine();
		}
		bw.close();
		}catch(Exception e){e.printStackTrace();}
	}

}