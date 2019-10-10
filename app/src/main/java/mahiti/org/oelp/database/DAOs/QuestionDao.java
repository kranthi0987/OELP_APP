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
import mahiti.org.oelp.models.QuestionModel;
import mahiti.org.oelp.utils.Logger;

import static mahiti.org.oelp.database.DBConstants.QUESTION_TABLE;

/**
 * Created by RAJ ARYAN on 2019-10-03.
 */
public class QuestionDao extends DatabaseHandlerClass {

    private static final String TAG = QuestionDao.class.getSimpleName();

    public QuestionDao(Context mContext) {
        super(mContext);
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

    public List<QuestionModel> getQuestion(String mediaUUID, String sectionId, int qa, int dcfId) {
        List<QuestionModel> questionModelsList = new ArrayList<>();
        QuestionModel questionModel;
        String query = DBConstants.SELECT + DBConstants.ALL_FROM + DBConstants.QUESTION_TABLE +
                DBConstants.WHERE + DBConstants.DCF + DBConstants.EQUAL_TO + +dcfId;
        initDatabase();
        Cursor cursor = null;
        try {
            Logger.logD(TAG, "Getting Question Item : " + query);
            cursor = database.rawQuery(query, null);
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
                    Logger.logD(TAG, "Data fetched from " + DBConstants.QUESTION_TABLE + questionModel.toString());
                    questionModelsList.add(questionModel);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "getSubject", e);
        }finally {
            closeCursor(cursor);
        }
        return questionModelsList;
    }


}
