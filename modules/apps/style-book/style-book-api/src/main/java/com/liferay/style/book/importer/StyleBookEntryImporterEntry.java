/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.importer;

import com.liferay.style.book.model.StyleBookEntry;

/**
 * @author Anderson Luiz
 */
public class StyleBookEntryImporterEntry {

	public StyleBookEntryImporterEntry(
		String name, Status status, StyleBookEntry styleBookEntry) {

		_name = name;
		_status = status;
		_styleBookEntry = styleBookEntry;
	}

	public StyleBookEntryImporterEntry(
		String errorMessage, String name, Status status) {

		_errorMessage = errorMessage;
		_name = name;
		_status = status;
	}

	public String getErrorMessage() {
		return _errorMessage;
	}

	public String getName() {
		return _name;
	}

	public Status getStatus() {
		return _status;
	}

	public StyleBookEntry getStyleBookEntry() {
		return _styleBookEntry;
	}

	public enum Status {

		IMPORTED("imported"), INVALID("invalid");

		public String getLabel() {
			return _label;
		}

		private Status(String label) {
			_label = label;
		}

		private final String _label;

	}

	private String _errorMessage;
	private final String _name;
	private final Status _status;
	private StyleBookEntry _styleBookEntry;

}