/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert, {DisplayType} from '@clayui/alert';
import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {Text} from '@clayui/core';
import {ClayInput} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import PropTypes from 'prop-types';
import React, {useEffect, useRef, useState} from 'react';

const getButtonSideLabel = (fileName: string, isValidatingJSON: boolean) => {
	if (isValidatingJSON) {
		return Liferay.Language.get('validating-json');
	}

	if (fileName) {
		return fileName;
	}

	return Liferay.Language.get('no-json-selected');
};

const MALFORMED_JSON = '{]/';

const EMPTY_FEEDBACK: {displayType: DisplayType; message: string} = {
	displayType: 'info',
	message: '',
};

const FileEntryPicker = ({
	companyId,
	externalReferenceCode,
	frontendTokenDefinition,
	frontendTokenDefinitionFileName,
	namespace,
}) => {
	const inputId = `${namespace}file`;

	const [feedback, setFeedback] = useState(EMPTY_FEEDBACK);
	const [fileInputValue, setFileInputValue] = useState('');
	const [fileName, setFileName] = useState(frontendTokenDefinitionFileName);
	const [isValidatingJSON, setIsValidatingJSON] = useState(false);

	const selectFileButtonRef = useRef<HTMLButtonElement>();
	const frontendTokenDefinitionRef = useRef<HTMLTextAreaElement>();
	const inputFileRef = useRef();

	const setFrontendTokenDefinitionString = (value: string) => {
		if (frontendTokenDefinitionRef.current) {
			frontendTokenDefinitionRef.current.value = value;
		}
	};

	const disableSubmitButton = (disabled: boolean) => {
		const submitButton = document.getElementById(
			namespace + 'edit_client_extension_entry_submit_button'
		) as HTMLButtonElement;

		if (submitButton) {
			if (disabled) {
				submitButton.classList.add('disabled');
			}
			else {
				submitButton.classList.remove('disabled');
			}
			submitButton.disabled = disabled;
		}
	};

	const clear = () => {
		setFileInputValue('');
		setFileName('');
		setFeedback(EMPTY_FEEDBACK);

		setFrontendTokenDefinitionString('');

		selectFileButtonRef.current?.focus();
	};

	const onInputChange = async (event) => {
		const target = event.target as HTMLInputElement;
		const filePath = target.value;

		const fileName = filePath.replace(/^.*[\\]/, '');

		if (!fileName.endsWith('.json')) {
			setFeedback({
				displayType: 'danger',
				message: Liferay.Language.get(
					'the-format-is-not-valid-please-upload-a-valid-json-file'
				),
			});

			setFileName(fileName);

			setFrontendTokenDefinitionString(MALFORMED_JSON);

			return;
		}

		disableSubmitButton(true);
		setIsValidatingJSON(true);
		setFeedback({
			displayType: 'info',
			message: 'json-is-being-uploaded-and-validated',
		});

		readInputFile(target);
	};

	function readInputFile(input: HTMLInputElement) {
		if (input.files === null) {
			return;
		}

		const file = input.files[0];

		const reader = new FileReader();

		reader.onload = async function (event) {
			if (event.target === null || event.target.result === null) {
				return;
			}

			let frontendTokenDefinitionString = event.target?.result as string;

			if (frontendTokenDefinitionString === '') {
				frontendTokenDefinitionString = '{}';
			}

			if (frontendTokenDefinitionRef.current) {
				frontendTokenDefinitionRef.current.value = frontendTokenDefinitionString;
			}

			await handleUpload(file);

			setIsValidatingJSON(false);
		};

		reader.readAsText(file);
	}

	async function handleUpload(file: File) {
		const formData = new FormData();

		formData.append('file', file);

		try {
			const response = await Liferay.Util.fetch(
				'/o/com-liferay-frontend-token-definition-impl/validate-file',
				{
					body: formData,
					method: 'POST',
				}
			);

			const data = await response.json();

			if (response.ok) {
				setFeedback({
					displayType: 'success',
					message: data.message,
				});
			}
			else {
				setFeedback({
					displayType: 'danger',
					message: data.message,
				});

				setFrontendTokenDefinitionString(MALFORMED_JSON);
			}
		}
		catch (error) {
			setFeedback({
				displayType: 'danger',
				message: Liferay.Language.get('an-unexpected-error-occurred'),
			});

			setFrontendTokenDefinitionString(MALFORMED_JSON);
		}

		disableSubmitButton(false);

		selectFileButtonRef.current?.focus();
	}

	const getTokensInfo = async () => {
		try {
			const response = await Liferay.Util.fetch(
				'/o/com-liferay-frontend-token-definition-impl/tokens-info',
				{
					body: Liferay.Util.objectToFormData({
						companyId,
						externalReferenceCode,
					}),
					method: 'POST',
				}
			);

			const data = await response.json();

			if (response.ok) {
				setFeedback({
					displayType: 'success',
					message: data.message,
				});
			}
			else {
				setFeedback({
					displayType: 'danger',
					message: data.message,
				});

				setFrontendTokenDefinitionString(MALFORMED_JSON);
			}
		}
		catch (error) {
			setFeedback({
				displayType: 'danger',
				message: Liferay.Language.get('an-unexpected-error-occurred'),
			});

			setFrontendTokenDefinitionString(MALFORMED_JSON);
		}
	};

	const buttonTitle = !fileName
		? Liferay.Language.get('select-json')
		: Liferay.Language.get('replace-json');

	useEffect(() => {
		if (frontendTokenDefinitionRef.current) {
			frontendTokenDefinitionRef.current.value = frontendTokenDefinition;

			if (frontendTokenDefinition && externalReferenceCode) {
				getTokensInfo();
			}
			else {
				clear();
			}
		}

		setFileName(fileName);

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	return (
		<>
			<label
				aria-describedby={namespace + 'description'}
				className="d-block"
				htmlFor={inputId}
				tabIndex={0}
			>
				{Liferay.Language.get('frontend-token-definition-json-upload')}
			</label>

			<Text
				as="p"
				color="secondary"
				id={namespace + 'description'}
				size={3}
			>
				{Liferay.Language.get('frontend-token-definition-is-a-json')}{' '}
				<a href="https://learn.liferay.com/w/dxp/site-building/site-appearance/style-books/developer-guide/style-book-token-definitions">
					{Liferay.Language.get(
						'learn-more-about-frontend-token-definition'
					)}
				</a>
			</Text>

			<ClayInput
				accept=".json"
				className="d-none"
				id={inputId}
				name={inputId}
				onChange={onInputChange}
				ref={inputFileRef}
				type="file"
				value={fileInputValue}
			/>

			<ClayInput
				className="d-none"
				id={`${namespace}frontendTokenDefinition`}
				name={`${namespace}frontendTokenDefinition`}
				ref={frontendTokenDefinitionRef}
				type="textarea"
			/>

			<div className="my-2">
				<ClayButton
					disabled={isValidatingJSON}
					displayType="secondary"
					onClick={() => inputFileRef.current?.click()}
					ref={selectFileButtonRef}
					title={buttonTitle}
				>
					{buttonTitle}
				</ClayButton>

				<ClayInput
					className="d-none"
					id={`${namespace}frontendTokenDefinitionFileName`}
					name={`${namespace}frontendTokenDefinitionFileName`}
					type="text"
					value={fileName}
				/>

				<div className="inline-item">
					<small className="inline-item inline-item-after">
						<strong>
							{getButtonSideLabel(fileName, isValidatingJSON)}
						</strong>
					</small>

					{fileName && (
						<ClayButtonWithIcon
							borderless
							displayType="secondary"
							monospaced
							onClick={clear}
							symbol="times-circle-full"
							title={Liferay.Language.get(
								'remove-file-from-selection'
							)}
						/>
					)}

					{isValidatingJSON && (
						<ClayLoadingIndicator
							className="ml-2"
							displayType="secondary"
							size="sm"
						/>
					)}
				</div>
			</div>

			<ClayAlert
				displayType={feedback.displayType}
				role={null} // todo: add first load
				style={{display: feedback.message ? 'block' : 'none'}}
				title={feedback.message}
				variant="feedback"
			/>
		</>
	);
};

FileEntryPicker.propTypes = {
	companyId: PropTypes.string.isRequired,
	externalReferenceCode: PropTypes.number.isRequired,
	frontendTokenDefinition: PropTypes.string.isRequired,
	frontendTokenDefinitionFileName: PropTypes.string.isRequired,
	namespace: PropTypes.string.isRequired,
};

export default FileEntryPicker;
