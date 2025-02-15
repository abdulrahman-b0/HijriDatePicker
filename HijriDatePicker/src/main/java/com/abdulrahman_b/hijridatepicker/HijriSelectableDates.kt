@file:OptIn(ExperimentalMaterial3Api::class)
package com.abdulrahman_b.hijridatepicker

/*
* Copyright 2023 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import java.time.chrono.HijrahDate

internal interface HijriSelectableDates : SelectableDates {

    fun isSelectableDate(date: HijrahDate): Boolean

    /**
     * This method is not supported for [HijriSelectableDates].
     * Calling this method will throw an [UnsupportedOperationException].
     *
     * Use [isSelectableDate] overload with [HijrahDate] parameter instead.
     */
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        throw UnsupportedOperationException("This method is not supported for HijriSelectableDates")
    }

}


fun hijriSelectableDates(
    isSelectableDate: (HijrahDate) -> Boolean = { true },
    isSelectableYear: (Int) -> Boolean = { true }
): SelectableDates = object : HijriSelectableDates {

    override fun isSelectableDate(date: HijrahDate): Boolean = isSelectableDate(date)

    override fun isSelectableYear(year: Int): Boolean = isSelectableYear(year)

}
