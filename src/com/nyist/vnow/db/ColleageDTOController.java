package com.nyist.vnow.db;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.nyist.vnow.struct.Colleage;
import com.stay.utilities.TextUtil;

public class ColleageDTOController {

	private static Dao<Colleage, String> getDao() throws SQLException, DBNotInitializeException {
		return DBController.getDB().getDao(Colleage.class);
	}

	public static boolean addOrUpdate(Colleage dto) {
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
	
	public static boolean addOrUpdate(ArrayList<Colleage> entities,String userId) {
		try {
			for (Colleage entity : entities) {
			    entity.setUserId(userId);
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

	public static Colleage queryById(String id) {
		try {
			return getDao().queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (DBNotInitializeException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static ArrayList<Colleage> queryAll(String userId){
		try {
			QueryBuilder<Colleage, String> queryBuilder = getDao().queryBuilder();
			Where<Colleage, String> where = queryBuilder.where();
            where.eq(Colleage.USER_ID, userId);
            queryBuilder.orderBy(Colleage.CREATETIME, false);
			List<Colleage> resultList = getDao().query(queryBuilder.prepare());
			if (TextUtil.isValidate(resultList)) {
				return (ArrayList<Colleage>) resultList;
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
