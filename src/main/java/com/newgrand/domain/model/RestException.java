package com.newgrand.domain.model;

import org.springframework.web.client.RestClientException;

import java.io.Serializable;

public class RestException extends RestClientException implements Serializable {

    private static final long serialVersionUID = 9166280219178545623L;

    private RestClientException restClientException;
    private String body;

    public RestException(String msg, RestClientException restClientException, String body) {
        super(msg);
        this.restClientException = restClientException;
        this.body = body;
    }

    public RestClientException getRestClientException() {
        return restClientException;
    }

    public void setRestClientException(RestClientException restClientException) {
        this.restClientException = restClientException;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}