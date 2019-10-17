package mahiti.org.oelp.database.DAOs;

import android.content.ContentValues;
import android.content.Context;

import com.google.gson.Gson;

import net.sqlcipher.Cursor;

import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.database.DBConstants;
import mahiti.org.oelp.database.DatabaseHandlerClass;
import mahiti.org.oelp.models.SubmittedAnswerResponse;
import mahiti.org.oelp.utils.Logger;

/**
 * Created by RAJ ARYAN on 2019-10-03.
 */
public class SurveyResponseDao extends DatabaseHandlerClass {

    private static final String TAG = SurveyResponseDao.class.getSimpleName();


    public SurveyResponseDao(Context mContext) {
        super(mContext);
    }


    public void insertAnsweredQuestion(List<SubmittedAnswerResponse> list) {
        if (list==null)
            return;

        Gson gson = new Gson();
        int attempt = 0;
        initDatabase();
        database.beginTransaction();
        try {
            for (SubmittedAnswerResponse response : list) {
                String responseData = "";
                try {
                    responseData = gson.toJson(response.getResponse());
                } catch (Exception xe) {
                    Logger.logE("Exception", xe.getMessage(), xe);
                }
                ContentValues values = new ContentValues();
                values.put(DBConstants.UUID, response.getCreationKey());
                values.put(DBConstants.QA_VIDEOID, response.getMediacontent());
                values.put(DBConstants.SECTION_UUID, response.getSectionUUID());
                values.put(DBConstants.UNIT_UUID, response.getUnitUUID());
                values.put(DBConstants.QA_DATA, responseData);
                values.put(DBConstants.QA_PREVIEW_TEXT, "");
                values.put(DBConstants.SUBMISSION_DATE, response.getSubmissionDate());
                if (response.getAttempts() == 0) {
                    attempt = getAttemptFromDb(response.getMediacontent(), 1);
                }
                values.put(DBConstants.ATTEMPT, attempt);
                values.put(DBConstants.QA_SCORE, response.getScore());
                values.put(DBConstants.QA_TOTAL, response.getTotal());
                values.put(DBConstants.SYNC_STATUS, response.getSyncStatus());
                Logger.logD("InsertQuestionAnswered", values.toString());
                database.insert(DBConstants.QA_TABLENAME, null, values);
                updateWatchStatus(response.getCreationKey());
            }
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Logger.logE(TAG, ex.getMessage(), ex);
        } finally {
            database.endTransaction();
        }
    }

    public int getAttemptFromDb(String mediaUUID, int type) {
        String query = DBConstants.SELECT + DBConstants.ATTEMPT + DBConstants.FROM + DBConstants.QA_TABLENAME +
                DBConstants.WHERE + DBConstants.QA_VIDEOID + DBConstants.EQUAL_TO + DBConstants.SINGLE_QUOTES + mediaUUID + DBConstants.SINGLE_QUOTES;
        Logger.logD(TAG, DBConstants.QA_TABLENAME + " Fetch Attempt Query :" + query);
        initDatabase();
        Cursor cursor = null;
        int attempt = 0;
        try {
            cursor = database.rawQuery(query, null);
            attempt = cursor.getCount();
        } catch (Exception ex) {
            Logger.logE(TAG, DBConstants.QA_TABLENAME + " Fetch Attempt Exception :" + ex.getMessage(), ex);
        } finally {
            closeCursor(cursor);
        }
        if (type == 1) {
            attempt++;
        }
        return attempt;
    }

    public void updateSyncStatus(String creationKey) {
        initDatabase();
        ContentValues cv = new ContentValues();
        String selection = DBConstants.UUID + " = ?";
        String[] selectionArgs = {creationKey};
        cv.put(DBConstants.SYNC_STATUS, 1); //These Fields should be your String values of actual column names
        database.update(DBConstants.QA_TABLENAME, cv, selection, selectionArgs);
    }

