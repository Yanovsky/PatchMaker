package ru.dreamkas.patches;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class Database {
    private final Connection connection;
    private final Session session;

    public Database(Path sshKey, String sshUserName, String userName, String password) throws Exception {
        JSch jsch = new JSch();
        jsch.addIdentity(sshKey.toString());

        session = jsch.getSession(sshUserName, "update.dreamkas.ru", 22);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        int forwardPort = session.setPortForwardingL(1234, "localhost", 5432);

        Properties props = new Properties();
        props.setProperty("user", userName);
        props.setProperty("password", password);
        props.setProperty("ssl", "false");

        connection = DriverManager.getConnection("jdbc:postgresql://localhost:" + forwardPort + "/update", props);
        System.out.println("Database connected successfully");
    }

    public void disconnect() {
        try {
            connection.close();
            session.disconnect();
            System.out.println("Database disconnected successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PatchData> select(String sql) throws Exception {
        List<PatchData> result = new ArrayList<>();
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            result.add(
                new PatchData(
                    rs.getString("from"),
                    rs.getString("to"),
                    rs.getLong("size"),
                    rs.getString("md5"),
                    rs.getString("url"),
                    rs.getString("info")
                )
            );
        }
        rs.close();
        st.close();
        return result;
    }
}
