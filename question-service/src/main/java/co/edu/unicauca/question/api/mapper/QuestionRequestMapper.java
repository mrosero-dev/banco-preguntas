package co.edu.unicauca.question.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.edu.unicauca.question.api.dto.QuestionOptionRequest;
import co.edu.unicauca.question.api.dto.QuestionOptionResponse;
import co.edu.unicauca.question.api.dto.QuestionResponse;
import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionOption;

@Mapper(componentModel = "spring")
public interface QuestionRequestMapper {

    // Convierte las opciones recibidas en la API al dominio.
    @Mapping(target = "id", ignore = true)
    QuestionOption toDomain(QuestionOptionRequest request);

    List<QuestionOption> toDomainOptions(List<QuestionOptionRequest> options);

    // Convierte las opciones del dominio a la respuesta de la API.
    QuestionOptionResponse toOptionResponse(QuestionOption option);

    List<QuestionOptionResponse> toOptionResponses(List<QuestionOption> options);

    // Convierte una pregunta del dominio a la respuesta de la API.
    @Mapping(target = "status", expression = "java(question.getStatus().name())")
    QuestionResponse toResponse(Question question);
}