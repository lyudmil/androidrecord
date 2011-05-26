package models.dateandtime;

import com.androidrecord.ActiveRecordBase;
import com.androidrecord.DateTime;

public class Album extends ActiveRecordBase<Album> {
    public String title;
    public DateTime release_date;
    public int units_sold;
    public boolean independent;
}
