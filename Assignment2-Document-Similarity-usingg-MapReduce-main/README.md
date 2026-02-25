# Assignment 2: Document Similarity using MapReduce

**Name:** 
Medha Karthik

**Student ID:** 
801238132

## Approach and Implementation

### Mapper Design

The Mapper class follows the logic of transforming the input into pairs of documents with their word sets. The input key-value pair is the Document ID followed by the text in the document. The mapper reads each document, extracts the document ID and its set of words, and stores them in memory. After reading all documents, the mapper generates all unique pairs of document IDs. The class emits as its output key-value pair the key and the two sets of words (one from each document) as the value. This helps in solving the overall proble by preparing the data for the reducer, which will calculate the Jaccard similarity for each document pair using the word sets provided.

### Reducer Design

The Reducer class transforms the word sets into a meaningful similarity metric that quantifies how similar the two documents are, based on vocabulary. The input key-value pair is a pair of documents and the words from both documents, separated by a pipe. Values for a given key are processed as follows: the reducer class iterates through each value and splits by "|" to separate the documents word sets. Then, two HashSet objects are created to store the words from each document. The function retainAll() is used to generate a set of common words between the two documents, and the function addAll() is used to generate a set of all unique words from both documents. These values are then divided to calculate the Jaccard Similarity score. The final output is the document pair and their similarity. 

### Overall Data Flow

Data begins flowing from the initial raw text documents, in this case input.txt, which holds all of the documents. The Mapper is used to read each line from the input file and extract the document ID and its words. Then, in the shuffle/sort phase, all of the values are grouped by their key (the document pair) before being sent to the Reducer. The Reducer computes the Jaccard similarity for each pair and then produces the final output.

## Setup and Execution

### ` Adjusted Commands `

### 1. **Start the Hadoop Cluster**

Run the following command to start the Hadoop cluster:

```bash
docker compose up -d
```

### 2. **Build the Code**

Build the code using Maven:

```bash
mvn clean package
```

### 3. **Copy JAR to Docker Container**

Copy the JAR file to the Hadoop ResourceManager container:

```bash
docker cp target/DocumentSimilarity-0.0.1-SNAPSHOT.jar resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 4. **Move Dataset to Docker Container**

Copy the dataset to the Hadoop ResourceManager container:

```bash
docker cp data/input.txt resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 5. **Connect to Docker Container**

Access the Hadoop ResourceManager container:

```bash
docker exec -it resourcemanager /bin/bash
```

Navigate to the Hadoop directory:

```bash
cd /opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 6. **Set Up HDFS**

Create a folder in HDFS for the input dataset:

```bash
hadoop fs -mkdir -p /input/data
```

Copy the input dataset to the HDFS folder:

```bash
hadoop fs -put ./input.txt /input/data
```

### 7. **Execute the MapReduce Job**

Run your MapReduce job using the following command: Here I got an error saying output already exists so I changed it to output2 instead as destination folder

```bash
hadoop jar /opt/hadoop-3.2.1/share/hadoop/mapreduce/DocumentSimilarity-0.0.1-SNAPSHOT.jar com.example.controller.DocumentSimilarityDriver /input/data/input.txt /output2
```


### 9. **View the Output**

To view the output of your MapReduce job, use:

```bash
hadoop fs -cat /output2/*
```

### 10. **Copy Output from HDFS to Local OS**

To copy the output from HDFS to your local machine:

1. Use the following command to copy from HDFS:
    ```bash
    hdfs dfs -get /output2 /opt/hadoop-3.2.1/share/hadoop/mapreduce/
    ```

2. use Docker to copy from the container to your local machine:
   ```bash
   exit 
   ```
    ```bash
    
    docker cp resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/output2/ data/output/
    ```
3. Commit and push to your repo so that we can able to see your output


---

## Challenges and Solutions

The biggest challenge I faced while completing this assignment was with my environment. When initially creating my file structure, I had incorrectly nested the driver, mapper, and reducer java files in \src\main\com\example. This caused a ClassNotFoundException when I was attempting to execute the map reduce job, as my JAR did not contain the compiled class in the correct package structure, a common source folder structure problem with Maven. To overcome this challenge, I restructured my repository to include \src\main\java\com\example, which allowed the code to run correctly. 

---
## Input

**Input from `input.txt`**
```
Document1 This is a sample document containing words
Document2 No sample documents here
Document3 Lost of testing to do

```
## Sample Output
```
Document2,Document1	Similarity: 0.18
Document3,Document1	Similarity: 0.20
Document3,Document2	Similarity: 0.10
