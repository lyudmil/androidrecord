

import com.androidrecord.relations.OneToOneRelation;
import models.onetoone.ExampleRecord;
import models.onetoone.SubRecord;
import junit.framework.TestCase;

public class OneToOneRelationTest extends TestCase {
    private OneToOneRelation relation;
    private ExampleRecord exampleRecord;
    private SubRecord subRecord;

    protected void setUp() throws Exception {
        super.setUp();
        relation = new OneToOneRelation("record", (long) 2);

        exampleRecord = new ExampleRecord();
        exampleRecord.id = (long) 2;

        subRecord = new SubRecord();
        subRecord.id = (long) 3;
    }

    public void testFulfillsRelationWhenOwnerConnectsFirst() throws Exception {
        relation.connectOwner(exampleRecord);
        relation.connectOwned(subRecord, (long) 2);

        assertRelationFulfilled();
    }

    public void testFulfillsRelationWhenOwnedConnectsFirst() throws Exception {
        relation.connectOwned(subRecord, (long) 2);
        relation.connectOwner(exampleRecord);

        assertRelationFulfilled();
    }

    public void testNotFulfilledIfJustOwnerConnected() throws Exception {
        relation.connectOwner(exampleRecord);
        assertFalse(relation.isFulfilled());
    }

    public void testNotFulfilledIfJustOwnedConnected() throws Exception {
        relation.connectOwned(subRecord, exampleRecord.id);
        assertFalse(relation.isFulfilled());
    }

    public void testDoesNotConnectRecordsIfUnexpectedOwnerIdWhenOwnerFirst() throws Exception {
        assertTrue(relation.connectOwner(exampleRecord));
        assertFalse(relation.connectOwned(new SubRecord(), (long) 44));
    }

    public void testDoesNotConnectRecordsIfUnexpectedOwnerIdWhenOwnedFirst() throws Exception {
        assertTrue(relation.connectOwned(subRecord, (long) 2));
        assertFalse(relation.connectOwner(new ExampleRecord()));

    }

    private void assertRelationFulfilled() {
        assertEquals(subRecord, exampleRecord.subrecord);
        assertEquals(exampleRecord, subRecord.record);
        assertTrue(relation.isFulfilled());
    }
}
