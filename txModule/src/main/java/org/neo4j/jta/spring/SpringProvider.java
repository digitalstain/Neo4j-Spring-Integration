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

import org.neo4j.helpers.Service;
import org.neo4j.kernel.impl.core.KernelPanicEventGenerator;
import org.neo4j.kernel.impl.transaction.AbstractTransactionManager;
import org.neo4j.kernel.impl.transaction.TransactionManagerProvider;
import org.neo4j.kernel.impl.transaction.TxFinishHook;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Service.Implementation( TransactionManagerProvider.class )
public class SpringProvider extends TransactionManagerProvider
{
    public SpringProvider()
    {
        super( "spring-jta" );
    }

    @Override
    protected AbstractTransactionManager loadTransactionManager( String txLogDir,
            KernelPanicEventGenerator kpe, TxFinishHook rollbackHook )
    {
        return new SpringServiceImpl();
    }
}
