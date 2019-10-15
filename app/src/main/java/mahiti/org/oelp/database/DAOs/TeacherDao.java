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
import mahiti.org.oelp.models.Member;
import mahiti.org.oelp.models.TeacherModel;
import mahiti.org.oelp.models.UserDetailsModel;
import mahiti.org.oelp.utils.Logger;


public class TeacherDao extends DatabaseHandlerClass {

    private static final String TAG = DatabaseHandlerClass.class.getSimpleName();
    Context mContext;

    public TeacherDao(Context mContext) {
        super(mContext);
        this.mContext = mContext;

    }


    public long insertTeacherDataToDB(List<TeacherModel> teacherList) {
        long insertLong = 0;
        initDatabase();
        deleteTeacherDataFromDB();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (TeacherModel teacher : teacherList) {
                values.put(DBConstants.USER_UID, teacher.getUserUuid());
                values.put(DBConstants.ACTIVE, teacher.getActive());
                values.put(DBConstants.CREATED, teacher.getCreated());
                values.put(DBConstants.VIDEOCOVERED_COUNT, teacher.getVideoCoveredCount());
                values.put(DBConstants.STATE_ID, teacher.getStateId());
                values.put(DBConstants.GROUP_UUID_COL, teacher.getGroupUuid());
                values.put(DBConstants.MOBILE_NUMBER, teacher.getMobileNumber());
                values.put(DBConstants.GROUP_NAME, teacher.getGroupName());
                values.put(DBConstants.LAST_ACTIVE, teacher.getLastActive());
                values.put(DBConstants.BLOCK_ID, teacher.getBlockIds());
                values.put(DBConstants.SCHOOL, teacher.getSchool());
                values.put(DBConstants.DISTRICT_ID, teacher.getDistrictId());
                values.put(DBConstants.IS_TRAINER, teacher.getIsTrainer());
                values.put(DBConstants.TEACHER_NAME, teacher.getName());
                values.put(DBConstants.LAST_LOGGEDIN, teacher.getLastLoggedIn());

                Log.d(TAG, DBConstants.TEACHER_TABLENAME + values.toString());
                insertLong = database.insertWithOnConflict(DBConstants.TEACHER_TABLENAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG, "Content values inserting into teacher table " + values.toString());
            }
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Logger.logD(TAG, ex.getMessage());
        } finally {
            database.endTransaction();
        }
        return insertLong;
    }

    public void deleteTeacherDataFromDB() {
        initDatabase();
        String query = DBConstants.DELETE + DBConstants.FROM + DBConstants.TEACHER_TABLENAME;
        Logger.logD(TAG, "Table Delete Query : " + query);
        database.execSQL(query);
    }

    /*
    * teachGroup =2 -> fetchList of user according to user*/

    public List<TeacherModel> getTeachers(String groupUUID, int teachGroup){
        String mainQuery = "";
        if(teachGroup==1)
            mainQuery = DBConstants.SELECT + DBConstants.ALL_FROM + DBConstants.TEACHER_TABLENAME + DBConstants.WHERE + DBConstants.GROUP_UUID_COL + DBConstants.IN+DBConstants.OPEN_BRACKET+DBConstants.SINGLE_QUOTES+ groupUUID +DBConstants.SINGLE_QUOTES+DBConstants.CLOSE_BRACKET;
        else
            mainQuery = DBConstants.SELECT + DBConstants.ALL_FROM + DBConstants.TEACHER_TABLENAME + DBConstants.WHERE + DBConstants.USER_UID + DBConstants.EQUAL_TO+DBConstants.SINGLE_QUOTES+ groupUUID +DBConstants.SINGLE_QUOTES;

        initDatabase();
        Cursor cursor=null;
        List<TeacherModel> teachersList = new ArrayList<>();
        try {
            Logger.logD(TAG, "getTeachers : " + mainQuery);
            cursor = database.rawQuery(mainQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    TeacherModel model = new TeacherModel();
                    model.setActive(cursor.getInt(cursor.getColumnIndex(DBConstants.ACTIVE)));
                    model.setCreated(cursor.getString(cursor.getColumnIndex(DBConstants.CREATED)));
                    model.setVideoCoveredCount(cursor.getString(cursor.getColumnIndex(DBConstants.VIDEOCOVERED_COUNT)));
                    model.setBlockName(blockName(cursor.getInt(cursor.getColumnIndex(DBConstants.BLOCK_ID))));
                    model.setSchool(cursor.getString(cursor.getColumnIndex(DBConstants.SCHOOL)));
                    model.setBlockIds(cursor.getInt(cursor.getColumnIndex(DBConstants.BLOCK_ID)));
                    model.setStateId(cursor.getInt(cursor.getColumnIndex(DBConstants.STATE_ID)));
                    model.setDistrictId(cursor.getInt(cursor.getColumnIndex(DBConstants.DISTRICT_ID)));
                    model.setGroupName(cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_NAME)));
                    model.setGroupUuid(cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_UUID_COL)));
                    model.setIsTrainer(cursor.getInt(cursor.getColumnIndex(DBConstants.IS_TRAINER)));
                    model.setLastActive(cursor.getString(cursor.getColumnIndex(DBConstants.LAST_ACTIVE)));
                    model.setUserUuid(cursor.getString(cursor.getColumnIndex(DBConstants.USER_UID)));
                    model.setLastLoggedIn(cursor.getString(cursor.getColumnIndex(DBConstants.LAST_LOGGEDIN)));
                    model.setMobileNumber(cursor.getString(cursor.getColumnIndex(DBConstants.MOBILE_NUMBER)));
                    model.setName(cursor.getString(cursor.getColumnIndex(DBConstants.TEACHER_NAME)));
                    model.setMediaCount(new MediaContentDao(mContext).fetchSharedMedia(model.getUserUuid(),groupUUID, false, 0).size());
                    teachersList.add(model);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "getTeachers", e);
        }finally {
            closeCursor(cursor);
        }
        return teachersList;
    }

    public String blockName(int blockId){
        String mainQuery = DBConstants.SELECT +DBConstants.NAME+ DBConstants.FROM + DBConstants.LOC_TABLE_NAME + DBConstants.WHERE + DBConstants.ID + DBConstants.EQUAL_TO+ blockId;
        initDatabase();
        Cursor cursor=null;
        String blockName = "";
        try {
            Logger.logD(TAG, "get block : " + mainQuery);
            cursor = database.rawQuery(mainQuery, null);
            if (cursor.getCount()!=0 && cursor.moveToFirst()) {
                blockName = cursor.getString(cursor.getColumnIndex(DBConstants.NAME));
            }
            cursor.close();
        } catch (Exception e) {
            Logger.logE(TAG, "get block", e);
        }finally {
            closeCursor(cursor);
        }
        return blockName;
    }

    public List<UserDetailsModel> getUserDetailsModels(List<TeacherModel> teacherList) {
        List<UserDetailsModel> modelLsit = new ArrayList<>();
        UserDetailsModel userDetailsModel;
        for (TeacherModel model : teacherList) {
            userDetailsModel = new UserDetailsModel();
            userDetailsModel.setName(model.getName());
            userDetailsModel.setUserid(model.getUserUuid());
            userDetailsModel.setMobile_number(model.getMobileNumber());
            userDetailsModel.setCheckBoxChecked(true);
            modelLsit.add(userDetailsModel);
        }
        return modelLsit;
    }





}

