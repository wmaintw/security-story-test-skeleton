package com.tw.security.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.core.Is.is;

public class SecurityHeaderSteps {

    private final CloseableHttpClient httpClient;
    private CloseableHttpResponse response;
    private String serverHostAndPort;

    public SecurityHeaderSteps() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        httpClient = buildHttpClient();
    }

    @Given("^web server is running on \"(.*)\"$")
    public void givenARunningServer(String serverHostAndPort) {
        System.out.println("server should be running on " + serverHostAndPort);
        this.serverHostAndPort = serverHostAndPort;
    }

    @When("^the URL (.*) was visited$")
    public void sendRequestToGivenUrl(String targetURL) throws IOException {
        String url = format("%s%s", serverHostAndPort, targetURL);
        response = httpClient.execute(new HttpGet(url));
    }

    @Then("^the response contains \"(.*)\" header with value \"(\\w.*)\"$")
    public void assertSecurityHeader(String headerName, String expectedHeaderValue) {
        String actualHeaderValue = getSingleHeaderValue(headerName);
        assertThat(actualHeaderValue, equalToIgnoringCase(expectedHeaderValue));
    }

    @Then("^the response contains \"(.*)\" header is either \"(.*)\" or \"(.*)\"$")
    public void assertThatHeaderValueShouldBeEitherValue1OrValue2(String headerName, String headerValue1, String headerValue2) {
        String actualHeaderValue = getSingleHeaderValue(headerName);
        assertThat(actualHeaderValue.equalsIgnoreCase(headerValue1), is(true));
        assertThat(actualHeaderValue, either(equalToIgnoringCase(headerValue1))
                .or(equalToIgnoringCase(headerValue2)));
    }

    @Then("the response contains \"(.*)\" header is set")
    public void assertThatHeaderIsInPlace(String headerName) {
        String actualHeaderValue = getSingleHeaderValue(headerName);
        assertThat(actualHeaderValue, not(isEmptyOrNullString()));
        assertThat(isNotBlank(actualHeaderValue), is(true));
    }

    private String getSingleHeaderValue(String headerName) {
        Header[] headers = response.getHeaders(headerName);
        assertThat(headers.length, is(1));
        return headers[0].getValue();
    }

    private CloseableHttpClient buildHttpClient() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        CloseableHttpClient httpClient;HttpClientBuilder builder = HttpClientBuilder.create();

        SSLContext ignoreErrorSSLContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
        }).build();
        builder.setSSLContext(ignoreErrorSSLContext);

        httpClient = builder.build();
        return httpClient;
    }
}
