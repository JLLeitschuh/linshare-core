/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */

package org.linagora.linshare.core.business.service.impl;

import java.util.Date;
import java.util.Set;

import org.linagora.linshare.core.business.service.UploadRequestEntryUrlBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestEntryUrl;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UploadRequestEntryUrlRepository;
import org.linagora.linshare.core.service.PasswordService;
import org.linagora.linshare.core.utils.HashUtils;

public class UploadRequestEntryUrlBusinessServiceImpl implements
		UploadRequestEntryUrlBusinessService {

	private final UploadRequestEntryUrlRepository uploadRequestEntryUrlRepository;

	private final PasswordService passwordService;

	private final AccountRepository<Account> accountRepository;

	private final String path;

	public UploadRequestEntryUrlBusinessServiceImpl(
			final UploadRequestEntryUrlRepository uploadRequestEntryUrlRepository,
			final PasswordService passwordService, final String baseUrl,
			final AccountRepository<Account> accountRepository) {
		super();
		this.uploadRequestEntryUrlRepository = uploadRequestEntryUrlRepository;
		this.passwordService = passwordService;
		this.path = baseUrl;
		this.accountRepository = accountRepository;
	}

	@Override
	public UploadRequestEntryUrl findByUuid(String uuid)
			throws BusinessException {
		return uploadRequestEntryUrlRepository.findByUuid(uuid);
	}

	@Override
	public UploadRequestEntryUrl create(UploadRequestEntry requestEntry,
			Boolean passwordProtected, Date expiryDate)
			throws BusinessException {
		UploadRequestEntryUrl UREUrl = new UploadRequestEntryUrl(requestEntry,
				path);
		UREUrl.setExpiryDate(expiryDate);
		if (passwordProtected) {
			String password = passwordService.generatePassword();
			// We store it temporary in this object for mail notification.
			UREUrl.setTemporaryPlainTextPassword(password);
			UREUrl.setPassword(HashUtils.hashSha1withBase64(password.getBytes()));
		}
		return uploadRequestEntryUrlRepository.create(UREUrl);
	}

	@Override
	public UploadRequestEntryUrl update(UploadRequestEntryUrl url)
			throws BusinessException {
		return uploadRequestEntryUrlRepository.update(url);
	}

	@Override
	public void delete(UploadRequestEntryUrl url) throws BusinessException {
		uploadRequestEntryUrlRepository.delete(url);
	}

	@Override
	public boolean isValidPassword(UploadRequestEntryUrl uploadRequestEntryUrl,
			String password) {
		if (uploadRequestEntryUrl == null)
			throw new IllegalArgumentException(
					"uploadRequestEntry url cannot be null");

		// Check password validity
		if (password != null) {
			String hashedPassword = HashUtils.hashSha1withBase64(password
					.getBytes());
			return hashedPassword.equals(uploadRequestEntryUrl.getPassword());
		}
		return true;
	}

	@Override
	public boolean isExpired(UploadRequestEntryUrl uploadRequestEntryUrl) {
		if (uploadRequestEntryUrl == null)
			throw new IllegalArgumentException(
					"UploadRequestEntryUrl url cannot be null");
		Date now = new Date();
		Date expiryDate = uploadRequestEntryUrl.getExpiryDate();
		return now.after(expiryDate);
	}

	@Override
	public SystemAccount getUploadRequestEntryURLAccount() {
		return accountRepository.getUploadRequestSystemAccount();
	}

	@Override
	public UploadRequestEntryUrl find(UploadRequestEntry entry)
			throws BusinessException {
		return uploadRequestEntryUrlRepository.findByUploadRequestEntry(entry);
	}

	@Override
	public Set<UploadRequestEntryUrl> findAllExpired() {
		return uploadRequestEntryUrlRepository.findAllExpired();
	}
}
