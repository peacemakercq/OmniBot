package org.innov8.tcb.lex.entity;

import lombok.Getter;
import software.amazon.awssdk.services.lexmodelbuilding.model.GetBotResponse;
import software.amazon.awssdk.services.lexmodelbuilding.model.Intent;
import software.amazon.awssdk.services.lexmodelbuilding.model.PutBotResponse;

import java.util.List;


public class BotEntity
{
    @Getter
    private String name;

    @Getter
    private List<Intent> intents;

    @Getter
    private String status;

    private BotEntity(String name, List<Intent> intents, String status)
    {
        this.name = name;
        this.intents = intents;
        this.status = status;
    }

    public static BotEntity fromGetResponse(GetBotResponse response)
    {
        String name = response.name();
        List<Intent> intents = response.intents();
        String status = response.statusAsString();

        return new BotEntity(name, intents, status);
    }


    public static BotEntity fromPutResponse(PutBotResponse response)
    {
        String name = response.name();
        List<Intent> intents = response.intents();
        String status = response.statusAsString();

        return new BotEntity(name, intents, status);
    }

    @Override
    public String toString()
    {
        return "BotEntity{" +
                "name='" + name + '\'' +
                ", intents=" + intents +
                ", status='" + status + '\'' +
                '}';
    }
}

