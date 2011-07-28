import android.content.ContentValues;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.DateTime;
import com.androidrecord.test.mocks.MockDatabase;
import junit.framework.TestCase;
import models.hierarchy.Major;
import models.hierarchy.Student;
import models.hierarchy.Teacher;
import models.onetomany.Blog;
import models.onetomany.Post;
import models.onetoone.ExampleRecord;
import models.onetoone.SubRecord;

import java.util.Arrays;

public class ActiveRecordBaseTest extends TestCase {

    private MockDatabase db;
    private ExampleRecord exampleRecord;
    private SubRecord subRecord;
    private Blog blog;
    private Post post1;
    private Post post2;

    @Override
    public void setUp() throws Exception {
        this.db = new MockDatabase();
        ActiveRecordBase.bootStrap(this.db, null);

        exampleRecord = new ExampleRecord();
        subRecord = new SubRecord();

        subRecord.record = exampleRecord;
        subRecord.id = (long) 123;

        exampleRecord.id = (long) 321;
        exampleRecord.subrecord = subRecord;

        blog = new Blog();
        blog.id = (long) 2;

        post1 = new Post();
        post2 = new Post();

        post1.id = (long) 1;
        post1.blog = blog;

        post2.id = (long) 3;
        post2.blog = blog;

        blog.posts.addAll(post1, post2);
    }

    public void testGeneratesSqlToCreateTable() throws Exception {
        String expectedSql = "create table example_records (" +
                "id integer primary key autoincrement not null, " +
                "field1 text, " +
                "created_at varchar(19));";
        String sql = ActiveRecordBase.createSqlFor(ExampleRecord.class);

        assertEquals(expectedSql, sql);
    }

    public void testGeneratesSqlToCreateTableIfOwnedByAnEntity() throws Exception {
        String expectedSql = "create table sub_records (" +
                "id integer primary key autoincrement not null, " +
                "name text, " +
                "record_id integer, " +
                "created_at varchar(19));";

        String sql = ActiveRecordBase.createSqlFor(SubRecord.class);
        assertEquals(expectedSql, sql);
    }

    public void testGeneratesSqlToCreateTableIfOwnedByManyEntities() throws Exception {
        String expectedSql = "create table posts (" +
                "id integer primary key autoincrement not null, " +
                "blog_id integer, " +
                "content text, " +
                "created_at varchar(19));";

        String sql = ActiveRecordBase.createSqlFor(Post.class);
        assertEquals(expectedSql, sql);
    }

    public void testGeneratesTableNameProperly() throws Exception {
        assertEquals("example_records", ActiveRecordBase.tableNameFor(ExampleRecord.class));
    }

    public void testRecordsKnowTheirTableName() {
        assertEquals("example_records", new ExampleRecord().tableName());
    }

    public void testRecordsCanCalculateTheirContentValues() {
        ExampleRecord record = new ExampleRecord();
        record.id = (long) 2;
        record.field1 = "Milk";

        ContentValues expectedValues = new ContentValues();
        expectedValues.put("id", (long) 2);
        expectedValues.put("created_at", now());
        expectedValues.put("field1", "Milk");

        assertEquals(expectedValues, record.contentValues());
    }

    public void testOwnedRecordsCanCalculateTheirContentValues() throws Exception {
        ExampleRecord exampleRecord = new ExampleRecord();
        exampleRecord.id = (long) 45;

        SubRecord subRecord = new SubRecord();
        subRecord.id = (long) 3;
        subRecord.name = "some name";
        subRecord.record = exampleRecord;

        ContentValues expectedValues = new ContentValues();
        expectedValues.put("id", (long) 3);
        expectedValues.put("created_at", now());
        expectedValues.put("name", "some name");
        expectedValues.put("record_id", (long) 45);

        assertEquals(expectedValues, subRecord.contentValues());
    }

    private String now() {
        return new DateTime().toString();
    }

    public void testRecordsOwnedByManyCanCalculateTheirContentValues() throws Exception {
        Post post = new Post();
        post.id = (long) 22;
        post.content = "ABC123";
        post.blog = blog;

        ContentValues expectedValues = new ContentValues();
        expectedValues.put("id", (long) 22);
        expectedValues.put("created_at", now());
        expectedValues.put("content", "ABC123");
        expectedValues.put("blog_id", blog.id);

        assertEquals(expectedValues, post.contentValues());
    }

    public void testSaveNewRecord() throws Exception {
        db.nextIdToReturn = 32;
        ExampleRecord record = new ExampleRecord();
        record.field1 = "value";
        ContentValues preSaveContent = record.contentValues();
        record.save();

        assertTrue(db.insertCalled);
        assertEquals("example_records", db.lastQueryParameters.get("tableName"));
        assertEquals(preSaveContent, db.lastQueryParameters.get("contentValues"));
        assertEquals(new Long(32), record.id);
    }

