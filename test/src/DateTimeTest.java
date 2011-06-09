import com.androidrecord.DateTime;
import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.util.Calendar.MAY;

public class DateTimeTest extends TestCase {

    private DateTime dateTime;

    protected void setUp() throws Exception {
        super.setUp();

        dateTime = new DateTime();
        dateTime.setDay(17);
        dateTime.setMonth(MAY);
        dateTime.setYear(1984);
        dateTime.setHour(14);
        dateTime.setMinutes(33);
        dateTime.setSeconds(45);
    }

    public void testCanCreateATimestamp() throws Exception {
        assertEquals(17, dateTime.getDay());
        assertEquals(MAY, dateTime.getMonth());
        assertEquals(1984, dateTime.getYear());
        assertEquals(14, dateTime.getHour());
        assertEquals(33, dateTime.getMinutes());
        assertEquals(45, dateTime.getSeconds());
    }

    public void testConversionFromString() throws Exception {
        DateTime dateTime = DateTime.from("1984-05-17 15:34:47");

        assertEquals(1984, dateTime.getYear());
        assertEquals(MAY, dateTime.getMonth());
        assertEquals(17, dateTime.getDay());
        assertEquals(15, dateTime.getHour());
        assertEquals(34, dateTime.getMinutes());
        assertEquals(47, dateTime.getSeconds());
    }

    public void testConversionToString() throws Exception {
        assertEquals("1984-05-17 14:33:45", dateTime.toString());
    }

    public void testNow() throws Exception {
        SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();

        assertEquals(DateTime.now().toString(), sqlFormat.format(now));
    }

    public void testDisplayFormat() throws Exception {
        DateTime date = DateTime.from("1984-05-07 14:33:45");

        assertEquals("7.5.1984", date.display("d.M.yyyy"));
        assertEquals("07.05.1984", date.display("dd.MM.yyyy"));
        assertEquals("5/7/1984", date.display("M/d/yyyy"));
    }

    public void testCopy() throws Exception {
        DateTime copy = DateTime.copy(dateTime);
        assertEquals(dateTime, copy);
        assertNotSame(dateTime, copy);
    }

    public void testCanTellIfBeforeOrAfter() throws Exception {
        DateTime copy = DateTime.copy(dateTime);

        DateTime preceding = DateTime.copy(dateTime);
        preceding.setSeconds(44);

        assertFalse(copy.isBefore(dateTime));
        assertFalse(copy.isAfter(dateTime));

        assertTrue(preceding.isBefore(dateTime));
        assertFalse(preceding.isAfter(dateTime));

        assertTrue(dateTime.isAfter(preceding));
        assertFalse(dateTime.isBefore(preceding));
    }
}
