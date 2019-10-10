package mahiti.org.oelp.database.DAOs;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import mahiti.org.oelp.database.DBConstants;
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.models.LocationContent;
import mahiti.org.oelp.models.LocationModel;
import mahiti.org.oelp.utils.Logger;

/**
 * Created by RAJ ARYAN on 2019-10-03.
 */
public class LocationDao extends DatabaseHandlerClass {

    private static final String TAG = LocationDao.class.getSimpleName();

    public LocationDao(Context mContext) {
        super(mContext);
    }

    public long insertLocationDataToDB(LocationModel model) {
        List<LocationContent> locationContentList = model.getLocationContent();
        long insertLong = 0;

        initDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (LocationContent locationContentData : locationContentList) {
                values.put(DBConstants.ID, locationContentData.getId());
                values.put(DBConstants.ACTIVE, locationContentData.getActive());
                values.put(DBConstants.CREATED, locationContentData.getCreated());
                values.put(DBConstants.MODIFIED, locationContentData.getModified());
                values.put(DBConstants.CT_NAME, locationContentData.getName());
                values.put(DBConstants.BOUNDARY_LEVEL_TYPE, locationContentData.getBoundaryLevelType());
                values.put(DBConstants.PARENT, locationContentData.getParent());
                Log.d(TAG, DBConstants.LOC_TABLE_NAME + values.toString());
                insertLong = database.insertWithOnConflict(DBConstants.LOC_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG, "Content values inserting into catalogue table " + values.toString());
            }
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Logger.logD(TAG, ex.getMessage());
        } finally {
            database.endTransaction();
        }
        return insertLong;
    }

    public MutableLiveData<List<LocationContent>> getSpinnerList(int parentId, int level) {
        List<LocationContent> locationContentList = new ArrayList<>();
        MutableLiveData<List<LocationContent>> locationList = new MutableLiveData<>();
        LocationContent locationContent;
        String mainQuery = "";
        String query1 = DBConstants.SELECT + DBConstants.ALL_FROM + DBConstants.LOC_TABLE_NAME + DBConstants.WHERE + DBConstants.BOUNDARY_LEVEL_TYPE + DBConstants.EQUAL_TO + level + DBConstants.AND + DBConstants.PARENT + DBConstants.EQUAL_TO + parentId;
        mainQuery = query1;
        initDatabase();
        Cursor cursor = null;
        try {
            Logger.logD(TAG, "Getting Spinner Item : " + mainQuery);
            cursor = database.rawQuery(mainQuery, null);
            if (cursor.moveToFirst()) {
                do {

                    locationContent = new LocationContent(cursor.getInt(cursor.getColumnIndex(DBConstants.PARENT)),
                            cursor.getInt(cursor.getColumnIndex(DBConstants.BOUNDARY_LEVEL_TYPE)),
                            cursor.getString(cursor.getColumnIndex(DBConstants.CREATED)),
                            cursor.getString(cursor.getColumnIndex(DBConstants.CT_NAME)),
                            cursor.getInt(cursor.getColumnIndex(DBConstants.ACTIVE)),
                            cursor.getString(cursor.getColumnIndex(DBConstants.MODIFIED)),
                            cursor.getInt(cursor.getColumnIndex(DBConstants.ID)));
                    locationContentList.add(locationContent);
                    locationList.setValue(locationContentList);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "getSubject", e);
        }finally {
            closeCursor(cursor);
        }
        return locationList;
    }

    public String getName(Integer blockIds) {
        String name ="";
        String id = String.valueOf(blockIds);
        String query = DBConstants.SELECT+DBConstants.NAME+DBConstants.FROM+DBConstants.LOC_TABLE_NAME+
                DBConstants.WHERE+DBConstants.ID+DBConstants.EQUAL_TO+DBConstants.SINGLE_QUOTES+id+DBConstants.SINGLE_QUOTES;
        initDatabase();
        Cursor cursor = null;
        Logger.logD(TAG, "Getting  : " + query);
        cursor = database.rawQuery(query, null);
        try {
           if (cursor.moveToFirst()){
               name = cursor.getString(cursor.getColumnIndex(DBConstants.NAME));
           }
        }catch (Exception e) {
            Logger.logE(TAG, "getSubject", e);
        }finally {
            closeCursor(cursor);
        }
        return name;
    }
}
