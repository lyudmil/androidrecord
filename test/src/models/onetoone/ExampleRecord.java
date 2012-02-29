package models.onetoone;

import com.androidrecord.ActiveRecordBase;
import com.androidrecord.associations.HasOne;

public class ExampleRecord extends ActiveRecordBase<ExampleRecord> {
    public String field1;

    @HasOne(name = "record")
    public SubRecord subrecord;
}
