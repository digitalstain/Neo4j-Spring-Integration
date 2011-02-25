package org.neo4j.integration.spring;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.neo4j.kernel.Config;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class SpringAtomikosTMIntegrationTest extends BaseTMIntegrationTest {

	@Override
	public void testLoadConfig() throws SystemException, NotSupportedException {
		assertEquals(com.atomikos.icatch.jta.UserTransactionManager.class, tm
				.getTransactionManager().getClass());
		Map<Object, Object> config = ((EmbeddedGraphDatabase) gds).getConfig()
				.getParams();
		assertEquals("spring-jta", config.get(Config.TXMANAGER_IMPLEMENTATION));
	}

	@Override
	protected String getConfigName() {
		return "classpath:spring-atomikos-tx-test-context.xml";
	}
}
