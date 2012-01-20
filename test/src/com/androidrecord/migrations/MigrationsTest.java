package com.androidrecord.migrations;

import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;

public class MigrationsTest extends InstrumentationTestCase {

    private Migrations migrations;

    protected void setUp() throws Exception {
        super.setUp();
        AssetManager assets = getInstrumentation().getContext().getAssets();
        migrations = new Migrations(assets);
    }

    public void testCanGetTheLatestVersion() throws Exception {
        assertEquals(3, migrations.latest());
    }

    public void testCanGetTheContentsOfAMigration() throws Exception {
        assertEquals("drop table stuff;", migrations.loadMigrationNumber(1));
        assertEquals("drop column id from students;", migrations.loadMigrationNumber(2));
    }

    public void testHandlesMissingMigrations() throws Exception {
        try {
            migrations.loadMigrationNumber(4);
            fail();
        } catch (RuntimeException e) {
            assertEquals("Missing migration: migrations/4.sql", e.getMessage());
        }
    }
}
