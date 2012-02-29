package models.hierarchy;

import com.androidrecord.ActiveCollection;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.associations.HasMany;

public class Major extends ActiveRecordBase<Major> {
    public String name;

    @HasMany(name = "major", relatedType = Student.class)
    public ActiveCollection<Student> enrolledStudents;
}
