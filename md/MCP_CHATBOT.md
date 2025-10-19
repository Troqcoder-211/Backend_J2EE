### MCP + Chatbot
```bash
[Frontend UI: Chatbox/Support UI]
            ↓
[Backend API - Spring Boot]
   ├── ChatController (REST)
   ├── ChatService
   ├── MCP Client (Gửi/nhận request tới model)
   └── KnowledgeBase (tùy chọn)
            ↓
[MCP Server / Model Gateway]
            ↓
[LLM: OpenAI / Claude / Local Model]

```

```bash
Sau khi có chatbot cơ bản, bạn có thể:

✅ Thêm bộ nhớ hội thoại (conversation memory) → context tốt hơn

✅ Thêm bộ FAQ nội bộ → trả lời nhanh không cần model

✅ Dùng WebSocket để phản hồi real-time như ChatGPT
```