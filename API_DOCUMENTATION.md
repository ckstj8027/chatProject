# Chat Server API 명세서

## Base URL
- Production: `https://api.bob-dev.click`
- Local: `http://localhost:8080`

## 인증 (Authentication)
대부분의 API는 JWT 토큰 인증이 필요합니다.
```
Authorization: Bearer {token}
```

## API 구분
- **HTTP REST API**: 일반적인 CRUD 작업 (회원가입, 로그인, 채팅방 생성/조회 등)
- **WebSocket API**: 실시간 채팅 메시지 송수신

---

# HTTP REST API

## 1. 회원 관리 API

### 1.1 회원가입
- **URL**: `/member/create`
- **Method**: `POST`
- **Auth Required**: No
- **Request Body**:
```json
{
  "name": "string",
  "email": "string",
  "password": "string"
}
```
- **Response**:
  - Status: 201 Created
  - Body: User ID (Long)

### 1.2 로그인
- **URL**: `/member/doLogin`
- **Method**: `POST`
- **Auth Required**: No
- **Request Body**:
```json
{
  "email": "string",
  "password": "string"
}
```
- **Response**:
  - Status: 200 OK
  - Body:
```json
{
  "id": "Long",
  "token": "JWT Token String"
}
```

### 1.3 회원 목록 조회
- **URL**: `/member/list`
- **Method**: `GET`
- **Auth Required**: Yes
- **Response**:
  - Status: 200 OK
  - Body:
```json
[
  {
    "id": "Long",
    "name": "string",
    "email": "string"
  }
]
```

---

## 2. 채팅 API

### 2.1 그룹 채팅방 생성
- **URL**: `/chat/room/group/create`
- **Method**: `POST`
- **Auth Required**: Yes
- **Query Params**:
  - `roomName`: string
- **Response**:
  - Status: 200 OK

### 2.2 그룹 채팅방 목록 조회
- **URL**: `/chat/room/group/list`
- **Method**: `GET`
- **Auth Required**: Yes
- **Response**:
  - Status: 200 OK
  - Body:
```json
[
  {
    "roomId": "Long",
    "roomName": "string",
    "participantCount": "integer",
    "createdAt": "datetime"
  }
]
```

### 2.3 그룹 채팅방 참여
- **URL**: `/chat/room/group/{roomId}/join`
- **Method**: `POST`
- **Auth Required**: Yes
- **Path Variable**:
  - `roomId`: Long
- **Response**:
  - Status: 200 OK

### 2.4 그룹 채팅방 나가기
- **URL**: `/chat/room/group/{roomId}/leave`
- **Method**: `DELETE`
- **Auth Required**: Yes
- **Path Variable**:
  - `roomId`: Long
- **Response**:
  - Status: 200 OK

### 2.5 개인 채팅방 생성/조회
- **URL**: `/chat/room/private/create`
- **Method**: `POST`
- **Auth Required**: Yes
- **Query Params**:
  - `otherMemberId`: Long
- **Response**:
  - Status: 200 OK
  - Body: Room ID (Long)

### 2.6 채팅 히스토리 조회
- **URL**: `/chat/history/{roomId}`
- **Method**: `GET`
- **Auth Required**: Yes
- **Path Variable**:
  - `roomId`: Long
- **Response**:
  - Status: 200 OK
  - Body:
```json
[
  {
    "messageId": "Long",
    "content": "string",
    "senderId": "Long",
    "senderName": "string",
    "timestamp": "datetime",
    "messageType": "string"
  }
]
```

### 2.7 메시지 읽음 처리
- **URL**: `/chat/room/{roomId}/read`
- **Method**: `POST`
- **Auth Required**: Yes
- **Path Variable**:
  - `roomId`: Long
- **Response**:
  - Status: 200 OK

### 2.8 내 채팅방 목록 조회
- **URL**: `/chat/my/rooms`
- **Method**: `GET`
- **Auth Required**: Yes
- **Response**:
  - Status: 200 OK
  - Body:
```json
[
  {
    "roomId": "Long",
    "roomName": "string",
    "isGroupChat": "boolean",
    "unreadCount": "integer",
    "lastMessage": "string",
    "lastMessageTime": "datetime"
  }
]
```

---

## 3. 헬스체크

### 3.1 서버 상태 확인
- **URL**: `/health`
- **Method**: `GET`
- **Auth Required**: No
- **Response**:
  - Status: 200 OK
  - Body: "OK"

---

# STOMP WebSocket API (실시간 채팅)

