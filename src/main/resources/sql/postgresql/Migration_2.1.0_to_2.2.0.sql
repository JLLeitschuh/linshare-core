-- Postgresql migration script template

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version) VALUES ((SELECT nextVal('hibernate_sequence')),'2.2.0');
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	DECLARE version_to VARCHAR := '2.2.0';
	DECLARE version_from VARCHAR := '2.1.0';
	DECLARE start VARCHAR := concat('You are about to upgrade from LinShare : ', version_from,  ' to ' , version_to);
	DECLARE version_history_from VARCHAR := (SELECT version from version ORDER BY id DESC LIMIT 1);
	DECLARE database_info VARCHAR = version();
	DECLARE error VARCHAR := concat('Your database upgrade history indicates that you already upgraded to : ', version_to);
	DECLARE connection_id INT := pg_backend_pid();
	DECLARE row record;
	BEGIN
		RAISE NOTICE '%', start;
		RAISE NOTICE 'Your actual version is: %', version_history_from;
		RAISE NOTICE 'Your databse history is :';
		FOR row IN (SELECT * FROM version ORDER BY id DESC) LOOP
			RAISE INFO '%', row.version;
		END LOOP;
		RAISE NOTICE 'Your database system information is : %', database_info;
		IF (version_from <> version_history_from) THEN
			RAISE WARNING 'You must be in version : % to run this script. You are actually in version: %', version_from, version_history_from;
			IF EXISTS (SELECT * from version where version = version_to) THEN
				RAISE WARNING '%', error;
			END IF;
			RAISE WARNING 'We are about to abort the migration script, all the following instructions will be aborted and transaction will rollback.';
			RAISE INFO 'You should expect the following error : "query has no destination for result data".';
	--		DIRTY: did it to stop the process cause there is no clean way to do it.
	--		Expected error: query has no destination for result data.
			select error;
		END IF;
	END;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_check_user_connected() RETURNS void AS $$
BEGIN
	DECLARE database VARCHAR := (SELECT current_database());
	DECLARE user_connected VARCHAR := (SELECT current_user);
	DECLARE error VARCHAR := ('You are actually connected with the user "postgres", you should be connected with your LinShare database user, we are about to stop the migration script.');
	BEGIN
		RAISE INFO 'Connected to "%" with user "%"', database, user_connected;
		IF (user_connected = 'postgres') THEN
			RAISE WARNING '%', error;
		--	DIRTY: did it to stop the process cause there is no clean way to do it.
		--	Expected error: query has no destination for result data.
			SELECT '';
		END IF;
	END;
END
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION ls_fix_current_value_for_all_accounts() RETURNS void AS $$
BEGIN
	DECLARE myaccount record;
	DECLARE q record;
	DECLARE i BIGINT;
	DECLARE j BIGINT;
	DECLARE op BIGINT;
	BEGIN
		FOR myaccount IN (SELECT id, mail FROM account WHERE account_type != 5) LOOP
			RAISE INFO 'account mail : % (account_id=%)', myaccount.mail, myaccount.id;
			i := (SELECT sum(ls_size) FROM account AS a join entry AS e on a.id = e.owner_id join document_entry AS de ON de.entry_id = e.id WHERE a.id = myaccount.id);
			IF i IS NULL THEN
				i := 0;
			END IF;
			j := (SELECT current_value FROM quota AS q WHERE account_id = myaccount.id);
			op := (SELECT - sum(operation_value) FROM operation_history AS q WHERE account_id = myaccount.id);
			IF op IS NULL THEN
				op := 0;
			END IF;
			RAISE INFO 'Value of current_value : %, sum(operation_value) : % (account=%)', j, op, myaccount.id;
			RAISE INFO 'Updating account with new value (sum(ls_size)) - sum(operation_value) : % - % = %', i, op, i - op;
			i := i - op;
			RAISE INFO 'Difference of current_value : % ', i - j;
			UPDATE quota SET current_value = i WHERE account_id = myaccount.id;
			RAISE INFO '----';
			RAISE INFO 'Delete OperationHistory for account : % ', myaccount.mail;
			DELETE FROM operation_history WHERE account_id = myaccount.id;
			RAISE INFO '----';
		END LOOP;
	END;
