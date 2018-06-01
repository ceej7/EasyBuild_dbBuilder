package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    /**
     * 数据库连接静态方法
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=utf8","root","root");
    }

    /**
     * 测试用main
     * @param args
     * @throws SQLException
     */
    public static void main(String[] args) throws SQLException {
        Connection connection=getConnection();
    }
}
