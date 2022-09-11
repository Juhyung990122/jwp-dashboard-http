package org.apache.coyote.http;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;

public class HttpResponseBuilder {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_DELIMITER = "=";
    private HttpStatus httpStatus;
    private Map<String, String> header;
    private String body;
    private HttpCookie cookie;

    public HttpResponseBuilder(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        this.header = new HashMap<>();
        this.body = "";
    }

    public HttpResponseBuilder header(final String key, final String value) {
        header.put(key, value);
        return this;
    }

    public HttpResponseBuilder body(final Path path) {
        try {
            header.put("Content-Type", Files.probeContentType(path) + ";charset=utf-8");
        } catch (IOException e) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
        }

        this.body = readFile(path);
        return this;
    }

    public HttpResponseBuilder body(final String bodyValue) {
        header.put("Content-Type", "text/html;charset=utf-8");
        this.body = bodyValue;
        return this;
    }

    public HttpResponseBuilder body(final Object object) {
        this.body = object.toString();
        return this;
    }

    public HttpResponseBuilder addCookie(HttpCookie cookie, Session session) {
        if(!cookie.checkJSessionIdInCookie()){
            final Map<String, String> setCookie = cookie.ofJSessionId(session.getId());
            header.put("Set-Cookie", JSESSIONID + COOKIE_DELIMITER + setCookie.get(JSESSIONID));
        }
        return this;
    }

    public HttpResponse build() {
        if (httpStatus == HttpStatus.NOT_FOUND) {
            body(Paths.get(Objects.requireNonNull(
                    getClass()
                    .getClassLoader()
                    .getResource("static/404.html"))
                    .getPath()));
        }
        return new HttpResponse(httpStatus, header, body, cookie);
    }

    private String readFile(final Path filePath) {
        try {
            return new String(Files.readAllBytes(filePath));
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }
}
