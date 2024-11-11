package com.liferay.layout.content.page.editor.web.internal.manager;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.util.comparator.StyleBookEntryNameComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = StyleBookManager.class)
public class StyleBookManager {

	public List<StyleBookEntry> getStyleBooks(long groupId) {
		return _styleBookEntryLocalService.getStyleBookEntries(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			StyleBookEntryNameComparator.getInstance(true));
	}

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}