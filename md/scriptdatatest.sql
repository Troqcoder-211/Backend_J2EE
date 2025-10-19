-- ===============================
-- USERS (thêm password)
-- ===============================
INSERT INTO users (id, username, email, display_name, avatar_s3_key, password, created_at, updated_at, is_disabled)
VALUES
('b0f1f9e2-4d3c-4a1b-9b9b-0f1d1f2e3a4b','john_doe','john@example.com','John Doe',NULL,'password123',NOW(),NOW(),FALSE),
('c1d2e3f4-5a6b-4c7d-8e9f-0a1b2c3d4e5f','alice_smith','alice@example.com','Alice Smith',NULL,'alicepwd',NOW(),NOW(),FALSE),
('d2e3f4a5-6b7c-4d8e-9f0a-1b2c3d4e5f6a','bob_jones','bob@example.com','Bob Jones',NULL,'bobpwd',NOW(),NOW(),FALSE),
('e3f4a5b6-7c8d-4e9f-0a1b-2c3d4e5f6a7b','carol_white','carol@example.com','Carol White',NULL,'carolpwd',NOW(),NOW(),FALSE),
('f4a5b6c7-8d9e-4f0a-1b2c-3d4e5f6a7b8c','dave_black','dave@example.com','Dave Black',NULL,'davepwd',NOW(),NOW(),FALSE);

-- ===============================
-- DEVICES (UUID v4 chuẩn)
-- ===============================
INSERT INTO devices (id, device_type, push_token, last_seen_at, created_at, user_id)
VALUES
('a1b2c3d4-1111-4e11-9111-a1b2c3d4e5f1','iOS','token_john',NOW(),NOW(),'b0f1f9e2-4d3c-4a1b-9b9b-0f1d1f2e3a4b'),
('a2b3c4d5-2222-4e22-9222-a2b3c4d5e6f2','Android','token_alice',NOW(),NOW(),'c1d2e3f4-5a6b-4c7d-8e9f-0a1b2c3d4e5f'),
('a3b4c5d6-3333-4e33-9333-a3b4c5d6e7f3','iOS','token_bob',NOW(),NOW(),'d2e3f4a5-6b7c-4d8e-9f0a-1b2c3d4e5f6a'),
('a4b5c6d7-4444-4e44-9444-a4b5c6d7e8f4','Android','token_carol',NOW(),NOW(),'e3f4a5b6-7c8d-4e9f-0a1b-2c3d4e5f6a7b'),
('a5b6c7d8-5555-4e55-9555-a5b6c7d8e9f5','iOS','token_dave',NOW(),NOW(),'f4a5b6c7-8d9e-4f0a-1b2c-3d4e5f6a7b8c');

-- ===============================
-- CONVERSATIONS
-- ===============================
INSERT INTO conversations (id, conversation_type, name, avatar_s3_key, created_by, created_at, is_archived)
VALUES
('6b7c8d9e-1234-4a1b-9c2d-3e4f5a6b7c8d', 'DM', 'Chat John-Alice', NULL, 'b0f1f9e2-4d3c-4a1b-9b9b-0f1d1f2e3a4b', NOW(), FALSE),
('7c8d9e0f-2345-4b2c-ad3e-4f5a6b7c8d9e', 'GROUP', 'Team Chat', NULL, 'c1d2e3f4-5a6b-4c7d-8e9f-0a1b2c3d4e5f', NOW(), FALSE),
('8d9e0f1a-3456-4c3d-be4f-5a6b7c8d9e0f', 'GROUP', 'Project Chat', NULL, 'd2e3f4a5-6b7c-4d8e-9f0a-1b2c3d4e5f6a', NOW(), FALSE);

