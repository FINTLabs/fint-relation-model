package no.novari.fint.model.relation;

import lombok.Data;

@Data
public class TestDto {
    public enum Relasjonsnavn {
        TESTREL
    }

    private String id = "id";

}
