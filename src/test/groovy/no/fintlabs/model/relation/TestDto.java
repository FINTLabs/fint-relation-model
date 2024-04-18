package no.fintlabs.model.relation;

import lombok.Data;

@Data
public class TestDto {
    public enum Relasjonsnavn {
        TESTREL
    }

    private String id = "id";

}
