package mahiti.org.oelp.database;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import mahiti.org.oelp.utils.Logger;

import static mahiti.org.oelp.database.DBConstants.CAT_TABLE_NAME;
import static mahiti.org.oelp.database.DBConstants.WATCH_STATUS;

/**
 * Created by RAJ ARYAN on 02/08/19.
 */
public class DatabaseHandlerClass extends SQLiteOpenHelper {

    public static SQLiteDatabase database;
    private static final String TAG = DatabaseHandlerClass.class.getSimpleName();


    public DatabaseHandlerClass(Context mContext) {
        super(mContext, DBConstants.DB_NAME, null, DBConstants.VERSION);
        try {
            SQLiteDatabase.loadLibs(mContext);
            initDatabase();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onCreate method", e);
        }
    }

    public void closeCursor(Cursor cursor) {
        if (cursor != null)
            cursor.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createLocationTable(sqLiteDatabase);
        createCatalogueTable(sqLiteDatabase);
        createQuestionTable(sqLiteDatabase);
        createQuestionChoicesTable(sqLiteDatabase);
        createQuestionAnswerTable(sqLiteDatabase);
        createGroupTable(sqLiteDatabase);
        createTeacherTable(sqLiteDatabase);
        createMediaStateTable(sqLiteDatabase);
        createMediaContentTable(sqLiteDatabase);
        createMessageTable(sqLiteDatabase);

    }

    private void createMediaContentTable(SQLiteDatabase sqLiteDatabase) {
        String query = DBConstants.CREATE_TABLE_IF_NOT_EXIST + DBConstants.MEDIA_CONTENT_TABLE + DBConstants.OPEN_BRACKET +
                DBConstants.UUID + DBConstants.TEXT_PRIMARY_KEY + DBConstants.COMMA +
                DBConstants.MEDIA_NAME + DBConstants.TEXT_COMMA +
                DBConstants.ACTIVE + DBConstants.INTEGER_COMMA+
                DBConstants.USER_NAME + DBConstants.TEXT_COMMA +
                DBConstants.USER_UUID + DBConstants.TEXT_COMMA +
                DBConstants.GROUP_UUID + DBConstants.TEXT_COMMA +
                DBConstants.MEDIA_TYPE + DBConstants.TEXT_COMMA +
                DBConstants.MEDIA_PATH + DBConstants.TEXT_COMMA +
                DBConstants.HASH_KEY + DBConstants.TEXT_COMMA +
                DBConstants.SUBMISSION_DATE + DBConstants.TEXT_COMMA +
                DBConstants.MODIFIED_DATE + DBConstants.TEXT_COMMA +
                DBConstants.IS_GLOBAL_SHARED + DBConstants.INTEGER +DBConstants.NOT_NULL_DEFAULT_ZERO +DBConstants.COMMA +
                DBConstants.SHARED_GLOBALLY_SYNC_STATUS + DBConstants.INTEGER + DBConstants.NOT_NULL_DEFAULT_ZERO +DBConstants.COMMA + // double not null default 0
                DBConstants.DELETE_MEDIA + DBConstants.INTEGER + DBConstants.NOT_NULL_DEFAULT_ZERO +DBConstants.COMMA + // double not null default 0
                DBConstants.SYNC_STATUS + DBConstants.INTEGER + DBConstants.NOT_NULL_DEFAULT_ZERO +  // double not null default 0
                DBConstants.CLOSE_BRACKET;
        Logger.logD(TAG, "Database creation query :" + query);
        sqLiteDatabase.execSQL(query);
    }

    private void createMediaStateTable(SQLiteDatabase sqLiteDatabase) {
        String query = DBConstants.CREATE_TABLE_IF_NOT_EXIST + DBConstants.MEDIA_STATUS_TABLE + DBConstants.OPEN_BRACKET +
                DBConstants.UUID + DBConstants.TEXT_PRIMARY_KEY + DBConstants.COMMA +
                DBConstants.PARENT + DBConstants.TEXT_COMMA +
                DBConstants.WATCH_STATUS + DBConstants.INTEGER + DBConstants.NOT_NULL_DEFAULT_ZERO +  // double not null default 0
                DBConstants.CLOSE_BRACKET;
        Logger.logD(TAG, "Database creation query :" + query);
        sqLiteDatabase.execSQL(query);
    }

