# process-large-data-with-less-memory
## Technology:
**Java 11**

## Caveat/Assumptions
- Currently, this Algorithm is based on the assumption that the data is almost equally spaced(Line separated) into multiple lines in the files. If not format the file to have data in multiple lines & almost equally spaced
- The need for the above preprocessing is because the data from the file is loaded line by line(Chunk of lines) into the memory and not by byte size
- Edge cases have not been completely tested as this is just a POC and placeholder for enhancement and further Developments using this as a base idea/algorithm

## Steps to run program:
1. Place a file at the project root with the name **input.txt** or place any file at any place and change the constant **INPUT_FILE** in the file **fileoperations.constants.FileConstants.java**
2. Run as a Java main class **fileoperations.MainClass.java**
