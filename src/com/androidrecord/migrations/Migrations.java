package com.androidrecord.migrations;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import java.io.*;

public class Migrations {
    private AssetManager assets;

    public Migrations(AssetManager assets) {
        this.assets = assets;
    }

    public int latest() throws IOException {
        String[] migrations = assets.list("migrations");
        int latestVersion = 1;
        for (String migration : migrations) {
            int migrationNumber = migrationNumberFrom(migration);
            if (migrationNumber > latestVersion) latestVersion = migrationNumber;
        }
        return latestVersion;
    }

    private int migrationNumberFrom(String fileName) {
        try {
            return Integer.parseInt(fileName.replace(".sql", ""));
        } catch (RuntimeException e) {
            throw new RuntimeException("Migrations directory includes a non-numeric file name. All migration file names should be of the form <number>.sql.", e);
        }
    }

    public String loadMigrationNumber(int migrationNumber) throws IOException {
        String path = "migrations/" + migrationNumber + ".sql";
        BufferedReader migrationReader = new BufferedReader(new InputStreamReader(assets.open(path)));

        StringBuilder contents = new StringBuilder();
        String line;
        while ((line = migrationReader.readLine()) != null) {
            contents.append(line);
        }
        return contents.toString();
    }
}
