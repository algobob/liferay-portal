package com.liferay.gradle.plugins.workspace.internal.client.extension;

import com.liferay.gradle.plugins.workspace.internal.util.FileUtil;
import com.liferay.gradle.plugins.workspace.internal.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientExtensionHelperTest {

	private final ClientExtensionHelper _clientExtensionHelper = new ClientExtensionHelper();
	@Test
	public void testIsWildCard() {
		Assert.assertTrue(_clientExtensionHelper.isWildcardValue("my*value/**/*_"));
		Assert.assertFalse(_clientExtensionHelper.isWildcardValue("some/path/to/file"));
	}

	@Test
	public void testDoNotExpandWildcards() {
		Map<String, Object> typeSettings = new HashMap<>();
		typeSettings.put("some-key", "my*value/**/*_");

		assertShouldExpand(false, typeSettings);
	}
	@Test
	public void testExpandWildcards() {
		Map<String, Object> typeSettings = new HashMap<>();
		typeSettings.put("some-key-url", "my*value/**/*_");

		assertShouldExpand(true, typeSettings);
	}

	private void assertShouldExpand(boolean shouldExpand, Map<String, Object> typeSettings) {
		try (
			MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(FileUtil.class);
			MockedStatic<StringUtil> stringUtilMockedStatic = Mockito.mockStatic(StringUtil.class);
		) {
			fileUtilMockedStatic.when(() -> FileUtil.getMatchingPaths(Mockito.any(Path.class), Mockito.anyString())).thenReturn(Mockito.mock(
				List.class));

			stringUtilMockedStatic.when(() -> StringUtil.containsIgnoreCase(Mockito.matches("url"), Mockito.anyString())).thenReturn(true);
			stringUtilMockedStatic.when(() -> StringUtil.containsIgnoreCase(AdditionalMatchers.not(Mockito.matches("url")),
				Mockito.anyString())).thenReturn(false);

			Path path = Mockito.mock(Path.class);

			_clientExtensionHelper.expandWildcards(path, typeSettings);

			if (shouldExpand) {
				fileUtilMockedStatic.verify(
					() -> FileUtil.getMatchingPaths(Mockito.any(Path.class), Mockito.anyString())
				);
			} else {
				fileUtilMockedStatic.verifyNoInteractions();
			}
		}

	}
}