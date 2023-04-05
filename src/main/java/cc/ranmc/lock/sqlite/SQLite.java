package cc.ranmc.lock.sqlite;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SQLite {

    private Connection connection;

    public SQLite(String file) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:"+ file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭数据库连接
     */
    public void close() {
        try {
            if(connection != null && !connection.isClosed()) connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 新增数据库表
     */
    public SQLite createTable() {
        runCommand("CREATE TABLE TRUST " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " Player TEXT," +
                " Trusted TEXT)");

        runCommand("CREATE TABLE LOCK " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " Player TEXT," +
                " World TEXT," +
                " X INTEGER," +
                " Y INTEGER," +
                " Z INTEGER)");

        runCommand("CREATE TABLE AUTO " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " Player TEXT)");
        return this;
    }

    /**
     * 新增锁箱记录
     * @param playerName 玩家名
     * @param location 位置
     */
    public void insertLock(String playerName, Location location) {
        String builder = "INSERT INTO LOCK (Player,X,Y,Z,World) VALUES ('" + playerName +
                "'," + location.getBlockX() + "," + location.getBlockY() + "," +
                location.getBlockZ() + ",'" + Objects.requireNonNull(location.getWorld()).getName() + "');";
        runCommand(builder);
    }

    /**
     * 删除锁箱记录
     * @param location 位置
     */
    public void deleteLock(Location location) {
        Map<String,String> map = getMap("SELECT * FROM LOCK WHERE World = '" +
                Objects.requireNonNull(location.getWorld()).getName() + "' AND X =" + location.getBlockX() + " AND Y =" + location.getBlockY() + " AND Z =" + location.getBlockZ());
        if (!map.isEmpty()) delete("LOCK", map.get("ID"));
    }

    /**
     * 增加对该玩家信任
     * @param playerName 玩家名
     * @param target 目标名
     */
    public void insertTrust(String playerName, String target) {
        runCommand("INSERT INTO TRUST (Player,Trusted) VALUES ('" + playerName +"','" +target +"');");
    }

    /**
     * 取消对该玩家信任
     * @param playerName 玩家名
     * @param target 目标名
     */
    public void deleteTrust(String playerName, String target) {
        Map<String,String> map = getMap("SELECT * FROM TRUST WHERE Player LIKE '" + playerName + "' AND TRUSTED LIKE '" + target + "'");
        if (!map.isEmpty()) delete("TRUST", map.get("ID"));
    }

    /**
     * 获取玩家信任列表
     * @param playerName 玩家
     * @return 信任列表
     */
    public List<String> selectTrustLst(String playerName) {
        List<String> list = new ArrayList<>();
        findList("TRUST", "Player", playerName).forEach(map -> list.add(map.get("Trusted")));
        return list;
    }

    /**
     * 获取当前方块拥有者
     * @param location 位置
     * @return 拥有者
     */
    public String selectOwner(Location location) {
        Map<String,String> map = getMap("SELECT * FROM LOCK WHERE World = '" + Objects.requireNonNull(location.getWorld()).getName() +
                "' AND X = " + location.getBlockX() + " AND Y = " + location.getBlockY() + " AND Z = " + location.getBlockZ());
        return map.getOrDefault("Player", null);
    }

    /**
     * 获取自动锁定状态
     * @param player 玩家
     * @return 是否开启动锁定
     */
    public boolean selectAuto(Player player) {
        Map<String,String> map = findMap("AUTO", "Player", player.getName());
        return !map.isEmpty();
    }

    /**
     * 切换自动锁定状态
     * @param player 玩家
     */
    public void updateAuto(Player player) {
        Map<String,String> map = findMap("AUTO", "Player", player.getName());
        if (map.isEmpty()) {
            insert("AUTO", "Player", player.getName());
        } else {
            delete("AUTO", map.get("ID"));
        }
    }

    /**
     * 新增数据库
     * @param table 表
     * @param name 名称
     * @param value 值
     */
    public void insert(String table,String name,String value) {
        runCommand("INSERT INTO " + table + " ("+name+") VALUES ('" + value.replace(",","','") + "');");
    }

    /*
    public List<Map<String, String>> findNearActList(Location location) {
        return selectList("SELECT * FROM LOCK WHERE World = '" + Objects.requireNonNull(location.getWorld()).getName() +
                "' AND (X BETWEEN " + (location.getBlockX() - 10) + " and " + (location.getBlockX() + 10) +
                ") AND (Y BETWEEN " + (location.getBlockY() - 10) + " and " + (location.getBlockY() + 10) +
                ") AND (Z BETWEEN " + (location.getBlockZ() - 10) + " and " + (location.getBlockZ() + 10) + ")");
    }
    */

    /**
     * 分析数据
     * @param command 命令
     * @return 数据
     */
    private Map<String, String> getMap(String command) {
        Map<String, String> map = new HashMap<>();
        ResultSet rs = null;
        try {
            rs = connection.createStatement().executeQuery(command);
            if(!rs.isClosed()) {
                ResultSetMetaData md = rs.getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    if (rs.getString(i) != null) {
                        map.put(md.getColumnName(i), rs.getString(i));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null && !rs.isClosed()) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 查询表数据
     * @param table 表
     * @param name 名称
     * @return 数据
     */
    public Map<String, String> findMap(String table, String name, String value) {
        String command = "SELECT * FROM " + table + " WHERE " + name + " LIKE '" + value + "'";
        return getMap(command);
    }

    public List<Map<String, String>> findList(String table, String name, String value) {
        return selectList("SELECT * FROM " + table + " WHERE " + name + " LIKE '" + value + "'");
    }

    private List<Map<String, String>> selectList(String command) {
        List<Map<String, String>> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = connection.createStatement().executeQuery(command);
            while (rs.next()) {
                if(!rs.isClosed()) {
                    Map<String, String> map = new HashMap<>();
                    ResultSetMetaData md = rs.getMetaData();
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        if (rs.getString(i) != null) {
                            map.put(md.getColumnName(i), rs.getString(i));
                        }
                    }
                    list.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null && !rs.isClosed()) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 删除表数据
     * @param table 表
     * @param id 编号
     */
    public void delete(String table,String id) {
        runCommand("DELETE FROM "+table+" WHERE ID="+id);
    }

    /**
     * 执行数据库指令
     * @param command 命令
     */
    public void runCommand(String command) {
        try {
            connection.createStatement().executeUpdate(command);
        } catch (SQLException e) {
            if (!command.contains("CREATE TABLE")) {
                e.printStackTrace();
            }
        }
    }

}
