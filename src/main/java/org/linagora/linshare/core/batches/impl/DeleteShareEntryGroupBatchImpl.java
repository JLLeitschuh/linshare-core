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

package org.linagora.linshare.core.batches.impl;

import java.util.List;

import org.linagora.linshare.core.batches.generics.impl.GenericBatchImpl;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.ShareEntryGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteShareEntryGroupBatchImpl extends GenericBatchImpl {

	private static final Logger logger = LoggerFactory
			.getLogger(DeleteShareEntryGroupBatchImpl.class);

	private final ShareEntryGroupService service;

	public DeleteShareEntryGroupBatchImpl(
			AccountRepository<Account> accountRepository,
			ShareEntryGroupService service) {
		super(accountRepository);
		this.service = service;
	}

	@Override
	public List<String> getAll() {
		logger.info("DeleteShareEntryGroupBatchImpl job starting ...");
		List<String> allShareEntries = service
				.findAllToPurge(getSystemAccount());
		logger.info(allShareEntries.size()
				+ " unconsistent shareEntryGroups to delete");
		return allShareEntries;
	}

	@Override
	public Context execute(String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SystemAccount actor = getSystemAccount();
		ShareEntryGroup shareEntryGroup = service.findByUuid(actor, identifier);
		Context context = new BatchResultContext<ShareEntryGroup>(
				shareEntryGroup);
		try {
			logInfo(total, position, "processing shareEntryGroup : "
					+ shareEntryGroup.getUuid());
			service.delete(accountRepository.getBatchSystemAccount(),
					shareEntryGroup);
			logger.info("shareEntryGroup " + shareEntryGroup.getUuid()
					+ " has been deleted");
		} catch (BusinessException businessException) {
			logError(total, position,
					"Error while trying to delete shareEntryGroup");
			logger.info("Error occured while deleting shareEntryGroup ",
					businessException);
			BatchBusinessException exception = new BatchBusinessException(
					context, "Error while trying to delete ShareEntryGroup");
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(Context context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<ShareEntryGroup> shareEntryGroupContext = (BatchResultContext<ShareEntryGroup>) context;
		logInfo(total, position,
				"The shareEntryGroup "
						+ shareEntryGroupContext.getResource().getUuid()
						+ " has been successfully deleted");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier,
			long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<ShareEntryGroup> shareEntryGroupContext = (BatchResultContext<ShareEntryGroup>) exception
				.getContext();
		logError(total, position, "Deleting shareEntryGroup has failed "
				+ shareEntryGroupContext.getResource().getUuid());
		logger.error("Error occured while deleting shareEntryGroup "
				+ shareEntryGroupContext.getResource().getUuid()
				+ ". BatchBusinessException ", exception);
	}

	@Override
	public void terminate(List<String> all, long errors, long unhandled_errors,
			long total) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " ShareEntryGroup have been deleted");
		if (errors > 0) {
			logger.error(errors + " shareEntryGroup failed to delete");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors
					+ " shareEntryGroup failed to delete (unhandled error).");
		}
		logger.info("DeleteShareEntryGroupBatchImpl job terminated.");
	}
}
