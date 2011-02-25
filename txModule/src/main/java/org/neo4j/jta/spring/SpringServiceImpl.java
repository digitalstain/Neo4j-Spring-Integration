/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.neo4j.jta.spring;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.neo4j.kernel.impl.transaction.AbstractTransactionManager;
import org.neo4j.kernel.impl.transaction.XaDataSourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * @author Chris Gioran
 */
@Configurable
class SpringServiceImpl extends AbstractTransactionManager
{
    @Autowired
    private JtaTransactionManager jtaTransactionManager;

    private TransactionManager delegate;

    SpringServiceImpl()
    {
    }

    @Override
    public void init( XaDataSourceManager xaDsManager )
    {
        delegate = jtaTransactionManager.getTransactionManager();
    }

    public void begin() throws NotSupportedException, SystemException
    {
        delegate.begin();
    }

    public void commit() throws RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException,
            IllegalStateException, SystemException
    {
        delegate.commit();
    }

    public int getStatus() throws SystemException
    {
        return delegate.getStatus();
    }

    public Transaction getTransaction() throws SystemException
    {
        return delegate.getTransaction();
    }

    public void resume( Transaction tobj ) throws InvalidTransactionException,
            IllegalStateException, SystemException
    {
        delegate.resume( tobj );
    }

    public void rollback() throws IllegalStateException, SecurityException,
            SystemException
    {
        delegate.rollback();
    }

    public void setRollbackOnly() throws IllegalStateException, SystemException
    {
        delegate.setRollbackOnly();
    }

    public void setTransactionTimeout( int seconds ) throws SystemException
    {
        delegate.setTransactionTimeout( seconds );
    }

    public Transaction suspend() throws SystemException
    {
        return delegate.suspend();
    }

    @Override
    public void stop()
    {
        // Currently a no-op
    }
}
