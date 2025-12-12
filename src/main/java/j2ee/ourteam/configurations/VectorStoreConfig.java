package j2ee.ourteam.configurations;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class VectorStoreConfig {

    private final JdbcTemplate jdbcTemplate;

    public VectorStoreConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void initDatabase() {
        // Tạo các extension cần thiết nếu chưa có
        jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");
        jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\"");

        // Tạo bảng
        jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS vector_store (
                        id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
                        content text,
                        metadata json,
                        embedding vector(768)
                    )
                """);
    }

}