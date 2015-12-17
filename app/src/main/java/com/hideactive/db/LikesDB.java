package com.hideactive.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hideactive.model.Like;

import java.util.List;

public class LikesDB {

	public static final String TB_NAME = "Likes";

	private DBOpenHelper helper;
	private SQLiteDatabase db;
	private String tableName;

	public LikesDB(Context context, String account) {
		helper = new DBOpenHelper(context, account);
		db = helper.getWritableDatabase();
		tableName = TB_NAME + "_" + account;
	}

	/**
	 * 添加一条喜欢的帖子
	 * @param like
	 * @return
	 */
	public boolean addOne(Like like) {
		ContentValues values = getValues(like);
		Long uid = db.insert(tableName, null, values);
		if (uid == -1) {
			return false;
		}
		return true;
	}

	/**
	 * 添加多条喜欢的帖子
	 * @param likes
	 * @return
	 */
	public boolean addMore(List<Like> likes) {
		db.beginTransaction();
		try {
			for (int i = 0; i < likes.size(); i++) {
				ContentValues values = getValues(likes.get(i));
				db.insert(tableName, null, values);
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
		Cursor cursor = db.query(tableName, null,
				"userId=? and postId=?", new String[] {userid, postid},
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
		int row = db.delete(tableName, "userId=? and postId=?", new String[]{userid, postid});
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
		int row = db.delete(tableName, "userId=?", new String[] {userid});
		if (row > 0) {
			return true;
		}
		return false;
	}

	private ContentValues getValues(Like like) {
		ContentValues values = new ContentValues();
		values.put("userId", like.getuId());
		values.put("postId", like.getPostId());
		values.put("createTime", like.getCreatedAt());
		return values;
	}
}
