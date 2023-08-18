package shetj.me.base.day;

import android.annotation.TargetApi;
import android.icu.text.DisplayContext;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LocalDates {





  /**
   * Returns a Calendar object in local time zone representing the first moment of current date.
   */
  static Calendar getTodayCalendar() {
    Calendar today = Calendar.getInstance(Locale.getDefault());
    today.set(Calendar.HOUR_OF_DAY, 0);
    today.set(Calendar.MINUTE, 0);
    today.set(Calendar.SECOND, 0);
    today.set(Calendar.MILLISECOND, 0);
    return today;
  }

  /**
   * Returns an empty Calendar in local time zone.
   *
   * @return An empty Calendar in local time zone.
   * @see {@link #getLocalCalendarOf(Calendar)}
   * @see Calendar#clear()
   */
  static Calendar getLocalCalendar() {
    return getLocalCalendarOf(null);
  }

  /**
   * Returns a Calendar object in local time zone representing the moment in input Calendar object. An
   * empty Calendar object in local will be return if input is null.
   *
   * @param rawCalendar the Calendar object representing the moment to process.
   * @return A Calendar object in local time zone.
   * @see @see Calendar#clear()
   */
  static Calendar getLocalCalendarOf(@Nullable Calendar rawCalendar) {
    Calendar calendar = Calendar.getInstance(Locale.getDefault());
    if (rawCalendar == null) {
      calendar.clear();
    } else {
      calendar.setTimeInMillis(rawCalendar.getTimeInMillis());
    }
    return calendar;
  }

  /**
   * Returns a Calendar object in local time zone representing the start of day in local represented in
   * the input Calendar object, i.e., the time (fields smaller than a day) is stripped based on the
   * local time zone.
   *
   * @param rawCalendar the Calendar object representing the moment to process.
   * @return A Calendar object representing the start of day in local time zone.
   */
  static Calendar getDayCopy(Calendar rawCalendar) {
    Calendar rawCalendarInLocal = getLocalCalendarOf(rawCalendar);
    Calendar localCalendar = getLocalCalendar();
    localCalendar.set(
        rawCalendarInLocal.get(Calendar.YEAR),
        rawCalendarInLocal.get(Calendar.MONTH),
        rawCalendarInLocal.get(Calendar.DAY_OF_MONTH));
    return localCalendar;
  }

  /**
   * Strips all information from the time in milliseconds at granularities more specific than day of
   * the month.
   *
   * @param rawDate A long representing the time as local milliseconds from the epoch
   * @return A canonical long representing the time as local milliseconds for the represented day.
   */
  static long canonicalYearMonthDay(long rawDate) {
    Calendar rawCalendar = getLocalCalendar();
    rawCalendar.setTimeInMillis(rawDate);
    Calendar sanitizedStartItem = getDayCopy(rawCalendar);
    return sanitizedStartItem.getTimeInMillis();
  }

  @TargetApi(VERSION_CODES.N)
  private static android.icu.text.DateFormat getAndroidFormat(String pattern, Locale locale) {
    android.icu.text.DateFormat format =
        android.icu.text.DateFormat.getInstanceForSkeleton(pattern, locale);
    format.setContext(DisplayContext.CAPITALIZATION_FOR_STANDALONE);
    return format;
  }

  private static DateFormat getFormat(int style, Locale locale) {
    DateFormat format = DateFormat.getDateInstance(style, locale);
    return format;
  }

  static SimpleDateFormat getDefaultTextInputFormat() {
    String defaultFormatPattern =
        ((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()))
            .toPattern()
            .replaceAll("\\s+", "");
    SimpleDateFormat format = new SimpleDateFormat(defaultFormatPattern, Locale.getDefault());
    format.setLenient(false);
    return format;
  }



  static SimpleDateFormat getSimpleFormat(String pattern) {
    return getSimpleFormat(pattern, Locale.getDefault());
  }

  private static SimpleDateFormat getSimpleFormat(String pattern, Locale locale) {
    SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
    return format;
  }

  @TargetApi(Build.VERSION_CODES.N)
  static android.icu.text.DateFormat getYearMonthFormat(Locale locale) {
    return getAndroidFormat(android.icu.text.DateFormat.YEAR_MONTH, locale);
  }

  @TargetApi(Build.VERSION_CODES.N)
  static android.icu.text.DateFormat getYearAbbrMonthDayFormat(Locale locale) {
    return getAndroidFormat(android.icu.text.DateFormat.YEAR_ABBR_MONTH_DAY, locale);
  }

  @TargetApi(Build.VERSION_CODES.N)
  static android.icu.text.DateFormat getAbbrMonthDayFormat(Locale locale) {
    return getAndroidFormat(android.icu.text.DateFormat.ABBR_MONTH_DAY, locale);
  }

  @TargetApi(Build.VERSION_CODES.N)
  static android.icu.text.DateFormat getAbbrMonthWeekdayDayFormat(Locale locale) {
    return getAndroidFormat(android.icu.text.DateFormat.ABBR_MONTH_WEEKDAY_DAY, locale);
  }

  @TargetApi(Build.VERSION_CODES.N)
  static android.icu.text.DateFormat getYearAbbrMonthWeekdayDayFormat(Locale locale) {
    return getAndroidFormat(android.icu.text.DateFormat.YEAR_ABBR_MONTH_WEEKDAY_DAY, locale);
  }

  static DateFormat getMediumFormat() {
    return getMediumFormat(Locale.getDefault());
  }

  static DateFormat getMediumFormat(Locale locale) {
    return getFormat(DateFormat.MEDIUM, locale);
  }

  static DateFormat getMediumNoYear() {
    return getMediumNoYear(Locale.getDefault());
  }

  static DateFormat getMediumNoYear(Locale locale) {
    SimpleDateFormat format = (SimpleDateFormat) getMediumFormat(locale);
    format.applyPattern(removeYearFromDateFormatPattern(format.toPattern()));
    return format;
  }

  static DateFormat getFullFormat() {
    return getFullFormat(Locale.getDefault());
  }

  static DateFormat getFullFormat(Locale locale) {
    return getFormat(DateFormat.FULL, locale);
  }

  @NonNull
  private static String removeYearFromDateFormatPattern(@NonNull String pattern) {
    String yearCharacters = "yY";

    int yearPosition = findCharactersInDateFormatPattern(pattern, yearCharacters, 1, 0);

    if (yearPosition >= pattern.length()) {
      // No year character was found in this pattern, return as-is
      return pattern;
    }

    String monthDayCharacters = "EMd";
    int yearEndPosition =
        findCharactersInDateFormatPattern(pattern, monthDayCharacters, 1, yearPosition);

    if (yearEndPosition < pattern.length()) {
      monthDayCharacters += ",";
    }

    int yearStartPosition =
        findCharactersInDateFormatPattern(pattern, monthDayCharacters, -1, yearPosition);
    yearStartPosition++;

    String yearPattern = pattern.substring(yearStartPosition, yearEndPosition);
    return pattern.replace(yearPattern, " ").trim();
  }

  private static int findCharactersInDateFormatPattern(
      @NonNull String pattern,
      @NonNull String characterSequence,
      int increment,
      int initialPosition) {
    int position = initialPosition;

    // Increment while we haven't found the characters we're looking for in the date pattern
    while ((position >= 0 && position < pattern.length())
        && characterSequence.indexOf(pattern.charAt(position)) == -1) {

      // If an open string is found, increment until we close the string
      if (pattern.charAt(position) == '\'') {
        position += increment;
        while ((position >= 0 && position < pattern.length()) && pattern.charAt(position) != '\'') {
          position += increment;
        }
      }

      position += increment;
    }

    return position;
  }
}