-- ===============================
-- ATTACHMENTS
-- ===============================
INSERT INTO attachments (id, uploader_id, conversation_id, s3_bucket, s3_key, filename, mime_type, size_bytes, checksum, created_at)
VALUES
('9e0f1a2b-4567-4d4e-cf5a-6b7c8d9e0f1a', 'b0f1f9e2-4d3c-4a1b-9b9b-0f1d1f2e3a4b', '6b7c8d9e-1234-4a1b-9c2d-3e4f5a6b7c8d', 'my-bucket', 'attachments/file1.png', 'file1.png', 'image/png', 2048, 'abc123', NOW()),
('0f1a2b3c-5678-4e5f-d06b-7c8d9e0f1a2b', 'c1d2e3f4-5a6b-4c7d-8e9f-0a1b2c3d4e5f', '7c8d9e0f-2345-4b2c-ad3e-4f5a6b7c8d9e', 'my-bucket', 'attachments/file2.png', 'file2.png', 'image/png', 1024, 'def456', NOW()),
('1a2b3c4d-6789-4f6a-e17c-8d9e0f1a2b3c', 'd2e3f4a5-6b7c-4d8e-9f0a-1b2c3d4e5f6a', '8d9e0f1a-3456-4c3d-be4f-5a6b7c8d9e0f', 'my-bucket', 'attachments/file3.png', 'file3.png', 'image/png', 3072, 'ghi789', NOW()),
('2b3c4d5e-789a-4a7b-f28d-9e0f1a2b3c4d', 'e3f4a5b6-7c8d-4e9f-0a1b-2c3d4e5f6a7b', '7c8d9e0f-2345-4b2c-ad3e-4f5a6b7c8d9e', 'my-bucket', 'attachments/file4.png', 'file4.png', 'image/png', 512, 'jkl012', NOW()),
('3c4d5e6f-89ab-4b8c-039e-0f1a2b3c4d5e', 'f4a5b6c7-8d9e-4f0a-1b2c-3d4e5f6a7b8c', '8d9e0f1a-3456-4c3d-be4f-5a6b7c8d9e0f', 'my-bucket', 'attachments/file5.png', 'file5.png', 'image/png', 1024, 'mno345', NOW());

-- ===============================
-- MESSAGES
-- ===============================
INSERT INTO messages (id, conversation_id, sender_id, content, type, created_at, is_deleted)
VALUES
('4d5e6f7a-9abc-4c9d-14af-1a2b3c4d5e6f', '6b7c8d9e-1234-4a1b-9c2d-3e4f5a6b7c8d', 'b0f1f9e2-4d3c-4a1b-9b9b-0f1d1f2e3a4b', 'Hello Alice!', 'TEXT', NOW(), FALSE),
('5e6f7a8b-abcd-4d0e-25ba-2b3c4d5e6f7a', '6b7c8d9e-1234-4a1b-9c2d-3e4f5a6b7c8d', 'c1d2e3f4-5a6b-4c7d-8e9f-0a1b2c3d4e5f', 'Hi John!', 'TEXT', NOW(), FALSE),
('6f7a8b9c-bcde-4e1f-36cb-3c4d5e6f7a8b', '7c8d9e0f-2345-4b2c-ad3e-4f5a6b7c8d9e', 'c1d2e3f4-5a6b-4c7d-8e9f-0a1b2c3d4e5f', 'Team meeting at 3PM', 'TEXT', NOW(), FALSE),
('7a8b9c0d-cdef-4f2a-47dc-4d5e6f7a8b9c', '7c8d9e0f-2345-4b2c-ad3e-4f5a6b7c8d9e', 'd2e3f4a5-6b7c-4d8e-9f0a-1b2c3d4e5f6a', 'Roger that!', 'TEXT', NOW(), FALSE),
('8b9c0d1e-def0-4a3b-58ed-5e6f7a8b9c0d', '8d9e0f1a-3456-4c3d-be4f-5a6b7c8d9e0f', 'e3f4a5b6-7c8d-4e9f-0a1b-2c3d4e5f6a7b', 'Project plan uploaded', 'TEXT', NOW(), FALSE),
('9c0d1e2f-ef01-4b4c-69fe-6f7a8b9c0d1e', '8d9e0f1a-3456-4c3d-be4f-5a6b7c8d9e0f', 'f4a5b6c7-8d9e-4f0a-1b2c-3d4e5f6a7b8c', 'Thanks!', 'TEXT', NOW(), FALSE);