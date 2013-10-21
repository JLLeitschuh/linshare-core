/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.core.service;

import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Tag;
import org.linagora.linshare.core.domain.entities.TagFilter;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.ThreadView;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface ThreadService {
	
	public Thread findByLsUuid(String uuid);

	public List<Thread> findAll();

	public Boolean create(Account actor, String name) throws BusinessException;
	
	public ThreadMember getThreadMemberById(long id) throws BusinessException;
	
	public ThreadMember getMemberFromUser(Thread thread, User user) throws BusinessException;

	public Set<ThreadMember> getMembers(User actor, Thread thread) throws BusinessException;

	public List<Thread> findAllWhereMember(User user);
	
	public List<Thread> findAllWhereAdmin(User user);

	public List<Thread> findAllWhereCanUpload(User user);
	
	public List<Thread> findLatestWhereMember(User actor, int limit);

	public boolean hasAnyWhereAdmin(User user);

	public boolean isUserAdmin(User user, Thread thread);

	public long countMembers(Thread thread);

	public void addMember(Account actor, Thread thread, User user, boolean readOnly) throws BusinessException;

	public void updateMember(Account actor, ThreadMember member, boolean admin, boolean canUpload) throws BusinessException;

	public void deleteMember(Account actor, Thread thread, ThreadMember member) throws BusinessException;
	
	public void deleteAllMembers(Account actor, Thread thread) throws BusinessException;

	public void deleteAllUserMemberships(Account actor, User user) throws BusinessException;

	public void deleteThread(User actor, Thread thread) throws BusinessException;

	public void deleteThreadView(User user, Thread thread, ThreadView threadView) throws BusinessException;
	
	public void deleteAllThreadViews(User user, Thread thread) throws BusinessException;

	public void deleteTagFilter(User user, Thread thread, TagFilter filter) throws BusinessException;

	public void deleteTag(User user, Thread thread, Tag tag) throws BusinessException;

	public void deleteAllTags(User user, Thread thread) throws BusinessException;

	public void rename(User actor, Thread thread, String threadName) throws BusinessException;

	public List<Thread> searchByName(User actor, String pattern);

	public List<Thread> searchByMembers(User actor, String pattern);

}
