package com.androidrecord.migrations;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Migrations {
    public static final String MIGRATIONS_SUB_DIRECTORY = "migrations";
    private AssetManager assets;

    public Migrations(AssetManager assets) {
        this.assets = assets;
    }

    public int latest() {
        String[] migrations = listMigrationFiles();
        int latestVersion = 1;
        for (String migration : migrations) {
            int migrationNumber = migrationNumberFrom(migration);
            if (migrationNumber > latestVersion) latestVersion = migrationNumber;
        }
        return latestVersion;
    }

    private String[] listMigrationFiles() {
        try {
            return assets.list(MIGRATIONS_SUB_DIRECTORY);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open " + MIGRATIONS_SUB_DIRECTORY + " folder.");
        }
    }

    private int migrationNumberFrom(String fileName) {
        try {
            return Integer.parseInt(fileName.replace(".sql", ""));
        } catch (RuntimeException e) {
            throw new RuntimeException("Migrations directory includes a non-numeric file name. All migration file names should be of the form <number>.sql.", e);
        }
    }

    public String loadMigrationNumber(int migrationNumber) {
        String file = MIGRATIONS_SUB_DIRECTORY + "/" + migrationNumber + ".sql";

        return contentsOf(file);
    }

    private String contentsOf(String path) {
        try {
            return contentsOfFileAsString(path);
        } catch (IOException e) {
            throw new RuntimeException("Missing migration: " + path);
        }

    }

    private String contentsOfFileAsString(String path) throws IOException {
        BufferedReader migrationReader = new BufferedReader(new InputStreamReader(assets.open(path)));
        StringBuilder contents = new StringBuilder();
        String line;
        while ((line = migrationReader.readLine()) != null) contents.append(line);
        return contents.toString();
    }
}
