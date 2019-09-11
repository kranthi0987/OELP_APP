package mahiti.org.oelp.database.DAOs;

import android.content.ContentValues;
import android.content.Context;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.database.DBConstants;
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.models.CatalogueDetailsModel;
import mahiti.org.oelp.utils.Logger;

/**
 * Created by RAJ ARYAN on 09/09/19.
 */
public class ModuleWatchLockDao extends DatabaseHandlerClass {

    private static final String TAG = ModuleWatchLockDao.class.getSimpleName();

    public ModuleWatchLockDao(Context mContext) {
        super(mContext);
    }

    private void initDatabase() {
        if (database == null || !database.isOpen() || database.isReadOnly())
            database = this.getWritableDatabase(DBConstants.DATABASESECRETKEY);
    }

//    public long insertWatchStatus(CatalogueDetailsModel model) {
    public long insertWatchStatus(String mediaUUID, String sectionUUID) {
        initDatabase();
        long insertLong = 0;
        database.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBConstants.USER_UID, mediaUUID);
            contentValues.put(DBConstants.PARENT, sectionUUID);
            contentValues.put(DBConstants.SCORE, 0);
            contentValues.put(DBConstants.WATCH_STATUS, 1);
            contentValues.put(DBConstants.ATTEMPT, getAttemptFromDb(mediaUUID));
            insertLong = database.insertWithOnConflict(DBConstants.ModuleWatchLockTable, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            Logger.logD(TAG, DBConstants.ModuleWatchLockTable + " Inserting Values :" + contentValues.toString());
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Logger.logE(TAG, DBConstants.ModuleWatchLockTable + " Insertion Exception :" + ex.getMessage(), ex);
        } finally {
            database.endTransaction();
        }
        return insertLong;
    }

    public int getAttemptFromDb(String mediaUUID) {
        String query = DBConstants.SELECT+DBConstants.ATTEMPT+DBConstants.FROM+DBConstants.ModuleWatchLockTable+
                DBConstants.WHERE+DBConstants.UUID+DBConstants.SINGLE_QUOTES+mediaUUID+DBConstants.SINGLE_QUOTES;
        Logger.logD(TAG, DBConstants.ModuleWatchLockTable + " Fetch Attempt Query :" + query);
        Cursor cursor;
        int attempt=0;
        try{
            cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            attempt = cursor.getInt(cursor.getColumnIndex(DBConstants.ATTEMPT));
        } catch (Exception ex) {
            Logger.logE(TAG, DBConstants.ModuleWatchLockTable + " Fetch Attempt Exception :" + ex.getMessage(), ex);
        }
        return attempt+1;
    }

    public List<CatalogueDetailsModel> getWatchStatus(String parentUUID) {
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
    }
}