END
$$ LANGUAGE plpgsql;

SELECT ls_check_user_connected();
SELECT ls_prechecks();

SET client_min_messages = warning;

DROP VIEW IF EXISTS alias_users_list_all;
DROP VIEW IF EXISTS alias_users_list_active;
DROP VIEW IF EXISTS alias_users_list_destroyed;
DROP VIEW IF EXISTS alias_threads_list_all;
DROP VIEW IF EXISTS alias_threads_list_active;
DROP VIEW IF EXISTS alias_threads_list_destroyed;
-- Here your request

ALTER TABLE upload_request DROP COLUMN domain_abstract_id;
ALTER TABLE upload_request DROP COLUMN account_id;
ALTER TABLE upload_request ADD COLUMN dirty bool DEFAULT 'false' NOT NULL;
ALTER TABLE upload_request ADD COLUMN enable_notification bool NOT NULL;

ALTER TABLE upload_request_group ADD COLUMN domain_abstract_id int8 NOT NULL;
ALTER TABLE upload_request_group ADD COLUMN account_id int8 NOT NULL;
ALTER TABLE upload_request_group ADD COLUMN secured bool NOT NULL;
ALTER TABLE upload_request_group ADD COLUMN mail_message_id varchar(255);
ALTER TABLE upload_request_group ADD COLUMN activation_date timestamp(6) NOT NULL;
ALTER TABLE upload_request_group ADD COLUMN expiry_date timestamp(6) NOT NULL;
ALTER TABLE upload_request_group ADD COLUMN notification_date timestamp(6) NOT NULL;
ALTER TABLE upload_request_group ADD COLUMN max_deposit_size int8;
ALTER TABLE upload_request_group ADD COLUMN max_file int4;
ALTER TABLE upload_request_group ADD COLUMN max_file_size int8;
ALTER TABLE upload_request_group ADD COLUMN can_delete bool NOT NULL;
ALTER TABLE upload_request_group ADD COLUMN can_close bool NOT NULL;
ALTER TABLE upload_request_group ADD COLUMN can_edit_expiry_date bool NOT NULL;
ALTER TABLE upload_request_group ADD COLUMN locale varchar(255) NOT NULL;
ALTER TABLE upload_request_group ADD COLUMN enable_notification bool NOT NULL;
ALTER TABLE upload_request_group ADD COLUMN restricted bool NOT NULL;
ALTER TABLE upload_request_group ADD COLUMN status varchar(255) NOT NULL;
ALTER TABLE upload_request_group ADD CONSTRAINT FKupload_req220337 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE upload_request_group ADD CONSTRAINT FKupload_req840249 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);

ALTER TABLE upload_request_entry ADD COLUMN document_id int8 NOT NULL;
ALTER TABLE upload_request_entry ADD COLUMN copied bool DEFAULT 'false' NOT NULL;
ALTER TABLE upload_request_entry ADD COLUMN ciphered bool NOT NULL;
ALTER TABLE upload_request_entry ADD COLUMN ls_type varchar(255) NOT NULL;
ALTER TABLE upload_request_entry ADD COLUMN sha256sum varchar(255);
ALTER TABLE upload_request_entry ADD CONSTRAINT FKupload_req11782 FOREIGN KEY (document_id) REFERENCES document (id);

-- Mail Activation

-- End MailActivation

-- Mail Layout

-- End mail layout

--Mail Content
UPDATE mail_content SET body = '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/*  Upper main-content */-->
  <section id="main-content">
    <!--/* If the sender has added a  customized message */-->
    <th:block data-th-if="${!#strings.isEmpty(body)}">
      <div th:replace="layout :: contentMessageSection(~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgFrom}">You have a message from</span>
          <b data-th-text="#{name(${requestOwner.firstName} , ${requestOwner.lastName})}">Peter Wilson</b> :
        </span>
        <span id="message-content" data-th-text="*{body}">
          Hi Amy,<br>
          As agreed,  i am sending you the report as well as the related files. Feel free to contact me if need be. <br>Best regards, Peter.
        </span>
      </div>
    </th:block>
    <!--/* End of customized message */-->
    <!--/* main-content container */-->
    <div th:replace="layout :: contentUpperSection(~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings for external or internal user */-->
        <div>
          <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
        </div>
          <!--/* End of Greetings for external or internal recipient */-->
        <!--/* Main email  message content*/-->
        <p>
                 <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName})}">
                     Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
                </span>
 <span th:with="df=#{customDate}" data-th-text="${#dates.format(request.activationDate,df)}">7th of November, 2018</span>
        </p>
        <!--/* End of Main email message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container */-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
  <div data-th-if="${!#strings.isEmpty(request.activationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{activationDate},${request.activationDate})"/>
            </div>
     <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
       <div data-th-if="(${totalMaxDepotSize})">
               <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
         </div>
  <div data-th-if="(${isgrouped})">
         <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
    </div>
  </section>  <!--/* End of Secondary content for bottom email section */-->
  </div>
