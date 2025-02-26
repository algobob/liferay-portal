/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.model.listener;

import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.site.configuration.manager.MenuAccessConfigurationManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(service = ModelListener.class)
public class RoleModelListener extends BaseModelListener<Role> {

	@Override
	public void onAfterRemove(Role role) throws ModelListenerException {
		if (_executorService == null) {
			return;
		}

		if ((role.getType() == RoleConstants.TYPE_REGULAR) ||
			(role.getType() == RoleConstants.TYPE_SITE)) {

			_executorService.execute(
				() -> {
					try {
						_menuAccessConfigurationManager.
							deleteRoleAccessToControlMenu(role);
					}
					catch (Exception exception) {
						_log.error(exception);
					}
				});
		}
	}

	@Activate
	protected void activate() {
		_executorService = Executors.newFixedThreadPool(10);
	}

	@Deactivate
	protected void deactivate() {
		if (_executorService != null) {
			_executorService.shutdown();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RoleModelListener.class);

	private ExecutorService _executorService;

	@Reference
	private MenuAccessConfigurationManager _menuAccessConfigurationManager;

}