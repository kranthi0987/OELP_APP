package oelp.mahiti.org.newoepl.database;

import android.arch.lifecycle.MutableLiveData;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import oelp.mahiti.org.newoepl.models.CatalogueDetailsModel;
import oelp.mahiti.org.newoepl.models.LocationContent;
import oelp.mahiti.org.newoepl.models.LocationModel;
import oelp.mahiti.org.newoepl.utils.Logger;

import static oelp.mahiti.org.newoepl.database.DBConstants.CAT_TABLE_NAME;

/**
 * Created by RAJ ARYAN on 02/08/19.
 */
public class DatabaseHandlerClass extends SQLiteOpenHelper {

    Context mContext;
    public static SQLiteDatabase database;
    private static final String TAG = DatabaseHandlerClass.class.getSimpleName();


    public DatabaseHandlerClass(Context mContext) {
        super(mContext, DBConstants.DB_NAME, null, DBConstants.VERSION);
        this.mContext = mContext;


        if (mContext == null)
            return;
        try {
            SQLiteDatabase.loadLibs(mContext);
            initDatabase();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onCreate method", e);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createLocationTable(sqLiteDatabase);
        createCatalogueTable(sqLiteDatabase);
    }

    private void createCatalogueTable(SQLiteDatabase sqLiteDatabase) {
        String query = DBConstants.CREATE_TABLE_IF_NOT_EXIST + CAT_TABLE_NAME + DBConstants.OPEN_BRACKET +
                DBConstants.UUID + DBConstants.TEXT_PRIMARY_KEY + DBConstants.COMMA +
                DBConstants.NAME + DBConstants.TEXT_COMMA +
                DBConstants.CODE + DBConstants.TEXT_COMMA +
                DBConstants.ACTIVE + DBConstants.INTEGER_COMMA +
                DBConstants.ORDER + DBConstants.INTEGER_COMMA +
                DBConstants.COLOR + DBConstants.TEXT_COMMA +
                DBConstants.PARENT + DBConstants.TEXT_COMMA +
                DBConstants.MODIFIED + DBConstants.DATETIME_COMMA +
                DBConstants.ICON_TYPE + DBConstants.TEXT_COMMA +
                DBConstants.ICON_PATH + DBConstants.TEXT_COMMA +
                DBConstants.MEDIA_TYPE + DBConstants.TEXT_COMMA +
                DBConstants.MEDIA_PATH + DBConstants.TEXT_COMMA +
                DBConstants.MEDIA_LEVEL_TYPE + DBConstants.TEXT_COMMA +
                DBConstants.DESC + DBConstants.TEXT_COMMA +
                DBConstants.TYPE_CONTENT + DBConstants.TEXT_COMMA +
                DBConstants.CONTENT_UUID + DBConstants.TEXT +
                DBConstants.CLOSE_BRACKET;
        Logger.logD(TAG, "Database creation query :" + query);
        sqLiteDatabase.execSQL(query);
    }

    private void createLocationTable(SQLiteDatabase sqLiteDatabase) {
        String query = DBConstants.CREATE_TABLE_IF_NOT_EXIST + DBConstants.LOC_TABLE_NAME + DBConstants.OPEN_BRACKET +
                DBConstants.ID + DBConstants.TEXT_PRIMARY_KEY + DBConstants.COMMA +
                DBConstants.ACTIVE + DBConstants.INTEGER_COMMA +
                DBConstants.CREATED + DBConstants.DATETIME_COMMA +
                DBConstants.MODIFIED + DBConstants.DATETIME_COMMA +
                DBConstants.NAME + DBConstants.DATETIME_COMMA +
                DBConstants.BOUNDARY_LEVEL_TYPE + DBConstants.INTEGER_COMMA +
                DBConstants.PARENT + DBConstants.TEXT +
                DBConstants.CLOSE_BRACKET;
        Logger.logD(TAG, "Database creation query :" + query);
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

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
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase(DBConstants.DATABASESECRETKEY);
        try {
            Logger.logD(TAG, "Getting Spinner Item : " + mainQuery);
            Cursor cursor = sqLiteDatabase.rawQuery(mainQuery, null);
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
        }
        return locationList;
    }

    public String getModifiedDate() {
        String selectQuery = DBConstants.SELECT + DBConstants.MAX + DBConstants.OPEN_BRACKET + DBConstants.MODIFIED + DBConstants.CLOSE_BRACKET + DBConstants.AS + DBConstants.MODIFIED + DBConstants.FROM + DBConstants.CAT_TABLE_NAME;
        Logger.logD(TAG, selectQuery);
        android.database.Cursor cursor = database.rawQuery(selectQuery, null);
        String date = null;
        if (cursor.moveToFirst()) {
            date = cursor.getString(cursor.getColumnIndex(DBConstants.MODIFIED));
            cursor.close();
        } else {
            cursor.close();
        }
        if (date == null)
            date = "";

        return date;
    }

    public Long insertDataToCatalogueTable(List<CatalogueDetailsModel> catalogueDetailsModel) {
        List<CatalogueDetailsModel> catalogueDetailsModelList = catalogueDetailsModel;
        long insertLong = 0;

        initDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (CatalogueDetailsModel detailsModel : catalogueDetailsModelList) {
                values.put(DBConstants.UUID, detailsModel.getUuid());
                values.put(DBConstants.ACTIVE, detailsModel.getActive());
                values.put(DBConstants.NAME, detailsModel.getName());
                values.put(DBConstants.CODE, detailsModel.getCode());
                values.put(DBConstants.ORDER, detailsModel.getOrder());
                values.put(DBConstants.COLOR, detailsModel.getUdf1().getColorCode());
                values.put(DBConstants.PARENT, detailsModel.getParent());
                values.put(DBConstants.MODIFIED, detailsModel.getModified());
                values.put(DBConstants.ICON_PATH, detailsModel.getIcon());
                values.put(DBConstants.ICON_TYPE, detailsModel.getIconType());
                values.put(DBConstants.MEDIA_TYPE, detailsModel.getContType());
                values.put(DBConstants.MEDIA_PATH, detailsModel.getPath());
                values.put(DBConstants.MEDIA_LEVEL_TYPE, detailsModel.getMediaLevelType());
                values.put(DBConstants.DESC, detailsModel.getDesc());
                values.put(DBConstants.TYPE_CONTENT, detailsModel.getTypeContent());
                values.put(DBConstants.CONTENT_UUID, detailsModel.getConUuid());
                Log.d(TAG, CAT_TABLE_NAME + values.toString());
                insertLong = database.insertWithOnConflict(CAT_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG, "Content values inserting into catalogue table " + CAT_TABLE_NAME + values.toString());
            }
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Logger.logD(TAG, ex.getMessage());
        } finally {
            database.endTransaction();
        }
        return insertLong;
    }

