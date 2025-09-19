Of course\! Here is the `README.md` file for your project in English.

-----

# WorkoutSmart KMP

WorkoutSmart is a cross-platform application (Android and iOS) developed with Kotlin Multiplatform and Compose Multiplatform. It allows users to create, manage, and track their workouts in a simple and efficient way.


## Features

The application offers a complete experience for workout management, including:

* **🏋️‍♂️ Create Custom Workouts:**

    * Set a name and description for each workout.
    * Add detailed exercises, including name, notes, number of sets, and repetitions.
    * Attach a photo to each exercise for easy reference.

* **▶️ Execute Workouts:**

    * Start a saved workout and track the total exercise time.
    * Mark exercises as completed to track your progress.
    * Use a configurable rest timer between sets.
    * Skip the rest time when you're ready for the next set.

* **📈 Activity History:**

    * View a history of all completed workouts, ordered by date.
    * Track the date and name of each workout performed.

* **🗑️ Manage Workouts:**

    * View all your saved workouts on the main screen.
    * Delete workouts that are no longer needed.

## Architecture and Technologies

This project was built using a modern, cross-platform approach with the following technologies and libraries:

* **Language:** [Kotlin](https://kotlinlang.org/)
* **Cross-Platform Framework:** [Kotlin Multiplatform (KMP)](https://www.google.com/search?q=https://kotlinlang.org/docs/multiplatform-mobile-overview.html)
* **User Interface:** [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) to share the UI between Android and iOS.
* **Navigation:** [Decompose](https://github.com/arkivanov/Decompose) for shared, component-based navigation.
* **Dependency Injection:** [Koin](https://insert-koin.io/) for lightweight dependency management in the shared code.
* **Database:** [SQLDelight](https://github.com/cashapp/sqldelight) to generate type-safe Kotlin APIs from SQL statements.
* **Coroutines:** For managing concurrency and asynchronous operations.
* **Image Loading:** [Coil](https://coil-kt.github.io/coil/compose/) for efficient image loading.
* **Image Picker:** [Peekaboo](https://github.com/onseok/peekaboo) for an easy, cross-platform image selection.
* **Serialization:** [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) for serializing Kotlin objects.
* **Date and Time:** [Kotlinx DateTime](https://github.com/Kotlin/kotlinx-datetime) for date and time manipulation.

## How to Run

To build and run this project, you will need:

* Android Studio
* Xcode (for the iOS version)
* JDK 1.8 or higher

**Steps:**

1.  Clone the repository.
2.  Open the project in Android Studio.
3.  To run the Android version, select the `composeApp` module and run it on an emulator or a physical device.
4.  To run the iOS version, open the project in Xcode and run it on a simulator or a physical device.