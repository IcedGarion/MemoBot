package it.stanzino.memobot.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import it.stanzino.memobot.configurations.PropertiesManager;

public class ChatDb
{
    private Connection connect()
    {
        Connection conn = null;
        // db parameters
        String url = "jdbc:sqlite:" + PropertiesManager.DATABASE_PATH;
        // create a connection to the database
        try
        {
            conn = DriverManager.getConnection(url);
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
           
        return conn;
    }
    
    public String query(String sql) throws SQLException
    {
    	String ret = "";
    	
        Connection conn = connect();
        Statement stmt  = conn.createStatement();
        ResultSet rs    = stmt.executeQuery(sql);
                	
        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();    
            
        // msg(date, sender, txt)
        // loop through the result set
        while (rs.next()) 
        {
        	for (int i = 1; i <= columnCount; i++)
                ret += rs.getString(i) + " - ";
        	ret = ret.substring(0, ret.length()-3) + "\n";
        }
        
        return ret;
    }
}