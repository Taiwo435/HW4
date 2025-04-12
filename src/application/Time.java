package application;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

public class Time {
    public static String getUTCTimeAsString() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'");
        return now.format(formatter);
    }

    public String getTime() {
       String utcTime = getUTCTimeAsString();
       
       return utcTime;
    }
}

