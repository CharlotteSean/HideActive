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

	public static String SQL_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ LikesDB.TB_NAME
			+ "(userId text, postId text)";

	public LikesDB(Context context, String account) {
		helper = new DBOpenHelper(context, account);
		db = helper.getWritableDatabase();
	}

	/**
	 * 添加一条喜欢的帖子
	 * @param like
	 * @return
	 */
	public boolean addOne(Like like) {
		ContentValues values = getValues(like);
		Long uid = db.insert(TB_NAME, null, values);
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
	 * @return
	 */
	public boolean isLike(Like like) {
		Cursor cursor = db.query(TB_NAME, null,
				"userId=? and postId=?", new String[] {like.getuId(), like.getPostId()},
				null, null, null);
		if (cursor.moveToFirst()) {
			return true;
		}
		cursor.close();
		return false;
	}

	/**
	 * 删除一条喜欢的帖子
	 * @return
	 */
	public boolean delete(Like like) {
		int row = db.delete(TB_NAME, "userId=? and postId=?", new String[]{like.getuId(), like.getPostId()});
		if (row > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 清除用户数据
	 * @return
	 */
	public boolean deleteAll() {
		int row = db.delete(TB_NAME, null, null);
		if (row > 0) {
			return true;
		}
		return false;
	}

	private ContentValues getValues(Like like) {
		ContentValues values = new ContentValues();
		values.put("userId", like.getuId());
		values.put("postId", like.getPostId());
		return values;
	}
}
