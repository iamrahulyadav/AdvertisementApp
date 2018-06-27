package com.projects.owner.camlocation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.projects.owner.camlocation.model.ImagesModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 10;
    public static final String DATABASE_NAME = "Cameraapp.db";
    public static final String TABLE_NAME = "TblImg";
    public static final String ID = "id";
    public static final String BRAND = "brand";
    public static final String VENDOR = "vendor";
    public static final String RATING = "rating";
    public static final String SIZE = "size";
    public static final String CATAGORY = "catagory";
    public static final String AGENCY = "agency";
    public static final String IMAGE = "image";
    public static final String DATE_CURRENT = "date_current";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String DATE_MILLI = "date_milli";
    private Date date;
    private int year, month, day;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY," + BRAND + " TEXT," + VENDOR + " TEXT," + RATING +
                " INTEGER," + DATE_CURRENT + " TEXT, " + SIZE + " TEXT,"
                + CATAGORY + " TEXT," + AGENCY + " TEXT," + IMAGE + " BLOB," + LAT + " DOUBLE ,"
                + LNG + " DOUBLE, " + DATE_MILLI + " LONG " + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    private String getDateTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        Date d = new Date(year, month + 1, day);
        return String.valueOf(new StringBuilder().append(year).append("/")
                .append(month + 1).append("/").append(day));
    }

    private Long getDateTimeMilli() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        Date d = new Date(year, month + 1, day);
        return d.getTime();
    }

    public long addValues(ImagesModel image) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BRAND, image.getBrandName()); //
        values.put(VENDOR, image.getVendor()); //
        values.put(RATING, image.getRating()); //
        values.put(DATE_CURRENT, getDateTime()); //
        values.put(SIZE, image.getSize()); //
        values.put(CATAGORY, image.getCatagory());
        values.put(AGENCY, image.getAgency()); //
        values.put(IMAGE, image.getBitmap()); //
        values.put(LAT, image.getLat());
        values.put(LNG, image.getLng());
        values.put(DATE_MILLI, getDateTimeMilli());
        long a = -2;
        // Inserting Row
        try {
            a = db.insertOrThrow(TABLE_NAME, null, values);
            Log.d("", "db add succes: " + a);


        } catch (Exception e) {
            Log.d("", "addValues: wrong " + e.getMessage());
        }
        db.close();
        return a;
    }

    public ImagesModel getSingleImage(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[]{ID,
                        BRAND, VENDOR, RATING, DATE_CURRENT, SIZE, CATAGORY, AGENCY, IMAGE, LAT, LNG}, ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        assert cursor != null;
        ImagesModel image = new ImagesModel(cursor.getString(0),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)
                , cursor.getString(6), cursor.getString(7), cursor.getBlob(8), Double.parseDouble(cursor.getString(9)), Double.parseDouble(cursor.getString(10)));
        cursor.close();
        return image;
    }

    // Getting All images
    public List<ImagesModel> getAllImages() {
        List<ImagesModel> imagesModelList = new ArrayList<ImagesModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null)
            if (cursor.moveToFirst()) {
                byte[] a = cursor.getBlob(8);
                do {
                    ImagesModel image = new ImagesModel(cursor.getString(0),
                            cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)
                            , cursor.getString(6), cursor.getString(7), cursor.getBlob(8), Double.parseDouble(cursor.getString(9)),
                            Double.parseDouble(cursor.getString(10)));
                    imagesModelList.add(image);
                } while (cursor.moveToNext());
            }
        assert cursor != null;
        cursor.close();
        return imagesModelList;
    }

    public List<ImagesModel> getAllImagesDatewise(Long argdate) {
        List<ImagesModel> imagesModelList = new ArrayList<ImagesModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{ID,
                        BRAND, VENDOR, RATING, DATE_CURRENT, SIZE, CATAGORY, AGENCY, IMAGE, LAT, LNG}, DATE_MILLI + "=?",
                new String[]{String.valueOf(argdate)}, null, null, null, null);
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    ImagesModel image = new ImagesModel(cursor.getString(0),
                            cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)
                            , cursor.getString(6), cursor.getString(7), cursor.getBlob(8), Double.parseDouble(cursor.getString(9)),
                            Double.parseDouble(cursor.getString(10)));
                    imagesModelList.add(image);
                } while (cursor.moveToNext());
            }
        assert cursor != null;
        cursor.close();
        return imagesModelList;
    }

    public int getImagesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }


}
