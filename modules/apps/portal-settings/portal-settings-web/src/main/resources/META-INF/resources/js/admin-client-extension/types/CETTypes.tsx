/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type ILoadTypeOptions = 'default' | 'async' | 'defer';

export type IScriptLocationOptions = string;

export interface IGlobalCETCheckbox {
	value: boolean;
	onChange: any;
}

export interface IAddExtensionButton {
	addGlobalJSCET: (scriptLocation: IScriptLocationOptions) => unknown;
	isReadOnly: boolean;
	portletNamespace: string;
}

export interface IGlobalJSCET {
	cetExternalReferenceCode: string;
	inherited: boolean;
	inheritedLabel: string;
	loadType?: ILoadTypeOptions;
	name: string;
	restricted: boolean;
	scriptElementAttributesJSON?: string;
	scriptLocation?: IScriptLocationOptions;
}

export interface IExtensionRowProps {
	deleteGlobalJSCET: (globalJSCET: IGlobalJSCET) => unknown;
	globalJSCET: IGlobalJSCET;
	handleRestrictedToAdmin: (restricted: boolean) => void;
	order: number;
	portletNamespace: string;
	restrictedValue: boolean;
	updateGlobalJSCET: <T extends keyof IGlobalJSCET>(
		globalJSCET: IGlobalJSCET,
		propName: T,
		value: IGlobalJSCET[T]
	) => unknown;
}

export interface IGlobalJSCETGroup {
	items: Array<{
		globalJSCET: IGlobalJSCET;
		order: number;
		restricted: boolean;
	}>;
	scriptLocation: IScriptLocationOptions;
}

export interface IProps {
	globalJSCETSelectorURL: string;
	globalJSCETs: IGlobalJSCET[];
	isReadOnly: boolean;
	portletNamespace: string;
	selectGlobalJSCETsEventName: string;
}
