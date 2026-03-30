package com.github.senocak.easyspec.integration;

import com.github.senocak.easyspec.builder.Operator;
import com.github.senocak.easyspec.builder.SpecBuilder;
import com.github.senocak.easyspec.integration.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("")
public class TestApplication {
    private final UserRepository userRepository;

    public TestApplication(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @PostMapping("/search")
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List search(@RequestBody final SearchRequest request) throws ClassNotFoundException {
        final Class requestClass = Class.forName(request.entity());
        final SpecBuilder specBuilder = SpecBuilder.forClass(requestClass);
        for (FilterDto filterDto: request.filters()) {
            switch (filterDto.operator()) {
                case EQUAL -> specBuilder.eq(filterDto.field(), filterDto.value()[0]);
                case NOT_EQUAL -> specBuilder.ne(filterDto.field(), filterDto.value()[0]);
                case GREATER_THAN -> specBuilder.greaterThan(filterDto.field(), (Comparable<?>) filterDto.value()[0]);
                case LESS_THAN -> specBuilder.lessThan(filterDto.field(), (Comparable<?>) filterDto.value()[0]);
                case GREATER_THAN_OR_EQUAL -> specBuilder.gte(filterDto.field(), (Comparable<?>) filterDto.value()[0]);
                case LESS_THAN_OR_EQUAL -> specBuilder.lte(filterDto.field(), (Comparable<?>) filterDto.value()[0]);
                case CONTAINS -> specBuilder.contains(filterDto.field(), (String) filterDto.value()[0]);
                case STARTS_WITH -> specBuilder.startsWith(filterDto.field(), (String) filterDto.value()[0]);
                case ENDS_WITH -> specBuilder.endsWith(filterDto.field(), (String) filterDto.value()[0]);
                case IN -> specBuilder.in(filterDto.field(), (List<?>) filterDto.value()[0]);
                case BETWEEN -> specBuilder.between(filterDto.field(), (Comparable<?>) filterDto.value()[0], (Comparable<?>) filterDto.value()[1]);
                case IS_NULL -> specBuilder.isNull(filterDto.field());
                case IS_NOT_NULL -> specBuilder.isNotNull(filterDto.field());
                case ORDER_BY -> specBuilder.orderBy(filterDto.field(), Sort.Direction.fromString(filterDto.value()[0].toString()));
            }
        }
        return userRepository.findAll(specBuilder.build());
    }
}

record SearchRequest(String entity, List<FilterDto> filters){}
record FilterDto(String field, Operator operator, Object... value) {}
