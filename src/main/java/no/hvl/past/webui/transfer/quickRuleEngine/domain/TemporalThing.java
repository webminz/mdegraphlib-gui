package no.hvl.past.webui.transfer.quickRuleEngine.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class TemporalThing extends Thing {

    public static class Moment extends TemporalThing {

        private final LocalDateTime timestamp;

        public Moment(String name, LocalDateTime timestamp) {
            super(timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            this.timestamp = timestamp;
        }

        public int getYear() {
            return timestamp.getYear();
        }

        public int getMonth() {
            return timestamp.getMonthValue();
        }

        public int getDayOfWeek() {
            return timestamp.getDayOfWeek().getValue();
        }

        public int getDayOfMonth() {
            return timestamp.getDayOfMonth();
        }

        public int getHour() {
            return timestamp.getHour();
        }

        public int getMinutes() {
            return timestamp.getMinute();
        }

        public int getSeconds() {
            return timestamp.getSecond();
        }

        @Override
        public boolean isMatchableWith(Thing other) {
            if (other instanceof Moment) {
                return this.timestamp.equals(((Moment) other).timestamp);
            }
            return false;
        }
    }

    public static class Duration extends TemporalThing {

        private static final long MILLIS_IN_SECOND = 1000;
        private static final long MILLIS_IN_MINUTE = 60 * MILLIS_IN_SECOND;
        private static final long MILLIS_IN_HOUR = 60 * MILLIS_IN_MINUTE;
        private static final long MILLIS_IN_DAY = 24 * MILLIS_IN_HOUR;
        private static final long MILLIS_IN_MONTH = 30 * MILLIS_IN_DAY;
        private static final long MILLIS_IN_YEAR = 364 * MILLIS_IN_MONTH;

        private final long timeInMS;

        public Duration(long timeInMS) {
            super(printDuration(timeInMS));
            this.timeInMS = timeInMS;
        }

        private static String printDuration(long timeInMS) {
            long rest = timeInMS;
            int ys = (int) (rest / MILLIS_IN_YEAR);
            rest = rest % MILLIS_IN_YEAR;
            int ms = (int) (rest / MILLIS_IN_MONTH);
            rest = rest % MILLIS_IN_MONTH;
            int ds = (int) (rest / MILLIS_IN_DAY);
            rest = rest % MILLIS_IN_DAY;
            int hs = (int) (rest / MILLIS_IN_HOUR);
            rest = rest % MILLIS_IN_HOUR;
            int mins = (int) (rest / MILLIS_IN_MINUTE);
            rest = rest % MILLIS_IN_MINUTE;
            int secs = (int) (rest / MILLIS_IN_SECOND);
            return (ys == 0 ? "" : Integer.toString(ys) + " years ") +
                    (ms == 0 ? "" : Integer.toString(ms) + " months ") +
                    (ds == 0 ? "" : Integer.toString(ds) + " days ") +
                    (hs == 0 ? "" : Integer.toString(hs) + " hours ") +
                    (mins == 0 ? "" : Integer.toString(mins) + " minutes ") +
            (secs == 0 ? "" : Integer.toString(secs) + " second ");
        }

        public long getTimeInMS() {
            return timeInMS;
        }

        @Override
        public boolean isMatchableWith(Thing other) {
            if (other instanceof Duration) {
                return this.timeInMS == ((Duration) other).timeInMS;
            }
            return false;
        }
    }

    public static class Appointment extends TemporalThing {

        private final Moment start;
        private final Duration duration;

        public Appointment(Moment start, Duration duration) {
            super("Appointment starts " + start.getName() + " and lasts " + duration.getName());
            this.start = start;
            this.duration = duration;
        }

        public Moment getStart() {
            return start;
        }

        public Duration getDuration() {
            return duration;
        }

        @Override
        public boolean isMatchableWith(Thing other) {
            return false; // TODO liesWithin
        }
    }

    public static class Schedule extends TemporalThing {

        private final Appointment firstInSeries;
        private final List<Duration> waitTimes;

        public Schedule(String name, Appointment firstInSeries, List<Duration> waitTimes) {
            super(name);
            this.firstInSeries = firstInSeries;
            this.waitTimes = waitTimes;
        }

        @Override
        public boolean isMatchableWith(Thing other) {
            return false; // TODO fits into schedule
        }
    }



    public TemporalThing(String name) {
        super(name);
    }
}
