/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.importer;

import java.io.File;

import java.util.List;

/**
 * @author Anderson Luiz
 */
public interface StyleBookEntryImporter {

	public List<StyleBookEntryImporterImportResultEntry> importStyleBookEntries(
			long userId, long groupId, File file, boolean overwrite)
		throws Exception;

}