-- topdomain 2, PLATFORM_QUOTA
update quota set
	current_value=1096,
	last_value=100,
	quota=2300,
	quota_warning=2000,
	file_size_max=10
	where id=1;

-- topdomain 2, DOMAIN_QUOTA
update quota set
	current_value=1096,
	last_value=500,
	quota=1900,
	quota_warning=1800,
	file_size_max=5
	where id=2;

-- topdomain 2, ENSEMBLE_QUOTA - USER
update quota set
	current_value=496,
	last_value=0,
	quota=1900,
	quota_warning=1300,
	file_size_max=5
	where id=5;

-- topdomain 2, ENSEMBLE_QUOTA - THREAD
update quota set
	current_value=900,
	last_value=200,
	quota=2000,
	quota_warning=1500,
	file_size_max=5
	where id=6;

-- topdomain 2, ACCOUNT_QUOTA - Jane (11)
INSERT INTO quota (
	id, uuid, creation_date, modification_date, batch_modification_date,
	domain_quota, ensemble_quota, current_value, last_value,
	domain_id, account_id, parent_domain_id, quota,
	quota_warning, file_size_max, ensemble_type, quota_type)
VALUES (
	101, 'aebe1b64-39c0-11e5-9fa8-080027b8274a', NOW(), NOW(), NOW(),
	null, 5, 800, 0,
	2, 11, null, 1600,
	1480, 5, null, 'ACCOUNT_QUOTA');

-- topdomain 2, ACCOUNT_QUOTA - John (12)
INSERT INTO quota (
	id, uuid, creation_date, modification_date, batch_modification_date,
	domain_quota, ensemble_quota, current_value, last_value,
	domain_id, account_id, parent_domain_id, quota,
	quota_warning, file_size_max, ensemble_type, quota_type)
VALUES (
	102, 'aebe1b64-39c0-11e5-9fa8-080027b8274b', NOW(), NOW(), NOW(),
	null, 5, 900, 100,
	2, 12, null, 1500,
	1000, 6, null, 'ACCOUNT_QUOTA');

-- topdomain 2, ACCOUNT_QUOTA - thread (20) cf import-tests-stat.sql
INSERT INTO quota (
	id, uuid, creation_date, modification_date, batch_modification_date,
	domain_quota, ensemble_quota, current_value, last_value,
	domain_id, account_id, parent_domain_id, quota,
	quota_warning, file_size_max, ensemble_type, quota_type)
VALUES (
	103, 'aebe1b64-39c0-11e5-9fa8-080027b8274c', NOW(), NOW(), NOW(),
	null, 6, 700, 0,
	2, 20, null, 1000,
	800, 5, null, 'ACCOUNT_QUOTA');

	-- topdomain 2, ACCOUNT_QUOTA - thread (21) cf import-tests-stat.sql
INSERT INTO quota (
	id, uuid, creation_date, modification_date, batch_modification_date,
	domain_quota, ensemble_quota, current_value, last_value,
	domain_id, account_id, parent_domain_id, quota,
	quota_warning, file_size_max, ensemble_type, quota_type)
VALUES (
	104, 'aebe1b64-39c0-11e5-9fa8-080027b8274d', NOW(), NOW(), NOW(),
	null, 6, 500, 200,
	2, 21, null, 1300,
	1000, 6, null, 'ACCOUNT_QUOTA');