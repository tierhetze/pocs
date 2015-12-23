

s.avenge - directory encription (scala+java)

The utility encodes all small (less than 10 Mb) and not executable files
   
in the specified directory recursively!
  
The output file, called avenge.txt has all the required information to rollback the changes(restore encoded files)

You better not to miss avenge.txt file!

Or...you delete the file and changes you done become PERMANENT!!!
  

Run it: 

java -jar s.avenge.jar   (Java 8 required!)

Not to use with Java 7!!! It will fail in the middle. 



