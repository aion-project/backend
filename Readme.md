# Backend for project aion

![Travis (.org) branch](https://img.shields.io/travis/aion-project/backend/master)

## How to run

### IntelliJ IDEA

1. Open IntelliJ IDEA and select _File > Open..._.
2. Choose the backend directory and click _OK_.
3. Select _File > Project Structure..._ and ensure that the Project SDK and language level are set to use Java 11.
4. In the Gradle view, double-click `run` under _Tasks > application_ to run the app.

### Command Line

1. `cd` into the project's root directory.
2. Run `./gradlew clean build` on Linux/Mac or `gradlew.bat clean build` on Windows.
3. Run `./gradlew run` on Linux/Mac or `gradlew.bat run` on Windows to run the app.

> For GCP Storage integrations, Service Account credentials file pointed to by the GOOGLE_APPLICATION_CREDENTIALS environment variable is required. 
>
>[Get Credential file from here]("https://cloud.google.com/compute/docs/access/service-accounts")

## Requirements

- JDK 11
- Gradle 5.4

## Authors
- Udesh Kumarasinghe - [ThatUdeshUK]("https://github.com/UdeshUK")
- Sandun Weerasekara
- Ishara Lakshan
- Chamal Pramod