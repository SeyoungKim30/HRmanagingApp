package vos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
	
		public static Connection con() throws SQLException {
			Connection con = null;
			//1. 메모리 로딩
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				System.out.println("드라이버 로딩 성공");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.out.println("드라이버 로딩 실패: "+e.getMessage());
			}
			//2. 연결정보
			String info="jdbc:oracle:thin:@localhost:1521:xe";
			con = DriverManager.getConnection(info,"scott","tiger");
			System.out.println("연결 성공");
			return con;
		}

		public static void close(ResultSet rs, Statement stmt, Connection con) {
			try {
				if(rs!=null) rs.close();
				if(stmt!=null) stmt.close();
				if(con!=null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
				try {
					if(rs!=null) {rs.close();}
					if(stmt!=null) stmt.close();
					if(con!=null) con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			
			
		}
		

	
}
