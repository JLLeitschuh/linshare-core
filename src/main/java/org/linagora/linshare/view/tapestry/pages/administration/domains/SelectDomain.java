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

package org.linagora.linshare.view.tapestry.pages.administration.domains;


import java.util.List;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.constants.DomainAccessRuleType;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.AllowDomainVo;
import org.linagora.linshare.core.domain.vo.DenyDomainVo;
import org.linagora.linshare.core.domain.vo.DomainPolicyVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.domain.vo.DomainAccessRuleVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.DomainPolicyFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectDomain {

	@SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

	private static Logger logger = LoggerFactory.getLogger(SelectDomain.class);
	
	@SessionState(create=false)
	@Property
	private DomainPolicyVo domainPolicy;
	
    @Persist
    @Property
    private List<String> domains;
	
	@Property
    private AbstractDomainVo domainVo;
    
    @Inject
    private AbstractDomainFacade domainFacade;
    
    @Inject
    private DomainPolicyFacade domainPolicyFacade;
    
    @Property
    private DomainAccessRuleVo ruleVo;
    
    @SessionState
    private UserVo loginUser;
	
    @Inject
    private Messages messages;
    
    @Persist
	@Property
    private DomainAccessRuleType rule;
    
    @Persist
    @Property
	@Validate("required")
    private String domainSelection;

    @InjectPage
    private org.linagora.linshare.view.tapestry.pages.administration.domains.SelectRules selectRulepage;
    
    @InjectPage
    private org.linagora.linshare.view.tapestry.pages.administration.domains.ManageDomainPolicy manageDomainPolicypage;
    
    @Property
    private boolean cancel;
    
    

	@SetupRender
	public void init() {
		if (domainPolicy == null) {
			domainPolicy = new DomainPolicyVo();
		}
		domains = domainFacade.findAllDomainIdentifiers();
	}
	
    public Object onActionFromCancel() {
        return SelectRules.class;
    }

    void onSelectedFromCancel() {
        
    	cancel=true;
    }
     
   public Object onSuccess() {

    		try{
    			domainVo=domainFacade.retrieveDomain(domainSelection);
    			} catch (BusinessException e) {
			logger.error("Can not retrieve domain : " + e.getMessage());
			logger.debug(e.toString());}
    		
    		if(rule.toInt() ==2)
    		{	
    			ruleVo=new AllowDomainVo(domainVo.getIdentifier());
    			try{
    				domainPolicy.getDomainAccessPolicy().addRule(domainPolicyFacade.setDomainAccessRule(ruleVo, domainVo));
    				} catch (BusinessException e) {
    			logger.error("Can not retrieve domain : " + e.getMessage());
    			logger.debug(e.toString());}
    		}else {
    			ruleVo=new DenyDomainVo(domainVo.getIdentifier());
    			try{
    				domainPolicy.getDomainAccessPolicy().addRule(domainPolicyFacade.setDomainAccessRule(ruleVo, domainVo));
    				} catch (BusinessException e) {
    			logger.error("Can not retrieve domain : " + e.getMessage());
    			logger.debug(e.toString());}
    	}
    	
    	try {
				domainPolicyFacade.updateDomainPolicy(loginUser,domainPolicy);
			} catch (BusinessException e) {
			e.printStackTrace();
			}

    	return manageDomainPolicypage;
    }

    public void set(DomainAccessRuleType rule)
    {
    	this.rule=rule;
    }
    
	Object onException(Throwable cause) {
    	shareSessionObjects.addError(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }

}

