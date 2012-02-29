package models.hierarchy;


import com.androidrecord.ActiveRecordBase;
import com.androidrecord.associations.BelongsTo;

public class Student extends ActiveRecordBase<Student> {
    public String name;

    @BelongsTo
    public Teacher adviser;

    @BelongsTo
    public Major major;

}
