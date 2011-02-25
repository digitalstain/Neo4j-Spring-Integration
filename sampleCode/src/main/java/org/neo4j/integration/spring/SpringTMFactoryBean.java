package org.neo4j.spring_tx;

/**
 * @author mh
 * @since 21.02.11
 */

import javax.transaction.TransactionManager;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import com.springsource.transaction.core.TransactionService;

public class SpringTMFactoryBean implements FactoryBean<TransactionManager>,
        DisposableBean
{
    private TransactionService transactionService;

    public TransactionService getTransactionService()
    {
        return transactionService;
    }
    
    public void setTransactionService( TransactionService transactionService )
    {
        this.transactionService = transactionService;
    }
    
    public SpringTMFactoryBean()
    {
    }

    @Override
    public void destroy() throws Exception
    {
    }

    @Override
    public TransactionManager getObject() throws Exception
    {
        return transactionService.getTransactionManager();
    }

    @Override
    public Class<?> getObjectType()
    {
        return transactionService.getTransactionManager().getClass();
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

}
