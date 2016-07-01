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
    
    /** 
     * Simple variable used to force the connection to stay open.
     * This is to prevent redundant overhead from repeatedly opening and closing
     * Neo4j connections by sub-methods if the upper method knows it will need
     * the connection once more afterwards.
     */
    private static boolean keepOpen = false;
    
    /**
     * Creates a connection to the local Neo4j database if not already connected.
     */
    public static void establishConnection(){
        if (isConnected()) return;
        else closeConnection();
        driver = GraphDatabase.driver(ADDRESS, AuthTokens.basic(USERNAME, PASSWORD));
        session = driver.session();
    }
    
    /**
     * Closes the connection unless the keepOpen variable is set.
     */
    public static void closeConnection(){
        if (session!=null && (!keepOpen || !session.isOpen())){
            session.close();
            session = null;
            driver.close();
            driver = null;
        }
    }
    
    /**
     * Performs the given query and results the StatementResult.
     * This method will automatically connect to the database if required.
     * @param q Query to perform.
     * @return 
     */
    public static StatementResult query(Statement q){
        establishConnection();
        StatementResult result = session.run(q);
        return result;
    }
    
    /**
     * Tells this controller to not close the Neo4j connection even if told to do so.
     */
    public static void keepOpen(){
        establishConnection();
        keepOpen = true;
    }
    
    /**
     * Overrides the keepOpen variable and closes the session.
     */
    public static void forceClose(){
        keepOpen = false;
        closeConnection();
    }
    
    /**
     * Quick check to ensure the connection is set and open.
     * @return 
     */
    public static boolean isConnected(){
        return session!=null && session.isOpen();
    }
}
