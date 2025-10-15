### TypeScript Interface (Frontend)
```bash
// src/models/PageResponse.ts
export interface PageResponse<T> {
  content: T[]; // Dữ liệu trong trang hiện tại
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
}
```