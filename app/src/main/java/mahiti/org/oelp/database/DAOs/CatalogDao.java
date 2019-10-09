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
import mahiti.org.oelp.fileandvideodownloader.FileModel;
import mahiti.org.oelp.models.CatalogueDetailsModel;
import mahiti.org.oelp.utils.Logger;

import static mahiti.org.oelp.database.DBConstants.CAT_TABLE_NAME;
import static mahiti.org.oelp.database.DBConstants.ICON_PATH;

/**
 * Created by RAJ ARYAN on 2019-10-03.
 */
public class CatalogDao extends DatabaseHandlerClass {
    private static final String TAG = CatalogDao.class.getSimpleName();

    public CatalogDao(Context mContext) {
        super(mContext);
    }

    public Long insertDataToCatalogueTable(List<CatalogueDetailsModel> catalogueDetailsModel) {
        deleteAllDataFromDB(1);
        long insertLong = 0;

        initDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (CatalogueDetailsModel detailsModel : catalogueDetailsModel) {
                String uuid = detailsModel.getUuid();
                String parent_uuis = "";
                values.put(DBConstants.UUID, uuid);
                values.put(DBConstants.ACTIVE, detailsModel.getActive());
                values.put(DBConstants.NAME, detailsModel.getName());
                values.put(DBConstants.CODE, detailsModel.getCode());
                values.put(DBConstants.ORDER, detailsModel.getOrder());
                values.put(DBConstants.COLOR, detailsModel.getUdf1().getColorCode());
                if (detailsModel.getParent() != null)
                    parent_uuis = detailsModel.getParent();
                values.put(DBConstants.PARENT, parent_uuis);
                values.put(DBConstants.MODIFIED, detailsModel.getModified());
                values.put(DBConstants.ICON_PATH, detailsModel.getIcon());
                values.put(DBConstants.ICON_TYPE, detailsModel.getIconType());
                values.put(DBConstants.MEDIA_TYPE, detailsModel.getContType());
                values.put(DBConstants.MEDIA_PATH, detailsModel.getPath());
                values.put(DBConstants.MEDIA_LEVEL_TYPE, detailsModel.getMediaLevelType());
                values.put(DBConstants.DESC, detailsModel.getDesc());
                values.put(DBConstants.TYPE_CONTENT, detailsModel.getTypeContent());
                values.put(DBConstants.DCF, detailsModel.getDcfid());
                values.put(DBConstants.CONTENT_UUID, detailsModel.getConUuid());
                Log.d(TAG, CAT_TABLE_NAME + values.toString());
                insertWatchStatus(parent_uuis, uuid);
                insertLong = database.insertWithOnConflict(CAT_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                Log.d(TAG, "Content values inserting into catalogue table " + CAT_TABLE_NAME + values.toString());
            }
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Logger.logE(TAG, ex.getMessage(), ex);
        } finally {
            database.endTransaction();
        }
        return insertLong;
    }

