@file:OptIn(ExperimentalMaterial3Api::class)
package com.abdulrahman_b.hijridatepicker

import androidx.compose.material3.ExperimentalMaterial3Api
import java.time.chrono.HijrahDate

interface HijriSelectableDates {

    fun isSelectableDate(date: HijrahDate): Boolean = true

    fun isSelectableYear(year: Int): Boolean = true

}
