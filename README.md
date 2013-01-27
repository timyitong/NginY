Application: 	NginY (Search Engine of Yi)

Author: Yitong Zhou

Git: https://github.com/timyitong/NginY

How to Run:
	This application uses Apache-Ant to build and run, see build.xml of the specific configuration
	Just swith to the root directory of NginY, run the following command:

	$NginY: ant
	
	OR 
	
	$NginY: ant run
	
	If you want to execute it in direct command line, it should be 
	
	(notice you need extra memory larger than the default JVM settings):

	$NginY:	java -jar -Xms256M -Xmx512M build/jar/NginY.jar

	If you occur to a OutOfMemory error, please expand the memory to -Xms1G -Xmx2G
	
	(Directly replace this two sections in command line or build.xml)


The directory details:

	.					Root
	
	./build.xml				Apache-ant build file
	
	./build/classes				classes
	
	./build/jar				jar file
	
	./src/com/nginy				Source code

	./data					Data Folder

	./data/queries				Query Folder
    	
	./data/inv_local			Inverted Lists
		    		
		body_local
    	  	
		title_local
    	   	
		inv_web
    	
	./sample_result_file			Sample Results File Folder*NOTICE*
   	
   	./data/result				Query Results Folder *The results will defaultly be put here*
    	
	./data/stoplists			Stop List
