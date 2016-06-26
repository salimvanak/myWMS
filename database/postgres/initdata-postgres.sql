
-- H2 only
--CREATE ALIAS MD5 FOR "org.apache.commons.codec.digest.DigestUtils.md5Hex(java.lang.String)";

-- Create System Client
insert into mywms_client(id, version, entity_lock, created, modified, name, CL_NR, cl_code, 
	additionalcontent) 
values(0, 0, 0, current_timestamp, current_timestamp, 'System-Client', 'System', 'System-Client',
    'This is a system used entity. DO NOT REMOVE OR LOCK IT! Some processes may use it. But feel free to choose a suitable name.');
			  
-- Create role System Administrator
insert into mywms_role(id, version, entity_lock, created, modified, name, description,
	additionalcontent)
values(0, 0, 0, current_timestamp, current_timestamp, 'Admin', 'System Administrator',
    'This is a system used entity. DO NOT REMOVE, LOCK OR RENAME IT! Some processes may use it.');

-- Create user admin, passwort admin, role : System Administrator
insert into mywms_user(id, version, entity_lock, created, modified,	name, locale, password, client_id, 
	additionalcontent) 
values(0, 0, 0, current_timestamp, current_timestamp, 'admin', 'en', MD5('admin'), 0,
	'This is a system used entity. DO NOT REMOVE OR LOCK IT! Some processes may use it. But feel free to choose a suitable name and password.');

-- Create users for available languages
insert into mywms_user(id, version, entity_lock, created, modified,	name, locale, password, client_id) 
values(1, 0, 0, current_timestamp, current_timestamp, 'deutsch', 'de', MD5('deutsch'), 0);
insert into mywms_user(id, version, entity_lock, created, modified,	name, locale, password, client_id) 
values(2, 0, 0, current_timestamp, current_timestamp, 'english', 'en', MD5('english'), 0);


-- Give all default users the admin-role 
insert into mywms_user_mywms_role 
select u.id, r.id from mywms_user as u, mywms_role as r 
where r.name='Admin';


