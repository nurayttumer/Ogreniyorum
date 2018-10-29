package com.mnm.ogreniyorum;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emirc on 10.12.2017.
 */
public class TableControllerSubject extends DatabaseHandler {

    public TableControllerSubject(Context context) {
        super(context);
    }

    public boolean create(ObjectSubject objectSubject) {

        ContentValues values = new ContentValues();

        values.put("id", objectSubject.id);
        values.put("title", objectSubject.title);
        values.put("body", objectSubject.body);

        SQLiteDatabase db = this.getWritableDatabase();

        boolean createSuccessful = db.insert("subjects", null, values) > 0;
        db.close();

        return createSuccessful;
    }

    public int count() {

        SQLiteDatabase db = getWritableDatabase();

        String sql = "SELECT * FROM subjects";
        int recordCount = db.rawQuery(sql, null).getCount();
        db.close();

        return recordCount;

    }

    public List<ObjectSubject> read() {

        List<ObjectSubject> recordsList = new ArrayList<ObjectSubject>();

        String sql = "SELECT * FROM subjects ORDER BY id DESC";

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {

                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String body = cursor.getString(cursor.getColumnIndex("body"));

                ObjectSubject objectSubject = new ObjectSubject();
                objectSubject.id = id;
                objectSubject.title = title;
                objectSubject.body = body;

                recordsList.add(objectSubject);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return recordsList;
    }
}
