# Countdown Widget for Android

A simple, customizable 1x1 home screen widget for Android that counts the days until or since a
specific date. This project is an excellent starting point for anyone looking to learn Android
widget development with Kotlin.

---

## ✨ Features

* **Daily Countdown:** Automatically updates every day to show the number of days until a future
  date or since a past date.
* **Fully Customizable:** Use the configuration screen to:
    * Pick any target date.
    * Set a custom label or title.
    * Choose a unique color for the widget's background.
* **Dynamic Theming:** The widget's circular background and a slightly darker stroke are generated
  dynamically based on the user's color choice.
* **Reconfigurable:** Simply tap the widget on your home screen at any time to re-open the
  configuration and change the date, text, or color.
* **Widget-Only Application:** The project has no main launcher activity, making it a true
  background utility.

---

## 🛠️ How to Build

To build and run this project yourself, you will need:

1. **Android Studio:** The latest stable version is recommended.
2. **An Android Device or Emulator:** The widget is designed for API 26 (Android 8.0 Oreo) and
   higher.

**Steps:**

1. **Clone the repository:**
   ```bash
   git clone https://github.com/rickscherer/CountdownWidget.git
   ```
2. **Open in Android Studio:**
    * Launch Android Studio.
    * Select "Open an existing project".
    * Navigate to the cloned repository folder and open it.
3. **Sync Gradle:**
    * Android Studio will automatically sync the project's dependencies. If not, go to **File > Sync
      Project with Gradle Files**.
4. **Set the Run Configuration:**
    * Since this is a widget-only app, you need to tell Android Studio not to launch an activity on
      run.
    * Go to **Run > Edit Configurations...**.
    * Under **Launch Options**, set **Launch** to **Nothing**.
    * Click **Apply** and **OK**.
5. **Run the App:**
    * Click the green "Run" button (▶️) to install the app on your device or emulator.
    * Navigate to your home screen, open the widget picker, and add the "Countdown Widget" to your
      screen.

---

## 🚀 Key Technologies Used

* **Kotlin:** The modern, official language for Android development.
* **Android SDK:** For core application functionality.
* **App Widgets (`AppWidgetProvider`):** The framework for building home screen widgets.
* **`RemoteViews`:** Used to manage the widget's layout, which runs in a separate process.
* **`AlarmManager`:** To schedule precise daily updates for the widget.
* **`SharedPreferences`:** For persisting each widget's unique configuration (date, text, and
  color).
* **[Android Color Picker](https://github.com/QuadFlask/colorpicker):** A third-party library for
  the user-friendly color selection dialog.

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
