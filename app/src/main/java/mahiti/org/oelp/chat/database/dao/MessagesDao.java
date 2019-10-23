/*
 * Copyright (c) 2019.
 * Project created and maintained by sanjay kranthi kumar
 * if need to contribute contact us on
 * kranthi0987@gmail.com
 */

package mahiti.org.oelp.chat.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import mahiti.org.oelp.chat.exceptions.OelpException;


public class MessagesDao {


    public boolean insertMessages(String uuid, String message, String from_user, String to_user, String message_date, String mesage_type, String status, String groupname, String groupuuid) throws OelpException {
        boolean isCreated = false;
        net.sqlcipher.database.SQLiteDatabase db = mahiti.org.oelp.utils.Constants.databaseHandlerClass.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        try {
            values.put("uuid", uuid);
            values.put("message", message);
            values.put("from_user", from_user);
            values.put("to_user", to_user);
            values.put("message_date", message_date);
            values.put("status", status);
            values.put("message_type", mesage_type);
            values.put("group_name", groupname);
            values.put("group_uuid", groupuuid);
            long createdRecordsCount = db.insertWithOnConflict("tbl_messages", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            if (createdRecordsCount != 0)
                isCreated = true;
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            isCreated = false;
            throw new OelpException(e.getMessage(), e);
        } finally {
            db.endTransaction();

        }
        return isCreated;
    }

    public List<MessagesDTO> getRecievedMessages(String groupUuid) throws OelpException {
        List<MessagesDTO> messagesList = new ArrayList<>();

        net.sqlcipher.database.SQLiteDatabase db = mahiti.org.oelp.utils.Constants.databaseHandlerClass.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();

        MessagesDTO messageDTO = new MessagesDTO();
        try {
            Cursor idCursor = db.rawQuery("SELECT * from tbl_messages where message_type = ? and group_uuid = ?", new String[]{"RECEIVED", groupUuid});
            if (idCursor.getCount() != 0) {
                while (idCursor.moveToNext()) {
                    messageDTO = new MessagesDTO();
                    messageDTO.setUuid(idCursor.getString(idCursor.getColumnIndexOrThrow("uuid")));
                    messageDTO.setMessage(idCursor.getString(idCursor.getColumnIndexOrThrow("message")));
                    messageDTO.setFrom_user(idCursor.getString(idCursor.getColumnIndexOrThrow("from_user")));
                    messageDTO.setTo_user(idCursor.getString(idCursor.getColumnIndexOrThrow("to_user")));
                    messageDTO.setMessage_date(idCursor.getString(idCursor.getColumnIndexOrThrow("message_date")));
                    messageDTO.setMessage_type(idCursor.getString(idCursor.getColumnIndexOrThrow("message_type")));
                    messageDTO.setStatus(idCursor.getString(idCursor.getColumnIndexOrThrow("status")));
                    messageDTO.setGroup_name(idCursor.getString(idCursor.getColumnIndexOrThrow("group_name")));
                    messageDTO.setGroup_uuid(idCursor.getString(idCursor.getColumnIndexOrThrow("group_uuid")));

                    messagesList.add(messageDTO);
                }
            }
            idCursor.close();
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            throw new OelpException(e.getMessage(), e);
        } finally {
            db.endTransaction();

        }

        return messagesList;
    }

    public List<MessagesDTO> getSentMessages(String groupUuid) throws OelpException {
        List<MessagesDTO> messagesList = new ArrayList<>();

        net.sqlcipher.database.SQLiteDatabase db = mahiti.org.oelp.utils.Constants.databaseHandlerClass.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();

        MessagesDTO messageDTO = new MessagesDTO();
        try {
            Cursor idCursor = db.rawQuery("SELECT * from tbl_messages where message_type = ? and group_uuid = ?", new String[]{"RECEIVED", groupUuid});
            if (idCursor.getCount() != 0) {
                while (idCursor.moveToNext()) {
                    messageDTO = new MessagesDTO();
                    messageDTO.setUuid(idCursor.getString(idCursor.getColumnIndexOrThrow("uuid")));
                    messageDTO.setMessage(idCursor.getString(idCursor.getColumnIndexOrThrow("message")));
                    messageDTO.setFrom_user(idCursor.getString(idCursor.getColumnIndexOrThrow("from_user")));
                    messageDTO.setTo_user(idCursor.getString(idCursor.getColumnIndexOrThrow("to_user")));
                    messageDTO.setMessage_date(idCursor.getString(idCursor.getColumnIndexOrThrow("message_date")));
                    messageDTO.setMessage_type(idCursor.getString(idCursor.getColumnIndexOrThrow("message_type")));
                    messageDTO.setStatus(idCursor.getString(idCursor.getColumnIndexOrThrow("status")));
                    messageDTO.setGroup_name(idCursor.getString(idCursor.getColumnIndexOrThrow("group_name")));
                    messageDTO.setGroup_uuid(idCursor.getString(idCursor.getColumnIndexOrThrow("group_uuid")));
                    messagesList.add(messageDTO);
                }
            }
            idCursor.close();
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            throw new OelpException(e.getMessage(), e);
        } finally {
            db.endTransaction();

        }

        return messagesList;
    }

    public List<MessagesDTO> getAllMessages(String groupUuid) throws OelpException {
        List<MessagesDTO> messagesList = new ArrayList<>();

        net.sqlcipher.database.SQLiteDatabase db = mahiti.org.oelp.utils.Constants.databaseHandlerClass.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();

        MessagesDTO messageDTO = new MessagesDTO();
        try {
            Cursor idCursor = db.rawQuery("SELECT * from tbl_messages where group_uuid = ?", new String[]{groupUuid});
            if (idCursor.getCount() != 0) {
                while (idCursor.moveToNext()) {
                    messageDTO = new MessagesDTO();
                    messageDTO.setUuid(idCursor.getString(idCursor.getColumnIndexOrThrow("uuid")));
                    messageDTO.setMessage(idCursor.getString(idCursor.getColumnIndexOrThrow("message")));
                    messageDTO.setFrom_user(idCursor.getString(idCursor.getColumnIndexOrThrow("from_user")));
                    messageDTO.setTo_user(idCursor.getString(idCursor.getColumnIndexOrThrow("to_user")));
                    messageDTO.setMessage_date(idCursor.getString(idCursor.getColumnIndexOrThrow("message_date")));
                    messageDTO.setMessage_type(idCursor.getString(idCursor.getColumnIndexOrThrow("message_type")));
                    messageDTO.setStatus(idCursor.getString(idCursor.getColumnIndexOrThrow("status")));
                    messageDTO.setGroup_name(idCursor.getString(idCursor.getColumnIndexOrThrow("group_name")));
                    messageDTO.setGroup_uuid(idCursor.getString(idCursor.getColumnIndexOrThrow("group_uuid")));
                    messagesList.add(messageDTO);
                }
            }
            idCursor.close();
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            throw new OelpException(e.getMessage(), e);
        } finally {
            db.endTransaction();

        }

        return messagesList;
    }
}
