package com.hideactive.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

public class LikesOfUserDB {

	public static final String TB_NAME = "LikesOfUser";

	private DBOpenHelper helper;
	private SQLiteDatabase db;

	public LikesOfUserDB(Context context) {
		helper = new DBOpenHelper(context);
		db = helper.getWritableDatabase();
	}

	public static final String sql = "CREATE TABLE IF NOT EXISTS " + TB_NAME 
			+ "(userid text, postid text)";

	/**
	 * 添加一条喜欢的帖子
	 * @param userid
	 * @param postid
	 * @return
	 */
	public boolean addOne(String userid, String postid) {
		ContentValues values = getValues(userid, postid);
		Long uid = db.insert(TB_NAME, null, values);
		if (uid == -1) {
			return false;
		}
		return true;
	}

	/**
	 * 添加多条喜欢的帖子
	 * @param userid
	 * @param postids
	 * @return
	 */
	public boolean addMore(String userid, List<String> postids) {
		db.beginTransaction();
		try {
			for (int i = 0; i < postids.size(); i++) {
				ContentValues values = getValues(userid, postids.get(i));
				db.insert(TB_NAME, null, values);
			}
			db.setTransactionSuccessful();
		} catch(Exception e) {
			return false;
		} finally {
			db.endTransaction();
		}
		return true;
	}

	/**
	 * 判断用户是否喜欢此帖子
	 * @param userid
	 * @param postid
	 * @return
	 */
	public boolean isLike(String userid, String postid) {
		Cursor cursor = db.query(TB_NAME, null,
				"userid=? and postid=?", new String[] {userid, postid},
				null, null, null);
		if (cursor.moveToFirst()) {
			return true;
		}
		cursor.close();
		return false;
	}

	/**
	 * 删除一条喜欢的帖子
	 * @param userid
	 * @param postid
	 * @return
	 */
	public boolean delete(String userid, String postid) {
		int row = db.delete(TB_NAME, "userid=? and postid=?", new String[]{userid, postid});
		if (row > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 清楚用户数据
	 * @param userid
	 * @return
	 */
	public boolean deleteAll(String userid) {
		int row = db.delete(TB_NAME, "userid=?", new String[] {userid});
		if (row > 0) {
			return true;
		}
		return false;
	}

	private ContentValues getValues(String userid, String postid) {
		ContentValues values = new ContentValues();
		values.put("userid", userid);
		values.put("postid", postid);
		return values;
	}
}
