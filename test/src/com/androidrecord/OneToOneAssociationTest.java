package com.androidrecord;

import com.androidrecord.associations.OneToOneAssociation;
import junit.framework.TestCase;
import models.onetoone.ExampleRecord;
import models.onetoone.SubRecord;

public class OneToOneAssociationTest extends TestCase {
    private OneToOneAssociation association;
    private ExampleRecord exampleRecord;
    private SubRecord subRecord;

    protected void setUp() throws Exception {
        super.setUp();
        association = new OneToOneAssociation("record", (long) 2);

        exampleRecord = new ExampleRecord();
        exampleRecord.id = (long) 2;

        subRecord = new SubRecord();
        subRecord.id = (long) 3;
    }

    public void testFulfillsAssociationsWhenOwnerConnectsFirst() throws Exception {
        association.connectOwner(exampleRecord);
        association.connectOwned(subRecord, (long) 2);

        assertAssociationFulfilled();
    }

    public void testFulfillsAssociationWhenOwnedConnectsFirst() throws Exception {
        association.connectOwned(subRecord, (long) 2);
        association.connectOwner(exampleRecord);

        assertAssociationFulfilled();
    }

    public void testNotFulfilledIfJustOwnerConnected() throws Exception {
        association.connectOwner(exampleRecord);
        assertFalse(association.isFulfilled());
    }

    public void testNotFulfilledIfJustOwnedConnected() throws Exception {
        association.connectOwned(subRecord, exampleRecord.id);
        assertFalse(association.isFulfilled());
    }

    public void testDoesNotConnectRecordsIfUnexpectedOwnerIdWhenOwnerFirst() throws Exception {
        assertTrue(association.connectOwner(exampleRecord));
        assertFalse(association.connectOwned(new SubRecord(), (long) 44));
    }

    public void testDoesNotConnectRecordsIfUnexpectedOwnerIdWhenOwnedFirst() throws Exception {
        assertTrue(association.connectOwned(subRecord, (long) 2));
        assertFalse(association.connectOwner(new ExampleRecord()));

    }

    private void assertAssociationFulfilled() {
        assertEquals(subRecord, exampleRecord.subrecord);
        assertEquals(exampleRecord, subRecord.record);
        assertTrue(association.isFulfilled());
    }
}
