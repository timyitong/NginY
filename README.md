/** Application: NginY (Search Engine of Yi)
	Author: Yitong Zhou

	This application uses Apache-Ant to build and run, see build.xml of the specific configuration
	Just run the following command in the NginY directory
		$: ant
	OR 
		$: ant run
	


	If you want to execute it in direct command line, it should be:

	$:	java -jar -Xms256M -Xmx512M build/jar/NginY.jar


	If you occur to a OutOfMemory error, please expand the memory to, -Xms1G -Xmx2G, which may be most safely.
		(Directly replace this two sections in command line or build.xml)

	
*/