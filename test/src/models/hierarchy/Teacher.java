package models.hierarchy;

import com.androidrecord.ActiveCollection;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.associations.HasMany;

public class Teacher extends ActiveRecordBase<Teacher> {
    public String name;

    @HasMany(name = "adviser", relatedType = Student.class)
    public ActiveCollection<Student> students;
}
