/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.exporter;

import com.liferay.style.book.model.StyleBookEntry;

import java.io.File;

import java.util.List;

import javax.portlet.PortletException;

/**
 * @author Anderson Luiz
 */
public interface StyleBookEntryExporter {

	public File exportStyleBookEntries(List<StyleBookEntry> styleBookEntries)
		throws PortletException;

}