<!-- # minor-project-yuvraj-aditya

# 🍽️ MealScale – Smart Recipe & Cooking Guide

**Developed by:** Yuvraj Saini (23FS10BCA00057)  
**Semester:** BCA – V Sem  
**Subject Code:** CA3170  

**Developed by:** Aditya Gandhi (23FS10BCA00009)  
**Semester:** BCA – V Sem  
**Subject Code:** CA3170 



## 📖 Overview
#MealScale is an Android app that personalizes cooking by scaling recipes based on available ingredients, guiding users step-by-step, and including a built-in timer.

🍳 Features
•	Splash Screen: A welcoming splash screen (activity_login.xml) to launch the app.
•	User Login: A simple login interface to get started. (Note: Functionality to be expanded in future versions).
•	Recipe Dashboard: A main screen (activity_main.xml) to display and browse a wide variety of recipes.
•	Extensive Recipe Database: Includes dozens of recipes with images, from aloo_gobi to veg_spring_rolls.
•	Custom UI: Features custom-styled elements like gradient backgrounds (gradient_background.xml).
🛠️ Technologies Used
•	Language: Kotlin
•	Platform: Android Native
•	IDE: Android Studio
•	UI: XML (eXtensible Markup Language)
•	Build System: Gradle

🚀 How to Run
To run this project:
1.	Clone the repository:
2.	git clone [https://github.com/Yuviiii67/minor-project-yuvraj-aditya.git](https://github.com/Yuviiii67/minor-project-yuvraj-aditya.git)
3.	Open in Android Studio:
o	Open Android Studio.
o	Select "Open" or "Open an existing Android Studio project".
o	Navigate to the cloned minor-project-yuvraj-aditya folder and select it.
4.	Sync Gradle: Let Android Studio build the project and sync the Gradle files.
5.	Run: Select an emulator or a physical device and click the "Run" button.



📸 Screenshots
                         



## 📁 File Structure Here is the high-level structure of the `app/src` directory, which contains all the source code for the application. ```bash
Tree structure of the recipe app
app
└───src
    ├───androidTest
    │   └───java
    │       └───com/example/myapplication
    │               └── ExampleInstrumentedTest.kt
    │
    ├───main
    │   │   AndroidManifest.xml
    │   │
    │   ├───java
    │   │   └───com/example/myapplication
    │   │           LoginActivity.kt
    │   │           MainActivity.kt
    │   │
    │   └───res
    │       ├───drawable
    │       │       aloo_gobi.png
    │       │       paneer_butter_masala.png
    │       │       button_gradient.xml
    │       │       ... (and 30+ other recipe images)
    │       │
    │       ├───layout
    │       │       activity_login.xml
    │       │       activity_main.xml
    │       │
    │       ├───mipmap-anydpi-v26
    │       │       ic_launcher.xml
    │       │       ic_launcher_round.xml
    │       │
    │       ├───mipmap-hdpi (and mdpi, xhdpi, etc.)
    │       │       ... (app launcher icons)
    │       │
    │       ├───values
    │       │       colors.xml
    │       │       strings.xml
    │       │       themes.xml
    │       │
    │       ├───values-night
    │       │       themes.xml
    │       │
    │       └───xml
    │               backup_rules.xml
    │               data_extraction_rules.xml
    │
    └───test
        └───java
            └───com/example/myapplication
                    └── ExampleUnitTest.kt


Key Folders
•	src/main/java: Contains all the Kotlin source code (.kt files), like MainActivity.kt and LoginActivity.kt.
•	src/main/res: Contains all non-code resources.
o	res/drawable: All images and custom shapes used in the app.
o	res/layout: The XML files that define the app's user interface (screens), like activity_main.xml.
o	res/mipmap: The app's launcher icons for different device densities.
o	res/values: Core app values like colors (colors.xml), text strings (strings.xml), and styles (themes.xml).
•	src/main/AndroidManifest.xml: The central file that describes the app's components (like its activities) and permissions to the Android system.
•	androidTest & test: Folders for writing automated tests (unit tests and instrumentation tests).

-->
