package associations;

import com.androidrecord.query.QueryContext;
import com.androidrecord.associations.AssociationResolver;
import junit.framework.TestCase;
import models.onetomany.Blog;
import models.onetoone.ExampleRecord;
import models.onetoone.SubRecord;

import java.lang.reflect.Field;

import static com.androidrecord.associations.AssociationResolver.*;

public class AssociationResolverTest extends TestCase {

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
        assertEquals(hasOne, association(subrecord));
        assertEquals(belongsTo, association(record));
    }

    public void testNoAssociationsForNormalFields() throws Exception {
        assertEquals(none, association(name));
        none.establish();
    }

    public void testCanBuildUpContextForEstablishingAssociations() throws Exception {
        ExampleRecord record = new ExampleRecord();
        QueryContext queryContext = new QueryContext<ExampleRecord>(null, ExampleRecord.class);

        for (AssociationResolver resolver : values()) {
            assertEquals(resolver, resolver.on(record).within(queryContext));
            assertEquals(record, resolver.getRecord());
            assertEquals(queryContext, resolver.getContext());
        }
    }
}
