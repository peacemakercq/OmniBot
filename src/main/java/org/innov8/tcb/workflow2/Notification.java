package org.innov8.tcb.workflow2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Notification {
    @JsonProperty
    private String sendTo;
    @JsonProperty
    private String message;
    @JsonProperty
    private String condition;
}
