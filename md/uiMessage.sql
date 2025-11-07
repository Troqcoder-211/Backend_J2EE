DO $$
DECLARE
    i INT;
    sender UUID := '0f419e6b-854f-49e2-a8b0-2607fa813295';
    receiver UUID := 'b0f1f9e2-4d3c-4a1b-9b9b-0f1d1f2e3a4b';
BEGIN
    FOR i IN 1..100 LOOP
        INSERT INTO messages (id, conversation_id, sender_id, content, type, created_at, is_deleted)
        VALUES (
            gen_random_uuid(),
            '6b7c8d9e-1234-4a1b-9c2d-3e4f5a6b7c8d',
            CASE WHEN i % 2 = 0 THEN sender ELSE receiver END,
            CONCAT('Sample message number ', i, ' from ', CASE WHEN i % 2 = 0 THEN 'A' ELSE 'B' END),
            'TEXT',
            NOW() - (INTERVAL '1 minute' * i),
            FALSE
        );
    END LOOP;
END $$;