</body>
</html>', messages_french = 'activationDate = Ouverture du dépôt le
closureDate = Dépôt  disponible jusqu\''''au
customDate= d MMMM yyyy.
depotSize = Taille du dépôt
mainMsg = <b>{0} {1}</b> vous a créé une Invitation de Dépôt, qui sera ouverte le
msgFrom = Le message de
name = {0} {1}
recipientsOfDepot = Destinataires associés au dépôt
subject = {0} {1} vous a créé une Invitation de Dépot', messages_english = 'activationDate = Request activation date
closureDate = Request closing date
customDate= MMMM d, yyyy.
depotSize = Size of the upload repository
mainMsg = <b>{0} {1}</b> has created an Upload Request for you, set to open 
msgFrom = Message from
name = {0} {1}
recipientsOfDepot = Recipients of the upload request
subject = {0} {1} has created an Upload Request repository for you.' WHERE id = 20;
UPDATE mail_content SET body = '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* If the sender has added a customized message */-->
            <th:block data-th-if="${!#strings.isEmpty(body)}">
               <div th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
                  <span id="message-title">
                  <span data-th-text="#{msgFrom}">You have a message from</span>
                  <b data-th-text="#{name(${requestOwner.firstName} , ${requestOwner.lastName})}">Peter Wilson</b> :
                  </span>
                  <span id="message-content" data-th-text="${body}">
                  Hi Amy,<br>
                  As agreed,  could you send me the report. Feel free to contact me if need be. <br/>Best regards, Peter.
                  </span>
               </div>
            </th:block>
            <!--/* End of customized message */-->
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection(~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                   <th:block data-th-if="(${!request.wasPreviouslyCreated})">
                       <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                          Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
                       </span>
                   </th:block>
                    <th:block data-th-if="(${request.wasPreviouslyCreated})">
                       <span data-th-text="#{msgAlt(${requestOwner.firstName} , ${requestOwner.lastName})}"> Peter Wilson''s Upload Request depot is now activated..</span>
                     </th:block>
                     <br/>
                     <!--/* Check if the external user has a password protected file share */-->
                     <span data-th-if="(${!protected})">
                     <span data-th-text="#{msgUnProtected}">In order to access it click the link below.</span>
                     </span>
                     <span data-th-if="(${protected})">
                     <span data-th-text="#{msgProtected}">In order to access it click the link below and enter the provided password.</span>
                     </span>
                  </p>
                  <th:block data-th-replace="layout :: actionButtonLink(#{buttonMsg},${requestUrl})"/>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <div data-th-if="(${protected})">
               <th:block data-th-replace="layout :: infoStandardArea(#{password},${password})"/>
            </div>
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
           <div data-th-if="(${totalMaxDepotSize})">
                    <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
            </div>
            <div data-th-if="(${isgrouped})">
               <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>', messages_french = 'buttonMsg = Accéder au dépôt
closureDate = Dépôt disponible jusqu\''''au
depotSize = Taille du dépôt
mainMsg = <b>{0} {1}</b> vous invite à déposer des fichiers via l\''''Invitation de Dépôt intitulée : <b>{2}</b>.
msgAlt = L\''''Invitation de Dépôt de {0} {1} est désormais activée.
msgFrom = Le message de
msgProtected = Vous pouvez y accéder en cliquant sur le lien ci-dessous et en saisissant le mot de passe fourni.
msgUnProtected = Vous pouvez y accéder en cliquant sur le lien ci-dessous.
name = {0} {1}
password = Mot de passe
recipientsOfDepot = Destinataires associés au dépôt
subject = {0} {1} vous invite à déposer des fichiers dans le dépôt : {2}', messages_english = 'buttonMsg = Access to repository
closureDate = Request closing date
depotSize = Size of the upload repository
mainMsg = <b>{0} {1}</b> invited you to upload some files with the request labeled : <b>{2}</b>.
msgFrom = Message from
msgAlt = The upload request from {0} {1} is now activated.
msgProtected = Access it by clicking the link below and enter the provided password.
msgUnProtected = Access it by clicking the link below.
name = {0} {1} 
password = Password
recipientsOfDepot = Recipients of the upload request
subject = {0} {1} invites you to upload some files with the request labeled : {2}' WHERE id = 16;
UPDATE mail_content SET body = '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${request.subject})}">
                      Your Upload Request repository labeled $subject is now activated.
                     </span>
                     <span data-th-text="#{msgLink}">In order to access it click the link below.</span>
                  </p>
                  <th:block data-th-replace="layout :: actionButtonLink(#{buttonMsg},${requestUrl})"/>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
            <div data-th-if="(${isgrouped})">
               <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
            </div>
             <div data-th-if="(${totalMaxDepotSize})">
                   <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>', messages_french = 'buttonMsg = Accès
