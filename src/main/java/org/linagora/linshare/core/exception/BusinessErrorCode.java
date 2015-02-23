/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
package org.linagora.linshare.core.exception;

import javax.ws.rs.core.Response.Status;

/** Exception error code.
 */
public enum BusinessErrorCode implements ErrorCode {
	UNKNOWN(1000, Status.INTERNAL_SERVER_ERROR),
	AUTHENTICATION_ERROR(2000),
	DATABASE_INCOHERENCE_NO_ROOT_DOMAIN(2001, Status.INTERNAL_SERVER_ERROR),
	USER_NOT_FOUND(2200, Status.NOT_FOUND),
	DUPLICATE_USER_ENTRY(2201),
	CANNOT_DELETE_USER(2203, Status.FORBIDDEN),
	CANNOT_UPDATE_USER(2204, Status.FORBIDDEN),
	USER_CANNOT_CREATE_GUEST(2205, Status.FORBIDDEN),
	USER_CANNOT_DELETE_GUEST(2206, Status.FORBIDDEN),
	USER_CANNOT_UPDATE_GUEST(2207, Status.FORBIDDEN),
	MIME_NOT_FOUND(3000),
	FILE_TOO_LARGE(3001),
	FILE_MIME_NOT_ALLOWED(3002),
	FILE_CONTAINS_VIRUS(3003),
	FILE_MIME_WARNING(3004),
	FILE_ENCRYPTION_UNDEFINED(3005),
	FILE_TIMESTAMP_NOT_COMPUTED(3006),
	FILE_SCAN_FAILED(3007),
	FILE_TIMESTAMP_WRONG_TSA_URL(3008),
	FILE_UNREACHABLE(3009),
	INVALID_FILENAME(3010),
	INVALID_UUID(4000),
	SHARED_DOCUMENT_NOT_FOUND(5000, Status.NOT_FOUND),
	CANNOT_SHARE_DOCUMENT(5001),
	CANNOT_DELETE_SHARED_DOCUMENT(5002),
	SHARE_NOT_FOUND(5003, Status.NOT_FOUND),
	SHARE_MISSING_RECIPIENTS(5400, Status.BAD_REQUEST),
	NO_SUCH_ELEMENT(6000, Status.NOT_FOUND),
	CANNOT_SIGN_DOCUMENT(9001),
	CANNOT_ENCRYPT_GENERATE_KEY(9002),
	CANNOT_ENCRYPT_DOCUMENT(9003),
	CANNOT_DECRYPT_DOCUMENT(9004),
	WRONG_URL(10000),
	SECURED_URL_IS_EXPIRED(12000),
	SECURED_URL_BAD_PASSWORD(12001),
	SECURED_URL_WRONG_DOCUMENT_ID(12002),
	DOMAIN_ID_ALREADY_EXISTS(13000),
	DOMAIN_ID_NOT_FOUND(13001, Status.NOT_FOUND),
	DOMAIN_INVALID_TYPE(13002),
	DOMAIN_POLICY_NOT_FOUND(13003, Status.NOT_FOUND),
	LDAP_CONNECTION_NOT_FOUND(13004, Status.NOT_FOUND),
	DOMAIN_PATTERN_NOT_FOUND(13005, Status.NOT_FOUND),
	DOMAIN_BASEDN_NOT_FOUND(13006),
	DOMAIN_INVALID_OPERATION(13007, Status.BAD_REQUEST),
	DOMAIN_DO_NOT_EXIST(13008, Status.NOT_FOUND),
	DOMAIN_POLICY_INVALID(13009),
	DOMAIN_ID_BAD_FORMAT(13010),
	LDAP_CONNECTION_ID_BAD_FORMAT(13011),
	DOMAIN_PATTERN_ID_BAD_FORMAT(13012),
	LDAP_CONNECTION_ID_ALREADY_EXISTS(13013),
	DOMAIN_PATTERN_ID_ALREADY_EXISTS(13014),
	LDAP_CONNECTION_CANNOT_BE_REMOVED(13015, Status.BAD_REQUEST),
	DOMAIN_PATTERN_CANNOT_BE_REMOVED(13016, Status.BAD_REQUEST),
	LDAP_CONNECTION_STILL_IN_USE(13017, Status.FORBIDDEN),
	DOMAIN_PATTERN_STILL_IN_USE(13018, Status.FORBIDDEN),
	FUNCTIONALITY_ENTITY_OUT_OF_DATE(14000),
	UNAUTHORISED_FUNCTIONALITY_UPDATE_ATTEMPT(14001),
	RELAY_HOST_NOT_ENABLE(15000),
	XSSFILTER_SCAN_FAILED(15666),
	DIRECTORY_UNAVAILABLE(16000),

