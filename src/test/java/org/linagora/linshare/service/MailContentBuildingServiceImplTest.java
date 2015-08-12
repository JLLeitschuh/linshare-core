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
package org.linagora.linshare.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.linagora.linshare.core.business.service.AnonymousShareEntryBusinessService;
import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.FileInfo;
import org.linagora.linshare.core.domain.objects.MailContainer;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AnonymousShareEntryService;
import org.linagora.linshare.core.service.MailBuildingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.google.common.collect.Sets;

@Ignore
@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-service.xml",
//		"classpath:springContext-facade.xml",
		"classpath:springContext-jackRabbit.xml",
		"classpath:springContext-test.xml"
		})
public class MailContentBuildingServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests{
	
	private static Logger logger = LoggerFactory.getLogger(MailContentBuildingServiceImplTest.class);
	
	@Autowired
	private AbstractDomainService abstractDomainService;
	
	@Autowired
	private GuestRepository guestRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;
	
	@Autowired
	private DomainPolicyRepository domainPolicyRepository;
	
	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;
	
	
	@Autowired
	private MailBuildingService mailBuildingService;
	
	@Autowired
	private AnonymousShareEntryService anonymousShareEntryService;
	
	@Autowired
	private AnonymousShareEntryBusinessService anonymousShareEntryBusinessService;
	
	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private DocumentEntryRepository documentEntryRepository;
	
	@Autowired
	private FileSystemDao fileRepository;
	
	private InputStream inputStream;
	
	private String inputStreamUuid;
	
	private User john;
	
	private User jane;
	
	
	private Document aDocument;
	
	private DocumentEntry aDocumentEntry;
	
	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		
		john = userRepository.findByMail("user1@linshare.org");
		jane = userRepository.findByMail("user2@linshare.org");
		
		inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
		inputStreamUuid = fileRepository.insertFile(john.getLogin(), inputStream, 10000, "linshare-default.properties", "text/plain");
				
		FileInfo inputStreamInfo = fileRepository.getFileInfoByUUID(inputStreamUuid);
		
		Calendar lastModifiedLin = inputStreamInfo.getLastModified();
		Calendar exp=inputStreamInfo.getLastModified();
		exp.add(Calendar.HOUR, 4);
		
		aDocument = new Document(inputStreamUuid,inputStreamInfo.getName(),inputStreamInfo.getMimeType(),lastModifiedLin,exp, john,false,false,new Long(10000));
		Set<Signature> signatures = new HashSet<Signature>();
		aDocument.setSignatures(signatures);
		aDocumentEntry = new DocumentEntry(john, "new document", aDocument);

		try {
			documentRepository.create(aDocument);
			documentEntryRepository.create(aDocumentEntry);
			john.getEntries().add(aDocumentEntry);
			userRepository.update(john);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail();
		}

		
		
//		MessagesConfiguration messagesConfiguration = new MessagesConfiguration();
//		
//		MailTemplate mailTemplate = new MailTemplate();
//		mailTemplate.setLanguage(Language.FRENCH);
//				
//		MailSubject mailSubject = new MailSubject();
//		mailSubject.setMailSubject(MailSubjectEnum.ANONYMOUS_DOWNLOAD);
//		mailSubject.setLanguage(Language.FRENCH);
//		
//		// Add all mailTemplates
//		for (MailTemplateEnum mailTemplateEnum : MailTemplateEnum.values()) {
//			mailTemplate.setMailTemplate(mailTemplateEnum);
//			messagesConfiguration.addMailTemplate(mailTemplate);
//			messagesConfiguration.addMailSubject(mailSubject);
//		}
//	
//		
//		john.getDomain().setMessagesConfiguration(messagesConfiguration);
//		
//		abstractDomainRepository.update(john.getDomain());
//		
//		userRepository.update(john);
		
