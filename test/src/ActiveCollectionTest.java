import com.androidrecord.ActiveCollection;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.test.mocks.MockDatabase;
import junit.framework.TestCase;
import models.onetomany.Blog;
import models.onetomany.Post;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class ActiveCollectionTest extends TestCase {

    private MockDatabase db;
    private ActiveCollection<Post> posts;
    private Post post1;
    private Post post2;
    private Post post3;
    private Blog blog;

    protected void setUp() throws Exception {
        super.setUp();
        db = new MockDatabase();
        ActiveRecordBase.bootStrap(db);

        blog = new Blog();

        Field field = blog.getClass().getField("posts");
        posts = new ActiveCollection<Post>(blog, field);

        post1 = new Post();
        post2 = new Post();
        post3 = new Post();

        db.returnEmptyResult().forever();
    }

    public void testCanAddRecords() throws Exception {
        posts.add(post1);
        posts.add(post3);

        assertTrue(posts.contain(post1));
        assertTrue(posts.contain(post3));

        assertFalse(posts.contain(post2));
    }

    public void testAddSetsOwnerCorrectly() throws Exception {
        posts.add(post1);
        posts.add(post2);

        assertEquals(blog, post1.blog);
        assertEquals(blog, post2.blog);
        assertNull(post3.blog);
    }

    public void testAddingMultipleItemsSetsTheOwnerOnAll() throws Exception {
        posts.addAll(post1, post2);

        assertEquals(blog, post1.blog);
        assertEquals(blog, post2.blog);
        assertNull(post3.blog);
    }

    public void testCanDetermineIfEmpty() throws Exception {
        assertTrue(posts.areEmpty());

        posts.add(post1);

        assertFalse(posts.areEmpty());
    }

    public void testCanAddManyRecordsAtOnce() throws Exception {
        posts.addAll(post1, post2);

        assertTrue(posts.contain(post1));
        assertTrue(posts.contain(post2));

        assertFalse(posts.contain(post3));
    }

    public void testCanRemoveRecords() throws Exception {
        posts.addAll(post1, post2, post3);

        posts.remove(post1);

        assertFalse(posts.contain(post1));

        assertTrue(posts.contain(post2));
        assertTrue(posts.contain(post3));
    }

    public void testCanIterateOverRecords() throws Exception {
        posts.addAll(post1, post2, post3);

        Field field = blog.getClass().getField("posts");
        ActiveCollection<Post> postsCopy = new ActiveCollection<Post>(blog, field);
        postsCopy.addAll(post1, post2, post3);

        for (Post post : posts) {
            postsCopy.remove(post);
        }

        assertTrue(postsCopy.areEmpty());
        assertFalse(posts.areEmpty());
    }

    public void testLoadsCollectionFromTheDatabaseOnAccess() throws Exception {
        setUpRelationships();
        assertCollectionSize(0);

        assertEquals(3, blog.posts.size());
    }

    public void testLoadsCollectionWhenIterating() throws Exception {
        setUpRelationships();
        assertCollectionSize(0);

        for (Post post : posts) {}

        assertCollectionSize(3);
    }

    public void testLoadsCollectionWhenEvaluatingContain() throws Exception {
        setUpRelationships();
        assertCollectionSize(0);

        posts.contain(post1);

        assertCollectionSize(3);
    }

    public void testLoadsCollectionWhenEvaluatingAreEmpty() throws Exception {
        setUpRelationships();
        assertCollectionSize(0);

        posts.areEmpty();

        assertCollectionSize(3);
    }

    private void setUpRelationships() {
        blog.id = (long) 151;

        post1.blog = blog;
        post1.id = (long) 1;

        post2.blog = blog;
        post2.id = (long) 2;

        post3.blog = blog;
        post3.id = (long) 3;

        db.firstReturn(Arrays.asList(post1, post2, post3)).thenReturn(blog);
    }

    private void assertCollectionSize(int size) throws NoSuchFieldException, IllegalAccessException {
        Field recordsField = posts.getClass().getDeclaredField("records");
        recordsField.setAccessible(true);
        ArrayList records = (ArrayList) recordsField.get(posts);
        assertEquals(size, records.size());
    }


}
