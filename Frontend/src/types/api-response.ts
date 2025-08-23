export interface ApiResponse<T> {
  code: number;
  message?: string;
  result?: T;
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}

export interface PageableRequest {
  page: number;
  size: number;
  sort?: string;
}
