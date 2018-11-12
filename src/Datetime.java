import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: aimozg
 * Date: 11.09.12
 * Time: 16:30
 */
public final class Datetime {
    public static final String DEFAULT_FORMAT = "YYYY-MM-DD HH:mm:ss.ccc";
    public static final String DEFAULT_FORMAT_NOMS = "YYYY-MM-DD HH:mm:ss";
    public static final String DEFAULT_FORMAT_DATE = "YYYY-MM-DD";
    public static final String XML_FORMAT = "YYYY-MM-DDTHH:mm:ss.ccczUU:uu";
    public static final String RFC_822_FORMAT = "WW, DD MMM YYYY HH:mm:ss \\G\\M\\T";

    public static Datetime now() {
        return new Datetime();
    }

    public static String getMinSecMs(long time) {
        if (time == 0) return "";
        long ms = time % 1000;
        time /= 1000;
        long sec = time % 60;
        time /= 60;
        long min = time % 60;
        if (ms > 0) return String.format("%02d:%02d.%03d", min, sec, ms);
        else return String.format("%02d:%02d", min, sec);
    }

    public static int daysInYear(int year) {
        return isLeapYear(year) ? 366 : 365;
    }

    public static String getTimePeriod(long durms, boolean withms, char decsep, String ns, String nm, String nh, String nd) {
        if (durms == 0) return "0";
        long ms = durms % 1000;
        durms /= 1000;
        long sec = durms % 60;
        durms /= 60;
        long min = durms % 60;
        durms /= 60;
        long hr = durms % 24;
        long days = hr / 24;
        String result = (ms > 0 && withms) ? String.format("%d%s%03d%s", sec, decsep, ms, ns) : (sec + (ms + 500) / 1000 + ns);
        if (min > 0 || hr > 0 || days > 0) result = min + nm + result;
        if (hr > 0 || days > 0) result = hr + nh + result;
        if (days > 0) result = days + nd + result;
        return result;
    }

    public static String getTimePeriod(long durms, boolean withms) {
        return getTimePeriod(durms, withms, '.', "s", "m", "h", "d");
    }

    public static String getTimePeriod(long durms) {
        return getTimePeriod(durms, true, '.', "s", "m", "h", "d");
    }

    @Nullable
    public static Datetime safeParse(String format, String s) {
        try {
            return parse(format, s);
        } catch (ParseException ignored) {
        }
        return null;
    }

