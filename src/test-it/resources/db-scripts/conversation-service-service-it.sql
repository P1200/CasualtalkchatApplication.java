INSERT INTO `user` (id, username, email, password, is_account_confirmed, is_service_terms_accepted) VALUES ('a2c43ade-742e-40d6-b0b3-a933c29a9d7d', 'Paul', 'paul@gmail.com', 'password123', true, true);
INSERT INTO `user` (id, username, email, password, is_account_confirmed, is_service_terms_accepted) VALUES ('c66e1a75-a34d-4c28-9429-191dc59b86f4', 'madame89', 'cristinefreud89@gmail.com', 'password123', true, true);
INSERT INTO conversation (id, name) VALUES ('e7f4003b-4c3b-4aaa-9c52-611cc9e2c7eb', 'Paul');
INSERT INTO conversation_members (chat_conversation_entity_id, members_id) VALUES ('e7f4003b-4c3b-4aaa-9c52-611cc9e2c7eb', 'a2c43ade-742e-40d6-b0b3-a933c29a9d7d');
INSERT INTO conversation_members (chat_conversation_entity_id, members_id) VALUES ('e7f4003b-4c3b-4aaa-9c52-611cc9e2c7eb', 'c66e1a75-a34d-4c28-9429-191dc59b86f4');
INSERT INTO conversation_admins (chat_conversation_entity_id, admins_id) VALUES ('e7f4003b-4c3b-4aaa-9c52-611cc9e2c7eb', 'c66e1a75-a34d-4c28-9429-191dc59b86f4');
INSERT INTO message (id, sender_id, conversation_id, content, sent_time) VALUES ('6c62098e-9bcb-46a9-a19b-17ab210aacef', 'a2c43ade-742e-40d6-b0b3-a933c29a9d7d', 'e7f4003b-4c3b-4aaa-9c52-611cc9e2c7eb', 'Hello Suzie!', '2023-10-08 12:15:30.0');
INSERT INTO message (id, sender_id, conversation_id, content, sent_time) VALUES ('e8d483aa-8dfd-4cf4-9301-882b5d8b3000', 'a2c43ade-742e-40d6-b0b3-a933c29a9d7d', 'e7f4003b-4c3b-4aaa-9c52-611cc9e2c7eb', 'Hello Mark!', '2023-10-08 12:15:30.0');
INSERT INTO conversation (id, name) VALUES ('ad7feeec-dbb9-4e37-ad72-a1e5f24a2c6f', 'Paul');
INSERT INTO conversation_members (chat_conversation_entity_id, members_id) VALUES ('ad7feeec-dbb9-4e37-ad72-a1e5f24a2c6f', 'a2c43ade-742e-40d6-b0b3-a933c29a9d7d');
INSERT INTO conversation_members (chat_conversation_entity_id, members_id) VALUES ('ad7feeec-dbb9-4e37-ad72-a1e5f24a2c6f', 'c66e1a75-a34d-4c28-9429-191dc59b86f4');
INSERT INTO conversation_admins (chat_conversation_entity_id, admins_id) VALUES ('ad7feeec-dbb9-4e37-ad72-a1e5f24a2c6f', 'c66e1a75-a34d-4c28-9429-191dc59b86f4');