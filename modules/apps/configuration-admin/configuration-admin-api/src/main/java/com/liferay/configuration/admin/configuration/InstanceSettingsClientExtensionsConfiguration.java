/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Anderson Luiz
 */
@ExtendedObjectClassDefinition(
	category = "instance-configuration", generateUI = true,
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.configuration.admin.configuration.InstanceSettingsClientExtensionsConfiguration",
	localization = "content/Language",
	name = "instance-settings-client-extensions-configuration-name"
)
public interface InstanceSettingsClientExtensionsConfiguration {

	@Meta.AD(
		deflt = "", name = "js-client-extensions-external-reference-codes",
		required = false
	)
	public String[] getJSClientExtensionsExternalReferenceCodes();

}