package com.hellobike.dbPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;


/**
 * JDBC辅助组件
 * @author Administrator
 *
 */
public class JDBCHelper {

	static {
		try {
			String driver = "org.postgresql.Driver";
			Class.forName(driver);
		} catch (Exception e) {
			e.printStackTrace();  
		}
	}

	private static JDBCHelper instance = null;
	
	/**
	 * 获取单例
	 * @return 单例
	 */
	public static JDBCHelper getInstance() {
		if(instance == null) {
			synchronized(JDBCHelper.class) {
				if(instance == null) {
					instance = new JDBCHelper();
				}
			}
		}
		return instance;
	}
	
	// 数据库连接池
	private LinkedList<Connection> datasource = new LinkedList<Connection>();
	private JDBCHelper() {
		// 首先第一步，获取数据库连接池的大小，就是说，数据库连接池中要放多少个数据库连接
		// 这个，可以通过在配置文件中配置的方式，来灵活的设定
		int datasourceSize = 2;
		
		// 然后创建指定数量的数据库连接，并放入数据库连接池中
		for(int i = 0; i < datasourceSize; i++) {
			String url = "jdbc:postgresql://node01:5432/exampledb";
			String user = "dbuser";
			String password = "123456";
			
			try {
				Connection conn = DriverManager.getConnection(url, user, password);
				datasource.push(conn);  
			} catch (Exception e) {
				e.printStackTrace(); 
			}
		}
	}

	public synchronized Connection getConnection() {
		while(datasource.size() == 0) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}  
		}
		return datasource.poll();
	}

	/**
	 * 执行增删改SQL语句
	 * @param sql 
	 * @param params
	 * @return 影响的行数
	 */
	public int executeUpdate(String sql, Object[] params) {
		int rtn = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = getConnection();
			conn.setAutoCommit(false);  
			
			pstmt = conn.prepareStatement(sql);
		 
			if(params != null && params.length > 0) {
				for(int i = 0; i < params.length; i++) {
					pstmt.setObject(i + 1, params[i]);  
				}
			}
			
			rtn = pstmt.executeUpdate();
			
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();  
		} finally {
			if(conn != null) {
				datasource.push(conn);  
			}
		}
		
		return rtn;
	}
	
	/**
	 * 执行查询SQL语句
	 * @param sql
	 * @param params
	 * @param callback
	 */
	public void executeQuery(String sql, Object[] params, 
			QueryCallback callback) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			
			if(params != null && params.length > 0) {
				for(int i = 0; i < params.length; i++) {
					pstmt.setObject(i + 1, params[i]);   
				}
			}
			
			rs = pstmt.executeQuery();
			
			callback.process(rs);  
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				datasource.push(conn);  
			}
		}
	}
	
	/**
	 * 批量执行SQL语句
	 *
	 * @param sql
	 * @param paramsList
	 * @return 每条SQL语句影响的行数
	 */
	public int[] executeBatch(String sql, List<Object[]> paramsList) {
		int[] rtn = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = getConnection();
			
			conn.setAutoCommit(false);
			
			pstmt = conn.prepareStatement(sql);
			
			if(paramsList != null && paramsList.size() > 0) {
				for(Object[] params : paramsList) {
					for(int i = 0; i < params.length; i++) {
						pstmt.setObject(i + 1, params[i]);  
					}
					pstmt.addBatch();
				}
			}
			
			rtn = pstmt.executeBatch();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();  
		} finally {
			if(conn != null) {
				datasource.push(conn);  
			}
		}
		
		return rtn;
	}
	
	/**
	 * 静态内部类：查询回调接口
	 * @author Administrator
	 *
	 */
	public static interface QueryCallback {
		
		/**
		 * 处理查询结果
		 * @param rs 
		 * @throws Exception
		 */
		void process(ResultSet rs) throws Exception;
		
	}
	
}