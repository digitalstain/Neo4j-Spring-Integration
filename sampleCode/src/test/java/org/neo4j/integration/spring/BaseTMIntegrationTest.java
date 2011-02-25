package org.neo4j.spring_tx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.XADataSource;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.jta.JtaTransactionManager;

public abstract class BaseTMIntegrationTest
{

    private ApplicationContext ctx;

    public abstract void testLoadConfig() throws SystemException,
            NotSupportedException;

    protected GraphDatabaseService gds;
    protected XADataSource ds;
    protected JtaTransactionManager tm;

    public BaseTMIntegrationTest()
    {
        super();
    }

    @Before
    public void setUp() throws Exception
    {
        ctx = new ClassPathXmlApplicationContext( getConfigName() );
        gds = ctx.getBean( GraphDatabaseService.class );

        ds = ctx.getBean( "dataSource", XADataSource.class );
        tm = ctx.getBean( "JtaTransactionManager", JtaTransactionManager.class );
//        StandardXADataSource xa = (StandardXADataSource) ds;
//        ds.setTransactionManager( tm.getTransactionManager() );
    }

    @After
    public void tearDown() throws Exception
    {
         gds.shutdown();
    }

//    @Test
//    public void testBothSimpleInsertRollback() throws Exception
//    {
//        UserTransaction tx = tm.getUserTransaction();
//        getContext().getBean("TestServices", TestServices.class).testBothSimpleInsertRollback( gds, ds, tx );
//    }

//    @Test
//    @Transactional
//    public void testIndexDependencies() throws Exception
//    {
//        Node node = null;
//        Index<Node> index = gds.index().forNodes( "node" );
//        IndexHits<Node> indexHits = index.get( "field", "value" );
//        node = gds.createNode();
//        assertNotNull( node );
//        assertEquals( false, indexHits.hasNext() );
//        Node readBackOutsideOfTx = gds.getNodeById( node.getId() );
//        assertEquals( node, readBackOutsideOfTx );
//        Node readBackInsideOfTx = gds.getNodeById( node.getId() );
//        assertEquals( node, readBackInsideOfTx );
//    }
//
//    @Test
//    public void testReadOutsideTx() throws Exception
//    {
//        for ( Node n : gds.getAllNodes() )
//        {
//            n.getId();
//        }
//    }
//
//    @Test
//    public void testJustMySQL() throws Exception
//    {
//        // jdbcTemplate.execute( "select * from Node" );
//        ds.getConnection().prepareStatement( "select * from Node" );
//    }
//
//    @Test
//    // @Transactional
//    public void testBothSimpleInsert() throws Exception
//    {
//        UserTransaction tx = tm.getUserTransaction();
//        try
//        {
//            tx.begin();
//            Node n = gds.createNode();
//            // jdbcTemplate.execute( "insert into Node values(" + n.getId() +
//            // ")" );
//            PreparedStatement stmt = DataSourceUtils.getConnection( ds ).prepareStatement(
//                    "insert into Node values(?)" );
//            stmt.setLong( 1, n.getId() );
//            stmt.execute();
//
//            gds.getNodeById( n.getId() );
//            stmt = DataSourceUtils.getConnection( ds ).prepareStatement(
//                    "select count(*) from Node where NodeId=?" );
//            stmt.setLong( 1, n.getId() );
//            stmt.execute();
//            ResultSet rs = stmt.getResultSet();
//            rs.next();
//
//            assertEquals( 1, rs.getInt( 1 ) );
//            tx.commit();
//        }
//        catch ( Exception e )
//        {
//            tx.rollback();
//            throw e;
//        }
//    }
//
    @Test
//     @Transactional
    public void testBothSimpleInsertRollback() throws Exception
    {
        tm.getTransactionManager().begin();
        Transaction tx = tm.getTransactionManager().getTransaction();
        
        Node n = gds.createNode();
        Connection conn = ds.getXAConnection().getConnection();
        conn.setAutoCommit( false );
        tx.enlistResource( ds.getXAConnection().getXAResource() );
        PreparedStatement stmt = conn.prepareStatement(
                "insert into Node values(?)" );
        stmt.setLong( 1, n.getId() );
        stmt.execute();
        conn.rollback();

        // Node n = new TransactionTemplate( tm ).execute( new
        // TransactionCallback<Node>()
        // {
        // @Override
        // public Node doInTransaction( TransactionStatus status )
        // {
        // Node n = null;
        // try
        // {
        // n = gds.createNode();
        // PreparedStatement stmt = ds.getConnection().prepareStatement(
        // "insert into Node values(?)" );
        // stmt.setLong( 1, n.getId() );
        // stmt.execute();
        // }
        // catch ( SQLException e )
        // {
        // e.printStackTrace();
        // try
        // {
        // tx.rollback();
        // }
        // catch ( Exception se )
        // {
        // e.printStackTrace();
        // }
        // }
        // return n;
        // }
        // } );
        tx.rollback();

        boolean exceptionThrown = false;
        try
        {
            gds.getNodeById( n.getId() );
        }
        catch ( NotFoundException e )
        {
            exceptionThrown = true;
        }
        assertTrue( exceptionThrown );
        stmt = conn.prepareStatement(
                "select count(*) from Node where NodeId=?" );
        stmt.setLong( 1, n.getId() );
        stmt.execute();
        ResultSet rs = stmt.getResultSet();
        rs.next();
        assertEquals( 0, rs.getInt( 1 ) );
    }

    protected ApplicationContext getContext()
    {
        return ctx;
    }

    protected abstract String getConfigName();

}
