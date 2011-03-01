package org.neo4j.spring.example.proteins;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.Config;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.graph.neo4j.config.AbstractNeo4jConfiguration;

/**
 * A custom Neo4jConfiguration
 */
@Configuration
public class ProteinsNeo4jConfiguration extends AbstractNeo4jConfiguration
{
    private static String storeDir = "target/neo4j-proteins";

    @Override
    public boolean isUsingCrossStorePersistence() {
        return false;
    }

    // ABKNOTE
    // - @Bean duplicated in overridden parent method. why both places?
    // - destroyMethod does not seem to do anything.
    //@Bean(destroyMethod = "shutdown")
    
//    @Bean 
//    String someBean() {
//    	return "DUMMY";
//    }

    @Override
    public GraphDatabaseService graphDatabaseService() {
        Map<String, String> config = new HashMap<String, String>();
        config.put( Config.TXMANAGER_IMPLEMENTATION, "spring-jta" );
        return new EmbeddedGraphDatabase( storeDir, config );
    }

    public String getStoreDir()
    {
        return storeDir;
    }
}

