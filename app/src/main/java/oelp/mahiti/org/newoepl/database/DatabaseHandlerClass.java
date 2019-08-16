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

import oelp.mahiti.org.newoepl.fileandvideodownloader.FileModel;
import oelp.mahiti.org.newoepl.models.CatalogueDetailsModel;
import oelp.mahiti.org.newoepl.models.LocationContent;
import oelp.mahiti.org.newoepl.models.LocationModel;
import oelp.mahiti.org.newoepl.models.QuestionChoicesModel;
import oelp.mahiti.org.newoepl.models.QuestionModel;
import oelp.mahiti.org.newoepl.utils.Logger;

import static oelp.mahiti.org.newoepl.database.DBConstants.CAT_TABLE_NAME;
import static oelp.mahiti.org.newoepl.database.DBConstants.ICON_PATH;
import static oelp.mahiti.org.newoepl.database.DBConstants.QUESTION_CHOICES_TABLE;
import static oelp.mahiti.org.newoepl.database.DBConstants.QUESTION_TABLE;

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
        createQuestionTable(sqLiteDatabase);
        createQuestionChoicesTable(sqLiteDatabase);

    }


    private void createQuestionChoicesTable(SQLiteDatabase sqLiteDatabase) {

        String query = DBConstants.CREATE_TABLE_IF_NOT_EXIST + DBConstants.QUESTION_CHOICES_TABLE + DBConstants.OPEN_BRACKET +
                DBConstants.ID + DBConstants.TEXT_PRIMARY_KEY + DBConstants.COMMA +
                DBConstants.IS_CORRECT + DBConstants.TEXT_COMMA +
                DBConstants.CHOICE_TEXT + DBConstants.TEXT_COMMA +
                DBConstants.Q_ID + DBConstants.INTEGER_COMMA +
                DBConstants.ACTIVE + DBConstants.INTEGER_COMMA +
                DBConstants.MODIFIED + DBConstants.TEXT_COMMA +
                DBConstants.ANS_EXPLAIN + DBConstants.TEXT_COMMA +
                DBConstants.SCORE + DBConstants.INTEGER +
                DBConstants.CLOSE_BRACKET;
        Logger.logD(TAG, "Database creation query :" + query);
        sqLiteDatabase.execSQL(query);
    }

    private void createQuestionTable(SQLiteDatabase sqLiteDatabase) {
        String query = DBConstants.CREATE_TABLE_IF_NOT_EXIST + DBConstants.QUESTION_TABLE + DBConstants.OPEN_BRACKET +
                DBConstants.ID + DBConstants.TEXT_PRIMARY_KEY + DBConstants.COMMA +
                DBConstants.Q_TYPE + DBConstants.TEXT_COMMA +
                DBConstants.Q_TEXT + DBConstants.TEXT_COMMA +
                DBConstants.Q_HELP_TEXT + DBConstants.TEXT_COMMA +
                DBConstants.ACTIVE + DBConstants.INTEGER_COMMA +
                DBConstants.MODIFIED + DBConstants.TEXT_COMMA +
                DBConstants.DCF + DBConstants.INTEGER_COMMA +
                DBConstants.MEDIA_CONTENT + DBConstants.TEXT+
                DBConstants.CLOSE_BRACKET;
        Logger.logD(TAG, "Database creation query :" + query);
        sqLiteDatabase.execSQL(query);
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


    public String getModifiedDate(String tableName) {
        String selectQuery = DBConstants.SELECT + DBConstants.MAX + DBConstants.OPEN_BRACKET + DBConstants.MODIFIED + DBConstants.CLOSE_BRACKET + DBConstants.AS + DBConstants.MODIFIED + DBConstants.FROM + tableName;
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

    public void insertDatatoQuestionTable(List<QuestionModel> questionModelList) {
        initDatabase();
        database.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            for (QuestionModel questionModel : questionModelList) {
                contentValues.put(DBConstants.ID, questionModel.getId());
                contentValues.put(DBConstants.Q_TYPE, questionModel.getQtype());
                contentValues.put(DBConstants.Q_TEXT, questionModel.getText());
                contentValues.put(DBConstants.Q_HELP_TEXT, questionModel.getHelpText());
                contentValues.put(DBConstants.ACTIVE, questionModel.getActive());
                contentValues.put(DBConstants.MODIFIED, questionModel.getModified());
                contentValues.put(DBConstants.DCF, questionModel.getDcf());
                contentValues.put(DBConstants.MEDIA_CONTENT, questionModel.getMediacontent());
                Log.d(TAG, QUESTION_TABLE + contentValues.toString());
                database.insertWithOnConflict(QUESTION_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG, "Question values inserting into " + QUESTION_TABLE + contentValues.toString());
            }
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Logger.logE(TAG, ex.getMessage(), ex);
        } finally {
            database.endTransaction();
        }
    }

    public void insertDatatoQuestionChoicesTable(List<QuestionChoicesModel> questionChoicesModelList) {
        initDatabase();
        database.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            for (QuestionChoicesModel questionModel : questionChoicesModelList) {
                contentValues.put(DBConstants.ID, questionModel.getId());
                String isCorrect;
                if (questionModel.getCorrect()) {
                    isCorrect = "true";
                } else {
                    isCorrect = "false";
                }
                contentValues.put(DBConstants.IS_CORRECT, isCorrect);
                contentValues.put(DBConstants.CHOICE_TEXT, questionModel.getText());
                contentValues.put(DBConstants.Q_ID, questionModel.getQuestionId());
                contentValues.put(DBConstants.ACTIVE, questionModel.getActive());
                contentValues.put(DBConstants.MODIFIED, questionModel.getModifiedDate());
                contentValues.put(DBConstants.ANS_EXPLAIN, questionModel.getAnswerExplaination());
                contentValues.put(DBConstants.SCORE, questionModel.getScore());
                Log.d(TAG, QUESTION_CHOICES_TABLE + contentValues.toString());
                database.insertWithOnConflict(QUESTION_CHOICES_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG, "Question values inserting into " + QUESTION_CHOICES_TABLE + contentValues.toString());
            }
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Logger.logE(TAG, ex.getMessage(), ex);
        } finally {
            database.endTransaction();
        }
    }


    public Long insertDataToCatalogueTable(List<CatalogueDetailsModel> catalogueDetailsModel) {
        long insertLong = 0;

        initDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (CatalogueDetailsModel detailsModel : catalogueDetailsModel) {
                values.put(DBConstants.UUID, detailsModel.getUuid());
                values.put(DBConstants.ACTIVE, detailsModel.getActive());
                values.put(DBConstants.NAME, detailsModel.getName());
                values.put(DBConstants.CODE, detailsModel.getCode());
                values.put(DBConstants.ORDER, detailsModel.getOrder());
                values.put(DBConstants.COLOR, detailsModel.getUdf1().getColorCode());
                if (detailsModel.getParent() == null)
                    values.put(DBConstants.PARENT, "");
                else
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
            Logger.logE(TAG, ex.getMessage(), ex);
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
            query2 = "'" + parentId + "'";
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

    public List<QuestionModel> getQuestion(String videoId, String sectionId, int qa) {
        MutableLiveData<List<QuestionModel>> questionModelList = new MutableLiveData<>();
        List<QuestionModel> questionModelsList = new ArrayList<>();
        QuestionModel questionModel;
        String query = DBConstants.SELECT + DBConstants.ALL_FROM + DBConstants.QUESTION_TABLE;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase(DBConstants.DATABASESECRETKEY);
        try {
            Logger.logD(TAG, "Getting Question Item : " + query);
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    questionModel = new QuestionModel();
                    questionModel.setId(cursor.getInt(cursor.getColumnIndex(DBConstants.ID)));
                    questionModel.setQtype(cursor.getString(cursor.getColumnIndex(DBConstants.Q_TYPE)));
                    questionModel.setText(cursor.getString(cursor.getColumnIndex(DBConstants.Q_TEXT)));
                    questionModel.setHelpText(cursor.getString(cursor.getColumnIndex(DBConstants.Q_HELP_TEXT)));
                    questionModel.setActive(cursor.getInt(cursor.getColumnIndex(DBConstants.ACTIVE)));
                    questionModel.setModified(cursor.getString(cursor.getColumnIndex(DBConstants.MODIFIED)));
                    questionModel.setDcf(cursor.getInt(cursor.getColumnIndex(DBConstants.DCF)));
                    questionModel.setMediacontent(cursor.getString(cursor.getColumnIndex(DBConstants.MEDIA_CONTENT)));
                    questionModelsList.add(questionModel);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "getSubject", e);
        }
        questionModelList.setValue(questionModelsList);
        return questionModelList.getValue();
    }

    public List<QuestionChoicesModel> getChoices(int questionId) {
        MutableLiveData<List<QuestionChoicesModel>> questionChoicesModelList1 = new MutableLiveData<>();
        List<QuestionChoicesModel> questionChoicesModelsList = new ArrayList<>();
        QuestionChoicesModel questionChoicesModel;
        String query = DBConstants.SELECT + DBConstants.ALL_FROM + DBConstants.QUESTION_CHOICES_TABLE + DBConstants.WHERE + DBConstants.Q_ID + DBConstants.EQUAL_TO + questionId;
        initDatabase();
        try {
            Logger.logD(TAG, "Getting Question Choices : " + query);
            Cursor cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    questionChoicesModel = new QuestionChoicesModel();
                    questionChoicesModel.setId(cursor.getInt(cursor.getColumnIndex(DBConstants.ID)));
                    questionChoicesModel.setText(cursor.getString(cursor.getColumnIndex(DBConstants.CHOICE_TEXT)));
                    questionChoicesModel.setQuestionId(cursor.getInt(cursor.getColumnIndex(DBConstants.Q_ID)));
                    questionChoicesModel.setActive(cursor.getInt(cursor.getColumnIndex(DBConstants.ACTIVE)));
                    questionChoicesModel.setModifiedDate(cursor.getString(cursor.getColumnIndex(DBConstants.MODIFIED)));
                    questionChoicesModel.setAnswerExplaination(cursor.getString(cursor.getColumnIndex(DBConstants.ANS_EXPLAIN)));
                    questionChoicesModel.setScore(cursor.getInt(cursor.getColumnIndex(DBConstants.SCORE)));

                    questionChoicesModelsList.add(questionChoicesModel);

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "getSubject", e);
        }
        questionChoicesModelList1.setValue(questionChoicesModelsList);
        return questionChoicesModelList1.getValue();
    }


    public List<FileModel> getImageListFromTable() {
        FileModel fileModel;
        List<FileModel> imageList = new ArrayList<>();
        String query = DBConstants.SELECT + DBConstants.ICON_PATH +DBConstants.COMMA+DBConstants.NAME +DBConstants.COMMA+DBConstants.UUID +DBConstants.FROM+ DBConstants.CAT_TABLE_NAME +
                DBConstants.WHERE + ICON_PATH + DBConstants.NOT_EQUAL_TO + DBConstants.EMPTY +
                DBConstants.AND + DBConstants.ICON_PATH+DBConstants.NOT_NULL;
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
                    imageList.add(fileModel);

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "get ICON", e);
        }
        return imageList;
    }
}