    @NotNull
    public static Datetime parse(String format, String s) throws ParseException {
        int year = 0;
        int month = -1;
        int day = -1;
        int doy = -1;
        int hr24 = -1;
        int hr12 = -1;
        boolean pm = false;
        int min = 0;
        int sec = 0;
        int mil = 0;
        int minoff = 0;
        int offsgn = 1;
        char[] fmt = format.toCharArray();
        char[] str = s.toCharArray();
        int fi = 0;
        int si = 0;
        final char YR = 'Y';
        final char YR2 = 'y';
        final char MON = 'M';
        final char DAY = 'D';
        final char DOY = 'd';
        final char HR24 = 'H';
        final char HR12 = 'h';
        final char AMPMUC = 'P';
        final char AMPMLC = 'p';
        final char MIN = 'm';
        final char SEC = 's';
        final char ZMIL = 'c';
        final char MIL = 'C';
        final char HOFF = 'U';
        final char MOFF = 'u';
        final char SOFF = 'z';
        while (fi < fmt.length && si < str.length) {
            char fc = fmt[fi++];
            switch (fc) {
                case '\\':
                    if (fi == fmt.length) throw new ParseException(format + " : " + (fi - 1), fi - 1);
                    if (si == str.length || str[si++] != fmt[fi++])
                        throw new ParseException(s + " : " + (si - 1), si - 1);
                    break;
                case SOFF:
                    char c = str[si++];
                    if (c == '+' || c == ' ') offsgn = 1;
                    else if (c == '-') offsgn = -1;
                    else throw new ParseException(s + " : " + (si - 1), si - 1);
                    break;
                case YR:
                case MON:
                case DAY:
                case HR24:
                case HR12:
                case AMPMUC:
                case AMPMLC:
                case MIN:
                case SEC:
                case ZMIL:
                case MIL:
                case HOFF:
                case MOFF:
                    int fcn = 1;
                    while (fi < fmt.length && fmt[fi] == fc) {
                        fcn++;
                        fi++;
                    }
                    int minlen = 0, maxlen = 0;
                    String[] special = null;
                    switch (fc) {
                        case YR:
                            switch (fcn) {
                                case 4:
                                    minlen = 4;
                                    maxlen = 4;
                                    break;
                                case 3:
                                    minlen = 1;
                                    maxlen = 4;
                                    break;
                                case 2:
                                    minlen = 2;
                                    maxlen = 2;
                                    fc = YR2;
                                    break;
								/*case 1:
									minlen = 1;
									maxlen = 2;
									c = YR2;
									break;*/
                            }
                            break;
                        case MON:
                            switch (fcn) {
                                case 4:
                                    special = MONTH_LONG_NAMES;
                                    break;
                                case 3:
                                    special = MONTH_SHORT_NAMES;
                                    break;
                                case 2:
                                    minlen = 2;
                                    maxlen = 2;
                                    break;
                                case 1:
                                    minlen = 1;
                                    maxlen = 2;
                                    break;
                            }
                            break;
                        case DAY:
                            switch (fcn) {
                                case 4:
                                    minlen = 3;
                                    maxlen = 3;
                                    fc = DOY;
                                    break;
                                case 3:
                                    minlen = 1;
                                    maxlen = 3;
                                    fc = DOY;
                                    break;
                                case 2:
                                    minlen = 2;
                                    maxlen = 2;
                                    break;
                                case 1:
                                    minlen = 1;
                                    maxlen = 2;
                                    break;
                            }
                            break;
                        case HR24:
                        case HR12:
                        case MIN:
                        case SEC:
                        case HOFF:
                        case MOFF:
                            switch (fcn) {
                                case 2:
                                    minlen = 2;
                                    maxlen = 2;
                                    break;
                                case 1:
                                    minlen = 1;
                                    maxlen = 2;
                                    break;
                            }
                            break;
                        case AMPMUC:
                            switch (fcn) {
                                case 2:
                                    special = new String[]{"A.M.", "P.M."};
                                    break;
                                case 1:
                                    special = new String[]{"AM", "PM"};
                                    break;
                            }
                            break;
                        case AMPMLC:
                            switch (fcn) {
                                case 2:
                                    special = new String[]{"a.m.", "p.m."};
                                    break;
                                case 1:
                                    special = new String[]{"am", "pm"};
                                    break;
                            }
                            break;
                        case ZMIL:
                            switch (fcn) {
                                case 3:
                                case 2:
                                case 1:
                                    minlen = fcn;
                                    maxlen = fcn;
                                    break;
                            }
                            break;
                        case MIL:
                            switch (fcn) {
                                case 1:
                                    minlen = 1;
                                    maxlen = 3;
                                    break;
                            }
                    }
                    if (special != null) {
                        boolean[] fit = new boolean[special.length];
                        Arrays.fill(fit, true);
                        int mini = 0;
                        int maxi = special.length - 1;
                        int fiti = 0;
                        int found = -1;
                        while (si < str.length) {
                            char sc = str[si++];
                            int nt = 0;
                            for (int i = mini; i <= maxi; i++) {
                                if (fit[i]) {
                                    if (special[i].length() - 1 == fiti) {
                                        found = i;
                                        fit[i] = false;
                                    } else if (special[i].charAt(fiti) == sc) {
                                        nt++;
                                    } else {
                                        fit[i] = false;
                                    }
                                } else {
                                    if (i == mini) mini++;
                                    if (i == maxi) maxi--;
                                }
                            }
                            fiti++;
                            if (nt == 0) break;
                        }
                        if (found == -1) throw new ParseException(s + " : " + (si - 1), si - 1);
                        switch (fc) {
                            case MON:
                                month = found;
                                break;
                            case AMPMUC:
                            case AMPMLC:
                                pm = found == 1;
                                break;
                        }
                    } else if (minlen > 0) {
                        StringBuilder sb = new StringBuilder();
                        char sc = 0;
                        while (minlen > 0) {
                            if (si == str.length) throw new ParseException(s + " : " + (si - 1), si - 1);
                            sc = str[si++];
                            if (sc < '0' || sc > '9') throw new ParseException(s + " : " + (si - 1), si - 1);
                            if (!(sc == '0' && sb.length() == 0)) sb.append(sc);
                            minlen--;
                            maxlen--;
                        }
                        if (si < str.length) sc = str[si];
                        while (maxlen > 0 && si < str.length && sc >= '0' && sc <= '9') {
                            if (si == str.length) throw new ParseException(s + " : " + (si - 1), si - 1);
                            si++;
                            if (!(sc == '0' && sb.length() == 0)) sb.append(sc);
                            if (si < str.length) sc = str[si];
                            maxlen--;
                        }
                        if (sb.length() == 0) sb.append('0');
                        int n = Integer.parseInt(sb.toString());
                        switch (fc) {
                            case YR:
                                year = n;
                                break;
                            case YR2:
                                if (n >= 70) year = 1900 + n;
                                else year = 2000 + n;
                                break;
                            case MON:
                                month = n;
                                break;
                            case DAY:
                                day = n;
                                break;
                            case DOY:
                                doy = n;
                                break;
                            case HR24:
                                hr24 = n;
                                break;
                            case HR12:
                                hr12 = n;
                                break;
                            case MIN:
                                min = n;
                                break;
                            case SEC:
                                sec = n;
                                break;
                            case MIL:
                                mil = n;
                                break;
                            case ZMIL:
                                if (fcn == 3) mil = n;
                                else if (fcn == 2) mil = n * 10;
                                else if (fcn == 1) mil = n * 100;
                                break;
                            case HOFF:
                                minoff += n * 60;
                                break;
                            case MOFF:
                                minoff += n;
                                break;
                        }
                    } else {
                        while (fcn > 0) {
                            if (si == str.length || str[si++] != fc)
                                throw new ParseException(s + " : " + (si - 1), si - 1);
                            fcn--;
                        }
                    }
                    break;
                default:
                    if (str[si++] != fc) throw new ParseException(s + " : " + (si - 1), si - 1);
                    break;
            }
        }
        Datetime dt = new Datetime(year, FIRST_MONTH, FIRST_DAY_OF_MONTH);
        if (doy != -1) dt.add(Field.DAY_OF_MONTH, doy - 1);
        else {
            if (month != -1) dt.set(Field.MONTH, month);
            if (day != -1) dt.set(Field.DAY_OF_MONTH, day);
        }
        if (hr24 != -1) dt.set(Field.HOUR_OF_DAY, hr24);
        if (hr12 != -1) {
            if (hr12 == 12) hr12 = 0;
            if (pm) hr12 += 12;
            dt.set(Field.HOUR_OF_DAY, hr12);
        }
        dt.set(Field.MINUTE, min);
        dt.set(Field.SECOND, sec);
        dt.set(Field.MILLISECOND, mil);
        dt.add(Field.MINUTE, -minoff * offsgn);
        return dt;
    }