## 1. STOMP 연결 정보
- **WebSocket Endpoint**: `/connect`
- **Protocol**: STOMP over WebSocket
- **URL**:
  - Production: `wss://api.bob-dev.click/connect`
  - Local: `ws://localhost:8080/connect`
- **Auth**: JWT 토큰을 메시지에 포함

## 2. STOMP 구독/발행 경로

### 구독 (Subscribe) - 메시지 수신
- **그룹 채팅방 메시지 수신**: `/topic/chat/{roomId}`
  - 해당 채팅방의 모든 참여자가 메시지 수신
- **개인 메시지 수신**: `/user/queue/messages`
  - 1:1 채팅 메시지 수신용 (현재 주석 처리)

### 발행 (Send) - 메시지 전송
- **채팅 메시지 전송**: `/app/chat/{roomId}`
  - 특정 채팅방으로 메시지 전송

## 3. 메시지 형식

### 3.1 채팅 메시지 (CHAT)
```json
{
  "messageType": "CHAT",
  "roomId": 1,
  "senderId": 123,
  "content": "안녕하세요!",
  "token": "JWT_TOKEN_STRING"
}
```

### 3.2 입장 메시지 (JOIN)
```json
{
  "messageType": "JOIN",
  "roomId": 1,
  "senderId": 123,
  "content": "",
  "token": "JWT_TOKEN_STRING"
}
```

### 3.3 퇴장 메시지 (LEAVE)
```json
{
  "messageType": "LEAVE",
  "roomId": 1,
  "senderId": 123,
  "content": "",
  "token": "JWT_TOKEN_STRING"
}
```

## 4. 수신 메시지 형식
```json
{
  "messageId": 1,
  "messageType": "CHAT",
  "roomId": 1,
  "senderId": 123,
  "senderName": "홍길동",
  "content": "안녕하세요!",
  "timestamp": "2025-01-01T12:00:00"
}
```


---

# 공통 사항

## 에러 응답 형식

모든 에러는 다음 형식으로 반환됩니다:

```json
{
  "error": "에러 메시지",
  "status": "에러 코드"
}
```

### 주요 에러 코드
- `400 Bad Request`: 잘못된 요청 (비밀번호 불일치 등)
- `401 Unauthorized`: 인증 실패 (토큰 없음/만료)
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 리소스 없음 (존재하지 않는 이메일 등)
- `500 Internal Server Error`: 서버 오류

---

# 사용 예시

## 1. HTTP API 사용 예시

### 로그인 후 채팅방 목록 조회
```javascript
// 1. 로그인
const loginRes = await fetch('https://api.bob-dev.click/member/doLogin', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email: 'user@example.com', password: 'password' })
});
const { token } = await loginRes.json();
localStorage.setItem('token', token);

// 2. 채팅방 목록 조회
const roomsRes = await fetch('https://api.bob-dev.click/chat/my/rooms', {
  headers: { 'Authorization': `Bearer ${token}` }
});
const rooms = await roomsRes.json();
```

## 2. STOMP WebSocket API 사용 예시

### STOMP 연결 및 메시지 송수신
```javascript
// 1. STOMP 클라이언트 연결
const socket = new WebSocket('wss://api.bob-dev.click/connect');
const stompClient = Stomp.over(socket);

// 2. 연결 및 구독
stompClient.connect({}, (frame) => {
  console.log('STOMP Connected:', frame);

  // 3. 채팅방 메시지 구독
  const subscription = stompClient.subscribe(`/topic/chat/${roomId}`, (message) => {
    const chatMessage = JSON.parse(message.body);
    console.log('받은 메시지:', chatMessage);
    // UI에 메시지 표시
    displayMessage(chatMessage);
  });

  // 4. 입장 메시지 전송
  stompClient.send(`/app/chat/${roomId}`, {}, JSON.stringify({
    messageType: 'JOIN',
    roomId: roomId,
    senderId: userId,
    content: '',
    token: localStorage.getItem('token')
  }));
});

// 5. 채팅 메시지 전송 함수
function sendMessage(content) {
  stompClient.send(`/app/chat/${roomId}`, {}, JSON.stringify({
    messageType: 'CHAT',
    roomId: roomId,
    senderId: userId,
    content: content,
    token: localStorage.getItem('token')
  }));
}

// 6. 연결 해제
function disconnect() {
  // 퇴장 메시지 전송
  stompClient.send(`/app/chat/${roomId}`, {}, JSON.stringify({
    messageType: 'LEAVE',
    roomId: roomId,
    senderId: userId,
    content: '',
    token: localStorage.getItem('token')
  }));

  stompClient.disconnect();
}
```