package org.neo4j.integration.spring;

/**
 * @author mh
 * @since 21.02.11
 */

import java.io.File;
import java.io.IOException;

import javax.transaction.TransactionManager;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import com.springsource.transaction.alarm.internal.StandardAlarmManager;
import com.springsource.transaction.core.TransactionService;
import com.springsource.transaction.core.TransactionServiceConfiguration;
import com.springsource.transaction.core.internal.StandardTransactionService;
import com.springsource.transaction.log.TransactionLogConfiguration;
import com.springsource.transaction.log.howl.HOWLTransactionLog;

public class SpringTSFactoryBean implements FactoryBean<TransactionService>,
        DisposableBean
{
    private TransactionService ts;

    public SpringTSFactoryBean()
    {
        create();
    }

    private void create()
    {
        TransactionServiceConfiguration configuration = new TransactionServiceConfiguration(
                new HOWLTransactionLog( new TransactionLogConfiguration(
                        "nativeLog" ) ), 1, 3, null, null, 30 );
//        StandardAlarmManager alarmManager = new StandardAlarmManager();
//        alarmManager.start();
        ts = new StandardTransactionService( configuration );
    }

    @Override
    public void destroy() throws Exception
    {
        ts.shutdown();
    }

    @Override
    public TransactionService getObject() throws Exception
    {
        return ts;
    }

    @Override
    public Class<?> getObjectType()
    {
        return ts.getClass();
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

}
