SPRING BOOT IMPORT EXCEL TRANSACTIONS TO ORACLE DB
The project requires to tranfer all the data from excel file to oracle DB maybe up to 10m rows, so the main logic here are:
Make sure the project can read file excel(WorkBook and StreamingReader) making loop all the rows in it => Check the tranx_Time (LocalDateTime), amount (money) format => If that row valid then make them become an object and put them into valid list => Check “tranx_time” and “amount” if they are valid => If the list reach 5000 objects, batch insert them into OracleDB then clean the list, keep continue the loop => If after the loop end there are no more rows in excel file and the valid list didn’t reach 5000 objects yet still having objects in it, call batch insert all the remaining of that valid list.

TECHNOLOGIES AND LIBRARY USED IN THIS PROJECT
     Frameworks:
	- JDK 21: One of the newest java stable framework to work with.
  - Spring Boot: To manage dependencies and injection and making java project become more modern.

     Libraries:
  - Apache POI: To work with office file such as excel, word,....
  - StreamingReader: Normal apache poi WorkBook can’t handle if the excel file too big, so StreamingReader is the solution. It can handle massive excel file without worrying about out of memory. But due to StreamingReader is too old so we have to downgrade apache POI to 4.1.2 version.
  - Oracle JDBC11 Driver: Basic connection and work with Oracle DB
  - Starter-JDBC: Clean code connection with Oracle JDBC in project, with this we don’t have to write all what JDBC connection required.
  - Starter-Web: To make a basic web FE to test REST and to make user can be more comfortable with using this project.

    Tools:
	- Lombok: To make code become cleaner especially in model without write getter, setter, constructor too long.
	- Maven: Manage all the library

DURATION OF THE PROJECT
To learn and make this project: 2 weeks and 1 day
-	10 days to learn and to familar with java syntax, spring, library, project structure, annotation, different between spring java and nodejs, what are the laws of it,.... => After 10 days know how to do batch Insert and CRUD with spring java and done a basic web.
-	4 days to make this project completely.
-	1 day to fix the bugs and upgrade it to handle from 50k to 1m rows

A.I FOR LEARNING AND MAKING THIS PROJECT
- Claude: ask for logics of the project and fixing code (not much)
- Perplexity: ask for docs to read (avarage)
- Gemini (Deep thinking and Pro): logic, how to use library, explaining code, generate 50k rows excel file (mostly)
=> Not using much claude because this project is small, gemini explaining better.
=> At first i just using perplexity to search docs to learn, asking how the logic of the project but the speed is too slow, 7 days just can only familar with java syntax and how it work, there too many library and tools to learn. So i decide to used AI on the next 8 days to learn and my learning speed really has increased faster.
=> Sadly i want to learn and do it by not using AI but i have to, since there are too many thing, library i don’t know yet or forgot like StreamingReader, handle duplicate with hashSet and merrge, how to solve excel problem with format similar to LocalDateTime(it keep auto reformat tranx_time data), generate excel file and the index.html to test.

HOW TO RUN THIS PROJECT
Step 1: git clone it to your pc in any folder you want to.
Step 2: Open it and put file application.properties or yml in resources folderm for example this is my application.properties, username and password are for connection with Oracle DB.

spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
spring.datasource.username=Your-username
spring.datasource.password=Your-password
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.servlet.multipart.max-file-size=500MB
spring.servlet.multipart.max-request-size=500MB
  

Step 3(only if you don’t have any excel with too much rows to test): open DataGenerator in the same folder with BankApplication(org.example), run the main function and it will generate an excel file in the same folder root, pom.xml. You can also edit in the loop to change 1m to any excel size you want(maximum is 1m rows per excel file). After you successfully generate an excel file, it will automatic make amount data entire collumn contain “,” so we have to solve it. Open that excel file, click to the collumn ”D” => right mouse => formatCell => number => change decimal places to “0”.

