package com.mrcruz.personapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrcruz.personapi.model.Endereco;
import com.mrcruz.personapi.repository.EnderecoRepository;
import com.mrcruz.personapi.service.EnderecoService;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class EnderecoControllerTest {

    private RequestSpecification requisicao;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @BeforeEach
    void setUp() {

        enderecoRepository.deleteAll();

//        requisicao = new RequestSpecBuilder()
//                .setBasePath("/api/v1/solicitantes")
//                .setPort(porta)
//                .setAccept(ContentType.JSON)
//                .setContentType(ContentType.JSON)
//                .log(LogDetail.ALL)
//                .build();

        requisicao = new RequestSpecBuilder()
                .setBasePath("/api/v1/person-api/enderecos")
                .setPort(8080)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void salvar() throws JsonProcessingException {

        Endereco endereco = new Endereco();
        endereco.setLogradouro("Rua 25 de Março");
        endereco.setCep("54512161");
        endereco.setNumero("568");
        endereco.setCidade("Quixeramobim");

        Response res =
                given()
                        .spec(requisicao)
                        .body(mapper.writeValueAsString(endereco))
                        .expect()
                        .statusCode(HttpStatus.CREATED.value())
                        .when()
                        .post();

        Endereco enderecoResponse = res.body().as(Endereco.class);

        assertNotNull(res.body());
        assertNotNull(enderecoResponse.getId());
        assertEquals(endereco.getLogradouro(), enderecoResponse.getLogradouro());
    }

    @Test
    void salvarExcecao() throws JsonProcessingException {

        Endereco endereco = new Endereco();
        endereco.setLogradouro("");
        endereco.setCep("54512161");
        endereco.setNumero("568");
        endereco.setCidade("Quixeramobim");


        given()
                .spec(requisicao)
                .body(mapper.writeValueAsString(endereco))
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void listar() {
    }

    @Test
    void buscar() {
    }

    @Test
    void atualizar() {
    }

    @Test
    void deletar() {
    }
}