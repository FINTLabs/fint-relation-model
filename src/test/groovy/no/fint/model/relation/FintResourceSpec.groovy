package no.fint.model.relation

import tools.jackson.core.type.TypeReference
import tools.jackson.databind.json.JsonMapper
import spock.lang.Specification
import spock.lang.Unroll

class FintResourceSpec extends Specification {
    private Relation relation
    private TestDto testDto

    void setup() {
        relation = new Relation.Builder().with(TestDto.Relasjonsnavn.TESTREL).link('http://localhost/test').build()
        testDto = new TestDto()
    }

    def "Create new FintResource with single relation"() {
        when:
        def fintResource = FintResource.with(testDto).addRelations(relation)

        then:
        fintResource.resource == testDto
        fintResource.relations.size() == 1
        fintResource.relations[0] == relation
    }

    def "Create new FintResource with relation list"() {
        when:
        def fintResource = FintResource.with(testDto).addRelations([relation, relation])

        then:
        fintResource.resource == testDto
        fintResource.relations.size() == 2
        fintResource.relations[0] == relation
        fintResource.relations[1] == relation
    }

    def "Serialize and deserialize FintResource to JSON"() {
        given:
        def objectMapper = JsonMapper.builder().build()
        def fintResource = FintResource.with('test')

        when:
        def json = objectMapper.writeValueAsString(fintResource)
        def result = objectMapper.readValue(json, FintResource)

        then:
        fintResource == result
    }

    def "New resources have independent empty relation lists"() {
        given:
        def first = new FintResource<TestDto>()
        def second = FintResource.with(testDto)

        expect:
        first.resource == null
        first.relations.empty
        second.resource.is(testDto)
        second.relations.empty

        when:
        first.addRelations(relation)

        then:
        first.relations == [relation]
        second.relations.empty
    }

    def "Adding relations appends in order and returns the same resource"() {
        given:
        def other = new Relation.Builder().with(TestDto.Relasjonsnavn.TESTREL).link('/other').build()
        def resource = FintResource.with(testDto)

        when:
        def afterVarargs = resource.addRelations(relation, other)
        def afterList = resource.addRelations([other, relation])

        then:
        afterVarargs.is(resource)
        afterList.is(resource)
        resource.resource.is(testDto)
        resource.relations == [relation, other, other, relation]
    }

    def "Adding empty collections preserves existing relations"() {
        given:
        def resource = FintResource.with(testDto).addRelations(relation)

        expect:
        resource.addRelations().is(resource)
        resource.addRelations([]).is(resource)
        resource.relations == [relation]
    }

    def "Relation list is copied when added"() {
        given:
        def input = [relation]
        def resource = FintResource.with(testDto).addRelations(input)

        when:
        input.clear()

        then:
        resource.relations == [relation]
    }

    def "Round trip a typed resource with populated relations"() {
        given:
        def mapper = JsonMapper.builder().build()
        def resource = FintResource.with(testDto).addRelations(relation)

        when:
        def json = mapper.writeValueAsString(resource)
        def result = mapper.readValue(json, new TypeReference<FintResource<TestDto>>() {})

        then:
        result == resource
        result.resource instanceof TestDto
        result.resource.id == 'id'
        result.relations == [relation]
    }

    def "Deserialize resource while ignoring unknown JSON properties"() {
        when:
        def resource = JsonMapper.builder().build().readValue(
                '{"resource":"model","futureField":true}', FintResource)

        then:
        resource.resource == 'model'
        resource.relations.empty
    }

    def "String representation includes the model and relation details"() {
        when:
        def description = FintResource.with('example-model').addRelations(relation).toString()

        then:
        description.contains('example-model')
        description.contains('testrel')
        description.contains('http://localhost/test')
    }

    @Unroll
    def "Resource equality uses model and relations: #modelA, #linksA versus #modelB, #linksB"() {
        given:
        def first = new FintResource()
        first.setResource(modelA)
        first.setRelations(linksA?.collect { new Relation(link: it) })
        def second = new FintResource()
        second.setResource(modelB)
        second.setRelations(linksB?.collect { new Relation(link: it) })

        expect:
        first.equals(second) == equal
        second.equals(first) == equal
        first.equals(first)
        !first.equals(null)
        !first.equals('unrelated')
        !equal || first.hashCode() == second.hashCode()
        new HashSet([first, second]).size() == (equal ? 1 : 2)

        where:
        modelA  | linksA   | modelB  | linksB   | equal
        'model' | ['/123'] | 'model' | ['/123'] | true
        null    | null     | null    | null     | true
        null    | ['/123'] | null    | ['/123'] | true
        'model' | null     | 'model' | null     | true
        'model' | ['/123'] | 'other' | ['/123'] | false
        'model' | ['/123'] | null    | ['/123'] | false
        'model' | ['/123'] | 'model' | []       | false
        'model' | ['/123'] | 'model' | null     | false
    }
}
