# Bench restTest

## How to install and run

1) Install Maven and Java 8
2) Clone project repository from command, ```git clone https://github.com/dennylee/bench.git```
3) In the project folder, run ```mvn clean package```
4) An executable JAR will be created in the ```/target``` folder.  It will most likely be ```/target/bench-rest-test-1.0.jar```
5) Execute the JAR by running the command ```java -jar <jar file>```

You may run the test cases by executing ```mvn clean test``` or ```mvn clean cobertura:cobertura``` to display code coverage through Cobertura.

## Handling non-200 responses
There are two approaches that could handle non-200 responses: Failed immediately or return what was successfully retrieved.

I chose to failed immediately because I feel it's better to fail fast so the caller can react and handle the failure than to return misleading information (
the total count value is different from the number of transactions it has successfully collected.)

## Data structure and algorithm

### Get transactions
The time complexity is O(n).  Because there's no requirement that the collection of transactions need to be in any order, a simple ArrayList is used.

### Calculate total balance
The time complexity is O(n).  Calculating the total balance will require to iterate through the elements and sum each transaction amount.

### Calculate running daily balance
The time complexity is O(n log n).  
In the method, I chose a sorted map as the return type where the key is the date and the value is the running daily balance.
I chose a sorted map because:

* It helps order the date in a chronological order
* Updating the balance for a date entry can be done in O(log n).  I could use a Set which would give a O(1) complexity, but eventually will require to sort the collection before returning to caller.

## Considerable larger transaction list
The algorithm should be fairly stable as it's not a polynomial complexity algorithm O(n^m).  However, space can be a concern as it needs to hold the collection in memory.
Ideally, it would be better if the list of transactions can be streamed.  However, using streams can increase the solution complexity for a simple problem.
 
## Limitation and trade-offs
* Because the transactions are stored in memory, the number of transactions it can handle is memory bounded.  This is probably the simplest solution, but it won't be able to handle dataset exceeding the memory size.  As mentioned, it could be improved by using stream so that only the calculated values are stored in memory.
* It assumes the total count is the same for all subsequent request API calls.  The assumption helps keeps the scenario cases simple, but can give nondeterministic behaviour as it's relying on the total count to be consistent.
* Blocking calls solution is easier to maintain, but won't be able take advantage of parallel execution and composition.
