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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.LdapGroupProvider;
import org.linagora.linshare.core.facade.webservice.common.dto.LightCommonDto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "LDAPGroupProvider")
@ApiModel(value = "LDAPGroupProvider", description = "Used to provide groups from an LDAP directory")
public class LDAPGroupProviderDto {

	@ApiModelProperty(value = "uuid")
	private String uuid;

	@ApiModelProperty(value = "LdapConnectionLight")
	private LightCommonDto ldapConnectionLight;

	@ApiModelProperty(value = "GroupLdapPatternLight")
	private LightCommonDto groupLdapPatternLight;

	@ApiModelProperty(value = "BaseDn")
	private String baseDn = "";

	@ApiModelProperty(value = "AutomaticUserCreation")
	private Boolean AutomaticUserCreation;

	@ApiModelProperty(value = "ForceCreation")
	private Boolean forceCreation;

	public LDAPGroupProviderDto() {
		super();
	}

	public LDAPGroupProviderDto(LdapGroupProvider groupProvider) {
		this.uuid = groupProvider.getUuid();
		this.ldapConnectionLight = new LightCommonDto(groupProvider.getLdapConnection().getLabel(),
				groupProvider.getLdapConnection().getUuid());
		this.groupLdapPatternLight = new LightCommonDto(groupProvider.getGroupPattern().getLabel(),
				groupProvider.getGroupPattern().getUuid());
		this.baseDn = groupProvider.getBaseDn();
		this.AutomaticUserCreation = groupProvider.getAutomaticUserCreation();
		this.forceCreation = groupProvider.getForceCreation();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public LightCommonDto getLdapConnectionLight() {
		return ldapConnectionLight;
	}

	public void setLdapConnectionLight(LightCommonDto ldapConnectionLight) {
		this.ldapConnectionLight = ldapConnectionLight;
	}

	public LightCommonDto getGroupLdapPatternLight() {
		return groupLdapPatternLight;
	}

	public void setGroupLdapPatternLight(LightCommonDto groupLdapPatternLight) {
		this.groupLdapPatternLight = groupLdapPatternLight;
	}

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}

	public Boolean getAutomaticUserCreation() {
		return AutomaticUserCreation;
	}

	public void setAutomaticUserCreation(Boolean automaticUserCreation) {
		AutomaticUserCreation = automaticUserCreation;
	}

	public Boolean getForceCreation() {
		return forceCreation;
	}

	public void setForceCreation(Boolean forceCreation) {
		this.forceCreation = forceCreation;
	}

}
