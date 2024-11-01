package com.liferay.style.book.exporter;

import com.liferay.style.book.model.StyleBookEntry;

import javax.portlet.PortletException;
import java.io.File;
import java.util.List;

public interface StyleBookEntryExporter {
	File exportStyleBookEntries(List<StyleBookEntry> styleBookEntries)
		throws PortletException;
}
