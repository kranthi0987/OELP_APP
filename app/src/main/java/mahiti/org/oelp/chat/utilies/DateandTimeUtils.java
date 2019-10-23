package mahiti.org.oelp.chat.utilies;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateandTimeUtils {

    public String currentDateTime() {
        DateFormat date = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault());
// you can get seconds by adding  "...:ss" to it
        Date todayDate = new Date();
        return date.format(todayDate);
    }

    public long getTimeInMilliSec(String DateAndTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        Calendar calendar = new GregorianCalendar();
        TimeZone timeZone = calendar.getTimeZone();
        sdf.setTimeZone(timeZone);
        Date date = null;
        try {
            date = sdf.parse(DateAndTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("in milliseconds: " + date.getTime());
        return date.getTime();
    }
}