    private void createGroupTable(SQLiteDatabase sqLiteDatabase) {
        String query = DBConstants.CREATE_TABLE_IF_NOT_EXIST + DBConstants.GROUP_TABLE + DBConstants.OPEN_BRACKET +
                DBConstants.GROUP_UUID + DBConstants.TEXT_PRIMARY_KEY + DBConstants.COMMA +
                DBConstants.GROUP_NAME + DBConstants.TEXT_COMMA +
                DBConstants.MEMBERS_COUNT + DBConstants.INTEGER_COMMA +
                DBConstants.UUID + DBConstants.TEXT +
                DBConstants.CLOSE_BRACKET;
        Logger.logD(TAG, "Database creation query :" + query);
        sqLiteDatabase.execSQL(query);
    }

    private void createQuestionAnswerTable(SQLiteDatabase sqLiteDatabase) {
        String query = DBConstants.CREATE_TABLE_IF_NOT_EXIST + DBConstants.QA_TABLENAME + DBConstants.OPEN_BRACKET +
                DBConstants.UUID + DBConstants.TEXT_PRIMARY_KEY + DBConstants.COMMA +
                DBConstants.QA_VIDEOID + DBConstants.TEXT_COMMA +
                DBConstants.SECTION_UUID + DBConstants.TEXT_COMMA +
                DBConstants.UNIT_UUID + DBConstants.TEXT_COMMA +
                DBConstants.QA_DATA + DBConstants.TEXT_COMMA +
                DBConstants.QA_PREVIEW_TEXT + DBConstants.TEXT_COMMA +
                DBConstants.SUBMISSION_DATE + DBConstants.DATETIME_COMMA +
                DBConstants.SYNC_STATUS + DBConstants.INTEGER_COMMA +
                DBConstants.ATTEMPT + DBConstants.INTEGER_COMMA +
                DBConstants.QA_SCORE + DBConstants.TEXT_COMMA +
                DBConstants.QA_TOTAL + DBConstants.TEXT +
                DBConstants.CLOSE_BRACKET;
        Logger.logD(TAG, "Database creation query :" + query);
        sqLiteDatabase.execSQL(query);
    }

    private void createQuestionChoicesTable(SQLiteDatabase sqLiteDatabase) {

        String query = DBConstants.CREATE_TABLE_IF_NOT_EXIST + DBConstants.QUESTION_CHOICES_TABLE + DBConstants.OPEN_BRACKET +
                DBConstants.ID + DBConstants.TEXT_PRIMARY_KEY + DBConstants.COMMA +
                DBConstants.IS_CORRECT + DBConstants.TEXT_COMMA +
                DBConstants.CHOICE_TEXT + DBConstants.TEXT_COMMA +
                DBConstants.Q_ID + DBConstants.INTEGER_COMMA +
                DBConstants.ACTIVE + DBConstants.INTEGER_COMMA +
                DBConstants.SUBMISSION_DATE + DBConstants.TEXT_COMMA +
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
                DBConstants.SUBMISSION_DATE + DBConstants.TEXT_COMMA +
                DBConstants.DCF + DBConstants.INTEGER +
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
                DBConstants.SUBMISSION_DATE + DBConstants.DATETIME_COMMA +
                DBConstants.ICON_TYPE + DBConstants.TEXT_COMMA +
                DBConstants.ICON_PATH + DBConstants.TEXT_COMMA +
                DBConstants.MEDIA_TYPE + DBConstants.TEXT_COMMA +
                DBConstants.MEDIA_PATH + DBConstants.TEXT_COMMA +
                DBConstants.MEDIA_LEVEL_TYPE + DBConstants.INTEGER_COMMA +
                DBConstants.DCF + DBConstants.INTEGER_COMMA +
                DBConstants.DESC + DBConstants.TEXT_COMMA +
                DBConstants.TYPE_CONTENT + DBConstants.TEXT_COMMA +
                DBConstants.CONTENT_UUID + DBConstants.TEXT_COMMA +
                DBConstants.FILE_SIZE + DBConstants.TEXT +
                DBConstants.CLOSE_BRACKET;
        Logger.logD(TAG, "Database creation query :" + query);
        sqLiteDatabase.execSQL(query);
    }

    private void createLocationTable(SQLiteDatabase sqLiteDatabase) {
        String query = DBConstants.CREATE_TABLE_IF_NOT_EXIST + DBConstants.LOC_TABLE_NAME + DBConstants.OPEN_BRACKET +
                DBConstants.ID + DBConstants.TEXT_PRIMARY_KEY + DBConstants.COMMA +
                DBConstants.ACTIVE + DBConstants.INTEGER_COMMA +
                DBConstants.CREATED + DBConstants.DATETIME_COMMA +
                DBConstants.SUBMISSION_DATE + DBConstants.DATETIME_COMMA +
                DBConstants.NAME + DBConstants.DATETIME_COMMA +
                DBConstants.BOUNDARY_LEVEL_TYPE + DBConstants.INTEGER_COMMA +
                DBConstants.PARENT + DBConstants.TEXT +
                DBConstants.CLOSE_BRACKET;
        Logger.logD(TAG, "Database creation query :" + query);
        sqLiteDatabase.execSQL(query);
    }

