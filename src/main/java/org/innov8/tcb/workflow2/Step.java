package org.innov8.tcb.workflow2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class Step {
    @JsonProperty
    private String name;
    @JsonProperty
    private boolean forLex;
    @JsonProperty
    private boolean isEntrance;
    @JsonProperty
    private String sendTo;
    @JsonProperty
    private List<String> questions;
    @JsonProperty
    private List<NextStep> nextSteps;
    @JsonProperty
    private List<Notification> notifications;
}
