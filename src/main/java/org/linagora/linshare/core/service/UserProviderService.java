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
package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface UserProviderService {

	public List<String> findAllDomainPatternIdentifiers();
	public List<String> findAllUserDomainPatternIdentifiers();
	public List<String> findAllSystemDomainPatternIdentifiers();
	public List<UserLdapPattern> findAllDomainPattern() throws BusinessException;
	public UserLdapPattern findDomainPattern(String id) throws BusinessException;
	public List<UserLdapPattern> findAllUserDomainPattern() throws BusinessException;
	public List<UserLdapPattern> findAllSystemDomainPattern() throws BusinessException;
	public UserLdapPattern createDomainPattern(UserLdapPattern domainPattern) throws BusinessException;
	public UserLdapPattern retrieveDomainPattern(String identifier) throws BusinessException;
	public UserLdapPattern updateDomainPattern(UserLdapPattern domainPattern) throws BusinessException;
	public void deletePattern(String patternToDelete) throws BusinessException;

	public List<String> findAllLDAPConnectionIdentifiers();
	public List<LdapConnection> findAllLDAPConnections() throws BusinessException;
	public LdapConnection findLDAPConnection(String id) throws BusinessException;
	public LdapConnection createLDAPConnection(LdapConnection ldapConnection) throws BusinessException;
	public LdapConnection retrieveLDAPConnection(String identifier) throws BusinessException;
	public LdapConnection updateLDAPConnection(LdapConnection ldapConnection) throws BusinessException;
	public void deleteConnection(String connectionToDelete) throws BusinessException;

	public void create(LdapUserProvider userProvider) throws BusinessException;
	public void delete(LdapUserProvider userProvider) throws BusinessException;
	public void update(LdapUserProvider userProvider) throws BusinessException;

	public User findUser(LdapUserProvider userProvider, String mail) throws BusinessException;
	public Boolean isUserExist(LdapUserProvider userProvider, String mail) throws BusinessException;

	public List<User> searchUser(LdapUserProvider userProvider, String mail, String firstName, String lastName) throws BusinessException;

	public List<User> autoCompleteUser(LdapUserProvider userProvider, String pattern) throws BusinessException;
	public List<User> autoCompleteUser(LdapUserProvider userProvider, String firstName, String lastName) throws BusinessException;

	public User auth(LdapUserProvider userProvider,	String login, String userPasswd) throws BusinessException;
	public User searchForAuth(LdapUserProvider userProvider, String login) throws BusinessException;

	public boolean patternIsDeletable(String patternToDelete);
	public boolean connectionIsDeletable(String connectionToDelete);
}
