---
layout: default
title: Download
permalink: /download/
---

# Download & Setup Beat Binder Limited™

Beat Binder Limited™ is currently available as an open-source project on GitLab.  
You can clone, build, and run it yourself following the instructions below.

**[View the GitLab Repository](https://code.cs.umanitoba.ca/comp3350-summer2025/a01-g59-no-merge-conflict)**


---

## Clone the Repository

To get started, clone the project repository:

(You can add these commands here in your preferred code block or terminal style:)

- `git clone https://code.cs.umanitoba.ca/comp3350-summer2025/a01-g59-no-merge-conflict.git`
- `cd a01-g59-no-merge-conflict`

---

## Build and Run

Make sure you have Java 21+ and Gradle installed.

To build the project, run the command to build with Gradle.  
```aiignore
./gradlew build
```
This will run all tests and create an executable JAR at `app/build/libs/app.jar`.

To run the app, run the command to launch the JAR file.
```aiignore
java -jar app/build/libs/app.jar
```
Alternatively, you can run the project directly with Gradle.
```aiignore
./gradlew run
```
Or open the project in your preferred IDE and run the `App.java` main class.

---

## Future Features & Roadmap

- Song, playlist, and album cover art support
- Dark mode UI for easier night-time use
- Text-to-speech accessibility features
- Intelligent random playlist generation

---

## Support

Encounter issues or have questions? Feel free to create an issue on the GitLab repository or contact us at support@example.com.

---

## Links

- [GitLab Repository](https://code.cs.umanitoba.ca/comp3350-summer2025/a01-g59-no-merge-conflict)
- [Project Roadmap & Retrospective](/postmortem/)

