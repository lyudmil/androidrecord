package relations;

import com.androidrecord.query.QueryContext;
import com.androidrecord.relations.RelationResolver;
import junit.framework.TestCase;
import models.onetomany.Blog;
import models.onetoone.ExampleRecord;
import models.onetoone.SubRecord;

import java.lang.reflect.Field;

import static com.androidrecord.relations.RelationResolver.*;

public class RelationResolverTest extends TestCase {

    private Field subrecord;
    private Field record;
    private Field name;
    private Field posts;

    protected void setUp() throws Exception {
        super.setUp();
        subrecord = ExampleRecord.class.getField("subrecord");
        record = SubRecord.class.getField("record");
        name = SubRecord.class.getField("name");
        posts = Blog.class.getField("posts");
    }

    public void testDeterminesResolverTypeDependingOnField() throws Exception {
        assertEquals(hasOne, relation(subrecord));
        assertEquals(belongsTo, relation(record));
    }

    public void testNoRelationForNormalFields() throws Exception {
        assertEquals(none, relation(name));
        none.establish();
    }

    public void testCanBuildUpContextForEstablishingRelation() throws Exception {
        ExampleRecord record = new ExampleRecord();
        QueryContext queryContext = new QueryContext<ExampleRecord>(null, record);

        for (RelationResolver resolver : values()) {
            assertEquals(resolver, resolver.on(record).within(queryContext));
            assertEquals(record, resolver.getRecord());
            assertEquals(queryContext, resolver.getContext());
        }
    }
}
