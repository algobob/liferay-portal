/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.commerce.application.list.constants.CommercePanelCategoryKeys;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"panel.app.order:Integer=100",
		"panel.category.key=" + CommercePanelCategoryKeys.COMMERCE_STORE_MANAGEMENT
	},
	service = PanelApp.class
)
public class CommerceChannelPanelApp extends BasePanelApp {

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return CPPortletKeys.COMMERCE_CHANNELS;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		return PortletPermissionUtil.contains(
			permissionChecker, _portlet.getPortletId(),
			ActionKeys.ACCESS_IN_CONTROL_PANEL);
	}

	@Reference(
		target = "(javax.portlet.name=" + CPPortletKeys.COMMERCE_CHANNELS + ")"
	)
	private Portlet _portlet;

}