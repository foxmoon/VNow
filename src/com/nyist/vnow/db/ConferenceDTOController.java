package com.nyist.vnow.db;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.nyist.vnow.struct.Conference;
import com.stay.utilities.TextUtil;

public class ConferenceDTOController {

	private static Dao<Conference, String> getDao() throws SQLException, DBNotInitializeException {
		return DBController.getDB().getDao(Conference.class);
	}

	public static boolean addOrUpdate(Conference dto) {
		try {
			getDao().createOrUpdate(dto);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (DBNotInitializeException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean addOrUpdate(ArrayList<Conference> entities) {
		try {
			for (Conference entity : entities) {
				getDao().createOrUpdate(entity);
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (DBNotInitializeException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Conference queryById(String id) {
		try {
			return getDao().queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (DBNotInitializeException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static ArrayList<Conference> queryAll(){
		try {
			QueryBuilder<Conference, String> queryBuilder = getDao().queryBuilder();
			List<Conference> resultList = getDao().query(queryBuilder.prepare());
			if (TextUtil.isValidate(resultList)) {
				return (ArrayList<Conference>) resultList;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (DBNotInitializeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean delete(String id) {
		try {
			getDao().deleteById(id);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (DBNotInitializeException e) {
			e.printStackTrace();
		}

		return false;
	}

}
