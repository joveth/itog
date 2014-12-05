package com.jov.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jov.bean.BlogBean;

public class DBHelper extends SQLiteOpenHelper {
	/**
	 * table
	 * */
	public static String TABLE_NAME_BLOG = "tb_blogs";
	private static final String DB_NAME = "itog.db";
	/**
	 * version
	 * */
	private static final int VERSION = 1;
	/**
	 * SQL for create table
	 * */
	private static final String CREATE_TABLE_BLOG = "create table IF NOT EXISTS "
			+ TABLE_NAME_BLOG
			+ "(id integer primary key autoincrement,url varchar(300),title varchar(500),shortDesc varchar(1200),"
			+ "date varchar(30),sortType varchar(40),author varchar(40),read varchar(30),comment  varchar(30))";
	/**
	 * SQL for drop table
	 * */
	private static final String DROP_TABLE_BLOG = "DROP TABLE IF EXISTS "
			+ TABLE_NAME_BLOG;
	private SQLiteDatabase db;

	public DBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	public DBHelper(Context context, String name, int version) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_BLOG);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_TABLE_BLOG);
		onCreate(db);
	}

	public List<BlogBean> getBlog(int pageNo) {
		if (db == null || !db.isOpen()) {
			db = this.getReadableDatabase();
		}
		Cursor cursor = null;
		List<BlogBean> list = new ArrayList<BlogBean>();
		int offset = (pageNo - 1) * 20;
		cursor = db.rawQuery(
				"select * from " + TABLE_NAME_BLOG
						+ " order by id desc  limit 20 offset "
						+ String.valueOf(offset), null);
		BlogBean obj = null;
		while (cursor != null && cursor.moveToNext()) {
			obj = new BlogBean();
			obj.setBid(cursor.getInt(cursor.getColumnIndex("id")));
			obj.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
			obj.setComment(cursor.getString(cursor.getColumnIndex("comment")));
			obj.setDate(cursor.getString(cursor.getColumnIndex("date")));
			obj.setLink(cursor.getString(cursor.getColumnIndex("url")));
			obj.setRead(cursor.getString(cursor.getColumnIndex("read")));
			obj.setShortDesc(cursor.getString(cursor
					.getColumnIndex("shortDesc")));
			obj.setSortType(cursor.getString(cursor.getColumnIndex("sortType")));
			obj.setTitle(cursor.getString(cursor.getColumnIndex("title")));
			list.add(obj);
		}
		cursor.close();
		db.close();
		return list;
	}

	public int getTotalCount() {
		if (db == null || !db.isOpen()) {
			db = this.getReadableDatabase();
		}
		String sql = "select count(*) from " + TABLE_NAME_BLOG;
		Cursor rec = db.rawQuery(sql, null);
		rec.moveToLast();
		long recSize = rec.getLong(0);
		rec.close();
		db.close();
		return (int) recSize;
	}

	public void insertBlog(BlogBean bean) {
		if (db == null || !db.isOpen()) {
			db = this.getReadableDatabase();
		}
		String sql = "insert into " + TABLE_NAME_BLOG
				+ "  values(null,?,?,?,?,?,?,?,?)";
		db.execSQL(
				sql,
				new String[] { bean.getLink(), bean.getTitle(),
						bean.getShortDesc(), bean.getDate(),
						bean.getSortType(), bean.getAuthor(), bean.getRead(),
						bean.getComment() });
		db.close();
	}

	public void deleteById(int id) {
		if (db == null || !db.isOpen()) {
			db = this.getReadableDatabase();
		}
		String sql = "delete from  " + TABLE_NAME_BLOG + "  where id=?";
		db.execSQL(sql, new String[] { String.valueOf(id) });
		db.close();
	}

	public void deleteAll() {
		if (db == null || !db.isOpen()) {
			db = this.getReadableDatabase();
		}
		String sql = "delete from  " + TABLE_NAME_BLOG;
		db.execSQL(sql);
		db.close();
	}
}
