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
//
//public class CtfController1 {
//
//    // ===== 공통 유틸 =====
//    private static Map<String, Object> resp(Object... kv) {
//        Map<String, Object> m = new LinkedHashMap<>();
//        for (int i = 0; i + 1 < kv.length; i += 2) m.put(String.valueOf(kv[i]), kv[i + 1]);
//        return m;
//    }
//    private static String str(Object v) { return v == null ? null : String.valueOf(v); }
//    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
//
//    // ===== Stage 안내: GET은 항상 JSON으로 설명/힌트만 반환 =====
//    @GetMapping(value = "/1", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Map<String, Object> stage1Info() {
//        return resp(
//                "ok", true,
//                "stage", "1",
//                "type", "sqli",
//                "message", "password 필드에 SQLi 페이로드를 보내 admin 비밀번호를 획득한 뒤 같은 엔드포인트에 제출하세요.",
//                "usage", Map.of(
//                        "endpoint", "POST /ctf/1",
//                        "contentType", "application/json or x-www-form-urlencoded",
//                        "example", Map.of("password", "' OR '1'='1' #")
//                ),
//                "nextHint", "정답 비밀번호를 얻으면 같은 /ctf/1 에서 password=정답 으로 제출"
//        );
//    }
//
//    @GetMapping(value = "/2", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Map<String, Object> stage2Info() {
//        return resp(
//                "ok", true,
//                "stage", "2",
//                "type", "xss",
//                "message", "comment 또는 password 필드에 정확한 페이로드를 제출하세요.",
//                "usage", Map.of(
//                        "endpoint", "POST /ctf/2",
//                        "contentType", "json / x-www-form-urlencoded / multipart",
//                        "fields", List.of("comment", "password"),
//                        "example", Map.of("comment", "<script>alert(1)</script>")
//                ),
//                "nextHint", "정답이면 next=/ctf/3"
//        );
//    }
//
//    @GetMapping(value = "/3", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Map<String, Object> stage3Info() {
//        return resp(
//                "ok", true,
//                "stage", "3",
//                "type", "next",
//                "message", "축하합니다! 다음 미션은 여기서 이어집니다."
//        );
//    }
//
//    // ===== Stage 2 (XSS) =====
//    private static final String EXACT_XSS = "<script>alert(1)</script>";
//    private static final String FLAG_XSS = "FLAG{XSS_STAGE2_PWNED}";
//
//    // JSON 입력
//    @PostMapping(
//            value = "/2",
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<?> xssJson(
//            @RequestBody Map<String, Object> body,
//            @RequestParam(name = "redirect", defaultValue = "false") boolean redirect
//    ) {
//        return handle2(flattenForXss(body), redirect);
//    }
//
//    // x-www-form-urlencoded
//    @PostMapping(
//            value = "/2",
//            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<?> xssForm(
//            @RequestParam Map<String, String> form,
//            @RequestParam(name = "redirect", defaultValue = "false") boolean redirect
//    ) {
//        return handle2(new HashMap<>(form), redirect);
//    }
//
//    // multipart/form-data
//    @PostMapping(
//            value = "/2",
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<?> xssMultipart(
//            @RequestParam Map<String, String> form,
//            @RequestParam(name = "redirect", defaultValue = "false") boolean redirect
//    ) {
//        return handle2(new HashMap<>(form), redirect);
//    }
//
//    private ResponseEntity<?> handle2(Map<String, String> in, boolean redirect) {
//        String comment = firstNonBlank(in.get("comment"), in.get("password"));
//
//        if (isBlank(comment)) {
//            return ResponseEntity.ok(resp(
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
//            Map<String, Object> body = resp(
//                    "ok", true,
//                    "stage", "cleared",
//                    "message", "XSS 트리거 확인!",
//                    "flag", FLAG_XSS,
//                    "next", "/ctf/3",
//                    "echo", comment
//            );
//
//            // 기본: JSON만 (리다이렉트 없음) → 리액트에서 body.next 읽고 처리
//            if (!redirect) return ResponseEntity.ok(body);
//
//            // 필요시 303 제공 (리액트가 Location 헤더를 읽어 라우팅)
//            return ResponseEntity.status(HttpStatus.SEE_OTHER)
//                    .header(HttpHeaders.LOCATION, "/ctf/3")
//                    .body(body);
//        }
//
//        return ResponseEntity.ok(resp(
//                "ok", false,
//                "stage", "verify",
//                "message", "정확한 페이로드가 아닙니다. comment/password 중 하나에 정확히 제출하세요.",
//                "hint", EXACT_XSS
//        ));
//    }
//
//    @SuppressWarnings("unchecked")
//    private Map<String, String> flattenForXss(Map<String, Object> body) {
//        Map<String, String> m = new HashMap<>();
//        if (body == null) return m;
//
//        putIfPresent(m, "comment", body.get("comment"));
//        putIfPresent(m, "password", body.get("password"));
//
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
//    private String firstNonBlank(String... ss) {
//        if (ss == null) return null;
//        for (String s : ss) if (!isBlank(s)) return s;
//        return null;
//    }
//
//    // ===== Stage 1 (SQLi) =====
//    private static final String MAGIC_PAYLOAD = "' or '1'='1' #";
//    private static final String ADMIN_PW = "siuu";
//
//    // JSON
//    @PostMapping(
//            value = "/1",
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<?> stage1Json(
//            @RequestBody Map<String, Object> body,
//            @RequestParam(name = "redirect", defaultValue = "false") boolean redirect
//    ) {
//        return handleStage1(new HashMap<>(body), redirect);
//    }
//
//    // x-www-form-urlencoded
//    @PostMapping(
//            value = "/1",
//            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<?> stage1Form(
//            @RequestParam Map<String, String> form,
//            @RequestParam(name = "redirect", defaultValue = "false") boolean redirect
//    ) {
//        return handleStage1(new HashMap<>(form), redirect);
//    }
//
//    private ResponseEntity<?> handleStage1(Map<?, ?> in, boolean redirect) {
//        String pw = str(in.get("password"));
//        if (pw == null || pw.isBlank()) {
//            return ResponseEntity.ok(resp(
//                    "ok", false,
//                    "stage", "discover",
//                    "message", "password 필드에 SQLi 페이로드를 넣어 admin 비밀번호를 먼저 획득하세요.",
//                    "usage", Map.of("password_example", "' OR '1'='1' #")
//            ));
//        }
//
//        if (matchPassword(pw)) {
//            Map<String, Object> body = resp(
//                    "ok", true,
//                    "stage", "cleared",
//                    "message", "정답 비밀번호 확인!",
//                    "flag", "FLAG{SQLI_STAGE1_CLEARED}",
//                    "next", "/ctf/2"
//            );
//            if (!redirect) return ResponseEntity.ok(body);
//            return ResponseEntity.status(HttpStatus.SEE_OTHER)
//                    .header(HttpHeaders.LOCATION, "/ctf/2")
//                    .body(body);
//        }
//
//        if (matchSqliPayload(pw)) {
//            return ResponseEntity.ok(resp(
//                    "ok", true,
//                    "stage", "pw_revealed",
//                    "message", "성공! admin의 비밀번호를 획득했습니다. 같은 엔드포인트에 password로 제출하세요.",
//                    "admin", Map.of("username", "admin", "password", ADMIN_PW),
//                    "hint", "POST /ctf/1 에 { password: \"siuu\" }로 다시 보내세요."
//            ));
//        }
//
//        return ResponseEntity.ok(resp(
//                "ok", false,
//                "stage", "verify",
//                "message", "password 값이 올바르지 않습니다. 정확한 SQLi 페이로드 또는 획득한 비밀번호로 다시 시도하세요.",
//                "hint", "SQLi 예: ' OR '1'='1' #"
//        ));
//    }
//
//    private static boolean matchSqliPayload(String s) {
//        String norm = s.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
//        return MAGIC_PAYLOAD.equals(norm);
//    }
//    private static boolean matchPassword(String s) { return ADMIN_PW.equalsIgnoreCase(s.trim()); }
//}