    private void initDatabase() {
        if (database == null || !database.isOpen() || database.isReadOnly())
            database = this.getWritableDatabase(DBConstants.DATABASESECRETKEY);
    }

    public MutableLiveData<List<CatalogueDetailsModel>> getCatalogData(String parentId) {
        MutableLiveData<List<CatalogueDetailsModel>> catalogList = new MutableLiveData<>();
        List<CatalogueDetailsModel> catalogList1 = new ArrayList<>();
        CatalogueDetailsModel catalogueDetailsModel;
        String mainQuery = "";
        String query1 = DBConstants.SELECT + DBConstants.ALL_FROM + DBConstants.CAT_TABLE_NAME + DBConstants.WHERE + DBConstants.PARENT + DBConstants.EQUAL_TO;
        String query2;
        if (parentId.isEmpty())
            query2 = DBConstants.EMPTY;
        else
            query2 = "'"+parentId+"'";
        mainQuery = query1 + query2;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase(DBConstants.DATABASESECRETKEY);
        try {
            Logger.logD(TAG, "Getting Spinner Item : " + mainQuery);
            Cursor cursor = sqLiteDatabase.rawQuery(mainQuery, null);
            if (cursor.moveToFirst()) {
                do {

                    catalogueDetailsModel = new CatalogueDetailsModel();
                    catalogueDetailsModel.setUuid(cursor.getString(cursor.getColumnIndex(DBConstants.UUID)));
                    catalogueDetailsModel.setActive(cursor.getInt(cursor.getColumnIndex(DBConstants.ACTIVE)));
                    catalogueDetailsModel.setName(cursor.getString(cursor.getColumnIndex(DBConstants.NAME)));
                    catalogueDetailsModel.setCode(cursor.getString(cursor.getColumnIndex(DBConstants.CODE)));
                    catalogueDetailsModel.setOrder(cursor.getInt(cursor.getColumnIndex(DBConstants.ORDER)));

                    CatalogueDetailsModel.UDF1 udf1 = new CatalogueDetailsModel().new UDF1();
                    catalogueDetailsModel.setUdf1(udf1, cursor.getString(cursor.getColumnIndex(DBConstants.COLOR)));

                    catalogueDetailsModel.setParent(cursor.getString(cursor.getColumnIndex(DBConstants.PARENT)));
                    catalogueDetailsModel.setModified(cursor.getString(cursor.getColumnIndex(DBConstants.MODIFIED)));
                    catalogueDetailsModel.setIcon(cursor.getString(cursor.getColumnIndex(DBConstants.ICON_PATH)));
                    catalogueDetailsModel.setIconType(cursor.getString(cursor.getColumnIndex(DBConstants.ICON_TYPE)));
                    catalogueDetailsModel.setContType(cursor.getString(cursor.getColumnIndex(DBConstants.MEDIA_TYPE)));
                    catalogueDetailsModel.setPath(cursor.getString(cursor.getColumnIndex(DBConstants.MEDIA_PATH)));
                    catalogueDetailsModel.setMediaLevelType(cursor.getString(cursor.getColumnIndex(DBConstants.MEDIA_LEVEL_TYPE)));
                    catalogueDetailsModel.setDesc(cursor.getString(cursor.getColumnIndex(DBConstants.DESC)));
                    catalogueDetailsModel.setTypeContent(cursor.getString(cursor.getColumnIndex(DBConstants.TYPE_CONTENT)));
                    catalogueDetailsModel.setConUuid(cursor.getString(cursor.getColumnIndex(DBConstants.CONTENT_UUID)));

                    catalogList1.add(catalogueDetailsModel);

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "getSubject", e);
        }
        catalogList.setValue(catalogList1);
        return catalogList;
    }
}