Step 4: run the main function in BankApplication, once it run come to the localhost:8080 and you will see 3 buttons. 
+ Button “choose file”: Uploading file excel you want.
+ Button “Upload and save to Oracle DB”: import all the data from the excel file you choose tranfer them to Oracle
+ Button “Delete all data in Oracle”: delete all the data in the table MB_TRANSACTION of Oracle, with this button it allow you to use the same excel file or using that DataGenerator without worrying about unique of TRACE in MB_TRANSACTION table.
 

Step 5: After imported the excel file and clicked upload it, you will see in the terminal how many rows success and failed, dupicate “trace” in excel file and Oracle DB, duration of the function tranfering data from that excel file to Oracle. 
     Note: If any row wrong format “tranx_time” or “amount”, it will print error of that row number and tell which one wrong format(maybe both) then that row will not insert to the Oracle; also if “trace” duplicate in excel file it will have hashSet check on it, if not but Oracle DB already have that “trace” in it, then with merge we can check condition between trace in excel and in Oracle whenever they are matched or not. With hashSet and merge sql command we can stop worry about duplicate “trace” will cause function to stop.

TEST CASE AND VALIDATION
     Case 1: Standard import excel file
	- Input: Excel file with 50k valid rows.
	- Result: Done without any problem, duration avarage is 5s.

     Case 2: File excel too big
	- Input: A massive excel file contain around 1 million rows.
	- Result: StreamingReader solve it, no “OutOfMemory” error.

     Case 3: Duplicate Trace in excel
	- Input: File excel contains mulitple trace with the same data.
	- Result: HashSet will check and detect if any trace duplicate exactly the trace it had seen, if duplicate it will skip that row and output which row it skipped.

     Case 4: Duplicate Trace in Oracle DB
	- Input: Trace in excel file have the same ID that already exist in Oracle DB.
	- Result: sql command with MERGE will check the trace in excel and see if it matched any trace in Oracle DB, if it matched, skip that row and continue process next rows without error “UNIQUE CONSTRAINT”.

     Case 5: Validation tranx_Time data format
	- Input: Tranx_Time column contain invalid date or invalid format 
	- Result: The system will search if tranx_Time in excel familar to LocalDateTime. If any row invalid then skip that row then report which row error tranx_time format. 

     Case 6: Validation amount data format
	- Input: Amount column contains non-numeric data or “,”
	- Result: The system will check if the amount data contain any thing that not BigDecimal. If any row invalid then skip that row then report which row error amount format.

WHAT NEED TO IMPROVE THE PROJECT TO MATCH THE REQUIREMENT 10M RECORDS EXCEL
- MultiThread.
- Handle multiple excel files since the requirement need to be 10 files, each contain 1m rows.
- Need to improve the speed since 1m rows file need around 70s to read.
- FE (Optional).

ISSUE MAY HAPPEN IN THE FUTURE
- Library: StreamingReader working okay but we have to downgrade apache POI to 4.1.2 version to used it, may have some issue with excel nowaday since the libaries are too old.
- Memory: StreamingReader manage 1m rows excel file but it run avarage 28% of my CPU (16GB  Ram) so who know what will happen if we update the project to make it work with 10m rows file.

WHAT I HAD GAINED FROM THIS PROJECT
- Adapt good with Java and Spring Boot => confidence enough to make java website.
- Optimize speed of CRUD (Batch Insert).
- Must expect the case limit of memory (excel file too big to handle).
- More understand the structure of java code.
 
WHAT NEED TO IMPROVE
- Learning speed still too slow,  i lost 10 days just to understand all the basic of spring web.
- There are too much libraries that i don’t know that i need to learn them to make a java spring project properly.

P/S
This is 100% my writing, i admit that i have basic english skills to reading, speaking, hearing enough to communicate but writing is my the worst skill:P
Althought i didn’t manage to process 10m rows of excel file but i found this project kinda interesting and deeper than i thought, i like this project.
