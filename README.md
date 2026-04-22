## _Beat Binder Limited (BBL)™_

Beat Binder Limited is a music manager to help you organise your favourite songs. Add songs to your
collection, create playlists to group your songs, and add tags and notes to songs to personalise
your experience.

## Website
To view our website, download the repo and navigate to the website folder where you can run the Jekyll command to build and serve the site:
`bundle exec jekyll serve`
This will require you to have Ruby installed on your machine. But after running this command you can view the website at http://127.0.0.1:4000/

# Building
Building is handled by Gradle. To build the project, clone `..\a01-g59-no-merge-conflict\` and run
the following command in the terminal:
```bash
./gradlew build
```

This will run all tests, including system tests, which take a couple minutes to complete.
To bypass this, you can build the project without running the tests with:
```bash
./gradlew build -x test
```

This will generate `app.jar` in `app/build/libs/`. 

To run `app.jar` and get the application started up, you can run the command
in the terminal:
```aiignore
java -jar app/build/libs/app.jar
```
This is if you are at the root directory of the project (`\a01-g59-no-merge-conflict`)

After building, you can also run the project with this command in the terminal:
```bash
./gradlew run
```

Otherwise you can run the project via your favourite IDE by navigating to App.java
in `/app/src/main/java/beatbinder` and clicking the play button.

There was an issue found while building with tests. Sometimes, the first system test will fail
because the application window would open behind all other windows, which would prevent the robot
from interacting with the interface. A workaround for this issue is to have no windows open on the
screen that the application window will appear. On a single monitor, this would mean running the build
command and then minimizing all windows.

# Guiding Documents
Our [vision statement](/docs/VISION.md) explains what we intend to achieve with our project. The
project's [architecture diagram](/docs/ARCHITECTURE.md) explains how the project is structured and
how it operates. Our [code standards](/docs/CODE-STANDARDS.md) lay out how code should be written
to meet the standards set by the rest of the codebase.

## Other Documents
A retrospective on the project's Iteration 2 can be found [here](/docs/RETROSPECTIVE.md).

# Support
If an issue is encountered, create an issue for **A01-G59-No Merge Conflict** describing what the
issue was and, if possible, what caused it. It will be fixed as soon as possible.

# Roadmap
- Song, playlist and album covers to give something more to look at.
- Dark mode for a display that's easier on the eyes.
- Text to speech for the visually impaired.
- Random playlist generation which uses selected criteria to build a tailored playlist.

# Contributors
A big thanks to our treasured contributors, who took valuable time out of their days at their
7-figure day jobs to build this project.
- Jeevan
- Jonas
- Luke
- Theo