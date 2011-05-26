package query;

import android.content.ContentValues;
import android.database.Cursor;
import com.androidrecord.DateTime;
import com.androidrecord.test.mocks.MockCursor;
import models.dateandtime.Album;
import junit.framework.TestCase;

import java.lang.reflect.Field;

import static com.androidrecord.query.ResultSetFieldTranslator.*;

public class ResultSetFieldTranslatorTest extends TestCase {

    private Field id;
    private Field title;
    private Field releaseDate;
    private ContentValues contentValues;
    private Album album;
    private Field unitsSold;
    protected Field independent;

    protected void setUp() throws Exception {
        super.setUp();
        id = Album.class.getField("id");
        title = Album.class.getField("title");
        releaseDate = Album.class.getField("release_date");
        unitsSold = Album.class.getField("units_sold");
        independent = Album.class.getField("independent");

        contentValues = new ContentValues();
        album = new Album();
    }

    public void testDeterminesTheCorrectTranslatorBasedOnTheFieldType() throws Exception {
        assertEquals(LONG, translate(id));
        assertEquals(STRING, translate(title));
        assertEquals(DATE_TIME, translate(releaseDate));
        assertEquals(INTEGER, translate(unitsSold));
        assertEquals(BOOLEAN, translate(independent));
    }

    public void testLongExtractsTheCorrectValue() throws Exception {
        contentValues.put("id", (long) 56);

        assertEquals((long) 56, translate(id).from(cursor()).value());
    }

    public void testStringExtractsTheCorrectValue() throws Exception {
        contentValues.put("title", "Kid A");

        assertEquals("Kid A", translate(title).from(cursor()).value());
    }

    public void testIntegerExtractsTheCorrectValue() throws Exception {
        contentValues.put("units_sold", 1000);

        assertEquals(1000, translate(unitsSold).from(cursor()).value());
    }

    public void testDateTimeExtractsValue() throws Exception {
        String dateString = DateTime.now().toString();
        contentValues.put("release_date", dateString);

        assertEquals(dateString, translate(releaseDate).from(cursor()).value().toString());
    }

    public void testCopiesLongFieldIntoRecord() throws Exception {
        contentValues.put("id", (long) 124);

        translate(id).from(cursor()).to(album);

        assertEquals((long) 124, (long) album.id);
    }

    public void testCopiesStringFieldIntoRecord() throws Exception {
        contentValues.put("title", "Kid A");

        translate(title).from(cursor()).to(album);

        assertEquals("Kid A", album.title);
    }

    public void testCopiesDateTimeFieldIntoRecord() throws Exception {
        DateTime date = DateTime.now();
        contentValues.put("release_date", date.toString());

        translate(releaseDate).from(cursor()).to(album);

        assertEquals(date.toString(), album.release_date.toString());
    }

    public void testCopiesIntegerFieldsIntoRecord() throws Exception {
        contentValues.put("units_sold", 12345);

        translate(unitsSold).from(cursor()).to(album);

        assertEquals(12345, album.units_sold);
    }

    public void testCopiesBooleanFieldsIntoRecord() throws Exception {
        contentValues.put("independent", 1);
        translate(independent).from(cursor()).to(album);
        assertEquals(true, album.independent);

        contentValues.put("independent", 0);
        translate(independent).from(cursor()).to(album);
        assertEquals(false, album.independent);
    }

    private Cursor cursor() {
        MockCursor cursor = new MockCursor(contentValues);
        cursor.moveToFirst();
        return cursor;
    }
}
