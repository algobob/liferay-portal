package com.liferay.client.extension.internal.type.deployer;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.service.ClientExtensionEntryService;
import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.deployer.CETDeployer;
import com.liferay.client.extension.type.factory.CETFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.ServiceRegistration;

/**
 * @author Anderson Luiz
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class CETDeployerImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws PortalException {
		_clientExtensionEntry =
			clientExtensionEntryLocalService.addClientExtensionEntry(
				"", "",
				HashMapBuilder.put(
					LocaleUtil.getDefault(), "abc"
				).build(),
				"", "", ClientExtensionEntryConstants.TYPE_THEME_CSS, "");
	}

	@After
	public void tearDown() throws PortalException {
		clientExtensionEntryLocalService.deleteClientExtensionEntry(
			_clientExtensionEntry.getClientExtensionEntryId());
	}

	@Test
	public void testRegisterThemeCSSCETServiceWhenDeployed()
		throws PortalException {

		CET cet = cetFactory.create(_clientExtensionEntry, false);

		List<ServiceRegistration<?>> serviceRegistrations = cetDeployer.deploy(
			cet);

		Assert.assertTrue(serviceRegistrations.size() == 1);
	}

	private ClientExtensionEntry _clientExtensionEntry;

	@Inject
	private CETDeployer cetDeployer;

	@Inject
	private CETFactory cetFactory;

	@Inject
	private ClientExtensionEntryService clientExtensionEntryLocalService;

}