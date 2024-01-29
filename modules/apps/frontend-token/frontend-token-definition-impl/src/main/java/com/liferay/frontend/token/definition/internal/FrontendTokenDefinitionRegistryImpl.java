/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.internal;

import com.liferay.client.extension.type.ThemeCSSCET;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.util.Portal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;

/**
 * @author Iván Zaera
 */
@Component(service = FrontendTokenDefinitionRegistry.class)
public class FrontendTokenDefinitionRegistryImpl
	implements FrontendTokenDefinitionRegistry {

	@Override
	public FrontendTokenDefinition getFrontendTokenDefinition(
		long companyId, String externalReferenceCode, String themeId) {

		if (FeatureFlagManagerUtil.isEnabled("LPD-10773") &&
			externalReferenceCode != null) {

			FrontendTokenDefinition frontendTokenDefinition =
				_frontendTokenDefinitionManager.getFrontendTokenDefinition(
					companyId, externalReferenceCode);

			if (frontendTokenDefinition != null) {
				return frontendTokenDefinition;
			}
		}

		return  _frontendTokenDefinitionManager.getFrontendTokenDefinition(_openBundleTracker, themeId);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_frontendTokenDefinitionManager = new FrontendTokenDefinitionManager(
				jsonFactory, new DCLSingleton<>(), new ConcurrentHashMap<>(),
				new ConcurrentHashMap<>());

		_bundleTracker = new BundleTracker<>(
			bundleContext, Bundle.ACTIVE,
			new ThemeBundleTrackerCustomizer(
				_frontendTokenDefinitionManager, portal));

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ThemeCSSCET.class, "external.reference.code",
			new ThemeClientExtensionServiceTracker(
				bundleContext, _frontendTokenDefinitionManager));
	}

	@Deactivate
	protected void deactivate() {
		_bundleTracker.close();

		_serviceTrackerMap.close();
	}

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected Portal portal;

	private BundleTracker<FrontendTokenDefinitionImpl> _bundleTracker;
	private FrontendTokenDefinitionManager _frontendTokenDefinitionManager;
	private ServiceTrackerMap<String, ThemeCSSCET> _serviceTrackerMap;
	private final Runnable _openBundleTracker = () -> _bundleTracker.open();

}