		logger.debug(LinShareTestConstants.END_SETUP);
	}
	
	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		
		john.getEntries().clear();
		userRepository.update(john);
		
		documentEntryRepository.delete(aDocumentEntry);
		documentRepository.delete(aDocument);
		fileRepository.removeFileByUUID(aDocument.getUuid());
		
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}
	
	private void testMailGenerate(MailContainer mailContainer){
		Assert.assertNotNull(mailContainer);
		Assert.assertNotNull(mailContainer.getSubject());
		Assert.assertNotNull(mailContainer.getContentHTML());
		Assert.assertNotNull(mailContainer.getContentTXT());
		
		Assert.assertFalse(mailContainer.getSubject().contains("${"));
		Assert.assertFalse(mailContainer.getContentHTML().contains("${"));
		Assert.assertFalse(mailContainer.getContentTXT().contains("${"));
	}
	
	
	@Test
	public void testBuildMailAnonymousDownload() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
	
		User actor = john;
		Contact recipient = new Contact(jane.getMail());
		MailContainer mailContainer = new MailContainer(Language.ENGLISH,"contentTxt","contentHTML");
		mailContainer.setLanguage(Language.FRENCH);
		List<DocumentEntry> docs = new ArrayList<DocumentEntry>();
		docs.add(aDocumentEntry);

		// buildMailAnonymousDownload
		AnonymousUrl anonymousUrl = anonymousShareEntryBusinessService.createAnonymousShare(docs, actor, recipient, Calendar.getInstance(), true, new ShareEntryGroup());
		List<AnonymousShareEntry> shareEntries = new ArrayList<AnonymousShareEntry>(anonymousUrl.getAnonymousShareEntries());
		
		MailContainer mailContainerBuild =  mailBuildingService.buildAnonymousDownload(shareEntries.get(0));
		testMailGenerate(mailContainerBuild);

		// buildMailAnonymousDownloadWithOneRecipient
		mailContainerBuild = mailBuildingService.buildNewSharingProtected(actor, mailContainer, anonymousUrl);
		testMailGenerate(mailContainerBuild);
	
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testBuildMailNewGuest() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		User actor = john;
		MailContainer mailContainer = new MailContainer(Language.ENGLISH,"contentTxt","contentHTML");
		mailContainer.setLanguage(Language.FRENCH);
		List<Document> docs = new ArrayList<Document>();
		docs.add(aDocument);
		
		String email = john.getMail();
		User recipient = jane;
		
		// buildMailNewGuest
		MailContainer mailContainerBuild =  mailBuildingService.buildNewGuest(actor, recipient, "password");
		testMailGenerate(mailContainerBuild);

		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testBuildMailResetPassword() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain guestDomain = abstractDomainService.getGuestDomain(john.getDomainId());
		Guest guest = new Guest("firstName", "lastName", "guest@linshare.org", "xxxxxxxx" , true,"");
		guest.setDomain(john.getDomain());
		guest.setOwner(john);
		// Guest must not be able to create other guests.
		guest.setCanCreateGuest(false);
		guest.setCanUpload(true);
		guest.setCreationDate(new Date());
		guest.setLocale(guestDomain.getDefaultTapestryLocale());
		
		Calendar cd = Calendar.getInstance();
		cd.add(Calendar.MONTH, 3);
		guest.setExpirationDate(cd.getTime());
		
		guestRepository.create(guest);
		
		
		// buildMailResetPassword
		MailContainer mailContainerBuild =  mailBuildingService.buildResetPassword(guest,  "password");
		testMailGenerate(mailContainerBuild);
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testBuildMailNewSharing() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		User actor = john;
		MailContainer mailContainer = new MailContainer(Language.ENGLISH,"contentTxt","contentHTML");
		mailContainer.setLanguage(Language.FRENCH);
		
		Set<ShareEntry> shares = Sets.newHashSet();
		shares.add(new ShareEntry(john, aDocumentEntry.getName(), "", jane, aDocumentEntry, Calendar.getInstance(), new ShareEntryGroup()));
		
		
		MailContainerWithRecipient mailContainerWithRecipientBuild =  mailBuildingService.buildNewSharing(actor, mailContainer, jane, shares);
		mailBuildingService.buildNewSharing(actor, mailContainer, jane, shares);
		
		testMailGenerate(mailContainerWithRecipientBuild);
		Assert.assertTrue(mailContainerWithRecipientBuild.getRecipient().equals(jane.getMail()));

		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testBuildMailNoDocumentHasBeenDownloaded() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		User actor = john;
		ShareEntryGroup shareEntryGroup = new ShareEntryGroup(actor, "test", new Date());
		Set<ShareEntry> shares = Sets.newHashSet();
		shares.add(new ShareEntry(john, aDocumentEntry.getName(), "", jane, aDocumentEntry, Calendar.getInstance(), shareEntryGroup));

		MailContainerWithRecipient mailContainerWithRecipientBuild = mailBuildingService.buildNoDocumentHasBeenDownloadedAcknowledgment(shareEntryGroup);

		testMailGenerate(mailContainerWithRecipientBuild);
		Assert.assertTrue(mailContainerWithRecipientBuild.getRecipient().equals(john.getMail()));

		logger.debug(LinShareTestConstants.END_TEST);
	}
}