	MAILCONFIG_IN_USE(16666),
	MAILCONFIG_NOT_FOUND(16667),
	MAILCONTENT_IN_USE(17666),
	MAILCONTENT_NOT_FOUND(17667),
	MAILCONTENTLANG_NOT_FOUND(17668),
	MAILCONTENTLANG_DUPLICATE(17669),
	MAILFOOTER_IN_USE(18666),
	MAILFOOTER_NOT_FOUND(18667),
	MAILFOOTERLANG_NOT_FOUND(18668),
	MAILFOOTERLANG_DUPLICATE(18669),
	MAILLAYOUT_IN_USE(19666),
	MAILLAYOUT_NOT_FOUND(19667),

	FORBIDDEN(17000, Status.FORBIDDEN),
	BAD_REQUEST(17400, Status.BAD_REQUEST),

	WEBSERVICE_FAULT(20000, Status.INTERNAL_SERVER_ERROR),
	WEBSERVICE_FORBIDDEN(20001, Status.FORBIDDEN),
	WEBSERVICE_NOT_FOUND(20002, Status.NOT_FOUND),

	LIST_DO_NOT_EXIST(25000, Status.NOT_FOUND),
	LIST_ALDREADY_EXISTS(25001),
	CONTACT_LIST_DO_NOT_EXIST(25002, Status.NOT_FOUND),

	THREAD_NOT_FOUND(26000, Status.NOT_FOUND),
	THREAD_MEMBER_NOT_FOUND(26001, Status.NOT_FOUND),
	THREAD_ENTRY_NOT_FOUND(26002, Status.NOT_FOUND),
	THREAD_FORBIDDEN(26403, Status.FORBIDDEN),
	THREAD_MEMBER_FORBIDDEN(26443, Status.FORBIDDEN),
	THREAD_ENTRY_FORBIDDEN(266443, Status.FORBIDDEN),

	GUEST_NOT_FOUND(28000, Status.NOT_FOUND),
	GUEST_ALREADY_EXISTS(28001, Status.BAD_REQUEST),
	GUEST_FORBIDDEN(28403, Status.FORBIDDEN),
	TECHNICAL_ACCOUNT_NOT_FOUND(29000, Status.NOT_FOUND),

	UPLOAD_REQUEST_NOT_FOUND(30404, Status.NOT_FOUND),
	UPLOAD_REQUEST_TOO_MANY_FILES(30000, Status.BAD_REQUEST),
	UPLOAD_REQUEST_NOT_ENABLE_YET(30001, Status.BAD_REQUEST),
	UPLOAD_REQUEST_EXPIRED(30002, Status.BAD_REQUEST),
	UPLOAD_REQUEST_TOTAL_DEPOSIT_SIZE_TOO_LARGE(30003, Status.BAD_REQUEST),
	UPLOAD_REQUEST_FILE_TOO_LARGE(30004, Status.BAD_REQUEST),
	UPLOAD_REQUEST_READONLY_MODE(30005, Status.FORBIDDEN),
	UPLOAD_REQUEST_FORBIDDEN(30406, Status.FORBIDDEN),
	UPLOAD_REQUEST_ENTRY_NOT_FOUND(31404, Status.NOT_FOUND),
	UPLOAD_REQUEST_ENTRY_FILE_UNREACHABLE(31405, Status.NOT_FOUND),

	UPLOAD_REQUEST_URL_FORBIDDEN(32401, Status.UNAUTHORIZED),

	DOCUMENT_ENTRY_FORBIDDEN(33403, Status.FORBIDDEN),
	DOCUMENT_ENTRY_NOT_FOUND(33404, Status.NOT_FOUND),

	ANONYMOUS_SHARE_ENTRY_FORBIDDEN(33403, Status.FORBIDDEN),
	ANONYMOUS_SHARE_ENTRY_NOT_FOUND(33404, Status.NOT_FOUND),

	SHARE_ENTRY_FORBIDDEN(34403, Status.FORBIDDEN),
	SHARE_ENTRY_NOT_FOUND(34404, Status.NOT_FOUND);

	private final int code;

	private final Status status;

	private BusinessErrorCode(int code) {
		this.code = code;
		this.status = Status.BAD_REQUEST;
	}

	private BusinessErrorCode(int code, Status status) {
		this.code = code;
		this.status = status;
	}

	public int getCode() {
		return code;
	}

	public Status getStatus() {
		return status;
	}
}