    public List<CatalogueDetailsModel> getCatalogData(String parentId) {
        List<CatalogueDetailsModel> catalogList1 = new ArrayList<>();
        CatalogueDetailsModel catalogueDetailsModel;
        String mainQuery;
        String query1 = DBConstants.SELECT + DBConstants.ALL_FROM + DBConstants.CAT_TABLE_NAME + DBConstants.WHERE + DBConstants.ACTIVE + DBConstants.EQUAL_TO + 2 + DBConstants.AND + DBConstants.PARENT + DBConstants.EQUAL_TO;
        String query2;
        if (parentId.isEmpty())
            query2 = DBConstants.EMPTY + DBConstants.ORDER_BY + DBConstants.ORDER;
        else
            query2 = "'" + parentId + "'" + DBConstants.ORDER_BY + DBConstants.ORDER;
        mainQuery = query1 + query2;
        initDatabase();
        try {
            Logger.logD(TAG, "Getting Spinner Item : " + mainQuery);
            Cursor cursor = database.rawQuery(mainQuery, null);
            if (cursor.moveToFirst()) {
                do {

                    catalogueDetailsModel = new CatalogueDetailsModel();
                    catalogueDetailsModel.setUuid(cursor.getString(cursor.getColumnIndex(DBConstants.UUID)));
                    catalogueDetailsModel.setActive(cursor.getInt(cursor.getColumnIndex(DBConstants.ACTIVE)));
                    catalogueDetailsModel.setName(cursor.getString(cursor.getColumnIndex(DBConstants.NAME)));
                    catalogueDetailsModel.setCode(cursor.getString(cursor.getColumnIndex(DBConstants.CODE)));
                    catalogueDetailsModel.setOrder(cursor.getInt(cursor.getColumnIndex(DBConstants.ORDER)));
                    catalogueDetailsModel.setWatchStatus(getWatchStatus(catalogueDetailsModel.getUuid()));

                    CatalogueDetailsModel.UDF1 udf1 = new CatalogueDetailsModel().new UDF1();
                    catalogueDetailsModel.setUdf1(udf1, cursor.getString(cursor.getColumnIndex(DBConstants.COLOR)));

                    catalogueDetailsModel.setParent(cursor.getString(cursor.getColumnIndex(DBConstants.PARENT)));
                    catalogueDetailsModel.setDcfid(cursor.getInt(cursor.getColumnIndex(DBConstants.DCF)));
                    catalogueDetailsModel.setModified(cursor.getString(cursor.getColumnIndex(DBConstants.MODIFIED)));
                    catalogueDetailsModel.setIcon(cursor.getString(cursor.getColumnIndex(DBConstants.ICON_PATH)));
                    catalogueDetailsModel.setIconType(cursor.getString(cursor.getColumnIndex(DBConstants.ICON_TYPE)));
                    catalogueDetailsModel.setContType(cursor.getString(cursor.getColumnIndex(DBConstants.MEDIA_TYPE)));
                    catalogueDetailsModel.setPath(cursor.getString(cursor.getColumnIndex(DBConstants.MEDIA_PATH)));
                    catalogueDetailsModel.setMediaLevelType(cursor.getInt(cursor.getColumnIndex(DBConstants.MEDIA_LEVEL_TYPE)));
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
        return catalogList1;
    }

    public List<FileModel> getImageListFromTable() {
        FileModel fileModel;
        List<FileModel> imageList = new ArrayList<>();
        String query = DBConstants.SELECT + DBConstants.ICON_PATH + DBConstants.COMMA + DBConstants.NAME + DBConstants.COMMA + DBConstants.DCF + DBConstants.COMMA + DBConstants.UUID + DBConstants.FROM + DBConstants.CAT_TABLE_NAME +
                DBConstants.WHERE + ICON_PATH + DBConstants.NOT_EQUAL_TO + DBConstants.EMPTY +
                DBConstants.AND + DBConstants.ICON_PATH + DBConstants.IS_NOT_NULL;
        initDatabase();
        try {
            Logger.logD(TAG, "Getting Image List Query : " + query);
            Cursor cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    fileModel = new FileModel();
                    fileModel.setFileName(cursor.getString(cursor.getColumnIndex(DBConstants.NAME)));
                    fileModel.setUuid(cursor.getString(cursor.getColumnIndex(DBConstants.UUID)));
                    fileModel.setFileUrl(cursor.getString(cursor.getColumnIndex(DBConstants.ICON_PATH)));
                    fileModel.setDcfId(cursor.getInt(cursor.getColumnIndex(DBConstants.DCF)));
                    imageList.add(fileModel);

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "get ICON", e);
        }
        return imageList;
    }

    public boolean addFileSize(String fileUuid, long filesize) {

        initDatabase();
        ContentValues values = new ContentValues();
        values.put(DBConstants.FILE_SIZE, filesize);
        Log.d("CatalogDBHandler", "updating the view status to database" + fileUuid);
        database.update(DBConstants.CAT_TABLE_NAME, values, DBConstants.UUID + " = ?", new String[]{fileUuid});

        return true;
    }

    public String getFileSize(String fileuuid) {
        initDatabase();
        String fileSize = null;
        String query = "Select filesize from CatalogTable where uuid='" + fileuuid + "'";
        Logger.logD(TAG, "Getting filesize Item : " + query);
        try {
            database.beginTransaction();
            Cursor cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                fileSize = cursor.getString(cursor.getColumnIndex("filesize"));
            }
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Logger.logE(TAG, ex.getMessage(), ex);
        } finally {
            database.endTransaction();
        }
        return fileSize;
    }
}