closureDate = Date de clôture
depotSize = Taille autorisée
mainMsg = Votre dépôt intitulé <b>{0}</b> est désormais actif.
msgLink = Vous pouvez y accéder en cliquant sur le lien ci-dessous.
recipientsOfDepot = Destinataires
subject = Votre invitation de dépôt {0} est désormais active', messages_english = 'buttonMsg = Access
closureDate = Closure date
depotSize = Allowed size
mainMsg = Your Upload Request labeled <b>{0}</b> is now active.
msgLink = Access it by following the link below.
recipientsOfDepot = Recipients
subject = Your Upload Request : {0}, is now active' WHERE id = 17;

UPDATE mail_content SET messages_french = '', messages_english = '' WHERE id =;
UPDATE mail_content SET messages_french = 'buttonMsg = Access
closureDate = Closure date
depotSize = Allowed size
mainMsg = <b>{0} {1}</b> invited you to its upload request : <b>{2}</b>.
msgFrom = Message from
msgAlt = The repository from {0} {1} is now active.
msgProtected = Unlock it by following the link below and entering the password.
msgUnProtected = Access it by following the link below.
name = {0} {1}
password = Password
recipientsOfDepot = Recipients
subject = {0} {1} invited you to its upload request : {2}', messages_english = 'buttonMsg = Accès
closureDate = Date de clôture
depotSize = Taille
mainMsg = <b>{0} {1}</b> vous invite à déposer des fichiers dans le dépôt : <b>{2}</b>.
msgAlt = L\''invitation de {0} {1} est désormais active.
msgFrom = Le message de
msgProtected = Vous pouvez déverrouiller le dépôt en suivant le lien ci-dessous et en saisissant le mot de passe fourni.
msgUnProtected = Vous pouvez y accéder en suivant le lien ci-dessous.
name = {0} {1}
password = Mot de passe
recipientsOfDepot = Destinataires
subject = {0} {1} vous invite à déposer des fichiers dans le dépôt : {2}' WHERE id =16;
UPDATE mail_content SET messages_french = 'closureDate = Date de clôture
filesInURDepot = Fichiers
mainMsg = <b>{0} {1}</b> a fermé son invitation de dépôt : {2}.
recipientsOfDepot = Destinataires
subject = {0} {1} a fermé l\''invitation de dépôt : {2}', messages_english = 'closureDate = Closure date
filesInURDepot = Files
mainMsg = <b>{0} {1}</b> has closed the upload request labeled : {2}.
recipientsOfDepot = Recipients
subject = {0} {1} has closed his upload request : {2}' WHERE id =21;
UPDATE mail_content SET messages_french = 'endingMainMsgPlural =  Il y a <b> {0} fichiers </b> dans le dépôt.
endingMainMsgSingular = Il y a  <b>1 fichier </b> dans le dépôt.
filesInURDepot =  Fichiers déposés
fileSize =  Taille
groupedBeginningMainMsg = <b>{0}</b> a clôturé votre Invitation de Dépôt.
invitationClosureDate = Date de clôture
invitationCreationDate = Date d\''activation
msgTitle = Message lié à l\''invitation :
numFilesInDepot = Nombre de fichiers déposés
recipientsURequest = Destinataires
subject = {0} a clôturé votre invitation de dépôt : {1}
ungroupedBeginningMainMsg = <b>{0}</b> a clôturé votre Invitation de Dépôt.
uploadedOverTotal = {0} / {1} fichiers
totalUploaded = {0} files', messages_english = 'endingMainMsgPlural = There are a total of <b> {0} files </b> in the depot.
endingMainMsgSingular =  There is a total <b>1 file </b> in the depot.
filesInURDepot = Files uploaded
fileSize =  Total filesize
groupedBeginningMainMsg = <b>{0}</b> has closed your grouped Upload Request depot.
invitationClosureDate = Closure date
invitationCreationDate = Activation date
msgTitle =  Upload request\''s  attached message :
numFilesInDepot = Total uploaded files
recipientsURequest = Recipients
subject =  {0}  has closed  your Upload Request depot : {1}
ungroupedBeginningMainMsg  = <b>{0}</b> has closed your Upload Request depot.
uploadedOverTotal = {0} / {1} files
totalUploaded = {0} files' WHERE id =14;
UPDATE mail_content SET messages_french = 'deletionDate = Deletion date
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has removed your access to the depot : {2}.
subject = {0} {1} has removed your access to the depot : {2}
', messages_english = 'deletionDate = Accès au dépôt retiré le
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  a retiré votre accès au dépôt de l\''invitation intitulée : {2}.
subject = {0} {1} a supprimé votre accès au dépôt : {2}
' WHERE id =22;
UPDATE mail_content SET messages_french = 'buttonMsg = Accès
closureDate = Date de clôture
depotSize = Taille
mainMsg = <b>{0} {1}</b> aimerais vous rappeller de déposer vos fichiers.
mainMsgEnd =
msgFrom =  Le message de
msgUnProtected = Pour accéder au dépôt, suivez le lien ci-dessous.
name = {0} {1}
recipientsOfDepot = Destinataires
subject = {0} {1} attend toujours des fichiers de votre part', messages_english = 'buttonMsg = Access
closureDate = Closure date
depotSize = Size
mainMsg = <b>{0} {1}</b> kindly reminds you to upload your files.
mainMsgEnd =
msgFrom = Message from
msgUnProtected = In order to upload your files, please follow the link below.
name = {0} {1}
recipientsOfDepot = Recipients
subject = {0} {1} is still awaiting your files' WHERE id =18;
UPDATE mail_content SET messages_french = 'invitationClosureDate = Date de clôture
invitationCreationDate = Date d\''activation
mainMsg =  <b>{0}</b>  n\''a pas pu déposer des fichiers dans le dépôt car il n\''y a plus d\''espace disponible dans votre Espace Personnel. Veuillez s\''il vous plait libérez de l\''espace.
mainMsgTitle = Vous n\''avez plus d\''espace disponible.
maxUploadDepotSize =  Taille total du dépôt
msgTitle = Message lié à l\''invitation de dépôt :
recipientsURequest = Destinataires
subject =  {0}  n\''a pu déposer un fichier car il n\''y a plus d\''espace disponible: {1}', messages_english = 'invitationClosureDate = Closure date
invitationCreationDate = Activation date
mainMsg =  <b>{0}</b> is not able to upload any file, since there is no more space available in your Personal Space. Please free up some space.
mainMsgTitle = No more space available.
maxUploadDepotSize = Maximum size of the depot
msgTitle = Upload Request\''s  attached message :
recipientsURequest = Recipients
subject =  {0} could not upload a file since there is no more space available' WHERE id =11;
UPDATE mail_content SET messages_french = 'activationDate = Date d\''activation
closureRight = Droits de dépôt
deletionRight = Droits de suppression
depotSize = Taille du dépôt
expiryDate = Date de clôture
local = Langue
enableNotification = Activation des notifications
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  a modifié des paramètres liés au dépôt.
maxFileNum = Nombre de Fichiers
maxFileSize = Taille autorisée
msgFrom = Nouveau message de
name = {0} {1}
nameOfDepot: Nom du dépôt
secondaryMsg = Les modifications sont listées ci-dessous.
subject = Modification des paramètres du dépôt : {0}', messages_english = 'activationDate = Activation date
closureRight = Closure rights
deletionRight = Deletion rights
depotSize = Repository size
expiryDate = Closure date
local = Local
enableNotification = Enable notifications
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  has updated some settings related to the Upload Request.
maxFileNum = File number
maxFileSize = File size
msgFrom =  New message from
name = {0} {1}
nameOfDepot: Name of the depot
secondaryMsg = Updated settings are listed below.
subject = Updated Settings for Upload Request : {0}
' WHERE id =23;
UPDATE mail_content SET messages_french = 'endingMainMsg = dans votre Invitation de Dépôt.
fileSize =  Taille du fichier
fileUploadedThe= Fichier déposé le
invitationClosureDate = Date de clôture
invitationCreationDate = Date d\''activation
beginningMainMsg = <b> {0} </b> vous a déposé le fichier
numFilesInDepot = Nombre de fichiers déposés
subject =  {0}  vous a déposé {1}  dans votre Invitation de Dépôt
uploadedOverTotal = {0} / {1} fichiers
totalUploaded = {0} fichiers', messages_english = 'endingMainMsg = in your Upload Request
fileSize =  File size
fileUploadedThe = Upload date
invitationClosureDate = Closure date
invitationCreationDate = Activation date
beginningMainMsg =  <b> {0} </b> has uploaded the file
endingMainMsg = in your Upload Request.
numFilesInDepot = Total uploaded files
subject =  {0}  has uploaded {1}  in your Upload Request
uploadedOverTotal = {0} / {1} files
totalUploaded = {0} files' WHERE id =10;
UPDATE mail_content SET messages_french = 'beginningMainMsgForRecipient =   L\''invitation dépôt de <b> {0} <span style="text-transform:uppercase">{1}</span> </b> va expirer dans <b>{2} jours</b>
beginningMainMsgGrouped =   Votre invitation groupée sera clôturée dans  <b>{0} jours</b>.
beginningMainMsgUnGrouped =   Votre invitation au dépôt sera clôturée dans  <b>{0} jours</b>.
defaultSubject = : {0}
endingMainMsgPlural = et vous avez actuellement reçu <b>{0} fichiers</b>.
endingMainMsgPlural = Il y a un total de <b> {0} fichiers </b> dans le dépôt.
endingMainMsgPluralForRecipient = et vous avez actuellement envoyé  <b> {0} fichiers </b> dans le dépôt.
endingMainMsgSingular = et vous avez actuellement reçu <b>1 fichier</b>.
endingMainMsgSingular = Il y a au total <b>1 fichier </b> dans le dépôt.
endingMainMsgSingularForRecipient = et vous avez actuellement envoyé  <b>1 fichier </b> dans le dépôt.
filesInURDepot = Fichiers déposés
formatMailSubject = : {0}
invitationClosureDate =  Date d\''activation
invitationCreationDate = Date de clôture
msgTitle = Message lié à l\''invitation :
recipientsURequest = Destinataires
subjectForOwner =  Votre invitation de dépôt sera bientôt clôturée
subjectForRecipient = L\'' invitation au dépôt de {0} {1} sera bientôt clôturée
uploadFileBtn = Déposer un fichier', messages_english = 'beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>\''s The Upload Request is about to reach it\''s end date in <b>{2} days</b>
beginningMainMsgGrouped = Your invitation will be closed in  <b>{0} days</b>.
beginningMainMsgUnGrouped =  Your invitation is about to be closed in <b>{0} days</b>.
endingMainMsgPlural =  and you currently have received<b>{0} files</b>.
endingMainMsgPlural = There are a total of <b> {0} files </b> in the depot.
endingMainMsgPluralForRecipient = and so far you have sent <b> {0} files </b> in the depot.
endingMainMsgSingular =   and you currently have received<b>1 file</b>.
endingMainMsgSingular = There is a total of <b>1 file </b> in the repository.
endingMainMsgSingularForRecipient = and you currently have sent <b>1 file </b>in the repository.
filesInURDepot = Files uploaded
formatMailSubject = : {0}
invitationClosureDate = Closure date
invitationCreationDate = Activation date
msgTitle =  Upload Request\''s  attached message :
recipientsURequest = Recipients
subjectForOwner =  Your invitation is about to be closed.
subjectForRecipient =  {0} {1}\''s  invitation is about to be closed
uploadFileBtn = Upload a file' WHERE id =12;
UPDATE mail_content SET messages_french = 'beginningMainMsgForRecipient = L\''invitation de Dépôt de <b> {0} <span style="text-transform:uppercase">{1}</span> </b> a expiré.
beginningMainMsgGrouped = Votre Invitation de Dépôt groupée a expiré.
beginningMainMsgUnGrouped = Votre Invitation de Dépôt a expiré.
endingMainMsgPlural = et vous avez reçu un total  de <b>{0} fichiers</b>.
endingMainMsgPluralForRecipient = et vous avez  envoyé  <b> {0} fichiers </b>.
endingMainMsgSingular = et vous avez  reçu au total <b>1 fichier</b>.
endingMainMsgSingularForRecipient = et vous avez  envoyé <b>1 fichier </b>.
filesInURDepot = Fichiers déposés
formatMailSubject = : {0}
invitationClosureDate = Date  de clôture
invitationCreationDate =  Date d\''activation
msgTitle = Message lié à l\''Invitation de Dépôt :
recipientsURequest = Destinataires
subjectForOwner = Votre Invitation de Dépôt est clôturée
subjectForRecipient = L\'' Invitation de Dépôt de {0} est clôturée', messages_english = 'beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>\''s Upload Request has expired
beginningMainMsgGrouped = Your grouped Upload Request has expired
beginningMainMsgUnGrouped = Your Upload Request has expired
endingMainMsgPlural = and you have received a total of <b>{0} files</b>.
endingMainMsgPluralForRecipient = and you currently have sent  <b> {0} files </b>.
endingMainMsgSingular = and you have received a total of <b>1 file</b>.
endingMainMsgSingularForRecipient = and you currently have uploaded <b>1 file </b> to the repository.
filesInURDepot = Files uploaded
formatMailSubject = : {0}
invitationClosureDate = Closure date
invitationCreationDate = Activation date
msgTitle = Upload Request\''s  attached message :
recipientsURequest = Recipients
subjectForOwner = Your invitation {0} is now closed
subjectForRecipient =  {0} {1}\''s  invitation {2} is now closed' WHERE id = 13;
UPDATE mail_content SET messages_french = 'activationDate = Ouverture du dépôt le
closureDate = Date de clôture
customDate= d MMMM yyyy.
depotSize = Taille autorisée
mainMsg = <b>{0} {1}</b> a créé une Invitation de dépôt, qui sera ouverte le
msgFrom = Le message de
name = {0} {1}
recipientsOfDepot = Destinataires
subject = {0} {1} vous a créé une Invitation de Dépôt', messages_english = 'activationDate = Activation date
closureDate = Closure date
customDate= MMMM d, yyyy.
depotSize = Allowed size
mainMsg = <b>{0} {1}</b> has invited you to access to his Upload Request, sets to open
msgFrom = Message from
name = {0} {1}
recipientsOfDepot = Recipients
subject = {0} {1} has sent an invitation to access to his Upload Request.' WHERE id = 20;

--End mail Content

-- End of your requests

-- LinShare version
SELECT ls_version();

-- Alias for Users
-- All users
CREATE VIEW alias_users_list_all AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id;
-- All active users
CREATE VIEW alias_users_list_active AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id where a.destroyed = 0;
-- All destroyed users
CREATE VIEW alias_users_list_destroyed AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id where a.destroyed != 0;

-- Alias for threads
-- All threads
CREATE VIEW alias_threads_list_all AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id;
-- All active threads
CREATE VIEW alias_threads_list_active AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed = 0;
-- All destroyed threads
CREATE VIEW alias_threads_list_destroyed AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed != 0;
COMMIT;

--Update the upload request statuses
UPDATE upload_request_history set status = replace(status, 'STATUS_', '');
UPDATE upload_request_group set status = replace(status, 'STATUS_', '');
UPDATE upload_request set status = replace(status, 'STATUS_', '');
