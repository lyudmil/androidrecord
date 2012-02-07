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

    public void testCanGetTheContentsOfAOneLineMigration() throws Exception {
        String[] expectedContentsForFirstMigration = {"drop table stuff"};
        String[] actualMigrationContentsForFirstMigration = migrations.loadMigrationNumber(1);
        assertEqual(expectedContentsForFirstMigration, actualMigrationContentsForFirstMigration);

        String[] expectedContentsForSecondMigration = {"drop column id from students"};
        String[] actualMigrationContentsForSecondMigration = migrations.loadMigrationNumber(2);
        assertEqual(expectedContentsForSecondMigration, actualMigrationContentsForSecondMigration);
    }

    public void testCanGetTheContentsOfAMultilineMigration() throws Exception {
        String[] expectedContents = {"drop table stuff", "drop column id from students"};
        String[] actualContents = migrations.loadMigrationNumber(3);

        assertEqual(expectedContents, actualContents);
    }

    public void testHandlesMissingMigrations() throws Exception {
        try {
            migrations.loadMigrationNumber(4);
            fail();
        } catch (RuntimeException e) {
            assertEquals("Missing migration: migrations/4.sql", e.getMessage());
        }
    }

    private void assertEqual(String[] expectedContentsForFirstMigration, String[] actualMigrationContents) {
        for (int i = 0; i < actualMigrationContents.length; i++) {
            assertEquals(expectedContentsForFirstMigration[i], actualMigrationContents[i]);
        }
    }
}