    public enum Field {
        YEAR, MONTH, DAY_OF_MONTH, HOUR_OF_DAY, MINUTE, SECOND, MILLISECOND
    }

    public static final Field YEAR = Field.YEAR;
    public static final Field MONTH = Field.MONTH;
    public static final Field DAY_OF_MONTH = Field.DAY_OF_MONTH;
    public static final Field HOUR_OF_DAY = Field.HOUR_OF_DAY;
    public static final Field MINUTE = Field.MINUTE;
    public static final Field SECOND = Field.SECOND;
    public static final Field MILLISECOND = Field.MILLISECOND;
    public static final int NFIELDS = Field.values().length;

    private long time;
    private boolean timeActual = false;
    private boolean fieldsActual = false;
    private final int fields[] = new int[NFIELDS];
    public static final String[] MONTH_SHORT_NAMES = {"Nul",
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static final String[] MONTH_LONG_NAMES = {"Nulluary",
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };
    public static final String[] DOWSZ_SHORT_NAMES = {"Sun",
            "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
    };
    public static final String[] DOWSZ_LONG_NAMES = {"Sunday",
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };
    public static final int[] DAYS_PER_MONTH = {-1, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31, -1};
    public static final int[] DAYS_BEFORE_MONTH = new int[DAYS_PER_MONTH.length];

    static {
        DAYS_BEFORE_MONTH[0] = -1;
        DAYS_BEFORE_MONTH[1] = 0;
        for (int i = 2; i < DAYS_BEFORE_MONTH.length; i++) {
            DAYS_BEFORE_MONTH[i] = DAYS_BEFORE_MONTH[i - 1] + DAYS_PER_MONTH[i - 1];
        }
    }

    public static final int FIRST_MONTH = 1;
    public static final int LAST_MONTH = FIRST_MONTH + 11;
    public static final int FIRST_DAY_OF_MONTH = 1;
    public static final int LEAP_DAY_MONTH = FIRST_MONTH + 1;
    // Day of week in Sunday-zero form
    public static final int SZ_SUNDAY = 0;
    public static final int SZ_MONDAY = 1;
    public static final int SZ_TUESDAY = 2;
    public static final int SZ_WEDNESDAY = 3;
    public static final int SZ_THURSDAY = 4;
    public static final int SZ_FRIDAY = 5;
    public static final int SZ_SATURDAY = 6;


    public static boolean isLeapYear(int year) {
        return (year & 3) == 0 && (year % 100 != 0 || year % 400 == 0);
    }

    public static int daysPerMonth(int year, int month) {
        if (isLeapYear(year) && month == LEAP_DAY_MONTH) return DAYS_PER_MONTH[month] + 1;
        return DAYS_PER_MONTH[month];
    }

    private int daysPerMonth() {
        return daysPerMonth(fields[YEAR.ordinal()], fields[MONTH.ordinal()]);
    }

    public Datetime() {
        this.time = System.currentTimeMillis();
        timeActual = true;
    }
    public static Datetime jan1(int year) {
        return new Datetime(year,FIRST_MONTH,FIRST_DAY_OF_MONTH);
    }

    public Datetime(long unixGmtMs) {
        this.time = unixGmtMs;
        timeActual = true;
    }

    public Datetime(int year, int month, int day) {
        this.fields[YEAR.ordinal()] = year;
        this.fields[MONTH.ordinal()] = month;
        this.fields[DAY_OF_MONTH.ordinal()] = day;
        fieldsActual = true;
    }

    public Datetime(int year, int month, int day, int hour, int min, int sec) {
        this.fields[YEAR.ordinal()] = year;
        this.fields[MONTH.ordinal()] = month;
        this.fields[DAY_OF_MONTH.ordinal()] = day;
        this.fields[HOUR_OF_DAY.ordinal()] = hour;
        this.fields[MINUTE.ordinal()] = min;
        this.fields[SECOND.ordinal()] = sec;
        fieldsActual = true;
    }

    public Datetime(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        this.fields[YEAR.ordinal()] = year;
        this.fields[MONTH.ordinal()] = month;
        this.fields[DAY_OF_MONTH.ordinal()] = day;
        this.fields[HOUR_OF_DAY.ordinal()] = hour;
        this.fields[MINUTE.ordinal()] = minute;
        this.fields[SECOND.ordinal()] = second;
        this.fields[MILLISECOND.ordinal()] = millisecond;
        fieldsActual = true;
    }

    public long getTime() {
        actualTime();
        return time;
    }

    public Datetime setTime(long time) {
        this.time = time;
        fieldsActual = false;
        return this;
    }

    public Datetime set(Field field, int value) {
        actualFields();
        fields[field.ordinal()] = value;
        timeActual = false;
        return this;
    }

    public int get(Field field) {
        actualFields();
        return fields[field.ordinal()];
    }

    public Datetime addMillis(long millis) {
        actualTime();
        time += millis;
        fieldsActual = false;
        return this;
    }

    @NotNull
    public Datetime add(Field field, int value) {
        actualFields();
        fields[field.ordinal()] += value;

        if (fields[MILLISECOND.ordinal()] < 0 || fields[MILLISECOND.ordinal()] >= 1000) {
            int ns = fields[MILLISECOND.ordinal()] / 1000;
            if (fields[MILLISECOND.ordinal()] % 1000 < 0) ns--;
            fields[SECOND.ordinal()] += ns;
            fields[MILLISECOND.ordinal()] -= ns * 1000;
        }
        if (fields[SECOND.ordinal()] < 0 || fields[SECOND.ordinal()] >= 60) {
            int nm = fields[SECOND.ordinal()] / 60;
            if (fields[SECOND.ordinal()] % 60 < 0) nm--;
            fields[MINUTE.ordinal()] += nm;
            fields[SECOND.ordinal()] -= nm * 60;
        }
        if (fields[MINUTE.ordinal()] < 0 || fields[MINUTE.ordinal()] >= 60) {
            int nh = fields[MINUTE.ordinal()] / 60;
            if (fields[MINUTE.ordinal()] % 60 < 0) nh--;
            fields[HOUR_OF_DAY.ordinal()] += nh;
            fields[MINUTE.ordinal()] -= nh * 60;
        }
        if (fields[HOUR_OF_DAY.ordinal()] < 0 || fields[HOUR_OF_DAY.ordinal()] >= 24) {
            int nd = fields[HOUR_OF_DAY.ordinal()] / 24;
            if (fields[HOUR_OF_DAY.ordinal()] % 24 < 0) nd--;
            fields[DAY_OF_MONTH.ordinal()] += nd;
            fields[HOUR_OF_DAY.ordinal()] -= nd * 24;
        }
        if (fields[MONTH.ordinal()] < FIRST_MONTH || fields[MONTH.ordinal()] > LAST_MONTH) {
            int ny = (fields[MONTH.ordinal()] - FIRST_MONTH) / 12;
            if ((fields[MONTH.ordinal()] - FIRST_MONTH) % 12 < 0) ny--;
            fields[YEAR.ordinal()] += ny;
            fields[MONTH.ordinal()] -= ny * 12;
        }
        while (fields[DAY_OF_MONTH.ordinal()] < FIRST_DAY_OF_MONTH) {//TODO optimize
            fields[MONTH.ordinal()]--;
            if (fields[MONTH.ordinal()] < FIRST_MONTH) {
                fields[YEAR.ordinal()]--;
                fields[MONTH.ordinal()] = LAST_MONTH;
            }
            fields[DAY_OF_MONTH.ordinal()] += daysPerMonth();
        }
        while (fields[DAY_OF_MONTH.ordinal()] >= daysPerMonth() + FIRST_DAY_OF_MONTH) {//TODO optimize
            fields[DAY_OF_MONTH.ordinal()] -= daysPerMonth();
            fields[MONTH.ordinal()]++;
            if (fields[MONTH.ordinal()] > LAST_MONTH) {
                fields[YEAR.ordinal()]++;
                fields[MONTH.ordinal()] = FIRST_MONTH;
            }
        }

        timeActual = false;
        return this;
    }

    public void actualFields() {
        if (!fieldsActual) setFieldsFromTime();
    }

    public void actualTime() {
        if (!timeActual) setTimeFromFields();
    }

    @NotNull
    public Datetime duplicate() {
        return new Datetime(getTime());
    }

    @NotNull
    public Datetime tomorrow() {
        return new Datetime(getTime() + 24L * 60 * 60 * 1000);
    }

    @NotNull
    public Datetime yesterday() {
        return new Datetime(getTime() - 24L * 60 * 60 * 1000);
    }

    private void setFieldsFromTime() {
        long MS_IN_DAY = 86400000L;
        long dt =  (time%MS_IN_DAY+MS_IN_DAY)%MS_IN_DAY;
        long t = (time - dt)/MS_IN_DAY;
        fields[MILLISECOND.ordinal()] = (int) (dt % 1000);
        dt /= 1000;
        fields[SECOND.ordinal()] = (int) (dt % 60);
        dt /= 60;
        fields[MINUTE.ordinal()] = (int) (dt % 60);
        dt /= 60;
        fields[HOUR_OF_DAY.ordinal()] = (int) (dt % 24);

        // t -- number of days since Jan 1 1970 (2 equals Jan 3)
        fields[YEAR.ordinal()] = 1970;

        if (t >= 365 * 2) {
            t -= 365 * 2;
            fields[YEAR.ordinal()] += 2;
        }
        int dp4y = 365 * 4 + 1;
        long days = (t%dp4y+dp4y)%dp4y;
        fields[YEAR.ordinal()] += 4 * ((t-days) / dp4y);

        t = days;
        while (true) {
            int dy = daysInYear(fields[YEAR.ordinal()]);
            if (t >= dy) {
                t -= dy;
                fields[YEAR.ordinal()]++;
            } else break;
        }
        // t -- number of days since Jan 1 (2 equals Jan 3)
        fields[MONTH.ordinal()] = FIRST_MONTH;
        fields[DAY_OF_MONTH.ordinal()] = FIRST_DAY_OF_MONTH;
        for (int i = FIRST_MONTH; i <= LAST_MONTH; i++) {
            int dpm = daysPerMonth();
            if (dpm > t) {
                fields[DAY_OF_MONTH.ordinal()] += t;
                break;
            } else {
                fields[MONTH.ordinal()]++;
                t -= dpm;
            }
        }
        fieldsActual = true;
    }

    private void setTimeFromFields() {
        int dy = fields[YEAR.ordinal()] - 1970;
        time = 0;
        if (dy >= 3) {
            dy -= 3;
            time += (3 * 365 + 1) * GnssUtils.MS_IN_DAY;
        }
        int dp4y = 365 * 4 + 1;
        time += (dy / 4) * dp4y * GnssUtils.MS_IN_DAY;
        time += (dy % 4) * 365 * GnssUtils.MS_IN_DAY;
        for (int i = FIRST_MONTH; i < fields[MONTH.ordinal()]; i++) {
            time += GnssUtils.MS_IN_DAY * daysPerMonth(fields[YEAR.ordinal()], i);
        }
        time += GnssUtils.MS_IN_DAY * (fields[DAY_OF_MONTH.ordinal()] - FIRST_MONTH);
        time += fields[HOUR_OF_DAY.ordinal()] * 3600000L;
        time += fields[MINUTE.ordinal()] * 60000L;
        time += fields[SECOND.ordinal()] * 1000L;
        time += fields[MILLISECOND.ordinal()];

        timeActual = true;
    }


    public boolean before(Datetime other) {
        return getTime() < other.getTime();
    }

    public boolean after(Datetime other) {
        return getTime() > other.getTime();
    }

    public boolean same(Datetime other) {
        return getTime() == other.getTime();
    }

    @NotNull
    protected String formatWithTZ(String format, int utcPlusMinutes) {
        int tzh = Math.abs(utcPlusMinutes) / 60;
        int tzm = Math.abs(utcPlusMinutes) % 60;
        char[] s = format.toCharArray();
        int i = 0;
        StringBuilder sb = new StringBuilder(s.length);
        boolean escape = false;
        while (i < s.length) {
            char c = s[i++];
            if (escape) {
                escape = false;
                sb.append(c);
                continue;
            }
            if (c == '\\') {
                escape = true;
                continue;
            }
            int n = 1;
            while (i < s.length && s[i] == c) {
                i++;
                n++;
            }
            switch (c) {
                case 'Y':
                    switch (n) {
                        case 1:
                            sb.append(year() % 100);
                            break;
                        case 2:
                            sb.append(String.format("%02d", year() % 100));
                            break;
                        case 3:
                            sb.append(year());
                            break;
                        case 4:
                        default:
                            sb.append(String.format("%04d", year()));
                            i -= (n - 4);
                            break;
                    }
                    break;
                case 'M':
                    switch (n) {
                        case 1:
                            sb.append(month() - FIRST_MONTH + 1);
                            break;
                        case 2:
                            sb.append(String.format("%02d", month() - FIRST_MONTH + 1));
                            break;
                        case 3:
                            sb.append(MONTH_SHORT_NAMES[month() - FIRST_MONTH + 1]);
                            break;
                        case 4:
                        default:
                            sb.append(MONTH_LONG_NAMES[month() - FIRST_MONTH + 1]);
                            i -= (n - 4);
                            break;
                    }
                    break;
                case 'D':
                    switch (n) {
                        case 1:
                            sb.append(String.valueOf(day() - FIRST_DAY_OF_MONTH + 1));
                            break;
                        case 2:
                            sb.append(String.format("%02d", day() - FIRST_DAY_OF_MONTH + 1));
                            break;
                        case 3:
                            sb.append(dayOfYear());
                            break;
                        case 4:
                        default:
                            sb.append(String.format("%03d", dayOfYear()));
                            i -= (n - 4);
                            break;
                    }
                    break;
                case 'H':
                    switch (n) {
                        case 1:
                            sb.append(hour24());
                            break;
                        case 2:
                        default:
                            sb.append(String.format("%02d", hour24()));
                            i -= (n - 2);
                            break;
                    }
                    break;
                case 'h':
                    switch (n) {
                        case 1:
                            sb.append(hour12());
                            break;
                        case 2:
                        default:
                            sb.append(String.format("%02d", hour12()));
                            i -= (n - 2);
                            break;
                    }
                    break;
                case 'm':
                    switch (n) {
                        case 1:
                            sb.append(min());
                            break;
                        case 2:
                        default:
                            sb.append(String.format("%02d", min()));
                            i -= (n - 2);
                            break;
                    }
                    break;
                case 's':
                    switch (n) {
                        case 1:
                            sb.append(sec());
                            break;
                        case 2:
                        default:
                            sb.append(String.format("%02d", sec()));
                            i -= (n - 2);
                            break;
                    }
                    break;
                case 'c':
                    switch (n) {
                        case 1:
                            sb.append(String.format("%01d", Math.round(milli() / 100.0)));
                            break;
                        case 2:
                            sb.append(String.format("%02d", Math.round(milli() / 10.0)));
                            break;
                        case 3:
                        default:
                            sb.append(String.format("%03d", milli()));
                            i -= (n - 3);
                    }
                    break;
                case 'C':
                    sb.append(milli());
                    i -= (n - 1);
                    break;
                case 'P':
                    switch (n) {
                        case 1:
                            sb.append(isAM() ? "AM" : "PM");
                            break;
                        case 2:
                        default:
                            sb.append(isAM() ? "A.M." : "P.M.");
                            i -= (n - 2);
                            break;
                    }
                    break;
                case 'p':
                    switch (n) {
                        case 1:
                            sb.append(isAM() ? "am" : "pm");
                            break;
                        case 2:
                        default:
                            sb.append(isAM() ? "a.m." : "p.m.");
                            i -= (n - 2);
                            break;
                    }
                    break;
                case 'z':
                    sb.append(utcPlusMinutes>=0?"+":"-");
                    i -= (n - 1);
                    break;
                case 'U':
                    switch (n) {
                        case 1:
                            sb.append(tzh);
                            i -= (n - 1);
                            break;
                        case 2:
                        default:
                            sb.append(String.format("%02d", tzh));
                            i -= (n - 2);
                            break;
                    }
                    break;
                case 'u':
                    switch (n) {
                        case 2:
                        default:
                            sb.append(String.format("%02d", tzm));
                            i -= (n - 2);
                            break;
                    }
                    break;
                case 'W':
                    switch (n) {
                        case 1:
                            sb.append(DOWSZ_SHORT_NAMES[dayOfWeekSZ()]);
                            break;
                        case 2:
                        default:
                            sb.append(DOWSZ_LONG_NAMES[dayOfWeekSZ()]);
                            i -= (n - 2);
                            break;
                    }
                    break;
                default:
                    while (n-- > 0) sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    @NotNull
    public String format(String format, int utcPlusHours, int utcPlusMinutes) {
        int minutes = utcPlusHours * 60 + utcPlusMinutes;
        if (minutes == 0) return formatWithTZ(format,0);
        else return new Datetime(getTime() + minutes * 60 * 1000).formatWithTZ(format, minutes);
    }

    @NotNull
    public String format(String format, int utcPlusHours) {
        return format(format, utcPlusHours, 0);
    }

    /**
     * \ to escape
     * YYYY, YYY - 4-digit padded/unpadded year <br/>
     * YY, Y - 2-digit padded/unpadded year <br/>
     * MMMM - January..December <br/>
     * MMM - Jan..Dec <br/>
     * MM, M - 2-digit padded/unpadded month <br/>
     * DDDD, DDD - 3-digit padded/unpadded day of year <br/>
     * DD, D - 2-digit padded/unpadded day of month <br/>
     * HH, H - 2-digit padded/unpadded hour 0-24 <br/>
     * hh, h - 2-digit padded/unpadded hour 1-12 <br/>
     * PP - "A.M."/"P.M." <br/>
     * P - "AM"/"PM" <br/>
     * pp - "a.m."/"p.m." <br/>
     * p - "am"/"pm" <br/>
     * mm, m - 2-digit padded/unpadded minutes <br/>
     * ss, s - 2-digit padded/unpadded seconds <br/>
     * ccc, C - 3-digit padded/unpadded milliseconds <br/>
     * cc - 2-digit padded centiseconds <br/>
     * c - 1-digit deciseconds <br/>
     * WWW - Monday..Sunday <br/>
     * WW - Mon..Sun <br/>
     * z - TZ sign<br/>
     * UU - TZ hour offset, subtracted from hours <br/>
     * uu - TZ minute offset, subtracted from minutes <br/>
     */
    @NotNull
    public String format(String format) {
        return format(format, 0, 0);
    }

    @Override
    public String toString() {
        return format(DEFAULT_FORMAT);
    }

    public int year() {
        return get(YEAR);
    }

    public int month() {
        return get(MONTH);
    }

    public int day() {
        return get(DAY_OF_MONTH);
    }

    public int dayOfYear() {
        return day() + DAYS_BEFORE_MONTH[month()] + (isLeapYear(year()) && month() > LEAP_DAY_MONTH ? 1 : 0);
        //return (int) (1 + (getTime() - duplicate().set(MONTH, 1).set(DAY_OF_MONTH, 1).getTime()) / 24 / 60 / 60 / 1000);
    }

    /**
     * @return day of week 0-6, Sunday is Zero
     */
    public int dayOfWeekSZ() {
        return (int) (getTime() / GnssUtils.MS_IN_DAY + SZ_THURSDAY) % 7;
    }

    public int hour() {
        return get(HOUR_OF_DAY);
    }

    public int hour24() {
        return get(HOUR_OF_DAY);
    }

    public int hour12() {
        int hour = hour24() % 12;
        if (hour == 0) return 12;
        return hour;
    }

    public boolean isAM() {
        return hour() >= 0 && hour() <= 11;
    }

    public boolean isPM() {
        return hour() >= 12 && hour() <= 23;
    }

    public int min() {
        return get(MINUTE);
    }

    public int sec() {
        return get(SECOND);
    }

    public int milli() {
        return get(MILLISECOND);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Datetime datetime = (Datetime) o;

        return getTime() == datetime.getTime();
    }

    @Override
    public int hashCode() {
        actualTime();
        return (int) (time ^ (time >>> 32));
    }

    /*public static void main(String[] args) {
        Datetime cstart = new Datetime(GnssUtils.gps2unix_leap(1040947200000l));
        cstart.actualFields();
        System.out.println(cstart);
        Datetime cend = cstart.duplicate();
        cend.add(Datetime.DAY_OF_MONTH, 1);
        cend.actualTime();
        System.out.println(cend);
        / *Datetime ctime = cstart.duplicate();
        ctime.actualFields();
        Field stepfield = Datetime.DAY_OF_MONTH;
        while (ctime.before(cend)) {
            Datetime cnext = ctime.duplicate();
            cnext.add(stepfield, 1);
            long gt0 = GnssUtils.unix2gps_leap(ctime.getTime());
            long gt1 = GnssUtils.unix2gps_leap(cnext.getTime());
            System.out.printf("%s %s\n",ctime,cnext);
            ctime = cnext;
        }
        System.out.println(cend);* /
    }*/
}