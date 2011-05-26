package models.onetoone;

import com.androidrecord.ActiveRecordBase;
import com.androidrecord.relations.BelongsTo;

public class SubRecord extends ActiveRecordBase<SubRecord> {
    @BelongsTo
    public ExampleRecord record;
    public String name;
}
