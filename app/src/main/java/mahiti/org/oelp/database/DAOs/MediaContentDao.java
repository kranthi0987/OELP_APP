package mahiti.org.oelp.database.DAOs;

import android.content.ContentValues;
import android.content.Context;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.database.DBConstants;
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.fileandvideodownloader.FileModel;
import mahiti.org.oelp.models.SharedMediaModel;

import mahiti.org.oelp.utils.Logger;

/**
 * Created by RAJ ARYAN on 09/09/19.
 */
public class MediaContentDao extends DatabaseHandlerClass {

    private static final String TAG = MediaContentDao.class.getSimpleName();

    public MediaContentDao(Context mContext) {
        super(mContext);
    }

    public long insertSharedData(List<SharedMediaModel> modelList, int syncStatus) {
        if (modelList==null)
            return 0;
        long insertlong = 0;
        initDatabase();
        database.beginTransaction();
        try {

            ContentValues contentValues = new ContentValues();
            for (SharedMediaModel model : modelList) {
                int globalShared = 0;
                if (model.getGlobalAccess())
                    globalShared=1;
                contentValues.put(DBConstants.UUID, model.getMediaUuid());
                contentValues.put(DBConstants.USER_UUID, model.getUserUuid());
                contentValues.put(DBConstants.GROUP_UUID, model.getGroupUuid());
                contentValues.put(DBConstants.MEDIA_NAME, model.getMediaTitle());
                contentValues.put(DBConstants.USER_NAME, model.getUserName());
                contentValues.put(DBConstants.MEDIA_TYPE, model.getMediaType());
                contentValues.put(DBConstants.MEDIA_PATH, model.getMediaFile());
                contentValues.put(DBConstants.SUBMISSION_DATE, model.getSubmissionTime());
                contentValues.put(DBConstants.HASH_KEY, model.getHashKey());
                contentValues.put(DBConstants.MODIFIED_DATE, model.getModifiedOn());
                contentValues.put(DBConstants.ACTIVE, model.getActive());
                contentValues.put(DBConstants.SYNC_STATUS, syncStatus);
                contentValues.put(DBConstants.IS_GLOBAL_SHARED, globalShared);
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

    /**
     * @param forGroup true for groupData false for user specific data
     * @param type     0 for both sync and unsync data and 1 for unsync data
     * @param userUUID useruuid
     * @param groupUUID groupuuid
     */
    public List<SharedMediaModel> fetchSharedMedia(String userUUID, String groupUUID, boolean forGroup, int type) {
        List<SharedMediaModel> sharedMediaList = new ArrayList<>();
        String query;
        if (type == 0) {
            if (forGroup) {
                query = DBConstants.SELECT + DBConstants.ALL_FROM + DBConstants.MEDIA_CONTENT_TABLE +
                        DBConstants.WHERE + DBConstants.GROUP_UUID + DBConstants.EQUAL_TO + DBConstants.SINGLE_QUOTES + groupUUID + DBConstants.SINGLE_QUOTES + DBConstants.AND + DBConstants.DELETE_MEDIA + DBConstants.NOT_EQUAL_TO + 1 + DBConstants.ORDER_BY + DBConstants.SUBMISSION_DATE + DBConstants.DESCENDING;
            } else {
                query = DBConstants.SELECT + DBConstants.ALL_FROM + DBConstants.MEDIA_CONTENT_TABLE +
                        DBConstants.WHERE + DBConstants.USER_UUID + DBConstants.EQUAL_TO + DBConstants.SINGLE_QUOTES + userUUID + DBConstants.SINGLE_QUOTES +DBConstants.AND+DBConstants.GROUP_UUID + DBConstants.EQUAL_TO + DBConstants.SINGLE_QUOTES + groupUUID + DBConstants.SINGLE_QUOTES + DBConstants.AND + DBConstants.DELETE_MEDIA + DBConstants.NOT_EQUAL_TO + 1 + DBConstants.ORDER_BY + DBConstants.SUBMISSION_DATE + DBConstants.DESCENDING;
            }
        } else {
            query = DBConstants.SELECT + DBConstants.ALL_FROM + DBConstants.MEDIA_CONTENT_TABLE +
                    DBConstants.WHERE + DBConstants.SYNC_STATUS + DBConstants.EQUAL_TO + 1;

        }

        Logger.logD(TAG, DBConstants.MEDIA_CONTENT_TABLE + " Fetch Attempt Query :" + query);
        Cursor cursor = null;
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
                    model.setSubmissionTime(cursor.getString(cursor.getColumnIndex(DBConstants.SUBMISSION_DATE)));

                    model.setModifiedOn(cursor.getString(cursor.getColumnIndex(DBConstants.MODIFIED_DATE)));
                    model.setHashKey(cursor.getString(cursor.getColumnIndex(DBConstants.HASH_KEY)));
                    model.setActive(cursor.getInt(cursor.getColumnIndex(DBConstants.ACTIVE)));


                    model.setSharedGloballySyncStatus(cursor.getInt(cursor.getColumnIndex(DBConstants.SHARED_GLOBALLY_SYNC_STATUS)));
                    boolean isGlobalShare = cursor.getInt(cursor.getColumnIndex(DBConstants.SHARED_GLOBALLY_SYNC_STATUS)) == 1;
                    model.setGlobalAccess(isGlobalShare);
                    sharedMediaList.add(model);

                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            Logger.logE(TAG, DBConstants.MEDIA_CONTENT_TABLE + " Fetch Attempt Exception :" + ex.getMessage(), ex);
        } finally {
            closeCursor(cursor);
            closeDatabase();
        }

        return sharedMediaList;
    }

    public long updateGloballyShareMediaUUID(String mediaUUID) {
        long updateLong = 0;
        initDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.UUID, mediaUUID); //These Fields should be your String values of actual column names
        cv.put(DBConstants.SHARED_GLOBALLY_SYNC_STATUS, 1); //These Fields should be your String values of actual column names
        cv.put(DBConstants.IS_GLOBAL_SHARED, 1); //These Fields should be your String values of actual column names
        updateLong = database.updateWithOnConflict(DBConstants.MEDIA_CONTENT_TABLE, cv, "uuid = ?", new String[]{mediaUUID}, SQLiteDatabase.CONFLICT_REPLACE);
        return updateLong;
    }

    /**
     *
     * @param mediaUUID media uuid to update
     * @param columnName column name which has to update
     */
    public void updateSyncData(String mediaUUID, String columnName) {
        if (mediaUUID!=null && !mediaUUID.isEmpty()) {
            long updateLong = 0;
            initDatabase();
            ContentValues cv = new ContentValues();
            cv.put(DBConstants.UUID, mediaUUID);
            cv.put(columnName, 0);
            database.updateWithOnConflict(DBConstants.MEDIA_CONTENT_TABLE, cv, "uuid = ?", new String[]{mediaUUID}, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public long deleteMediaUUID(String mediaUUID) {
        long updateLong = 0;
        initDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.UUID, mediaUUID); //These Fields should be your String values of actual column names
        cv.put(DBConstants.DELETE_MEDIA, 1); //These Fields should be your String values of actual column names
        updateLong = database.update(DBConstants.MEDIA_CONTENT_TABLE, cv, "uuid = ?", new String[]{mediaUUID});
        return updateLong;
    }

    public List<FileModel> getImageListFromMediaTable() {
        FileModel fileModel;
        List<FileModel> imageList = new ArrayList<>();
        String query = DBConstants.SELECT + DBConstants.MEDIA_PATH + DBConstants.COMMA + DBConstants.MEDIA_NAME +
                DBConstants.COMMA + DBConstants.UUID + DBConstants.FROM + DBConstants.MEDIA_CONTENT_TABLE +
                DBConstants.WHERE + DBConstants.MEDIA_PATH + DBConstants.NOT_EQUAL_TO + DBConstants.EMPTY +
                DBConstants.AND + DBConstants.MEDIA_PATH + DBConstants.IS_NOT_NULL + DBConstants.AND + DBConstants.MEDIA_TYPE + DBConstants.EQUAL_TO + 1;
        initDatabase();
        Cursor cursor = null;
        try {
            Logger.logD(TAG, "Getting Image List Query : " + query);
            cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    fileModel = new FileModel();
                    fileModel.setFileName(cursor.getString(cursor.getColumnIndex(DBConstants.MEDIA_NAME)));
                    fileModel.setUuid(cursor.getString(cursor.getColumnIndex(DBConstants.UUID)));
                    fileModel.setFileUrl(cursor.getString(cursor.getColumnIndex(DBConstants.MEDIA_PATH)));
                    imageList.add(fileModel);

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "get ICON", e);
        } finally {
            closeCursor(cursor);
            closeDatabase();
        }

        return imageList;
    }


    public String fetchDeletedMedia() {
        List<SharedMediaModel> sharedMediaList = new ArrayList<>();
        JSONArray array = new JSONArray();
        JSONObject jsonObject;
        String query = DBConstants.SELECT + DBConstants.UUID + DBConstants.FROM + DBConstants.MEDIA_CONTENT_TABLE +
                DBConstants.WHERE + DBConstants.DELETE_MEDIA + DBConstants.EQUAL_TO + 1;

        Logger.logD(TAG, DBConstants.MEDIA_CONTENT_TABLE + " Fetch Attempt Query :" + query);
        Cursor cursor=null;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {

                do {
                    jsonObject = new JSONObject();
                    jsonObject.put("media_uuid", cursor.getString(cursor.getColumnIndex(DBConstants.UUID)));
                    array.put(jsonObject);
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            Logger.logE(TAG, DBConstants.MEDIA_CONTENT_TABLE + " Fetch Attempt Exception :" + ex.getMessage(), ex);
        }finally {
            closeCursor(cursor);
            closeDatabase();
        }
        return array.toString();
    }

    public String fetchGlobalSharedMedia() {
        JSONArray array = new JSONArray();
        String data = "";
        JSONObject jsonObject;
        String query = DBConstants.SELECT + DBConstants.UUID + DBConstants.FROM + DBConstants.MEDIA_CONTENT_TABLE +
                DBConstants.WHERE + DBConstants.IS_GLOBAL_SHARED + DBConstants.EQUAL_TO + 1+DBConstants.AND+DBConstants.SHARED_GLOBALLY_SYNC_STATUS+DBConstants.EQUAL_TO+1;

        Logger.logD(TAG, DBConstants.MEDIA_CONTENT_TABLE + " Fetch Attempt Query :" + query);
        Cursor cursor=null;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {

                do {
                    jsonObject = new JSONObject();
                    jsonObject.put("media_uuid", cursor.getString(cursor.getColumnIndex(DBConstants.UUID)));
                    array.put(jsonObject);
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            Logger.logE(TAG, DBConstants.MEDIA_CONTENT_TABLE + " Fetch Attempt Exception :" + ex.getMessage(), ex);
        }finally {
            closeCursor(cursor);
            closeDatabase();
        }
        if (array.length()>0)
            data = array.toString();
        return data;
    }

    public long removeDeleteMedia(String mediaUUID) {
        long updateLong = 0;
        initDatabase();
        String selection = DBConstants.DELETE_MEDIA + " = ?";
        String[] selectionArgs = {"1"};
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.UUID, mediaUUID);
        cv.put(DBConstants.DELETE_MEDIA, 1);
        updateLong = database.delete(DBConstants.MEDIA_STATUS_TABLE, selection, selectionArgs);
        return updateLong;
    }


}
