/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
package org.linagora.linshare.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.InitMongoService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
public class SharedSpaceNodeServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger logger = LoggerFactory.getLogger(SharedSpaceNodeServiceImplTest.class);

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepo;

	private Account authUser;

	private LoadingServiceTestDatas datas;

	@Autowired
	private InitMongoService init;

	@Autowired
	private SharedSpaceNodeService service;

	@Before
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		this.executeSqlScript("import-tests-default-domain-quotas.sql", false);
		this.executeSqlScript("import-tests-quota-other.sql", false);
		datas = new LoadingServiceTestDatas(userRepo);
		datas.loadUsers();
		init.init();
		authUser = datas.getUser1();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void create() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode node = new SharedSpaceNode("My first node", null, NodeType.WORK_GROUP);
		SharedSpaceNode expectedNode = service.create(authUser, authUser, node);
		Assert.assertNotNull("node not created", expectedNode);
		Assert.assertEquals(expectedNode.getUuid(), node.getUuid());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void find() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode node = new SharedSpaceNode("Node to find", null, NodeType.WORK_GROUP);
		SharedSpaceNode expectedNode = service.create(authUser, authUser, node);
		SharedSpaceNode toFindNode = service.find(authUser, authUser, expectedNode.getUuid());
		Assert.assertNotNull("Node has not been found.", toFindNode);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void delete() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode node = new SharedSpaceNode("My first node", null, NodeType.WORK_GROUP);
		SharedSpaceNode createdNode = service.create(authUser, authUser, node);
		Assert.assertEquals(createdNode, node);
		service.delete(authUser, authUser, createdNode);
		try {
			service.find(authUser, authUser, createdNode.getUuid());
			Assert.fail("An exception should be thrown because the node is found.");
		} catch (BusinessException e) {
			Assert.assertEquals("The node has been found in the data base. but it has not been deleted",
					BusinessErrorCode.WORK_GROUP_NOT_FOUND, e.getErrorCode());
		}
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void update() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode nodeToUpdate = new SharedSpaceNode("nodeName ToUpdate", null, NodeType.WORK_GROUP);
		SharedSpaceNode createdNodeToUpdate = service.create(authUser, authUser, nodeToUpdate);
		SharedSpaceNode updatedNode = new SharedSpaceNode("nodeName Updated", null, NodeType.WORK_GROUP);
		updatedNode.setUuid(createdNodeToUpdate.getUuid());
		service.update(authUser, authUser, updatedNode);
		Assert.assertEquals("The shared space node is not updated.", updatedNode.getName(),
				service.update(authUser, authUser, updatedNode).getName());
		logger.info(LinShareTestConstants.END_TEST);
	}
}
