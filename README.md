# 🍽️ MealScale – Smart Recipe & Cooking Guide

---

### 👨‍💻 Developed By
- **Yuvraj Saini (23FS10BCA00057)**  
  **Semester:** BCA – V Sem  
  **Subject Code:** CA3170  

- **Aditya Gandhi (23FS10BCA00009)**  
  **Semester:** BCA – V Sem  
  **Subject Code:** CA3170  

---

## 📖 Overview

**MealScale** is an Android application that personalizes your cooking experience.  
It helps users **scale recipes based on available ingredients**, **adjust serving sizes**, and **follow step-by-step cooking instructions**.  
With a modern yellow-themed UI and smart features, it’s a simple yet elegant cooking companion app.

---

## 🍳 Features

✅ **Splash Screen** – A welcoming introduction that loads the app.  
✅ **User Login System** – Basic login page to enter user details (for future authentication integration).  
✅ **Recipe Dashboard** – Displays a variety of recipes with images and names.  
✅ **Category Filter** – Allows browsing recipes by category (Veg, Non-Veg, Snacks, etc.).  
✅ **Dynamic Ingredient Scaling** – Adjust ingredient quantities for 2, 4, or 200 people.  
✅ **Step-by-Step Guide** – Helps users follow the cooking process visually.  
✅ **Custom UI & Gradients** – Uses gradient backgrounds and vibrant color themes.

---

## 🛠️ Technologies Used

| Component | Description |
|------------|-------------|
| **Language** | Kotlin |
| **Platform** | Android Native |
| **IDE** | Android Studio |
| **UI Design** | XML (eXtensible Markup Language) |
| **Build System** | Gradle |

---

## 🚀 How to Run the Project

1. **Clone the repository**
   ```bash
   git clone https://github.com/Yuviiii67/minor-project-yuvraj-aditya.git


minor-project-yuvraj-aditya/
│
├── app/
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml          # App manifest describing components & permissions
│       │   ├── java/com/example/myapplication/
│       │   │   ├── MainActivity.kt          # Main screen (Recipe Dashboard)
│       │   │   └── LoginActivity.kt         # Login page logic
│       │   │
│       │   └── res/
│       │       ├── drawable/                # All images, gradients, icons
│       │       │   ├── button_gradient.xml
│       │       │   ├── aloo_gobi.png
│       │       │   └── veg_spring_rolls.png
│       │       │
│       │       ├── layout/                  # XML layout files for UI screens
│       │       │   ├── activity_login.xml
│       │       │   └── activity_main.xml
│       │       │
│       │       ├── values/                  # Colors, strings, themes
│       │       │   ├── colors.xml
│       │       │   ├── strings.xml
│       │       │   └── themes.xml
│       │       │
│       │       └── xml/                     # App data rules
│       │           ├── backup_rules.xml
│       │           └── data_extraction_rules.xml
│       │
│       ├── androidTest/                     # Instrumentation tests
│       └── test/                            # Unit tests
│
└── screenshot/                              # Folder containing all app screenshots

splashscreen.png
login_page.png
login_page2.png
home_page.png
category.png
recipe.png
Ingredients1.png
Ingredients2.png
Serving_people_for_2.png
stet_bystep.png
Ingredients_200.png
grid.png
mobile.png
