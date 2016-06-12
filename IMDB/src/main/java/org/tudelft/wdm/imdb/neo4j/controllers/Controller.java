package org.tudelft.wdm.imdb.neo4j.controllers;

import org.neo4j.driver.v1.*;

/**
 * @author Tom
 * 
 * Controller for Neo4j movie API
 */
public class Controller {
    
    private static final String ADDRESS = "bolt://localhost";
    private static final String USERNAME = "neo4j";
    private static final String PASSWORD = "1234";
    
    private static Driver driver = null;
    private static Session session = null;
    
    public static void establishConnection(){
        if (session!=null) closeConnection();
        driver = GraphDatabase.driver(ADDRESS, AuthTokens.basic(USERNAME, PASSWORD));
        session = driver.session();
    }
    
    public static void closeConnection(){
        if (session!=null){
            session.close();
            session = null;
            driver.close();
            driver = null;
        }
    }
    
    public static StatementResult query(Statement q){
        if (!isConnected()) establishConnection();
        StatementResult result = session.run(q);
        return result;
    }
    
    public static Transaction beginTransaction(){
        if (!isConnected()) establishConnection();
        return session.beginTransaction();
    }
    
    public static boolean isConnected(){
        return session!=null && session.isOpen();
    }
}
