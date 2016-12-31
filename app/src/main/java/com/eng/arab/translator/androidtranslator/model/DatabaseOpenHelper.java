package com.eng.arab.translator.androidtranslator.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseOpenHelper extends SQLiteAssetHelper {
	private static final String DATABASE_NAME = "translatorDatabase.db";
	private static final int DATABASE_VERSION = 1;

	public DatabaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		//setForcedUpgrade(2);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
		onCreate(db);*/
	}

	public int getUpgradeVersion() {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String [] sqlSelect = {"MAX (version)"};
		String sqlTables = "language";

		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, null, null,
				null, null, null);

		int v = 0;
		c.moveToFirst();
		if (!c.isAfterLast()) {
			v = c.getInt(0);
		}
		c.close();
		return v;
	}
}