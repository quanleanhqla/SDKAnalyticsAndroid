package com.mobio.analytics.client.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mobio.analytics.client.models.Campaign;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "campaignMobio";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "campaign";

    private static final String KEY_ID = "id";
    private static final String KEY_CAMPAIGN_ID = "campaign_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CONTENT = "content";

    private static DatabaseHandler sInstance;

    public static synchronized DatabaseHandler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_students_table = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT)", TABLE_NAME, KEY_ID, KEY_NAME, KEY_CONTENT, KEY_CAMPAIGN_ID);
        db.execSQL(create_students_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop_students_table = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
        db.execSQL(drop_students_table);

        onCreate(db);
    }

    public void addCampaign(Campaign campaign){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, campaign.getName());
        values.put(KEY_CAMPAIGN_ID, campaign.getCampaignId());
        values.put(KEY_CONTENT, campaign.getContent());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<Campaign> getAllStudents() {
        List<Campaign>  studentList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while(cursor.isAfterLast() == false) {
            Campaign student = new Campaign(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTENT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_CAMPAIGN_ID)));
            studentList.add(student);
            cursor.moveToNext();
        }
        return studentList;
    }

    public void deleteCampaign(int campaignId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_CAMPAIGN_ID + " =? ", new String[] { String.valueOf(campaignId) });
        db.close();
    }
}
