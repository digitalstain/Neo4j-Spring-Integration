package org.neo4j.integration.spring;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.XADataSource;
import javax.transaction.Transaction;
import javax.transaction.UserTransaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * Base class that outlines the expected behavior of every transaction manager.
 * Subclasses are expected to override the <code>getConfigName()</code> to return
 * a String with the name of their configuration file and provide any
 * implementation specific configuration via <code>@Before</code> and
 * <code>@After</code> methods.
 * 
 * @author Chris Gioran
 */
public abstract class BaseTMIntegrationTest {

	protected ApplicationContext ctx;

	protected GraphDatabaseService gds;
	protected XADataSource ds;
	protected JtaTransactionManager tm;

	public BaseTMIntegrationTest() {
		super();
	}

	/**
	 * Hook for testing the loaded configuration. Can be no-op.
	 */
	@Test
	public void testLoadConfig() throws Exception
	{}

	@Before
	public void setUp() throws Exception {
		ctx = new ClassPathXmlApplicationContext(getConfigName());
		tm = ctx.getBean("jtaTransactionManager", JtaTransactionManager.class);
		gds = ctx.getBean(GraphDatabaseService.class);
		ds = ctx.getBean("dataSource", XADataSource.class);
//		ds.getXAConnection().getConnection()
//				.prepareStatement("delete from Node").execute();
	}

	@After
	public void tearDown() throws Exception {
		gds.shutdown();
	}

	/**
	 * Tests just the graph database with some indexing on top.
	 * Writes a node into the database, indexes it, commits and
	 * then reads it back, once without a transaction running and
	 * once with. 
	 */
	@Test
	public void testIndexDependencies() throws Exception {
		UserTransaction tx = tm.getUserTransaction();
		tx.begin();
		Node node = null;
		Index<Node> index = gds.index().forNodes("node");
		IndexHits<Node> indexHits = index.get("field", "value");
		node = gds.createNode();
		assertNotNull(node);
		assertEquals(false, indexHits.hasNext());
		tx.commit();
		Node readBackOutsideOfTx = gds.getNodeById(node.getId());
		assertEquals(node, readBackOutsideOfTx);
		
		tx = tm.getUserTransaction();
		tx.begin();
		index.add(node, "field", "value"+node.getId());
		tx.commit();

		tx = tm.getUserTransaction();
		tx.begin();
		Node readBackInsideOfTx = gds.getNodeById(node.getId());
		assertEquals(node, readBackInsideOfTx);
		indexHits = gds.index().forNodes("node").get("field", "value"+node.getId());
		assertTrue(indexHits.hasNext());
		assertEquals(node, indexHits.next());
		assertFalse(indexHits.hasNext());
		tx.commit();
	}

	/**
	 * Reads in the id of every node in the database, outside
	 * of any transaction. 
	 */
	@Test
	public void testReadOutsideTx() {
		for (Node n : gds.getAllNodes()) {
			n.getId();
		}
	}

	@Test
	@Ignore
	public void testJustMySQL() throws Exception {
		ds.getXAConnection().getConnection()
				.prepareStatement("select * from Node").execute();
	}

	@Test
	@Ignore
	public void testBothSimpleInsert() throws Exception {
		UserTransaction tx = tm.getUserTransaction();
		try {
			tx.begin();
			Node n = gds.createNode();
			PreparedStatement stmt = ds.getXAConnection().getConnection()
					.prepareStatement("insert into Node values(?)");
			stmt.setLong(1, n.getId());
			stmt.execute();

			gds.getNodeById(n.getId());
			stmt = ds
					.getXAConnection()
					.getConnection()
					.prepareStatement(
							"select count(*) from Node where NodeId=?");
			stmt.setLong(1, n.getId());
			stmt.execute();
			ResultSet rs = stmt.getResultSet();
			rs.next();

			assertEquals(1, rs.getInt(1));
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			throw e;
		}
	}

	@Test
	@Ignore
	public void testBothSimpleInsertRollback() throws Exception {
		tm.getTransactionManager().begin();
		Transaction tx = tm.getTransactionManager().getTransaction();

		Node n = gds.createNode();
		Connection conn = ds.getXAConnection().getConnection();
		conn.setAutoCommit(false);
		PreparedStatement stmt = conn
				.prepareStatement("insert into Node values(?)");
		stmt.setLong(1, n.getId());
		stmt.execute();
		conn.rollback();

		tx.rollback();

		boolean exceptionThrown = false;
		try {
			gds.getNodeById(n.getId());
		} catch (NotFoundException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		stmt = conn
				.prepareStatement("select count(*) from Node where NodeId=?");
		stmt.setLong(1, n.getId());
		stmt.execute();
		ResultSet rs = stmt.getResultSet();
		rs.next();
		assertEquals(0, rs.getInt(1));
	}

	/**
	 * @return Returns the filename of the configuration file for the test.
	 */
	protected abstract String getConfigName();

}
