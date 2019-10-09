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
import mahiti.org.oelp.models.GroupModel;
import mahiti.org.oelp.utils.Logger;

import static mahiti.org.oelp.database.DBConstants.GROUP_TABLE;

/**
 * Created by RAJ ARYAN on 2019-10-03.
 */
public class GroupDao extends DatabaseHandlerClass {

    private static final String TAG = GroupDao.class.getSimpleName();

    public GroupDao(Context context) {
        super(context);
    }

    public long insertDataToGroupsTable(List<GroupModel> groups) {
        long insertData = 0;
        initDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (GroupModel groupModel : groups) {
                values.put(DBConstants.UUID, groupModel.getUserUUID());
                values.put(DBConstants.GROUP_NAME, groupModel.getGroupName());
                values.put(DBConstants.GROUP_UUID, groupModel.getGroupUUID());
                values.put(DBConstants.MEMBERS_COUNT, groupModel.getMembers().size());
//                insertDataToMembersTable(groupModel.getMembers(), groupModel.getGroupUUID());
                Log.d(TAG, GROUP_TABLE + values.toString());
                insertData = database.insertWithOnConflict(GROUP_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG, "Group values inserting into  " + GROUP_TABLE + values.toString());
            }
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Logger.logE(TAG, ex.getMessage(), ex);
        } finally {
            database.endTransaction();
        }
        return insertData;
    }

    public List<GroupModel> getGroupList() {
        List<GroupModel> groupList1 = new ArrayList<>();
        GroupModel groupModel;
        String query = DBConstants.SELECT + DBConstants.ALL_FROM + DBConstants.GROUP_TABLE;
        initDatabase();
        try {
            Logger.logD(TAG, "Getting Group Item : " + query);
            Cursor cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    groupModel = new GroupModel();
                    String groupUUID = cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_UUID));
                    groupModel.setGroupUUID(groupUUID);
                    groupModel.setGroupName(cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_NAME)));
                    groupModel.setMembersCount(cursor.getInt(cursor.getColumnIndex(DBConstants.MEMBERS_COUNT)));
                    groupList1.add(groupModel);

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "getSubject", e);
        }
        return groupList1;
    }
}
