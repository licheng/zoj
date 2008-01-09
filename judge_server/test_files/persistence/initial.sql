INSERT INTO role(role_id, name, description, create_user, create_date, last_update_user, last_update_date) VALUES(1, 'Anonymous', 'The privileges anonymous user poccesses.', 1, NOW(), 1, NOW());

INSERT INTO permission_level(permission_level_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(1, 'View', 1, NOW(), 1, NOW());
INSERT INTO permission_level(permission_level_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(2, 'Participate', 1, NOW(), 1, NOW());
INSERT INTO permission_level(permission_level_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(3, 'Admin', 1, NOW(), 1, NOW());

INSERT INTO limits(limits_id, time_limit, memory_limit, output_limit, submission_limit) VALUES(1, 1, 32768, 32768, 32);

INSERT INTO reference_type(reference_type_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(1, 'Description', 1, NOW(), 1, NOW());
INSERT INTO reference_type(reference_type_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(2, 'Input', 1, NOW(), 1, NOW());
INSERT INTO reference_type(reference_type_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(3, 'Output', 1, NOW(), 1, NOW());
INSERT INTO reference_type(reference_type_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(4, 'Auxiliary', 1, NOW(), 1, NOW());
INSERT INTO reference_type(reference_type_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(5, 'Checker', 1, NOW(), 1, NOW());
INSERT INTO reference_type(reference_type_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(6, 'Checker Source', 1, NOW(), 1, NOW());
INSERT INTO reference_type(reference_type_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(7, 'Judge Solution', 1, NOW(), 1, NOW());
INSERT INTO reference_type(reference_type_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(8, 'Download', 1, NOW(), 1, NOW());
INSERT INTO reference_type(reference_type_id, description, create_user, create_date, last_update_user, last_update_date) VALUES(9, 'Misc', 1, NOW(), 1, NOW());
