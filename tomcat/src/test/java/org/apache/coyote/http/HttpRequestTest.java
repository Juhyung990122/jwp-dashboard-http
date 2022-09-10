package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("url내의 쿼리문을 키/값 쌍으로 매핑하여 값으로 찾아온다.")
    void hasQuery_true() {
        final HttpRequest httpRequest = new HttpRequest(
                new HttpRequestLine("GET /login?account=gugu&password=password HTTP/1.1"),
                new Header(new HashMap<>()),
                ""
        );

        assertThat(httpRequest.findQueryByKey("account").get()).isEqualTo("gugu");
        assertThat(httpRequest.findQueryByKey("password").get()).isEqualTo("password");
    }

    @Test
    @DisplayName("Body내에 쿼리문이 있는 경우 키/값 쌍으로 매핑하여 값으로 찾아온다.")
    void hasQuery_false() {

        final HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type","application/x-www-form-urlencoded");

        final HttpRequest httpRequest = new HttpRequest(
                new HttpRequestLine("GET /login"),
                new Header(headerMap),
                "account=gugu&password=password"
        );

        assertThat(httpRequest.findQueryByKey("account").get()).isEqualTo("gugu");
        assertThat(httpRequest.findQueryByKey("password").get()).isEqualTo("password");
    }


    @Test
    @DisplayName("쿼리스트링이 없다면 예외를 반환한다.")
    void getQueryByValue_NotFoundQuery() {
        final HttpRequest httpRequest = new HttpRequest(
                new HttpRequestLine("GET /login"),
                new Header(new HashMap<>()),
                ""
        );

        assertThatThrownBy(() -> httpRequest.findQueryByKey("test").get())
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("요청내에 QueryString이 존재하지 않습니다.");
    }
}
