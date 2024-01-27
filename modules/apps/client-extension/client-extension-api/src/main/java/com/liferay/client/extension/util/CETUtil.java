/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.util;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntryRel;
import com.liferay.client.extension.service.ClientExtensionEntryRelLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Binh Tran
 */
public class CETUtil {

	public static String getThemeCSSCETExternalReferenceCode(
		LayoutSet layoutSet) {

		ClientExtensionEntryRel clientExtensionEntryRel =
			ClientExtensionEntryRelLocalServiceUtil.
				fetchClientExtensionEntryRel(
					PortalUtil.getClassNameId(LayoutSet.class),
					layoutSet.getLayoutSetId(),
					ClientExtensionEntryConstants.TYPE_THEME_CSS);

		if (clientExtensionEntryRel != null) {
			return clientExtensionEntryRel.getCETExternalReferenceCode();
		}

		return null;
	}

	public static String normalizeExternalReferenceCodeForPortletId(
		String externalReferenceCode) {

		if (Validator.isNotNull(externalReferenceCode)) {
			return externalReferenceCode.replaceAll(
				"\\W", StringPool.UNDERLINE);
		}

		return externalReferenceCode;
	}

}