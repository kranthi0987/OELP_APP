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
import mahiti.org.oelp.models.QuestionChoicesModel;
import mahiti.org.oelp.utils.Logger;

import static mahiti.org.oelp.database.DBConstants.QUESTION_CHOICES_TABLE;

/**
 * Created by RAJ ARYAN on 2019-10-03.
 */
public class ChoicesDao extends DatabaseHandlerClass {

    private static final String TAG = ChoicesDao.class.getSimpleName();

    public ChoicesDao(Context mContext) {
        super(mContext);
    }

    public List<QuestionChoicesModel> getChoices(int questionId) {
        MutableLiveData<List<QuestionChoicesModel>> questionChoicesModelList1 = new MutableLiveData<>();
        List<QuestionChoicesModel> questionChoicesModelsList = new ArrayList<>();
        QuestionChoicesModel questionChoicesModel;
        String query = DBConstants.SELECT + DBConstants.ALL_FROM + DBConstants.QUESTION_CHOICES_TABLE + DBConstants.WHERE + DBConstants.Q_ID + DBConstants.EQUAL_TO + questionId;
        initDatabase();
        Cursor cursor = null;
        try {
            Logger.logD(TAG, "Getting Question Choices : " + query);
            cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    questionChoicesModel = new QuestionChoicesModel();
                    questionChoicesModel.setId(cursor.getInt(cursor.getColumnIndex(DBConstants.ID)));
                    questionChoicesModel.setText(cursor.getString(cursor.getColumnIndex(DBConstants.CHOICE_TEXT)));
                    questionChoicesModel.setQuestionId(cursor.getInt(cursor.getColumnIndex(DBConstants.Q_ID)));
                    questionChoicesModel.setActive(cursor.getInt(cursor.getColumnIndex(DBConstants.ACTIVE)));
                    Boolean isCorect = cursor.getString(cursor.getColumnIndex(DBConstants.IS_CORRECT)).equalsIgnoreCase("true");
                    questionChoicesModel.setCorrect(isCorect);
                    questionChoicesModel.setModifiedDate(cursor.getString(cursor.getColumnIndex(DBConstants.MODIFIED)));
                    questionChoicesModel.setAnswerExplaination(cursor.getString(cursor.getColumnIndex(DBConstants.ANS_EXPLAIN)));
                    questionChoicesModel.setScore(cursor.getInt(cursor.getColumnIndex(DBConstants.SCORE)));

                    questionChoicesModelsList.add(questionChoicesModel);

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "getSubject", e);
        }finally {
            closeCursor(cursor);
        }
        questionChoicesModelList1.setValue(questionChoicesModelsList);
        return questionChoicesModelList1.getValue();
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
}
