# Getting Started

## Source:
* [Deepsek URL](https://chat.deepseek.com/a/chat/s/5e9399e6-2fe6-4c3f-b90b-009471b14eab)
* [Google Drive Link](https://drive.google.com/drive/folders/16ulD1BLFJRTjk8OTrohUqoomto1xmL1U?usp=sharing)

## Guide
* The CSV file and Python script are in the Google Drive Folder.
* There is a Python script that generates the CSV file with 10k record.

## Run The Project
1. Build

* ```mvn clean package```

2. Run the job
* ```java -jar target\fee-calculator-0.0.1.jar filePath=C:\Users\desab\Downloads\saturday_v2.csv```

## How To See DB Records
* The database is in the data folder from this project.
* Use DBeaver IDE to see the database records.
* Connection type: H2 Embedded. Connect By: Host.
* When creating the connection, use the h2 library version 2
* When selecting the database, remove the extension mv.db (batch_db.mv.db to batch_db)