    public void testSaveUpdatesOwnershipRelationships() throws Exception {
        ExampleRecord exampleRecord = new ExampleRecord();
        SubRecord subRecord = new SubRecord();
        exampleRecord.subrecord = subRecord;

        exampleRecord.save();

        assertEquals(exampleRecord, subRecord.record);
    }

    public void testSaveUpdatesMultipleOwnershipRelationships() throws Exception {
        Post post1 = new Post();
        Post post2 = new Post();

        Blog blog = new Blog();
        blog.id = (long) 43;
        blog.posts.addAll(post1, post2);
        blog.save();

        assertEquals(blog, post1.blog);
        assertEquals(blog, post2.blog);
    }

    public void testSaveFailsIfOwningEntitiesTransient() throws Exception {
        ExampleRecord exampleRecord = new ExampleRecord();
        SubRecord subRecord = new SubRecord();
        subRecord.record = exampleRecord;

        try {
            subRecord.save();
            fail("Should have thrown an exception. Cannot save with transient owners.");
        } catch (RuntimeException e) {
            assertEquals("Entity is owned by an entity that has not been saved.", e.getMessage());
        }
    }

    public void testUpdateRecord() throws Exception {
        ExampleRecord record = new ExampleRecord();
        record.id = (long) 1;
        record.field1 = "some value";
        record.save();

        assertTrue(db.updateCalled);
        assertEquals("example_records", db.lastQueryParameters.get("tableName"));
        assertEquals(record.contentValues(), db.lastQueryParameters.get("contentValues"));
        assertEquals("id=1", db.lastQueryParameters.get("whereClause"));
    }

    public void testFindRecord() throws Exception {
        ExampleRecord recordToFind = new ExampleRecord();
        recordToFind.id = (long) 12;
        recordToFind.field1 = "record value";

        db.firstReturn(recordToFind).thenReturnEmptyResult();

        ExampleRecord record = new ExampleRecord().find(12);

        assertTrue(db.selectCalled);
        assertEquals((long) 12, (long) record.id);
        assertEquals("record value", record.field1);
    }

    public void testFindOwningRecord() throws Exception {
        db.firstReturn(exampleRecord).thenReturn(subRecord).forever();

        ExampleRecord found = new ExampleRecord().find(exampleRecord.id);

        assertEquals(exampleRecord.id, found.id);
        assertEquals(subRecord.id, found.subrecord.id);
    }

    public void testFindOwnedRecord() throws Exception {
        db.firstReturn(subRecord).thenReturn(exampleRecord).forever();

        SubRecord found = new SubRecord().find(subRecord.id);

        assertEquals(subRecord.id, found.id);
        assertEquals(exampleRecord.id, found.record.id);
    }

    public void testFindRecordThatOwnsManyRecords() throws Exception {
        db.firstReturn(blog).thenReturn(Arrays.asList(post1, post2)).forever();

        Blog found = new Blog().find(blog.id);

        assertEquals(2, found.posts.size());
    }

    public void testFindMultiplyOwnedRecord() throws Exception {
        db.firstReturn(post1).thenReturn(blog).forever();

        Post post = new Post().find(post1.id);

        assertEquals(blog.id, post.blog.id);
    }

    public void testDestroyRecord() throws Exception {
        ExampleRecord record = new ExampleRecord();
        record.id = (long) 123;
        record.field1 = "field1_value";

        record.destroy();

        assertTrue(db.deleteCalled);
        assertEquals("example_records", db.lastQueryParameters.get("tableName"));
        assertEquals("id=123", db.lastQueryParameters.get("whereClause"));
    }

    public void testOneToOneRelation() throws Exception {
        SubRecord subRecord = new SubRecord();
        subRecord.name = "subrecord";

        ExampleRecord exampleRecord = new ExampleRecord();
        exampleRecord.subrecord = subRecord;
    }

    public void testLoadsADeepHierarchy() throws Exception {
        Teacher teacher = new Teacher();
        teacher.id = (long) 234;

        Major major = new Major();
        major.id = (long) 88;

        Student student = new Student();
        student.id = (long) 67;
        student.adviser = teacher;
        student.major = major;

        db.firstReturn(teacher).thenReturn(Arrays.asList(student)).thenReturn(teacher).thenReturn(major);

        Teacher found = new Teacher().find(teacher.id);

        assertEquals(major.id, found.students.first().major.id);
    }

    public void testLoadsDateTimeFields() throws Exception {
        exampleRecord.field1 = "blah";
        DateTime now = DateTime.now();

        db.firstReturn(exampleRecord);

        assertEquals(now.getYear(), exampleRecord.created_at.getYear());
        assertEquals(now.getMonth(), exampleRecord.created_at.getMonth());
        assertEquals(now.getDay(), exampleRecord.created_at.getDay());
        assertEquals(now.getHour(), exampleRecord.created_at.getHour());
        assertEquals(now.getMinutes(), exampleRecord.created_at.getMinutes());
    }
}
