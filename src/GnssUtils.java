import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Класс утилит для работы с GPS (в основном --- с GPS-временем).
 * <p/>
 * GPS-время --- число миллисекунд с 6 января 1980 года. При этом секунды координации не "пропускаются" как в
 * UNIX-времени
 * <p/>
 * UNIX: 123456789.00 123456789.50 123456789.99 123456789.00 123456789.50 123456789.99 1234567890.00
 * GPS:  123456789.00 123456789.50 123456789.99 123456790.00 123456790.50 123456790.99 1234567891.00
 */
public class GnssUtils {

    /**
     * 1.01.2100 +1 минута -секунды координации
     */
    public static final long Y2100 = 3786480060000L;

    private GnssUtils() {
    }

    public static final long MS_IN_HOUR = 60L * 60L * 1000L;
    public static final long MS_IN_DAY = 24L * MS_IN_HOUR;
    /**
     * 6 Jan 1980 - 1 Jan 1970
     * = 5 дней (1янв-6янв)
     * + 2 дня (28 февраля 1972 и 1976)
     * + 10 невисокосных лет (1970-1979)
     */
    public static final long GPS_UNIX_DIFF = (5L + 2L + 10L * 365L) * MS_IN_DAY;
    public static final long MS_IN_WEEK = 7L * MS_IN_DAY;
    /**
     * Moscow: GMT +3:00.
     */
    @SuppressWarnings("PointlessArithmeticExpression")
    public static final long GPS_GLO_DIFF = 3L * 60L * 60L * 1000L - 1 * MS_IN_DAY;

    /**
     * Секунды координации
     */
    public static final long[] GPST_LEAP_EPOCHS = {
            46828800000L,  // 30.06.1981 23:59:60
            78364801000L,  // 30.06.1982 23:59:60
            109900802000L, // 30.06.1983 23:59:60
            173059203000L, // 30.06.1985 23:59:60
            252028804000L, // 31.12.1987 23:59:60
            315187205000L, // 31.12.1989 23:59:60
            346723206000L, // 31.12.1990 23:59:60
            393984007000L, // 30.06.1992 23:59:60
            425520008000L, // 30.06.1993 23:59:60
            457056009000L, // 30.06.1994 23:59:60
            504489610000L, // 31.12.1995 23:59:60
            551750411000L, // 30.06.1997 23:59:60
            599184012000L, // 31.12.1998 23:59:60
            820108813000L, // 31.12.2005 23:59:60
            914803214000L, // 31.12.2008 23:59:60
            1025136015000L,// 30.06.2012 23:59:60
            1119744016000L,// 30.06.2015 23:59:60
            1167264017000L,// 31.12.2016 23:59:60
    };

    /**
     * Число секунд координации на текущее время
     */
    public static int getLeapSeconds() {
        return leapSeconds(gpstime());
    }


    /**
     * Конвертация GPS --> UNIX
     */
    public static long gps2unix(long gps_time_ms) {
        return gps_time_ms + GPS_UNIX_DIFF - leapSeconds(gps_time_ms) * 1000;
    }

