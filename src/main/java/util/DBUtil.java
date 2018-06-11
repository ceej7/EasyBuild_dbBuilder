package util;

import java.sql.*;

public class DBUtil {

    /**
     * 数据库连接静态方法
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        //local
        return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/building?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=utf8","root","root");
        //Remote
        //return DriverManager.getConnection("jdbc:mysql://rm-bp14b369b8i4hdm6j5o.mysql.rds.aliyuncs.com:3306/building?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=utf8","root","Aa123456");

    }

    /**
     * 测试用main
     * @param args
     * @throws SQLException
     */
    public static void main(String[] args) throws SQLException {
        Connection connection=getConnection();
    }

    public  static boolean checkItem(long id)
    {
        try {
            Connection conn=getConnection();
            PreparedStatement ps;
            ResultSet rs;
            String sql = "select * from items where id=?";
            ps=conn.prepareStatement(sql);
            ps.setLong(1,id);
            rs=ps.executeQuery();
            while (rs.next()) {
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
