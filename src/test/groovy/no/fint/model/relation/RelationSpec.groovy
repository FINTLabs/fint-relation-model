package no.fint.model.relation

import tools.jackson.databind.json.JsonMapper
import spock.lang.Specification
import spock.lang.Unroll

class RelationSpec extends Specification {

    def "Create relation with base url, path, field and value"() {
        when:
        def relation = new Relation.Builder().with(TestDto.Relasjonsnavn.TESTREL).forType(TestDto2).field('test-field').value('123').build()

        then:
        relation.relationName == 'testrel'
        relation.link == '${relation.testdto2}/test-field/123'
    }

    def "Create relation with base url, path and value"() {
        when:
        def relation = new Relation.Builder().with(TestDto.Relasjonsnavn.TESTREL).forType(TestDto2).value('123').build()

        then:
        relation.relationName == 'testrel'
        relation.link == '${relation.testdto2}/123'
    }

    def "Create relation with link"() {
        when:
        def relation = new Relation.Builder().with(TestDto.Relasjonsnavn.TESTREL).link('http://localhost/test').build()

        then:
        relation.relationName == 'testrel'
        relation.link == 'http://localhost/test'
    }

    def "Throw IllegalArgumentException when link / type & path is not set"() {
        when:
        new Relation.Builder().with(TestDto.Relasjonsnavn.TESTREL).build()

        then:
        thrown(IllegalArgumentException)
    }

    def "Create type with a FINT package structure"() {
        when:
        def type = Relation.createType('no.fint.model.test.TestDto')

        then:
        type == 'test.testdto'
    }

    def "Create type with a non FINT package structure"() {
        when:
        def type = Relation.createType(String.name)

        then:
        type == 'java.lang.string'
    }

    def "Serialize and deserialize Relation to JSON"() {
        given:
        def objectMapper = JsonMapper.builder().build()
        def relation = new Relation.Builder().with(TestDto.Relasjonsnavn.TESTREL).forType(TestDto2).value('123').build()

        when:
        def json = objectMapper.writeValueAsString(relation)
        def result = objectMapper.readValue(json, Relation)

        then:
        relation == result
    }

    @Unroll
    def "Reject generated link with type #type and value #value"() {
        when:
        new Relation.Builder().with(TestDto.Relasjonsnavn.TESTREL).forType(type).value(value).build()

        then:
        thrown(IllegalArgumentException)

        where:
        type     | value
        null     | null
        null     | '123'
        TestDto2 | null
    }

    @Unroll
    def "Generate a link when explicit link is #link"() {
        when:
        def relation = new Relation.Builder().with(TestDto.Relasjonsnavn.TESTREL)
                .link(link).forType(TestDto2).value('123').build()

        then:
        relation.relationName == 'testrel'
        relation.link == '${relation.testdto2}/123'

        where:
        link << [null, '']
    }

    def "Explicit link takes precedence over generated link fields"() {
        when:
        def relation = new Relation.Builder().with(TestDto.Relasjonsnavn.TESTREL)
                .forType(TestDto2).field('ignored').value('123').link('https://example.org/resource').build()

        then:
        relation.link == 'https://example.org/resource'
    }

    def "Deserialize relation while ignoring unknown JSON properties"() {
        when:
        def relation = JsonMapper.builder().build().readValue(
                '{"relationName":"testrel","link":"/resource/123","futureField":true}', Relation)

        then:
        relation.relationName == 'testrel'
        relation.link == '/resource/123'
    }

    def "String representation includes relation name and link"() {
        when:
        def description = new Relation.Builder().with(TestDto.Relasjonsnavn.TESTREL)
                .link('/resource/123').build().toString()

        then:
        description.contains('testrel')
        description.contains('/resource/123')
    }

    @Unroll
    def "Relation equality uses both fields: #nameA, #linkA versus #nameB, #linkB"() {
        given:
        def first = new Relation(relationName: nameA, link: linkA)
        def second = new Relation(relationName: nameB, link: linkB)

        expect:
        first.equals(second) == equal
        second.equals(first) == equal
        first.equals(first)
        !first.equals(null)
        !first.equals('unrelated')
        !equal || first.hashCode() == second.hashCode()
        new HashSet([first, second]).size() == (equal ? 1 : 2)

        where:
        nameA     | linkA  | nameB     | linkB    | equal
        'testrel' | '/123' | 'testrel' | '/123'   | true
        null      | null   | null      | null     | true
        null      | '/123' | null      | '/123'   | true
        'testrel' | null   | 'testrel' | null     | true
        'testrel' | '/123' | 'other'   | '/123'   | false
        'testrel' | '/123' | null      | '/123'   | false
        'testrel' | '/123' | 'testrel' | '/other' | false
        'testrel' | '/123' | 'testrel' | null     | false
    }
}
