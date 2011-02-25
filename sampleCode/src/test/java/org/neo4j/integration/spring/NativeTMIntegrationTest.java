package org.neo4j.integration.spring;

import java.util.LinkedList;

import javax.transaction.xa.XAResource;

import org.junit.Before;

import com.springsource.transaction.core.TransactionService;

/**
 * Spring source's native transaction manager specific test case.
 * 
 * @author Chris Gioran
 */
public class NativeTMIntegrationTest extends BaseTMIntegrationTest {
	/**
	 * Required call to recover since regardless of whether there is pending
	 * recovery Spring's txm won't start serving requests.
	 */
	@Before
	public void recover() {
		ctx.getBean("transactionService", TransactionService.class).recover(
				new LinkedList<XAResource>());
	}

	@Override
	protected String getConfigName() {
		return "classpath:spring-native-tx-test-context.xml";
	}
}
