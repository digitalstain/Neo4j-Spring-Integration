package org.neo4j.integration.spring;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

import org.junit.Test;
import org.neo4j.kernel.Config;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.objectweb.jotm.Current;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.jta.ManagedTransactionAdapter;

@ContextConfiguration(locations={"classpath:spring-jotm-tx-test-context.xml"})
public class JOTMIntegrationTest extends BaseTMIntegrationTest
{
    @Override
    protected String getConfigName()
    {
        return "classpath:spring-jotm-tx-test-context.xml";
    }

    @Test
    public void testLoadConfig() throws SystemException, NotSupportedException
    {
        Current current = getContext().getBean( "jotm", Current.class );
        JtaTransactionManager tm = getContext().getBean( "JtaTransactionManager",
                JtaTransactionManager.class );
        /*
        Transaction transaction = tm.createTransaction( "jotm", 1000 );
        assertEquals( ManagedTransactionAdapter.class, transaction.getClass() );
        assertEquals(
                Current.class,
                ( (ManagedTransactionAdapter) transaction ).getTransactionManager().getClass() );
         */
        Map<Object, Object> config = ( (EmbeddedGraphDatabase) gds ).getConfig().getParams();
        assertEquals( "spring-jta",
                config.get( Config.TXMANAGER_IMPLEMENTATION ) );

    }
}
