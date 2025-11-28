package com.newgrand.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BackMsg {

    @JsonProperty("IsOk")
    public Boolean IsOk;

    @JsonProperty("ErrorCode")
    public String ErrorCode;

    @JsonProperty("Message")
    public String Message;
}