    private void createTeacherTable(SQLiteDatabase sqLiteDatabase) {
        String query = DBConstants.CREATE_TABLE_IF_NOT_EXIST + DBConstants.TEACHER_TABLENAME + DBConstants.OPEN_BRACKET +
                DBConstants.USER_UID + DBConstants.TEXT_PRIMARY_KEY + DBConstants.COMMA +
                DBConstants.ACTIVE + DBConstants.INTEGER_COMMA +
                DBConstants.CREATED + DBConstants.DATETIME_COMMA +
                DBConstants.VIDEOCOVERED_COUNT + DBConstants.TEXT_COMMA +
                DBConstants.STATE_ID + DBConstants.INTEGER_COMMA +
                DBConstants.GROUP_UUID_COL + DBConstants.TEXT_COMMA +
                DBConstants.MOBILE_NUMBER + DBConstants.TEXT_COMMA +
                DBConstants.GROUP_NAME + DBConstants.TEXT_COMMA +
                DBConstants.LAST_ACTIVE + DBConstants.TEXT_COMMA +
                DBConstants.BLOCK_ID + DBConstants.INTEGER_COMMA +
                DBConstants.SCHOOL + DBConstants.TEXT_COMMA +
                DBConstants.DISTRICT_ID + DBConstants.INTEGER_COMMA +
                DBConstants.IS_TRAINER + DBConstants.INTEGER_COMMA +
                DBConstants.LAST_LOGGEDIN + DBConstants.TEXT_COMMA +
                DBConstants.TEACHER_NAME + DBConstants.TEXT +
                DBConstants.CLOSE_BRACKET;
        Logger.logD(TAG, "Database creation query :" + query);
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public String getModifiedDate(String tableName) {
        String selectQuery = DBConstants.SELECT + DBConstants.MAX + DBConstants.OPEN_BRACKET + DBConstants.SUBMISSION_DATE + DBConstants.CLOSE_BRACKET + DBConstants.AS + DBConstants.SUBMISSION_DATE + DBConstants.FROM + tableName;
        Logger.logD(TAG, selectQuery);
        initDatabase();
        android.database.Cursor cursor = database.rawQuery(selectQuery, null);
        String date = null;
        if (cursor.moveToFirst()) {
            date = cursor.getString(cursor.getColumnIndex(DBConstants.SUBMISSION_DATE));
            cursor.close();
        } else {
            cursor.close();
        }
        if (date == null)
            date = "";

        return date;
    }

    public void initDatabase() {
        if (database == null || !database.isOpen() || database.isReadOnly())
            database = this.getWritableDatabase(DBConstants.DATABASESECRETKEY);

    }

    public void closeDatabase(){
        /*if (database!=null)
            database.close();*/
    }

    public Integer getWatchStatus(String uuid) {
        int watchStatus = 0;

        String query = DBConstants.SELECT + DBConstants.WATCH_STATUS + DBConstants.FROM + DBConstants.MEDIA_STATUS_TABLE + DBConstants.WHERE + DBConstants.UUID +
                DBConstants.EQUAL_TO + DBConstants.SINGLE_QUOTES + uuid + DBConstants.SINGLE_QUOTES;

        initDatabase();
        Cursor cursor = null;
        try {
             cursor = database.rawQuery(query, null);
            if(cursor.moveToFirst())
                watchStatus = cursor.getInt(cursor.getColumnIndex(DBConstants.WATCH_STATUS));
        } catch (Exception xe) {
            Logger.logE(TAG, "fetching watch status error :" + xe.getMessage(), xe);
        }finally {
            closeCursor(cursor);
        }
        return watchStatus;
    }


    public void insertWatchStatus(String parentUUID, String mediaUUID) {
        initDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.PARENT, parentUUID); //These Fields should be your String values of actual column names
        cv.put(DBConstants.UUID, mediaUUID); //These Fields should be your String values of actual column names
        database.insertWithOnConflict(DBConstants.MEDIA_STATUS_TABLE, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        Log.d(TAG, "Watch values inserting into  " + DBConstants.MEDIA_STATUS_TABLE + cv.toString());
    }

    public void updateAllWatchStatus() {
        initDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.WATCH_STATUS, 0);
        try {
            database.update(DBConstants.MEDIA_STATUS_TABLE, cv, null, null);

        } catch (Exception ex) {
            Logger.logE("Exception", ex.getMessage(), ex);
        }
    }

