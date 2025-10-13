```bash
| Annotation   | Dùng khi                      | Ví dụ                     | Ý nghĩa                                  |
| ------------ | ----------------------------- | ------------------------- | ---------------------------------------- |
| `target`     | Trường đích (nơi gán giá trị) | `"sender.id"`             | Trường trong entity                      |
| `source`     | Trường nguồn (lấy giá trị)    | `"senderId"`              | Trường trong DTO                         |
| `ignore`     | Muốn bỏ qua không map         | `ignore = true`           | Dành cho các quan hệ hoặc field tự xử lý |
| `constant`   | Gán giá trị cố định           | `"false"`                 | Gán mặc định                             |
| `expression` | Tính toán giá trị bằng Java   | `"java(LocalDate.now())"` | Tự sinh giá trị                          |
```

### 🧩 Sơ đồ tư duy map giữa DTO → Entity
```bash
CreateMessageDTO                     Message (Entity)
──────────────────────────────▶────────────────────────────
conversationId          ───────────────▶ conversation.id     (ignore, set trong service)
senderId                ───────────────▶ sender.id           (ignore, set trong service)
content                 ───────────────▶ content
messageType (Enum DTO)  ───────────────▶ type (Enum Entity)
                         ───────────────▶ createdAt = LocalDate.now() (biểu thức java)
                         ───────────────▶ isDeleted = false (constant)
(Phần còn lại)          ───────────────▶ ignored (không map)

```

### 🧩 Hoạt động bên trong MapStruct (hình minh họa luồng dữ liệu)
```bash
┌────────────────────────┐
│ CreateMessageDTO dto    │
│ ─ conversationId        │
│ ─ senderId              │
│ ─ content               │
│ ─ messageType           │
└────────────┬────────────┘
             │
             ▼
┌────────────────────────┐
│ Message entity          │
│ ─ id            = null  (ignore) 
│ ─ conversation   = null (ignore)
│ ─ sender         = null (ignore)
│ ─ content        = dto.content
│ ─ type           = dto.messageType
│ ─ createdAt      = LocalDate.now()
│ ─ isDeleted      = false
└────────────────────────┘
```

### Tóm tắt bằng sơ đồ
```bash
CreateMessageDTO (client gửi)
──────────────────────────────▶ Message (Entity)
senderId = UUID   ───────────┐
replyTo  = UUID   ───────────┤   MapStruct không biết map
                            ▼
                    (ignore ở mapper)
                            ▼
                    Service lấy từ DB:
                    ├── sender = userRepository.findById(senderId)
                    └── replyTo = messageRepository.findById(replyTo)
```