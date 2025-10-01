/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../utils/getRandomString';
import getContainerDefinition from '../../layout-content-page-editor-web/main/utils/getContainerDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
);

test('Persist previous color reference when populating wrong digit characters',
		{
			tag: '@LPS-141568',
		},
 async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
	}) => {

  let layout;
  let containerId;

  await test.step('Add a Container to a content page', async () => {
		containerId = getRandomString();

		layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getContainerDefinition({id: containerId}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});
  });

  await test.step('Change the Background Color to Info', async () => {
		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Background Color',
			fragmentId: containerId,
			tab: 'Styles',
			value: 'Info',
			valueFromStylebook: true,
		});

		expect(
			await pageEditorPage.getFragmentStyle({
				fragmentId: containerId,
				style: 'backgroundColor',
			})
		).toBe('rgb(46, 90, 172)');
  });

  await test.step('Detach the linked token', async () => {
    await pageEditorPage.detachStyleButton.click();
  });

    await test.step('Assert previous color is referenced when populated with wrong chars', async () => {
		await pageEditorPage.backgroundColorInput.fill(getRandomString())
		await page.waitForTimeout(1000);
		await pageEditorPage.backgroundColorInput.blur()

		expect(
			await pageEditorPage.getFragmentStyle({
				fragmentId: containerId,
				style: 'backgroundColor',
			})
		).toBe('rgb(46, 90, 172)');
	});
});
