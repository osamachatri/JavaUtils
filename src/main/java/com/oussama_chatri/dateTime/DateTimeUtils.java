package com.oussama_chatri.dateTime;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.*;
import java.util.*;

public final class DateTimeUtils {

    private DateTimeUtils() {}

    // --- Current Date & Time ---

    public static LocalDate currentDate() {
        return LocalDate.now();
    }

    public static LocalTime currentTime() {
        return LocalTime.now();
    }

    public static LocalDateTime currentDateTime() {
        return LocalDateTime.now();
    }

    public static ZonedDateTime currentZonedDateTime(ZoneId zone) {
        return ZonedDateTime.now(zone);
    }

    public static Instant currentInstant() {
        return Instant.now();
    }

    // --- Parsing and Formatting ---

    public static LocalDate parseDate(String date, String pattern) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDateTime parseDateTime(String datetime, String pattern) {
        return LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern(pattern));
    }

    public static ZonedDateTime parseZonedDateTime(String datetime, String pattern, ZoneId zone) {
        return ZonedDateTime.parse(datetime, DateTimeFormatter.ofPattern(pattern).withZone(zone));
    }

    public static String formatDate(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatDateTime(LocalDateTime datetime, String pattern) {
        return datetime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatZonedDateTime(ZonedDateTime datetime, String pattern) {
        return datetime.format(DateTimeFormatter.ofPattern(pattern));
    }

    // --- Conversions ---

    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static ZonedDateTime toZonedDateTime(LocalDateTime localDateTime, ZoneId zone) {
        return localDateTime.atZone(zone);
    }

    public static LocalDateTime toLocalDateTime(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toLocalDateTime();
    }

    public static Instant toInstant(LocalDateTime localDateTime, ZoneId zone) {
        return localDateTime.atZone(zone).toInstant();
    }

    public static Instant toInstant(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toInstant();
    }

    // --- Date Arithmetic ---

    public static LocalDate addDays(LocalDate date, int days) {
        return date.plusDays(days);
    }

    public static LocalDate subtractDays(LocalDate date, int days) {
        return date.minusDays(days);
    }

    public static LocalDateTime addHours(LocalDateTime dateTime, int hours) {
        return dateTime.plusHours(hours);
    }

    public static LocalDateTime subtractHours(LocalDateTime dateTime, int hours) {
        return dateTime.minusHours(hours);
    }

    public static LocalDate addWeeks(LocalDate date, int weeks) {
        return date.plusWeeks(weeks);
    }

    public static LocalDate subtractWeeks(LocalDate date, int weeks) {
        return date.minusWeeks(weeks);
    }

    public static LocalDate addMonths(LocalDate date, int months) {
        return date.plusMonths(months);
    }

    public static LocalDate subtractMonths(LocalDate date, int months) {
        return date.minusMonths(months);
    }

    public static LocalDate addYears(LocalDate date, int years) {
        return date.plusYears(years);
    }

    public static LocalDate subtractYears(LocalDate date, int years) {
        return date.minusYears(years);
    }

    public static Duration durationBetween(Temporal startInclusive, Temporal endExclusive) {
        return Duration.between(startInclusive, endExclusive);
    }

    public static Period periodBetween(LocalDate startInclusive, LocalDate endExclusive) {
        return Period.between(startInclusive, endExclusive);
    }

    // --- Date Information ---

    public static boolean isLeapYear(int year) {
        return Year.isLeap(year);
    }

    public static boolean isLeapYear(LocalDate date) {
        return date.isLeapYear();
    }

    public static DayOfWeek dayOfWeek(LocalDate date) {
        return date.getDayOfWeek();
    }

    public static int dayOfYear(LocalDate date) {
        return date.getDayOfYear();
    }

    public static int weekOfYear(LocalDate date) {
        return date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
    }

    public static int daysInMonth(LocalDate date) {
        return date.lengthOfMonth();
    }

    public static int daysInYear(LocalDate date) {
        return date.lengthOfYear();
    }

    public static LocalDate firstDayOfMonth(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    public static LocalDate lastDayOfMonth(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth());
    }

    public static LocalDate firstDayOfYear(LocalDate date) {
        return date.withDayOfYear(1);
    }

    public static LocalDate lastDayOfYear(LocalDate date) {
        return date.withDayOfYear(date.lengthOfYear());
    }

    // --- Timezone & Offset ---

    public static ZoneId getSystemDefaultZone() {
        return ZoneId.systemDefault();
    }

    public static ZoneOffset getSystemDefaultOffset() {
        return ZonedDateTime.now().getOffset();
    }

    public static ZonedDateTime convertZone(ZonedDateTime dateTime, ZoneId targetZone) {
        return dateTime.withZoneSameInstant(targetZone);
    }

    public static ZoneId parseZoneId(String zoneId) {
        return ZoneId.of(zoneId);
    }

    // --- Formatting constants ---

    public static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_DATE;
    public static final DateTimeFormatter ISO_TIME = DateTimeFormatter.ISO_TIME;
    public static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_DATE_TIME;
    public static final DateTimeFormatter ISO_INSTANT = DateTimeFormatter.ISO_INSTANT;
    public static final DateTimeFormatter RFC_1123_DATE_TIME = DateTimeFormatter.RFC_1123_DATE_TIME;

    // --- Parsing with safety ---

    public static Optional<LocalDate> tryParseDate(String dateStr, String pattern) {
        try {
            return Optional.of(parseDate(dateStr, pattern));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    public static Optional<LocalDateTime> tryParseDateTime(String dateTimeStr, String pattern) {
        try {
            return Optional.of(parseDateTime(dateTimeStr, pattern));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    // --- Formatting with Locale ---

    public static String formatDateWithLocale(LocalDate date, String pattern, Locale locale) {
        return date.format(DateTimeFormatter.ofPattern(pattern, locale));
    }

    public static String formatDateTimeWithLocale(LocalDateTime datetime, String pattern, Locale locale) {
        return datetime.format(DateTimeFormatter.ofPattern(pattern, locale));
    }

    // --- Comparisons ---

    public static boolean isBefore(LocalDate a, LocalDate b) {
        return a.isBefore(b);
    }

    public static boolean isAfter(LocalDate a, LocalDate b) {
        return a.isAfter(b);
    }

    public static boolean isEqual(LocalDate a, LocalDate b) {
        return a.isEqual(b);
    }

    // --- Date Range Checks ---

    public static boolean isWithinRange(LocalDate date, LocalDate startInclusive, LocalDate endInclusive) {
        return !date.isBefore(startInclusive) && !date.isAfter(endInclusive);
    }

    public static boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    public static boolean isWeekday(LocalDate date) {
        return !isWeekend(date);
    }

    // --- Interval splitting ---

    public static List<LocalDate> datesBetween(LocalDate startInclusive, LocalDate endInclusive) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = startInclusive;
        while (!current.isAfter(endInclusive)) {
            dates.add(current);
            current = current.plusDays(1);
        }
        return dates;
    }

    public static List<LocalDateTime> dateTimesBetween(LocalDateTime startInclusive, LocalDateTime endInclusive, Duration step) {
        List<LocalDateTime> dateTimes = new ArrayList<>();
        LocalDateTime current = startInclusive;
        while (!current.isAfter(endInclusive)) {
            dateTimes.add(current);
            current = current.plus(step);
        }
        return dateTimes;
    }

    // --- Start and end of day ---

    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }

    // --- Start and end of week ---

    public static LocalDate startOfWeek(LocalDate date, Locale locale) {
        TemporalField fieldISO = WeekFields.of(locale).dayOfWeek();
        return date.with(fieldISO, 1);
    }

    public static LocalDate endOfWeek(LocalDate date, Locale locale) {
        TemporalField fieldISO = WeekFields.of(locale).dayOfWeek();
        return date.with(fieldISO, 7);
    }

    // --- Start and end of month ---

    public static LocalDate startOfMonth(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    public static LocalDate endOfMonth(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth());
    }

    // --- Start and end of year ---

    public static LocalDate startOfYear(LocalDate date) {
        return date.withDayOfYear(1);
    }

    public static LocalDate endOfYear(LocalDate date) {
        return date.withDayOfYear(date.lengthOfYear());
    }

    // --- Age calculation ---

    public static int calculateAge(LocalDate birthday, LocalDate onDate) {
        if (birthday == null || onDate == null) return 0;
        return Period.between(birthday, onDate).getYears();
    }

    public static int calculateAge(LocalDate birthday) {
        return calculateAge(birthday, currentDate());
    }

    // --- Unix timestamp conversions ---

    public static long toUnixTimestamp(Instant instant) {
        return instant.getEpochSecond();
    }

    public static long toUnixTimestamp(LocalDateTime dateTime, ZoneId zone) {
        return dateTime.atZone(zone).toEpochSecond();
    }

    public static Instant fromUnixTimestamp(long timestamp) {
        return Instant.ofEpochSecond(timestamp);
    }

    // --- Formatting durations ---

    public static String formatDuration(Duration duration) {
        if (duration == null) return null;
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format("%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }

    public static String formatPeriod(Period period) {
        if (period == null) return null;
        return String.format("P%dY%dM%dD", period.getYears(), period.getMonths(), period.getDays());
    }

    // --- Validate date formats ---

    public static boolean isValidDate(String dateStr, String pattern) {
        try {
            parseDate(dateStr, pattern);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    public static boolean isValidDateTime(String dateTimeStr, String pattern) {
        try {
            parseDateTime(dateTimeStr, pattern);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    // --- Helpers for comparing times ---

    public static boolean isBeforeNow(LocalDateTime dateTime) {
        return dateTime.isBefore(currentDateTime());
    }

    public static boolean isAfterNow(LocalDateTime dateTime) {
        return dateTime.isAfter(currentDateTime());
    }

    public static boolean isEqualNow(LocalDateTime dateTime) {
        return dateTime.isEqual(currentDateTime());
    }

    // --- Get start/end of quarter ---

    public static LocalDate startOfQuarter(LocalDate date) {
        int currentMonth = date.getMonthValue();
        int startMonth = currentMonth - (currentMonth - 1) % 3;
        return LocalDate.of(date.getYear(), startMonth, 1);
    }

    public static LocalDate endOfQuarter(LocalDate date) {
        return startOfQuarter(date).plusMonths(3).minusDays(1);
    }

    // --- Check if two dates are in the same day ---

    public static boolean isSameDay(LocalDateTime dt1, LocalDateTime dt2) {
        return dt1.toLocalDate().equals(dt2.toLocalDate());
    }

    public static boolean isSameDay(LocalDate d1, LocalDate d2) {
        return d1.equals(d2);
    }

    // --- Check if two times are in the same hour ---

    public static boolean isSameHour(LocalDateTime dt1, LocalDateTime dt2) {
        return dt1.getYear() == dt2.getYear()
                && dt1.getDayOfYear() == dt2.getDayOfYear()
                && dt1.getHour() == dt2.getHour();
    }

    // --- Check if two times are in the same minute ---

    public static boolean isSameMinute(LocalDateTime dt1, LocalDateTime dt2) {
        return dt1.getYear() == dt2.getYear()
                && dt1.getDayOfYear() == dt2.getDayOfYear()
                && dt1.getHour() == dt2.getHour()
                && dt1.getMinute() == dt2.getMinute();
    }

    // --- Safe parse with default ---

    public static LocalDate parseDateOrDefault(String dateStr, String pattern, LocalDate defaultDate) {
        return tryParseDate(dateStr, pattern).orElse(defaultDate);
    }

    public static LocalDateTime parseDateTimeOrDefault(String dateTimeStr, String pattern, LocalDateTime defaultDateTime) {
        return tryParseDateTime(dateTimeStr, pattern).orElse(defaultDateTime);
    }

    // --- Find next weekday ---

    public static LocalDate nextWeekday(LocalDate date) {
        LocalDate next = date.plusDays(1);
        while (isWeekend(next)) {
            next = next.plusDays(1);
        }
        return next;
    }

    // --- Find previous weekday ---

    public static LocalDate previousWeekday(LocalDate date) {
        LocalDate prev = date.minusDays(1);
        while (isWeekend(prev)) {
            prev = prev.minusDays(1);
        }
        return prev;
    }

    // --- Get random date between two dates ---

    public static LocalDate randomDateBetween(LocalDate startInclusive, LocalDate endInclusive) {
        long startEpochDay = startInclusive.toEpochDay();
        long endEpochDay = endInclusive.toEpochDay();
        long randomDay = startEpochDay + (long) (Math.random() * (endEpochDay - startEpochDay + 1));
        return LocalDate.ofEpochDay(randomDay);
    }

    // --- Format date to ISO8601 string ---

    public static String toIsoDate(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_DATE);
    }

    public static String toIsoDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public static String toIsoZonedDateTime(ZonedDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    // --- Parse ISO8601 dates safely ---

    public static Optional<LocalDate> tryParseIsoDate(String dateStr) {
        try {
            return Optional.of(LocalDate.parse(dateStr));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    public static Optional<LocalDateTime> tryParseIsoDateTime(String dateTimeStr) {
        try {
            return Optional.of(LocalDateTime.parse(dateTimeStr));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    public static Optional<ZonedDateTime> tryParseIsoZonedDateTime(String dateTimeStr) {
        try {
            return Optional.of(ZonedDateTime.parse(dateTimeStr));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    // --- Get month name ---

    public static String getMonthName(LocalDate date, Locale locale) {
        return date.getMonth().getDisplayName(TextStyle.FULL, locale);
    }

    public static String getMonthShortName(LocalDate date, Locale locale) {
        return date.getMonth().getDisplayName(TextStyle.SHORT, locale);
    }

    // --- Get day of week name ---

    public static String getDayOfWeekName(LocalDate date, Locale locale) {
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
    }

    public static String getDayOfWeekShortName(LocalDate date, Locale locale) {
        return date.getDayOfWeek().getDisplayName(TextStyle.SHORT, locale);
    }

    // --- Check if date is today ---

    public static boolean isToday(LocalDate date) {
        return date.equals(currentDate());
    }

    public static boolean isYesterday(LocalDate date) {
        return date.equals(currentDate().minusDays(1));
    }

    public static boolean isTomorrow(LocalDate date) {
        return date.equals(currentDate().plusDays(1));
    }

    // --- Timezone offset in hours ---

    public static int getZoneOffsetHours(ZoneId zone, Instant instant) {
        return zone.getRules().getOffset(instant).getTotalSeconds() / 3600;
    }

    // --- Round time to nearest minute ---

    public static LocalDateTime roundToNearestMinute(LocalDateTime dateTime) {
        if (dateTime.getSecond() >= 30) {
            return dateTime.plusMinutes(1).withSecond(0).withNano(0);
        } else {
            return dateTime.withSecond(0).withNano(0);
        }
    }

    // --- Round time to nearest hour ---

    public static LocalDateTime roundToNearestHour(LocalDateTime dateTime) {
        if (dateTime.getMinute() >= 30) {
            return dateTime.plusHours(1).withMinute(0).withSecond(0).withNano(0);
        } else {
            return dateTime.withMinute(0).withSecond(0).withNano(0);
        }
    }

    // --- Truncate time to start of minute ---

    public static LocalDateTime truncateToMinute(LocalDateTime dateTime) {
        return dateTime.withSecond(0).withNano(0);
    }

    // --- Truncate time to start of hour ---

    public static LocalDateTime truncateToHour(LocalDateTime dateTime) {
        return dateTime.withMinute(0).withSecond(0).withNano(0);
    }

    // --- Get first Monday of the month ---

    public static LocalDate firstMondayOfMonth(int year, int month) {
        LocalDate date = LocalDate.of(year, month, 1);
        while (date.getDayOfWeek() != DayOfWeek.MONDAY) {
            date = date.plusDays(1);
        }
        return date;
    }

    // --- Get last Sunday of the month ---

    public static LocalDate lastSundayOfMonth(int year, int month) {
        LocalDate date = LocalDate.of(year, month, 1).withDayOfMonth(LocalDate.of(year, month, 1).lengthOfMonth());
        while (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
            date = date.minusDays(1);
        }
        return date;
    }

    // --- Get number of weekdays between two dates ---

    public static long weekdaysBetween(LocalDate startInclusive, LocalDate endInclusive) {
        long days = ChronoUnit.DAYS.between(startInclusive, endInclusive) + 1;
        long weekdays = 0;
        for (int i = 0; i < days; i++) {
            DayOfWeek dow = startInclusive.plusDays(i).getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                weekdays++;
            }
        }
        return weekdays;
    }

    // --- Format with custom formatter ---

    public static String formatWithFormatter(TemporalAccessor temporal, DateTimeFormatter formatter) {
        return formatter.format(temporal);
    }

    // --- Misc utility ---

    /**
     * Returns the start of the day for the given Instant in the given zone.
     */
    public static ZonedDateTime startOfDay(Instant instant, ZoneId zone) {
        return instant.atZone(zone).toLocalDate().atStartOfDay(zone);
    }

    /**
     * Returns the end of the day for the given Instant in the given zone.
     */
    public static ZonedDateTime endOfDay(Instant instant, ZoneId zone) {
        return instant.atZone(zone).toLocalDate().atTime(LocalTime.MAX).atZone(zone);
    }

    // Add more methods as needed...

}