    /**
     * @param typeSync 0 for all data 1 for unsync data
     * @param videoId  video id
     */
    public List<SubmittedAnswerResponse> fetchAnsweredQuestion(String videoId, Integer typeSync) {
        List<SubmittedAnswerResponse> asyncQuestionAnswer = new ArrayList<>();
        String dataFetchQuery;
        if (typeSync == 1) {
            dataFetchQuery = DBConstants.SELECT + DBConstants.ALL_FROM + DBConstants.QA_TABLENAME +
                    DBConstants.WHERE + DBConstants.SYNC_STATUS + DBConstants.EQUAL_TO + 1;
        } else {
            dataFetchQuery = DBConstants.SELECT + DBConstants.ALL + DBConstants.COMMA + DBConstants.MAX + DBConstants.OPEN_BRACKET + DBConstants.SUBMISSION_DATE + DBConstants.CLOSE_BRACKET + DBConstants.FROM + DBConstants.QA_TABLENAME +
                    DBConstants.WHERE + DBConstants.QA_VIDEOID +
                    DBConstants.EQUAL_TO + DBConstants.SINGLE_QUOTES + videoId + DBConstants.SINGLE_QUOTES;
        }
        initDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(dataFetchQuery, null);
            Logger.logD("Query", dataFetchQuery);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    SubmittedAnswerResponse model = new SubmittedAnswerResponse();
                    model.setCreationKey(cursor.getString(cursor.getColumnIndex(DBConstants.UUID)));
                    model.setMediacontent(cursor.getString(cursor.getColumnIndex(DBConstants.QA_VIDEOID)));
                    model.setSectionUUID(cursor.getString(cursor.getColumnIndex(DBConstants.SECTION_UUID)));
                    model.setUnitUUID(cursor.getString(cursor.getColumnIndex(DBConstants.UNIT_UUID)));
                    model.setServerString(cursor.getString(cursor.getColumnIndex(DBConstants.QA_DATA)));
                    model.setPreviewString(cursor.getString(cursor.getColumnIndex(DBConstants.QA_PREVIEW_TEXT)));
                    model.setSubmissionDate(cursor.getString(cursor.getColumnIndex(DBConstants.SUBMISSION_DATE)));
                    model.setAttempts(cursor.getInt(cursor.getColumnIndex(DBConstants.ATTEMPT)));
                    String scorre;
                    scorre = cursor.getString(cursor.getColumnIndex(DBConstants.QA_SCORE));

                    if (scorre != null && !scorre.isEmpty() && scorre.equalsIgnoreCase("null"))
                        model.setScore(Float.parseFloat(scorre));
                    else
                        model.setScore(0.0f);

                    model.setTotal(cursor.getString(cursor.getColumnIndex(DBConstants.QA_TOTAL)));
                    asyncQuestionAnswer.add(model);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            Logger.logE(TAG, "getSubject", e);
        } finally {
            closeCursor(cursor);
        }
        Logger.logD("QuestionAnswered", asyncQuestionAnswer.toString());
        return asyncQuestionAnswer;

    }

    public SubmittedAnswerResponse getCount(String mediaUUID) {
        SubmittedAnswerResponse response = null;
        String query = DBConstants.SELECT + DBConstants.SCORE + DBConstants.COMMA + DBConstants.QA_TOTAL + DBConstants.COMMA
                + DBConstants.ATTEMPT + DBConstants.COMMA + DBConstants.MAX + DBConstants.OPEN_BRACKET + DBConstants.SUBMISSION_DATE + DBConstants.CLOSE_BRACKET +
                DBConstants.FROM + DBConstants.QA_TABLENAME + DBConstants.WHERE + DBConstants.QA_VIDEOID + DBConstants.EQUAL_TO + DBConstants.SINGLE_QUOTES +
                mediaUUID + DBConstants.SINGLE_QUOTES;
        initDatabase();
        Cursor cursor = null;
        try {
            database.beginTransaction();
            Logger.logD(TAG, DBConstants.QA_TABLENAME + " Get Attempt query :" + query);
            cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                response = new SubmittedAnswerResponse();
                response.setTotal(cursor.getString(cursor.getColumnIndex(DBConstants.QA_TOTAL)));
                String score = "0.0";
                score = cursor.getString(cursor.getColumnIndex(DBConstants.SCORE));
                if (score != null && !score.isEmpty() && score != "null")
                    response.setScore(Float.valueOf(score));
                else
                    response.setScore((float) 0.0);
                response.setTotal(cursor.getString(cursor.getColumnIndex(DBConstants.QA_TOTAL)));
                response.setAttempts(cursor.getInt(cursor.getColumnIndex(DBConstants.ATTEMPT)));
            }
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Logger.logE(TAG, DBConstants.QA_TABLENAME + " Get Attempt query Exception:" + ex.getMessage(), ex);
        } finally {
            database.endTransaction();
            closeCursor(cursor);
        }

        return response;
    }

}
