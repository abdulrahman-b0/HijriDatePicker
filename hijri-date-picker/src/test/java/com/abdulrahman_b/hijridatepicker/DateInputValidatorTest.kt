package com.abdulrahman_b.hijridatepicker

import android.text.format.DateFormat
import androidx.compose.material3.ExperimentalMaterial3Api
import com.abdulrahman_b.hijridatepicker.datepicker.DateInputFormat
import com.abdulrahman_b.hijridatepicker.datepicker.HijriDatePickerDefaults
import com.abdulrahman_b.hijridatepicker.datepicker.InputIdentifier
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockkStatic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.chrono.HijrahDate
import java.time.format.DateTimeParseException
import java.time.format.DecimalStyle
import java.util.*

@ExtendWith(MockKExtension::class)
class DateInputValidatorTest {

    private lateinit var subject : DateInputValidator


    @BeforeEach
    fun setup() {
        mockkStatic(DateFormat::class)
        every { DateFormat.getBestDateTimePattern(any(), any()) } answers { "dd/MM/yyyy" }
    }

    @Test
    fun `test that the out of range date is invalid`() {
        val date = HijrahDate.of(1451, 1, 1)
        setupInstance(date, null)

        val result = subject.validateSuccess(date, InputIdentifier.SingleDateInput)

        assertEquals("Date is out of year range: 1356 - 1450", result)
    }


    @Test
    fun `test that the date is not allowed`() {
        val date = HijrahDate.of(1450, 12, 29)
        setupInstance(date, null)

        val result = subject.validateSuccess(date, InputIdentifier.SingleDateInput)

        assertEquals("Date is not allowed: 29/12/1450", result)
    }

    @Test
    fun `test that the date is valid`() {
        val date = HijrahDate.of(1446, 12, 29)
        setupInstance(date, null)

        val result = subject.validateSuccess(date, InputIdentifier.SingleDateInput)

        assertEquals("", result)
    }

    @Test
    fun `test that the date pattern is invalid`() {
        setupInstance(HijrahDate.of(1450, 12, 29), null)

        val result = subject.validateFailure()

        assertEquals("Does not match the expected pattern: DD/MM/YYYY", result)
    }

    @Test
    fun `test that the date is valid in range`() {
        val startDate = HijrahDate.of(1440, 12, 29)
        val endDate = HijrahDate.of(1446, 12, 29)

        setupInstance(startDate, null)

        var result = subject.validateSuccess(startDate, InputIdentifier.StartDateInput)
        assertEquals("", result)
        result = subject.validateSuccess(endDate, InputIdentifier.EndDateInput)
        assertEquals("", result)
    }

    @Test
    fun `test that the date is invalid in range`() {
        val startDate = HijrahDate.of(1446, 12, 29)
        val endDate = HijrahDate.of(1440, 12, 29)

        setupInstance(startDate, null)

        var result = subject.validateSuccess(startDate, InputIdentifier.StartDateInput)
        assertEquals("", result)
        result = subject.validateSuccess(endDate, InputIdentifier.EndDateInput)
        assertEquals("Invalid range input", result)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun setupInstance(
        currentStartDate: HijrahDate, currentEndDate: HijrahDate?
    ) {
        subject = DateInputValidator(
            yearRange = 1356..1450,
            selectableDates = object : HijriSelectableDates {
                override fun isSelectableYear(year: Int): Boolean = year  in 1356..1446
            },
            dateInputFormat = DateInputFormat("dd/MM/yyyy", '/'),
            dateFormatter = HijriDatePickerDefaults.dateFormatter(),
            errorDatePattern = "Does not match the expected pattern: %s",
            errorDateOutOfYearRange = "Date is out of year range: %s - %s",
            errorInvalidNotAllowed = "Date is not allowed: %s",
            errorInvalidRangeInput = "Invalid range input",
            currentStartDate = currentStartDate,
            currentEndDate = currentEndDate,
        )
    }

    private fun DateInputValidator.validateSuccess(
        date: HijrahDate, inputIdentifier: InputIdentifier
    ): String? {
        return validate(Result.success(date), inputIdentifier, Locale.getDefault(), DecimalStyle.STANDARD)
    }

    private fun DateInputValidator.validateFailure(): String? {
        return validate(Result.failure(DateTimeParseException("", "", 0)), InputIdentifier.SingleDateInput, Locale.getDefault(), DecimalStyle.STANDARD)
    }



}