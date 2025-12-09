# Hijri Date Picker

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-purple.svg?logo=kotlin)]()
[![Java](https://img.shields.io/badge/Java-11-orange.svg?logo=java)]()
[![Donate](https://img.shields.io/badge/Donate-PayPal-blue.svg?logo=paypal)](https://www.paypal.com/paypalme/AbdulrahmanBahamel)

A modern and customizable Hijri Date Picker for Android, built with Jetpack Compose and inspired by the Material3 Date Picker.

The **Hijri Date Picker** allows users to select dates in the **Hijri calendar** with ease. It's designed to be **simple, flexible, and customizable**, making it easy for developers to integrate into their apps. This library fills the gap for modern, high-quality date pickers with Hijri calendar support, providing a seamless experience for Muslim users.

---

## Screenshots

Here are examples of the **Hijri Date Picker** in action:

Date Picker (Arabic/English):

![Hijri Date Picker AR](screenshots/hijridatepicker-picker-ar.jpg)![Hijri Date Picker EN](screenshots/hijridatepicker-picker-en.jpg)

Date Range Picker (Arabic/English):

![Hijri Date Range Picker AR](screenshots/hijridatepicker-rangepicker-ar.jpg)![Hijri Date Range Picker EN](screenshots/hijridatepicker-rangepicker-en.jpg)

Text Input Variants (Arabic/English):

![Input Range AR](screenshots/hijridatepicker-rangeinput-ar.jpg)![Input EN](screenshots/hijridatepicker-input-en.jpg)

> **Note:** The multi-date picker uses the same visual style as the single-date picker, but allows selecting multiple, non-contiguous Hijri dates.

---

## Table of Contents

- [Features](#features)
- [Usage](#usage)
    - [Single Date Selection](#single-date-picker-dialog)
    - [Date Range Selection](#range-picker-dialog)
    - [Multi-Date Selection](#multi-date-picker)
- [Installation](#installation)
- [Support Me](#support-me)

---

## Features

- **Modern Design** built with Material3 components.
- **Customizable** UI, locale, and behavior.
- **Single Date Selection**
- **Range Selection**
- **Multi-Date Selection (NEW)**
- **Text Input Mode**
- Uses **HijrahDate** from Java Time API.

---

## Usage

The library provides three main components:

1. `HijriDatePicker` – Single date selection.
2. `HijriDateRangePicker` – Range selection.
3. `HijriMultiDatePicker` – Multiple individual dates.

They are all Composable components, allowing you to use them as dialogs, bottom sheets, or standalone elements.
---

### Single Date Picker Dialog
Here’s how to set up the HijriDatePicker for selecting a single date:

```kotlin
@Composable
fun DatePickerExample() {
    val datePickerState = rememberHijriDatePickerState(
        initialSelectedDate = HijrahDate.now(), // Default selected date (optional)
        selectableDates = HijriSelectableDates { date ->
            // Allow all dates, but customize this to restrict selectable dates
            true
        },
        initialDisplayMode = DisplayMode.Picker // Start in calendar view (default is Picker)
    )

    var selectDateDialogOpen by remember { mutableStateOf(false) }

    // Render the HijriDatePicker inside a dialog when `selectDateDialogOpen` is true
    if (selectDateDialogOpen) {
        DatePickerDialog(
            onDismissRequest = { selectDateDialogOpen = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDate?.let { date: HijrahDate ->
                            // Do something with the selected date
                        }
                        selectDateDialogOpen = false
                    },
                    content = { Text(stringResource(R.string.ok)) }
                )
            },
        ) {
            HijriDatePicker(
                state = datePickerState,
                /* You can customize some appearance properties */
            )
        }
    }

    /*
     * Other code, You can show the dialog by setting `selectDateDialogOpen` to true on a button click or any other event
     */
}
```

---

### Range Picker Dialog
Here’s an example of how to implement a HijriDateRangePicker for selecting a date range:

```kotlin
@Composable
fun DateRangePickerExample() {
    var selectRangeDialogOpen by remember { mutableStateOf(false) }

    // State to manage the selected Hijri date range
    val rangePickerState = rememberHijriDateRangePickerState(
        initialSelectedRange = null, // Optional: Pre-select a date range
        selectableDates = HijriSelectableDates { date ->
            // Optional: Implement your own selectable date logic
            true // Select all dates for the range
        },
        //Other params ...
    )
    // Render the HijriDateRangePicker inside a dialog when `selectRangeDialogOpen` is true
    if (selectDateDialogOpen) {
        DatePickerDialog(
            onDismissRequest = { selectDateDialogOpen = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedRange: SelectedDateRange? = datePickerState.getSelectedDateRange()
                        selectedRange?.let { range: SelectedDateRange ->
                            //Do something with the selected date range
                        }
                        selectDateDialogOpen = false
                    },
                    content = { Text(stringResource(R.string.ok)) }
                )
            },
        ) {
            HijriDateRangePicker(state = datePickerState /* You can customize some appearance properties */)
        }
    }

    /*
     * Other code, You can show the dialog by setting `selectDateDialogOpen` to true on a button click or any other event
     */
}
```

---

### Multi-Date Picker (NEW)
Here’s an example of how to implement a HijriMultiDatePicker for selecting multiple different dates:

```kotlin
@Composable
@Composable
fun MultiDatePickerExample() {
    var selectMultiDatesDialogOpen by remember { mutableStateOf(false) }

    // State to manage the selected Hijri dates (multi-select)
    val multiDatePickerState = rememberHijriMultiDatePickerState(
        initialSelectedDates = emptySet(), // Optional: pre-select multiple dates
        selectableDates = HijriSelectableDates { date ->
            // Optional: Implement your own selectable date logic
            true // Allow all dates to be selected
        },
        // Other params ...
    )

    // Render the HijriMultiDatePicker inside a dialog when `selectMultiDatesDialogOpen` is true
    if (selectMultiDatesDialogOpen) {
        DatePickerDialog(
            onDismissRequest = { selectMultiDatesDialogOpen = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDates = multiDatePickerState.selectedDates
                        // Do something with the selected dates (e.g., save them in state or DB)

                        selectMultiDatesDialogOpen = false
                    },
                    content = { Text(stringResource(R.string.ok)) }
                )
            }
        ) {
            HijriMultiDatePicker(
                state = multiDatePickerState
                // You can customize some appearance properties here if needed
            )
        }
    }

    /*
     * Other code: You can show the dialog by setting `selectMultiDatesDialogOpen` to true
     * on a button click or any other event, for example:
     *
     * Button(onClick = { selectMultiDatesDialogOpen = true }) {
     *     Text(stringResource(R.string.select_dates))
     * }
     */
}
```

---

## Installation

### Requirements

- **Jetpack Compose** and **Material3** libraries.
- Minimum SDK: **26**
- JDK: **11 or above**

### Setup

Step 1: Add the Maven Central repository (if not present):

```kotlin
repositories {
    mavenCentral()
}
```

Step 2: Add the library dependency:

#### Kotlin DSL:

```kotlin
dependencies {
    implementation("com.abdulrahman-b.hijridatepicker:hijridatepicker:1.1.0")
}
```

#### Groovy DSL:

```groovy
dependencies {
    implementation "com.abdulrahman-b.hijridatepicker:hijridatepicker:1.1.0"
}
```

---

## Support Me

If you find this project helpful, please support me! Donations are greatly appreciated:

[![Donate via PayPal](https://img.shields.io/badge/Donate-PayPal-blue.svg?logo=paypal)](https://www.paypal.com/paypalme/AbdulrahmanBahamel)

Thank you for your contributions!