    public void updateWatchStatus(String mediaUUID) {
        initDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.WATCH_STATUS, 1); //These Fields should be your String values of actual column names
        database.update(DBConstants.MEDIA_STATUS_TABLE, cv, "uuid = ?", new String[]{mediaUUID});
    }

    public boolean getContentIsOpen(String uuid) {
        boolean isOpen = false;
        initDatabase();
        String query = DBConstants.SELECT + DBConstants.WATCH_STATUS + DBConstants.FROM + DBConstants.MEDIA_STATUS_TABLE + DBConstants.WHERE + DBConstants.PARENT + " = '" + uuid + "'" + DBConstants.AND + DBConstants.WATCH_STATUS + " = 0";
        Logger.logD(TAG, "Content View Status Query : " + query);
        try {
            android.database.Cursor cursor = database.rawQuery(query, null);
            // If count is greater than 0, then module is not fully completed
            if (cursor.getCount() > 0) {
                isOpen = false;
            } else {
                updateWatchStatus(uuid);
                isOpen = true;
            }
            cursor.close();
        } catch (Exception ex) {
            Logger.logE("Exception", ex.getMessage(), ex);
        }
        return isOpen;
    }

    public void deleteAllDataFromDB(int type) {
        initDatabase();
        String query = "";
        try {
            switch (type) {
                case 1:
                    query = DBConstants.DELETE + DBConstants.FROM + DBConstants.CAT_TABLE_NAME;
                    break;
                case 2:
                    query = DBConstants.DELETE + DBConstants.FROM + DBConstants.QUESTION_CHOICES_TABLE;
                    break;
                case 3:
                    query = DBConstants.DELETE + DBConstants.FROM + DBConstants.LOC_TABLE_NAME;
                    break;
                case 4:
                    query = DBConstants.DELETE + DBConstants.FROM + DBConstants.QUESTION_TABLE;
                    break;
                case 5:
                    query = DBConstants.DELETE + DBConstants.FROM + DBConstants.QA_TABLENAME;
                    break;
                case 6:
                    query = DBConstants.DELETE + DBConstants.FROM + DBConstants.GROUP_TABLE;
                    break;
                case 7:
                    query = DBConstants.DELETE + DBConstants.FROM + DBConstants.TEACHER_TABLENAME;
                    break;
                case 8:
                    query = DBConstants.DELETE + DBConstants.FROM + DBConstants.MEDIA_CONTENT_TABLE;
                    break;
                case 9:
                    query = DBConstants.DELETE + DBConstants.FROM + DBConstants.MEDIA_STATUS_TABLE;
                    break;

            }
            Logger.logD(TAG, "Table Delete Query : " + query);
            database.execSQL(query);
        } catch (Exception ex) {
            Logger.logE(TAG, ex.getMessage(), ex);
        }
    }

    public int getNoOfCompletedVideos(){
        int count = 0;

        String query = DBConstants.SELECT+DBConstants.COUNT+DBConstants.OPEN_BRACKET+WATCH_STATUS+
                DBConstants.CLOSE_BRACKET+DBConstants.FROM+DBConstants.MEDIA_STATUS_TABLE+
                DBConstants.WHERE+ DBConstants.WATCH_STATUS+DBConstants.EQUAL_TO+1;
        Logger.logD(TAG, "Video watch count query : " + query);
        Cursor cursor = null;
        initDatabase();
        try {
            cursor = database.rawQuery(query, null);
            // If count is greater than 0, then module is not fully completed
            cursor.moveToFirst();
            count = cursor.getInt(cursor.getColumnIndex(WATCH_STATUS));
        } catch (Exception ex) {
            Logger.logE("Exception", ex.getMessage(), ex);
        }finally {
            closeCursor(cursor);
        }
        return count;
    }


    private void createMessageTable(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE IF NOT EXISTS tbl_messages (" +
                "uuid TEXT," +
                "message TEXT," +
                "from_user TEXT," +
                "to_user TEXT," +
                "message_date TEXT," +
                "status integer DEFAULT 2 )";
        Logger.logD(TAG, "Database creation query :" + query);
        sqLiteDatabase.execSQL(query);
    }


    public SQLiteDatabase getWriteDb() {
        if (database != null && database.isOpen())
            return database;
        else
            return getWritableDatabase(DBConstants.DATABASESECRETKEY);
    }
}
