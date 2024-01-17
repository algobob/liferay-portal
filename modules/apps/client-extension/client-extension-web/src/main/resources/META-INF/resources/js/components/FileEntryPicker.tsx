/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {Text} from '@clayui/core';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {sub} from 'frontend-js-web';
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

const FileEntryPicker = ({
	frontendTokenDefinition,
	frontendTokenDefinitionFileName,
	namespace,
}) => {
	const inputId = `${namespace}file`;

	const [feedback, setFeedback] = useState({
		hasError: false,
		message: '',
	});
	const [fileInputValue, setFileInputValue] = useState('');
	const [fileName, setFileName] = useState(frontendTokenDefinitionFileName);
	const [isValidatingJSON, setIsValidatingJSON] = useState(false);

	const frontendTokenDefinitionRef = useRef<HTMLTextAreaElement>();
	const inputFileRef = useRef();

	const processFrontendTokenDefinition = (frontendTokenDefinition: JSON) => {
		const numOfCategories = Object.keys(frontendTokenDefinition).length;

		const values = Object.values(frontendTokenDefinition);

		const numOfSets = values.reduce(
			(acc, frontendTokenCategories) =>
				acc + frontendTokenCategories.length,
			0
		);

		let numOfTokens: number = 0;

		values.forEach((frontendTokenCategories) => {
			numOfTokens += frontendTokenCategories.reduce(
				(acc: number, {frontendTokenSets}) => {
					return (
						acc +
						frontendTokenSets.reduce(
							(acc: number, {frontendTokens}) => {
								return acc + frontendTokens.length;
							},
							0
						)
					);
				},
				0
			);
		});

		setFeedback({
			hasError: false,
			message: sub(
				Liferay.Language.get(
					'json-uploaded-contributing-x-token-categories-x-token-sets-and-x-tokens'
				),
				numOfCategories,
				numOfSets,
				numOfTokens
			),
		});
	};

	const onInputChange = ({target}) => {
		setIsValidatingJSON(true);

		const reader = new FileReader();

		const filePath = target.value;

		reader.onload = function (event) {
			if (event.target === null || event.target.result === null) {
				return;
			}

			try {
				const frontendTokenDefinitionString = event.target
					?.result as string;

				const jsonObj = JSON.parse(frontendTokenDefinitionString);

				setFileName(filePath.replace(/^.*[\\]/, ''));

				if (frontendTokenDefinitionRef.current) {
					frontendTokenDefinitionRef.current.value = frontendTokenDefinitionString;
				}

				processFrontendTokenDefinition(jsonObj);
			}
			catch (error) {
				setFeedback({
					hasError: true,
					message: Liferay.Language.get(
						'the-format-is-not-valid-please-upload-a-valid-json-file'
					),
				});
			}

			setIsValidatingJSON(false);
		};

		reader.readAsText(target.files[0]);
	};

	const removeFile = () => {
		setFileInputValue('');
		setFileName('');
		setFeedback({hasError: false, message: ''});

		if (frontendTokenDefinitionRef.current) {
			frontendTokenDefinitionRef.current.value = '';
		}
	};

	const buttonTitle = !fileName
		? Liferay.Language.get('select-json')
		: Liferay.Language.get('replace-json');

	useEffect(() => {
		if (frontendTokenDefinitionRef.current) {
			frontendTokenDefinitionRef.current.value = frontendTokenDefinition;

			if (frontendTokenDefinition) {
				processFrontendTokenDefinition(
					JSON.parse(frontendTokenDefinition)
				);
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
							onClick={removeFile}
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

			<ClayForm.FeedbackGroup
				className={feedback.hasError ? 'has-error' : 'has-success'}
			>
				<ClayForm.FeedbackItem role="status">
					{feedback.message && (
						<>
							<ClayIcon
								className="mr-2"
								symbol={
									feedback.hasError
										? 'exclamation-full'
										: 'check-circle-full'
								}
							/>

							{feedback.message}
						</>
					)}
				</ClayForm.FeedbackItem>
			</ClayForm.FeedbackGroup>
		</>
	);
};

FileEntryPicker.propTypes = {
	frontendTokenDefinition: PropTypes.string.isRequired,
	frontendTokenDefinitionFileName: PropTypes.string.isRequired,
	namespace: PropTypes.string.isRequired,
};

export default FileEntryPicker;
