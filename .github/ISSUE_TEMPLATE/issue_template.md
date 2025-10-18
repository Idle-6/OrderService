---
name: 이슈 템플릿
about: Describe this issue template's purpose here.
title: "[FEAT] B2C 상품 검색"
labels: ''
assignees: ''

---

### $\bf{\normalsize{\color{yellow}GET}}$ `/api/products`

### 담당자
- 

## 기능 이름
- 검색 키워드를 이용한 상품 검색

## 기능 내용
- 사용자가 키워드로 해당 상품이 있는지 검색하는 API
- 요청 시 쿼리 파라미터로 값 전달
  - page 기본값: 1
  - size 기본값: 10
  - keyword 필수입력 null불가
  - orderBy 기본값: asc
  - sortBy 기본값: name
  - category 기본값: none
  - subCategory 기본값: none
- `category`, `subCategory`를 통하여 카테고리를 선택적으로 추가 필터링
  - `category`와  `subCategory`의 enum클래스를 아직 정의 하지 않았으므로, 정의되어 있지 않은 카테고리로 요청하지 못하도록 설정해야 함
- `keyword`를 제외하고 동적 쿼리를 적용하여 모든 쿼리 파라미터를 인자로 전달하지 않아도 작동되도록 구성
- 상품 상태가 `OFF_SALE`, `PENDING` 는 검색되지 않도록 설정해야 함

예외 처리
- [RuntimeException] : 없는 카테고리를 인자로 전달하면 에러 발생
- [RuntimeException] : size를 50보다 크게 설정할 경우 에러 발생



<!-- notionvc: a72fa98c-668f-4db4-814b-7e8c6318af87 --><!--EndFragment-->
</body>
</html>

<table>
    <tr>
        <th>Requqest Body</th>
        <th>Response Body</th>
    </tr>
    N/A
    <tr>
        <td><pre lang="json"></pre></td>
        <td><pre lang="json">{
  "content": [
    {
      "id": 1,
      "name": "상품 이름",
      "description": "상품 내용",
      "stockQuantity": "상품 수량",
      "price": 10000,
      "status": "ON_SALE",
      "category": "",
      "subCategory": "",
      "createdAt": "2024-12-04 00:00:00",
      "modifiedAt": "2024-12-04 00:00:00"
    },
    {
      ...
    }
  ]
  "page": 1,
  "size": 10,
  "sortBy": "name",
  "orderBy": "desc",
  "totalPage": 5
}</pre></td>
    </tr>
</table>
