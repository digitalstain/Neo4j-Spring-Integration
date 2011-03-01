package org.neo4j.spring.example.proteins;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.graph.neo4j.finder.FinderFactory;
import org.springframework.data.graph.neo4j.finder.NodeFinder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.jta.JtaTransactionManager;

@Repository
public class ProteinClassRepository
{
    @Autowired
    private FinderFactory finderFactory;

    @Autowired
    private XADataSource dataSource;

    @Autowired
    private JtaTransactionManager tm;

    public Collection<ProteinClass> createProteinClasses() throws Exception
    {
        tm.getTransactionManager().begin();
        Transaction tx = tm.getTransactionManager().getTransaction();

        ArrayList<ProteinClass> proteinClasses = new ArrayList<ProteinClass>();
        try
        {
            XAConnection xaConn = dataSource.getXAConnection();
            Connection conn = xaConn.getConnection();
            tx.enlistResource( xaConn.getXAResource() );
            PreparedStatement insertStmt = conn.prepareStatement(
                    "insert into Node values(?)" );
            ProteinClass root1 = new ProteinClass( "root1" );
            proteinClasses.add( root1 );
            insertStmt.setLong( 1, root1.getUnderlyingState().getId() );
            insertStmt.execute();

            ProteinClass root2 = new ProteinClass( "root2" );
            proteinClasses.add( root2 );

            insertStmt.setLong( 1, root2.getUnderlyingState().getId() );
            insertStmt.execute();

            ProteinClass firstLevel1 = new ProteinClass( "firstLevel1" );
            root1.getSubclasses().add( firstLevel1 );
            firstLevel1.setSuperClass( root1 );
            proteinClasses.add( firstLevel1 );

            ProteinClass firstLevel2 = new ProteinClass( "firstLevel2" );
            root1.getSubclasses().add( firstLevel2 );
            firstLevel2.setSuperClass( root1 );
            proteinClasses.add( firstLevel2 );

            ProteinClass firstLevel3 = new ProteinClass( "firstLevel3" );
            root2.getSubclasses().add( firstLevel3 );
            firstLevel3.setSuperClass( root2 );
            proteinClasses.add( firstLevel3 );

            ProteinClass leaf1 = new ProteinClass( "leaf1" );
            firstLevel1.getSubclasses().add( leaf1 );
            leaf1.setSuperClass( firstLevel1 );
            proteinClasses.add( leaf1 );

            ProteinClass leaf2 = new ProteinClass( "leaf2" );
            firstLevel1.getSubclasses().add( leaf2 );
            leaf2.setSuperClass( firstLevel1 );
            proteinClasses.add( leaf2 );

            ProteinClass leaf3 = new ProteinClass( "leaf3" );
            firstLevel1.getSubclasses().add( leaf3 );
            leaf3.setSuperClass( firstLevel1 );
            proteinClasses.add( leaf3 );

            ProteinClass leaf4 = new ProteinClass( "leaf4" );
            firstLevel2.getSubclasses().add( leaf4 );
            leaf4.setSuperClass( firstLevel1 );
            proteinClasses.add( leaf4 );

            tx.commit();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            tx.rollback();
            throw new RuntimeException( e );
        }
        return proteinClasses;

    }

    public ProteinClass findProteinClassIdentifiedBy( long id )
    {
        return finder().findById( id );
    }

    private NodeFinder<ProteinClass> finder()
    {
        return finderFactory.createNodeEntityFinder( ProteinClass.class );
    }

    public Iterable<ProteinClass> findAllProteinClasses()
    {
        return finder().findAll();
    }

    public long countProteinClasses()
    {
        return finder().count();
    }

    public ProteinClass findProteinClassNamed( String name )
    {
        return finder().findByPropertyValue( null, "name", name );
    }
}
