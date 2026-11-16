package no.fint.model.relation

import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Specification
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.json.JsonMapper

class SpringBootCompatibilitySpec extends Specification {
    private AnnotationConfigApplicationContext context
    private JsonMapper mapper

    void setup() {
        context = new AnnotationConfigApplicationContext(JacksonAutoConfiguration)
        mapper = context.getBean(JsonMapper)
    }

    void cleanup() {
        context?.close()
    }

    def "Boot mapper preserves resource JSON properties and typed round trips"() {
        given:
        def model = new TestDto(id: '123')
        def relation = new Relation.Builder().with(TestDto.Relasjonsnavn.TESTREL)
                .forType(TestDto).field('id').value('123').build()
        def resource = FintResource.with(model).addRelations(relation)

        when:
        def json = mapper.writeValueAsString(resource)
        def tree = mapper.readTree(json)
        def result = mapper.readValue(json, new TypeReference<FintResource<TestDto>>() {})

        then:
        tree.size() == 2
        tree.get('resource').get('id').asText() == '123'
        tree.get('relations').size() == 1
        tree.get('relations').get(0).size() == 2
        tree.get('relations').get(0).get('relationName').asText() == 'testrel'
        tree.get('relations').get(0).get('link').asText() == '${relation.testdto}/id/123'
        result == resource
        result.resource instanceof TestDto
    }

    def "Boot mapper ignores unknown wrapper and relation JSON properties"() {
        given:
        def json = '''{
            "resource": {"id": "123"},
            "relations": [{"relationName": "testrel", "link": "/123", "futureRelation": true}],
            "futureWrapper": true
        }'''

        when:
        def result = mapper.readValue(json, new TypeReference<FintResource<TestDto>>() {})

        then:
        result.resource.id == '123'
        result.relations.size() == 1
        result.relations[0].relationName == 'testrel'
        result.relations[0].link == '/123'
    }
}