    /**
     * Число секунд коордианции на указанное время
     */
    public static int leapSeconds(long gps_time_ms) {
        for (int i = GPST_LEAP_EPOCHS.length - 1; i >= 0; i--) {
            if (gps_time_ms >= GPST_LEAP_EPOCHS[i]) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * Число секунд координации на указанное время, содержащее секунды координации
     */
    public static int leapSecondsU(long gps_time_ms) {
        for (int i = GPST_LEAP_EPOCHS.length - 1; i >= 0; i--) {
            if (gps_time_ms >= GPST_LEAP_EPOCHS[i] - i * 1000) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * Конвертация (GPS --> UNIX с секундами координации)
     */
    public static long gps2unix_leap(long gps_time_ms) {
        return gps_time_ms + GPS_UNIX_DIFF;
    }

    /**
     * Конвертация UNIX --> GPS
     */
    public static long unix2gps(long unix_time_ms) {
        long gt = unix_time_ms - GPS_UNIX_DIFF;
        return gt + leapSecondsU(gt) * 1000;
    }

    /**
     * Конвертация (UNIX с секундами координации) --> GPS
     */
    public static long unix2gps_leap(long unix_time_ms) {
        return unix_time_ms - GPS_UNIX_DIFF;
    }

    /**
     * Текущее GPS-время по системным часам. Не следует использовать для вычислений
     */
    public static long gpstime() {
        return unix2gps(System.currentTimeMillis());
    }

    /**
     * Извлекает номер GPS-недели из времени GPS
     */
    public static long extractGpsWeek(long gpstime) {
        return gpstime / MS_IN_WEEK;
    }

    public static final Datetime BDT_ZEROTIME = new Datetime(2006, 1, 1);

    /**
     * Извлекает номер BDS-недели из времени GPS
     */
    public static long extractBdsWeek(long gpstime) {
        return (gpstime - BDT_ZEROTIME.getTime()) / MS_IN_WEEK;
    }

    /**
     * Извлекает число миллисекунд с начала недели из времени GPS
     */
    public static long extractMs(long gpstime) {
        return gpstime % MS_IN_WEEK;
    }

    public static long[] separateMsSecMinHourDayWeek(long time) {
        long ms = time;
        long s = ms / 1000;
        long min = s / 60;
        long hr = min / 60;
        long day = hr / 24;
        long week = day / 7;
        ms %= 1000;
        s %= 60;
        min %= 60;
        hr %= 24;
        day %= 7;
        return new long[]{ms, s, min, hr, day, week};
    }

    /**
     * @deprecated Use {@link GnssUtils#separateMsSecMinHourDayWeek}
     */
    @Deprecated
    public static long[] separateMsSecMinHour(long time) {
        long ms = time;
        long s = ms / 1000;
        long min = s / 60;
        long hr = min / 60;
        ms %= 1000;
        s %= 60;
        min %= 60;
        hr %= 24;
        return new long[]{ms, s, min, hr};
    }

    /**
     * Конструирует GPS-время из номера недели и числа миллисекунд
     */
    public static long constructGpsTime(long week, long ms) {
        return week * MS_IN_WEEK + ms;
    }

    public static long gpsms2gloms(long gps_time_ms, long ref_gps_week) {
        return (MS_IN_DAY + gps_time_ms + GPS_GLO_DIFF - leapSeconds(constructGpsTime(ref_gps_week, gps_time_ms)) * 1000) % MS_IN_DAY;
    }
    public static long gps2gloms(long gpstime) {
        return (MS_IN_DAY + (gpstime%MS_IN_WEEK) + GPS_GLO_DIFF - leapSeconds(gpstime) * 1000) % MS_IN_DAY;
    }

    public static long gps2bdt(long gpstime) {
        return gpstime - 14*1000;
    }

    public static long bdt2gps(long gpstime) {
        return gpstime + 14*1000;
    }

    /**
     * @deprecated Use {@link GnssUtils#gloms2gpstime}
     */
    @Deprecated
    public static long gloms2gpsms(long glo_time_ms, long ref_gps_week) {
        long gt = glo_time_ms - GPS_GLO_DIFF;
        return gt + leapSecondsU(constructGpsTime(ref_gps_week, gt)) * 1000;
    }

    /**
     * @param glo_time_ms  GLONASS time with days part ignored
     * @param ref_gps_time Reference full GPS time
     * @return Full GPS time
     */
    public static long gloms2gpstime(long glo_time_ms, long ref_gps_time) {
        long gt = GnssUtils.addGuessedDays(ref_gps_time, glo_time_ms - GPS_GLO_DIFF);
        return gt + leapSecondsU(gt) * 1000;
    }

    public static long glodms2gpstime(int glo_time_dow, long glo_time_ms, long ref_gps_time) {
        long gt = GnssUtils.addGuessedWeek(ref_gps_time, ((glo_time_dow+7-1)%7) * MS_IN_DAY + glo_time_ms - GPS_GLO_DIFF);
        return gt + leapSecondsU(gt) * 1000;
    }

    public static int gpstime2glodow(long gpstime_full) {
        long glowms = gpstime_full + GPS_GLO_DIFF - leapSeconds(gpstime_full) * 1000;
        return (int) ((glowms/MS_IN_DAY + 1) % 7);
    }

    /**
     * Перевод из градусов/минут/секунд в градусы
     */
    public static double dms2deg(int deg, int min, double sec) {
        return deg + (min + sec / 60.0) / 60.0;
    }

    /**
     * Перевод из градусов в градусы/минуты/секунды
     */
    @NotNull
    public static double[] deg2dms(double deg) {
        int d = (int) deg;
        deg = (deg - d) * 60;
        int m = (int) deg;
        deg = (deg - m) * 60;
        return new double[]{d, m, deg};
    }

    /**
     * Возвращает полное GPS-время по имеющемуся числу миллисекунд с начала недели {@code msgms}.
     *
     * @param gpstime --- ориентировочное "текущее" время
     */
    public static long addGuessedWeek(long gpstime, long msgms) {
        // TODO выделить в метод DF-поля сообщения
        msgms %= MS_IN_WEEK;
        long week = extractGpsWeek(gpstime);
        long ms = extractMs(gpstime);
        if (Math.abs(msgms - ms) > (MS_IN_WEEK / 2)) {
            // Разница между миллисекундной состявляющей
            // текущего времени и времени сообщения велика.
            // Значит, они относятся к разным (соседним) неделям
            if (msgms < ms) {
                // У сообщения миллисекунд меньше, чем в системе.
                // Значит, часы сообщения перешли на следующую неделю
                week++;
            } else {
                week--;
            }
        }
        return constructGpsTime(week, msgms);
    }

    /**
     * Возвращает полное GPS-время по имеющемуся числу миллисекунд с начала дня ({@code msgms}).
     *
     * @param gpstime --- ориентировочное "текущее" время
     */
    public static long addGuessedDays(long gpstime, long msgms) {
        msgms %= MS_IN_DAY;
        long week = extractGpsWeek(gpstime);
        long ms = extractMs(gpstime);
        long day = week * 7 + ms / MS_IN_DAY;
        ms %= MS_IN_DAY;
        if (Math.abs(msgms - ms) > (MS_IN_DAY / 2)) {
            if (msgms < ms) {
                day++;
            } else {
                day--;
            }
        }
        return constructGpsTime(day / 7, msgms + (day % 7) * MS_IN_DAY);
    }

    /**
     * Возвращает полное GPS-время по имеющемуся числу миллисекунд с начала часа ({@code msgms}).
     *
     * @param gpstime --- ориентировочное "текущее" время
     */
    public static long addGuessedHours(long gpstime, long msgms) {
        long week = extractGpsWeek(gpstime);
        long ms = extractMs(gpstime);
        long hr = week * 7 * 24 + ms / MS_IN_HOUR;
        ms %= MS_IN_HOUR;
        if (Math.abs(msgms - ms) > (MS_IN_HOUR / 2)) {
            if (msgms < ms) {
                hr++;
            } else {
                hr--;
            }
        }
        return constructGpsTime(hr / 7 / 24, msgms + (hr % (7 * 24)) * MS_IN_HOUR);
    }

    /**
     * Относительные частоты ГЛОНАСС на 10.01.2012. Использовать если нет доступа к альманаху. Меняются редко
     * 1   2  3   4   5   6  7   8
     * 9  10 11  12  13  14 15  16
     * 17  18 19  20  21  22 23  24
     */
    public static Integer[] gloFreqBands = {
            1, -4, 5, 6, 1, -4, 5, 6,
            -2, -7, 0, -1, -2, -7, 0, -1,
            4, -3, 3, 2, 4, -3, 3, 2,
            null, -5, null, null, null};

    /**
     * @param gloSat номер спутника 1-24
     * @return Номер частоты ГЛОАНСС
     */
    public static Integer gloFreqBand(int gloSat) {
        try {
            return gloFreqBands[gloSat - 1];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return null;
        }
    }

    public static Integer gloSatByFreq(int gloFreq) {
        for (int i = 0; i < gloFreqBands.length; i++) {
            if (gloFreqBands[i] != null && gloFreqBands[i] == gloFreq) return i + 1;
        }
        return null;
    }

    public static Datetime gps2date(long gt) {
        return new Datetime(gps2unix(gt));
    }

    public static Datetime gps2date_leap(long gt) {
        return new Datetime(gps2unix_leap(gt));
    }

    public static long getMJD() {
        return getMJD(gpstime());
    }

    public static long getMJD(long gpstime) {
        return gpstime / GnssUtils.MS_IN_DAY + 44244;
    }

    public static long mjdToGpstime(long mjd, long msOfDay) {
        return (mjd - 44244) * 86400 + msOfDay;
    }

    public static boolean isLeapSecond(long gpsTime) {
        return Arrays.binarySearch(GPST_LEAP_EPOCHS, gpsTime) >= 0;
    }
}