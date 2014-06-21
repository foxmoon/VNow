package com.nyist.vnow.db;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.nyist.vnow.struct.User;
import com.stay.utilities.TextUtil;

public class UserDTOController {

	private static Dao<User, String> getDao() throws SQLException, DBNotInitializeException {
		return DBController.getDB().getDao(User.class);
	}

	public static boolean addOrUpdate(User dto) {
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
	
	public static boolean addOrUpdate(ArrayList<User> entities) {
		try {
			for (User entity : entities) {
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

	public static User queryById(String id) {
		try {
			return getDao().queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (DBNotInitializeException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static ArrayList<User> queryAll(){
		try {
			QueryBuilder<User, String> queryBuilder = getDao().queryBuilder();
			List<User> resultList = getDao().query(queryBuilder.prepare());
			if (TextUtil.isValidate(resultList)) {
				return (ArrayList<User>) resultList;
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
