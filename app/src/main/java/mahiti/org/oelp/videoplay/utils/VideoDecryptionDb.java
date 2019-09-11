package mahiti.org.oelp.videoplay.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mahiti.org.oelp.database.DBConstants;
import mahiti.org.oelp.videoplay.tracker.MediaTracker;

public class VideoDecryptionDb extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "VideoDecryptionDB";
    private static String DATABASE_NAME = VideoDbColumns.dbName;
    Context context;

    public VideoDecryptionDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String query;
            query = "CREATE TABLE IF NOT EXISTS " + VideoDbColumns.mediaTableName + " (" +
                    VideoDbColumns.serialno + " INTEGER PRIMARY KEY AUTOINCREMENT "+DBConstants.COMMA +
                    VideoDbColumns.mediaUuid + DBConstants.COMMA +
                    VideoDbColumns.mediaMediaName + " TEXT " + DBConstants.COMMA +
                    VideoDbColumns.mediaStartTime + " TEXT " + DBConstants.COMMA +
                    VideoDbColumns.mediaEndTime + " TEXT " + DBConstants.COMMA +
                    VideoDbColumns.mediaWatchMinutes + " TEXT " + DBConstants.COMMA +
                    VideoDbColumns.mediaCompleteStatus + " INTEGER )";
            db.execSQL(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public SQLiteDatabase getDatabase() {
        SQLiteDatabase database_q = null;
        if (doesDatabaseExist(context, DATABASE_NAME))
            database_q = this.getWritableDatabase();
        else
            return database_q;
        return database_q;
    }

    public void insertData(JSONArray array) {
        List<String> queryList = new ArrayList<String>();
        String insertQuery = "INSERT OR REPLACE INTO " + VideoDbColumns.TableName;
        String columnsForm = " (" + VideoDbColumns.primaryId + "," + VideoDbColumns.vId + "," + VideoDbColumns.vName + "," + VideoDbColumns.key + "," + VideoDbColumns.upTime + ") VALUES";
        try {
            for (int i = 0; i < array.length(); i++) {
//                JSONObject obj = array.getJSONObject(i);
//                String values = " (" + obj.getInt("primaryId") + "," + obj.getInt("videoId") + ",'" + obj.getString("videoName").replace("'", "_") + "','" + obj.getString("key") + "','" + getDateTime() + "')";
//                queryList.add(insertQuery + columnsForm + values);
            }
        } catch (Exception e) {
            Logger.logE("VideoDecryptionDB", "insertData", e);
        }

        try {
            //net.sqlcipher.database.SQLiteDatabase database_q = this.getWritableDatabase(context.getString(R.string.videostuffDb)); //this.getWritableDatabase(context.getString(R.string.videostuffDb));
            SQLiteDatabase database_q = this.getWritableDatabase();

            database_q.beginTransaction();
            for (int i = 0; i < queryList.size(); i++) {
                String query = queryList.get(i);
                Logger.logD("dynamicQueries", query);
                SQLiteStatement statement = database_q.compileStatement(query);
                statement.execute();
            }
            database_q.setTransactionSuccessful();
            database_q.endTransaction();
            database_q.close();
            // Log.i(" VIDEO DB ","ViALUES INSERTED TO DB : ");
        } catch (Exception e) {
            Logger.logE("VideoDecryptionDB", "SQLiteStatement ", e);
        }
    }

    private String getDateTimeFromDb(String mediaUUId) {
        String date="";
        SQLiteDatabase database = this.getWritableDatabase();
        String query = DBConstants.SELECT+VideoDbColumns.mediaStartTime+DBConstants.FROM+VideoDbColumns.mediaTableName+DBConstants.WHERE+VideoDbColumns.mediaUuid+" = '"+mediaUUId+"'";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()){
            date= cursor.getString(cursor.getColumnIndex(VideoDbColumns.mediaStartTime));
        }
        return date;

    }

    public String checkValidation(String fileName, String key) {

        //null;// this.getReadableDatabase(context.getString(R.string.videostuffDb));
        boolean flag = false;
        String videoId = "";
        Cursor cursor;
        String oldDate;
        Date d;
        try {

            SQLiteDatabase database; //= this.getWritableDatabase(context.getString(R.string.videostuffDb));


//            Log.i(" VIDEO DB ", "VideoDecryptionDB getdatabase : " + this.getDatabase());

            if (doesDatabaseExist(context, DATABASE_NAME))
                database = this.getWritableDatabase();
                //database= this.getWritableDatabase(context.getString(R.string.videostuffDb));
            else
                return videoId;

            String query = "SELECT " + VideoDbColumns.vId + "," + VideoDbColumns.key + " FROM " + VideoDbColumns.TableName + " where "/*+VideoDbColumns.vName+" = '"+fileName+"' AND "*/ + VideoDbColumns.key + "='" + key + "';";
            cursor = database.rawQuery(query, null);

            int c = cursor.getCount();
            //cursor.close();
            // Log.i(" VIDEO DB ", " CURSOR COUNT : " + c);

            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {

                videoId = cursor.getString(cursor.getColumnIndex(VideoDbColumns.vId));
                flag = true;
                cursor.close();
                database.close();
            } else {
                flag = false;
                cursor.close();
                database.close();
            }

        } catch (Exception e) {
            //  Log.i(" Vdb " ," Exception : "+e);

            e.printStackTrace();
        }
        return videoId;
    }

    private static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        Log.i(" VIDEO DB ", "VideoDecryptionDB Exists or NOT : " + dbFile.exists());
        return dbFile.exists();
    }


    public boolean checkForSeven(String fileName, String key) {
        boolean flag = false;
        Cursor cursor = null;
        try {

            SQLiteDatabase database; //= this.getWritableDatabase(context.getString(R.string.videostuffDb));

            //Log.i(" VIDEO DB ", "VideoDecryptionDB getdatabase : " + this.getDatabase());

            if (doesDatabaseExist(context, DATABASE_NAME))
                database = this.getWritableDatabase();
                //database= this.getWritableDatabase(context.getString(R.string.videostuffDb));
            else
                return false;

            String query = "SELECT " + VideoDbColumns.upTime + " FROM " + VideoDbColumns.TableName + " where "/*+VideoDbColumns.vName+" = '"+fileName+"' AND " */ + VideoDbColumns.key + "='" + key + "';";
            cursor = database.rawQuery(query, null);

            int c = cursor.getCount();
            //cursor.close();
            //Log.i(" VIDEO DB ", " date cursor count : " + c);

            // Log.i(" VIDEO DB ", " date cursor count : " + DatabaseUtils.dumpCursorToString(cursor));


            if (cursor != null && (c > 0)) {
                cursor.moveToFirst();
                flag = checkDates(cursor.getString(cursor.getColumnIndex(VideoDbColumns.upTime)));
                cursor.close();
                database.close();
            } else {
                flag = false;
                cursor.close();
                database.close();
            }

        } catch (Exception e) {
            Log.i(" Vdb ", " Exception : " + e);

            e.printStackTrace();
        }
        return flag;
    }


    private boolean checkDates(String oldDate) {

        //String dateStop = "01/15/2012 10:31:48";

        //HH converts hour in 24 hours format (0-23), day calculation

        boolean lessthansevenDays = false;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        Date date = new Date();
        // Log.i("VDB checkdates+ "," Date "+simpleDateFormat.format(date));
        String newDate = simpleDateFormat.format(date);


        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(oldDate);
            d2 = format.parse(newDate);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            /*cursor.moveToFirst();
            Log.i("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
            do{
                oldDate=cursor.getString(cursor.getColumnIndex(VideoDbColumns.upTime));

            }while(cursor.moveToNext());
            d=new Date(oldDate);
            if()*/

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            System.out.print(diffDays + " days, ");
            System.out.print(diffHours + " hours, ");
            System.out.print(diffMinutes + " minutes, ");
            System.out.print(diffSeconds + " seconds.");

            if (diffDays < SevendaysVariables.noOfDays) {
                if (diffHours < SevendaysVariables.noOfHours) {
                    if (diffMinutes < SevendaysVariables.noOfMins) {
                        if (diffSeconds < SevendaysVariables.noOfSecs) {
                            lessthansevenDays = true;
                        }
                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return lessthansevenDays;
    }


    /**
     * @param mediaTracker
     * @return
     */
    public long mediaTrackerInsert(MediaTracker mediaTracker) {
        long insertedRecord = -1;
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(VideoDbColumns.mediaUuid, mediaTracker.getMediaId());
            values.put(VideoDbColumns.mediaMediaName, mediaTracker.getMediaName());
            values.put(VideoDbColumns.mediaStartTime, mediaTracker.getStartTime());
            values.put(VideoDbColumns.mediaEndTime, mediaTracker.getEndTime());
            values.put(VideoDbColumns.mediaCompleteStatus, mediaTracker.getComStatus());
            values.put(VideoDbColumns.mediaWatchMinutes, mediaTracker.getWatchMin());
            Logger.logD(TAG, "Insert Tracker Data"+values.toString());
            insertedRecord = database.insertOrThrow(VideoDbColumns.mediaTableName, null, values);
            database.close();
        } catch (Exception e) {
            Log.i("VDB ", " mediaTrackerInsert ");
            e.printStackTrace();
        }
        return insertedRecord;
    }


    public void mediaTrackerUpdateDb(MediaTracker mediaTracker, String mediauuid) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(VideoDbColumns.mediaUuid, mediaTracker.getMediaId());
            values.put(VideoDbColumns.mediaMediaName, mediaTracker.getMediaName());
            values.put(VideoDbColumns.mediaStartTime, getDateTimeFromDb(mediauuid));
            values.put(VideoDbColumns.mediaEndTime, mediaTracker.getEndTime());
            values.put(VideoDbColumns.mediaWatchMinutes, mediaTracker.getWatchMin());
            values.put(VideoDbColumns.mediaCompleteStatus, mediaTracker.getComStatus());
            Logger.logD(TAG, "Update Tracker Data"+mediaTracker.toString());
            database.update(VideoDbColumns.mediaTableName, values, VideoDbColumns.mediaUuid+" = ?", new String[] {mediaTracker.getMediaId()});

        } catch (Exception e) {
            Log.i("VDB ", " mediaTrackerInsert ");
            e.printStackTrace();
        }
    }


    public JSONArray getMediaDetails() {

        Cursor cursor;
        JSONArray jsonMediaArray = new JSONArray();
        String watchMin="0";
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            String query = "SELECT * FROM " + VideoDbColumns.mediaTableName + " WHERE " + VideoDbColumns.mediaEndTime + "!='0'";
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                do {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("startTime", cursor.getString(cursor.getColumnIndex(VideoDbColumns.mediaStartTime)));
                    jsonObject.put("mediaId", cursor.getString(cursor.getColumnIndex(VideoDbColumns.mediaUuid)));
                    jsonObject.put("conStatus", cursor.getInt(cursor.getColumnIndex(VideoDbColumns.mediaCompleteStatus)));
                    jsonObject.put("watchMin", cursor.getString(cursor.getColumnIndex(VideoDbColumns.mediaWatchMinutes)));
                    jsonObject.put("endTime", cursor.getString(cursor.getColumnIndex(VideoDbColumns.mediaEndTime)));

                    jsonMediaArray.put(jsonObject);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.logD(TAG, "Fetch Tracker Data"+jsonMediaArray.toString());
        return jsonMediaArray;
    }


    public void delMediaTrackerRecords() {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            String query = "DELETE FROM " + VideoDbColumns.mediaTableName + " WHERE " + VideoDbColumns.mediaEndTime + "!='0'";
            database.execSQL(query);
        } catch (Exception e) {
            Log.i("VDB ", " mediaTrackerInsert ");
            e.printStackTrace();
        }

    }
}