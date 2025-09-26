//package com.example.chatserver.ctf;
//
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.*;
//
//@RestController
//@RequestMapping("/ctf")
//public class CtfController {
//
//    // ===== XSS 단계 (/ctf/2) =====
//    private static final String EXACT_XSS = "<script>alert(1)</script>";
//    private static final String FLAG = "FLAG{XSS_STAGE2_PWNED}";
//    @GetMapping("/3")
//    public String test3() { return "ctf3"; }
//
//    @GetMapping("/2")
//    public String test() { return "ctf2"; }
//
//    // JSON 입력 (중첩 키 일부 허용)
//    @PostMapping(
//            value = "/2",
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<?> xssJson(@RequestBody Map<String, Object> body) {
//        return handle2(flattenForXss(body));
//    }
//
//    // x-www-form-urlencoded 입력
//    @PostMapping(
//            value = "/2",
//            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<?> xssForm(@RequestParam Map<String, String> form) {
//        return handle2(new HashMap<>(form));
//    }
//
//    // multipart/form-data 입력 (Postman form-data)
//    @PostMapping(
//            value = "/2",
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<?> xssMultipart(@RequestParam Map<String, String> form) {
//        return handle2(new HashMap<>(form));
//    }
//
//    // XSS 처리부: comment 또는 password 둘 다 허용
//    private ResponseEntity<?> handle2(Map<String, String> in) {
//        String comment = firstNonBlank(
//                in.get("comment"),
//                in.get("password")
//        );
//
//        if (isBlank(comment)) {
//            return ResponseEntity.ok(Map.of(
//                    "ok", false,
//                    "stage", "discover",
//                    "message", "XSS 페이로드를 comment 또는 password 필드에 제출하세요.",
//                    "usage", Map.of(
//                            "comment_example", EXACT_XSS,
//                            "password_example", EXACT_XSS
//                    )
//            ));
//        }
//
//        if (EXACT_XSS.equalsIgnoreCase(comment.trim())) {
//            Map<String, Object> body = Map.of(
//                    "ok", true,
//                    "stage", "cleared",
//                    "message", "XSS 트리거 확인! 다음 문제로 이동합니다.",
//                    "flag", FLAG,
//                    "next", "/ctf/3",
//                    "echo", comment
//            );
//            return ResponseEntity
//                    .status(HttpStatus.SEE_OTHER)          // 303
//                    .header(HttpHeaders.LOCATION, "/ctf/3") // 다음 문제
//                    .body(body);
//        }
//
//        return ResponseEntity.ok(Map.of(
//                "ok", false,
//                "stage", "verify",
//                "message", "정확한 페이로드가 아닙니다. comment/password 중 하나에 정확히 제출하세요.",
//                "hint", EXACT_XSS
//        ));
//    }
//
//    // JSON 바디에서 필요한 키만 1뎁스 맵으로 정리
//    @SuppressWarnings("unchecked")
//    private Map<String, String> flattenForXss(Map<String, Object> body) {
//        Map<String, String> m = new HashMap<>();
//        if (body == null) return m;
//
//        // 루트
//        putIfPresent(m, "comment", body.get("comment"));
//        putIfPresent(m, "password", body.get("password"));
//
//        // 흔한 래핑 키 지원 (data, payload)
//        Object data = body.get("data");
//        if (data instanceof Map<?, ?> dm) {
//            putIfPresent(m, "comment", ((Map<String, Object>) dm).get("comment"));
//            putIfPresent(m, "password", ((Map<String, Object>) dm).get("password"));
//        }
//        Object payload = body.get("payload");
//        if (payload instanceof Map<?, ?> pm) {
//            putIfPresent(m, "comment", ((Map<String, Object>) pm).get("comment"));
//            putIfPresent(m, "password", ((Map<String, Object>) pm).get("password"));
//        }
//        return m;
//    }
//
//    private void putIfPresent(Map<String, String> out, String key, Object v) {
//        if (v == null) return;
//        String s = String.valueOf(v);
//        if (!isBlank(s)) out.put(key, s);
//    }
//
//    private String firstNonBlank(String... ss) {
//        if (ss == null) return null;
//        for (String s : ss) if (!isBlank(s)) return s;
//        return null;
//    }
//
//    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
//
//    // ===== SQLi 단계 (/ctf/1) =====
//    private static final String MAGIC_PAYLOAD = "' or '1'='1' #";
//    private static final String ADMIN_PW = "siuu";
//
//    // JSON 입력
//    @PostMapping(
//            value = "/1",
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<?> stage1Json(@RequestBody Map<String, Object> body) {
//        return handle(new HashMap<>(body));
//    }
//
//    // x-www-form-urlencoded 입력
//    @PostMapping(
//            value = "/1",
//            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<?> stage1Form(@RequestParam Map<String, String> form) {
//        return handle(new HashMap<>(form));
//    }
//
//    // 공통 처리 (SQLi)
//    private ResponseEntity<?> handle(Map<?, ?> in) {
//        String pw = str(in.get("password"));
//        if (pw == null || pw.isBlank()) {
//            return ResponseEntity.ok(Map.of(
//                    "ok", false,
//                    "stage", "discover",
//                    "message", "password 필드에 SQLi 페이로드를 넣어 admin 비밀번호를 먼저 획득하세요.",
//                    "usage", Map.of("password_example", "' OR '1'='1' #")
//            ));
//        }
//
//        if (matchPassword(pw)) {
//            Map<String, Object> body = Map.of(
//                    "ok", true,
//                    "stage", "cleared",
//                    "message", "정답 비밀번호 확인! 다음 문제로 이동합니다.",
//                    "flag", "FLAG{SQLI_STAGE1_CLEARED}",
//                    "next", "/ctf/2"
//            );
//            return ResponseEntity
//                    .status(HttpStatus.SEE_OTHER)
//                    .header(HttpHeaders.LOCATION, "/ctf/2")
//                    .body(body);
//        }
//
//        if (matchSqliPayload(pw)) {
//            return ResponseEntity.ok(Map.of(
//                    "ok", true,
//                    "stage", "pw_revealed",
//                    "message", "성공! admin의 비밀번호를 획득했습니다. 같은 엔드포인트에 password로 제출하여 통과하세요.",
//                    "admin", Map.of("username", "admin", "password", ADMIN_PW),
//                    "hint", "POST /ctf/1 에 { password: \"siuu\" }로 다시 보내세요."
//            ));
//        }
//
//        return ResponseEntity.ok(Map.of(
//                "ok", false,
//                "stage", "verify",
//                "message", "password 값이 올바르지 않습니다. 정확한 SQLi 페이로드 또는 획득한 비밀번호로 다시 시도하세요.",
//                "hint", "SQLi 예: ' OR '1'='1' #"
//        ));
//    }
//
//    // ===== 유틸 =====
//    private static String str(Object v) { return v == null ? null : String.valueOf(v); }
//    private static boolean matchSqliPayload(String s) {
//        String norm = s.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
//        return MAGIC_PAYLOAD.equals(norm);
//    }
//    private static boolean matchPassword(String s) { return ADMIN_PW.equalsIgnoreCase(s.trim()); }
//}
