package mahiti.org.oelp.database.DAOs;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.database.DBConstants;
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.models.CatalogueDetailsModel;
import mahiti.org.oelp.models.QuestionModel;
import mahiti.org.oelp.models.SharedMediaModel;
import mahiti.org.oelp.utils.Logger;

import static mahiti.org.oelp.database.DBConstants.QUESTION_TABLE;

/**
 * Created by RAJ ARYAN on 09/09/19.
 */
public class MediaContentDao extends DatabaseHandlerClass {

    private static final String TAG = MediaContentDao.class.getSimpleName();

    public MediaContentDao(Context mContext) {
        super(mContext);
    }

    private void initDatabase() {
        if (database == null || !database.isOpen() || database.isReadOnly())
            database = this.getWritableDatabase(DBConstants.DATABASESECRETKEY);
    }

    public long insertSharedMedia(List<SharedMediaModel> modelList) {
        long insertlong =0;
        initDatabase();
        database.beginTransaction();
        try {

            ContentValues contentValues = new ContentValues();
            for (SharedMediaModel model : modelList) {
                contentValues.put(DBConstants.UUID, model.getMediaUuid());
                contentValues.put(DBConstants.USER_UUID, model.getUserUuid());
                contentValues.put(DBConstants.GROUP_UUID, model.getGroupUuid());
                contentValues.put(DBConstants.MEDIA_NAME, model.getMediaTitle());
                contentValues.put(DBConstants.USER_NAME, model.getUserName());
                contentValues.put(DBConstants.MEDIA_TYPE, model.getMediaType());
                contentValues.put(DBConstants.MEDIA_PATH, model.getMediaFile());
                contentValues.put(DBConstants.MODIFIED, model.getSubmissionTime());
                insertlong = database.insertWithOnConflict(DBConstants.MEDIA_CONTENT_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                Logger.logD(TAG, DBConstants.MEDIA_CONTENT_TABLE + " Inserting Values :" + contentValues.toString());
            }
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Logger.logE(TAG, DBConstants.MEDIA_CONTENT_TABLE + " Insertion Exception :" + ex.getMessage(), ex);
        } finally {
            database.endTransaction();
        }

        return insertlong;
    }


    public List<SharedMediaModel> getSharedMedia(String groupUUI, boolean forGroup, String userUUId) {
        List<SharedMediaModel> sharedMediaList = new ArrayList<>();
        String query="";
        if (forGroup) {
            query = DBConstants.SELECT + DBConstants.ALL_FROM + DBConstants.MEDIA_CONTENT_TABLE +
                    DBConstants.WHERE + DBConstants.GROUP_UUID + DBConstants.EQUAL_TO + DBConstants.SINGLE_QUOTES + groupUUI + DBConstants.SINGLE_QUOTES;
        }else {
            query = DBConstants.SELECT + DBConstants.ALL_FROM + DBConstants.MEDIA_CONTENT_TABLE +
                    DBConstants.WHERE + DBConstants.USER_UUID + DBConstants.EQUAL_TO + DBConstants.SINGLE_QUOTES + userUUId + DBConstants.SINGLE_QUOTES;
        }
        Logger.logD(TAG, DBConstants.MEDIA_CONTENT_TABLE + " Fetch Attempt Query :" + query);
        Cursor cursor;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {

                do {
                    SharedMediaModel model = new SharedMediaModel();
                    model.setMediaUuid(cursor.getString(cursor.getColumnIndex(DBConstants.UUID)));
                    model.setUserUuid(cursor.getString(cursor.getColumnIndex(DBConstants.USER_UUID)));
                    model.setGroupUuid(cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_UUID)));
                    model.setMediaTitle(cursor.getString(cursor.getColumnIndex(DBConstants.MEDIA_NAME)));
                    model.setMediaFile(cursor.getString(cursor.getColumnIndex(DBConstants.MEDIA_PATH)));
                    model.setMediaType(cursor.getString(cursor.getColumnIndex(DBConstants.MEDIA_TYPE)));
                    model.setUserName(cursor.getString(cursor.getColumnIndex(DBConstants.USER_NAME)));
                    model.setSubmissionTime(cursor.getString(cursor.getColumnIndex(DBConstants.MODIFIED)));
                    model.setSharedGlobally(cursor.getInt(cursor.getColumnIndex(DBConstants.SHARED_GLOBALLY)));

                    sharedMediaList.add(model);

                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            Logger.logE(TAG, DBConstants.MEDIA_CONTENT_TABLE + " Fetch Attempt Exception :" + ex.getMessage(), ex);
        }
        return sharedMediaList;
    }

    /*public List<CatalogueDetailsModel> getWatchStatus(String parentUUID) {
        CatalogueDetailsModel model;
        List<CatalogueDetailsModel> list = new ArrayList<>();
        String query = DBConstants.SELECT + DBConstants.ALL_FROM + DBConstants.ModuleWatchLockTable +
                DBConstants.WHERE + DBConstants.PARENT + DBConstants.EQUAL_TO + DBConstants.SINGLE_QUOTES +
                parentUUID + DBConstants.SINGLE_QUOTES+DBConstants.AND +DBConstants.WATCH_STATUS+
                DBConstants.NOT_EQUAL_TO+DBConstants.ZERO;
        Logger.logD(TAG, DBConstants.ModuleWatchLockTable + " Fetch WatchStatusAndLock Query :" + query);
        initDatabase();
        Cursor cursor;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    model = new CatalogueDetailsModel();
                    model.setUuid(cursor.getString(cursor.getColumnIndex(DBConstants.UUID)));
                    model.setParent(cursor.getString(cursor.getColumnIndex(DBConstants.PARENT)));
                    model.setWatchStatus(cursor.getInt(cursor.getColumnIndex(DBConstants.WATCH_STATUS)));
                    model.setOrder(cursor.getInt(cursor.getColumnIndex(DBConstants.ORDER)));
                    model.setLock(cursor.getInt(cursor.getColumnIndex(DBConstants.SCORE)));
                    list.add(model);
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            Logger.logE(TAG, DBConstants.ModuleWatchLockTable + " Fetch WatchStatus Exception :" + ex.getMessage(), ex);
        }
        return list;
    }*/

    public long updateGloabllyShareMediaUUID(List<SharedMediaModel> sharedMediaGlobally) {
        long updateLong =0;
        initDatabase();
        for (SharedMediaModel model :sharedMediaGlobally ){
            ContentValues cv = new ContentValues();
            cv.put(DBConstants.UUID, model.getMediaUuid()); //These Fields should be your String values of actual column names
            updateLong = database.update(DBConstants.MEDIA_STATUS_TABLE, cv, "uuid = ?", new String[]{model.getMediaUuid()});
        }
            return updateLong;
    }
}
