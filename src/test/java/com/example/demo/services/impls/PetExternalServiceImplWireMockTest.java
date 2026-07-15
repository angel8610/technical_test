package com.example.demo.services.impls;

import com.example.demo.exceptions.PetException;
import com.example.demo.services.PetExternalService;
import com.example.demo.vos.PetVO;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.client.RestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PetExternalServiceImplWireMockTest {

  private WireMockServer wireMockServer;

  private static final Long ID = 1L;
  private static final String PET_NAME = "PetName";
  private static final String STATUS_AVAILABLE = "available";

  private PetExternalService petExternalService;

  @BeforeEach
  void setUp() {
    wireMockServer = new WireMockServer(options().dynamicPort());
    wireMockServer.start();
    WireMock.configureFor("localhost", wireMockServer.port());
    wireMockServer.resetAll();

    RestClient restClient = RestClient.builder()
      .baseUrl("http://localhost:" + wireMockServer.port() + "/pet")
      .build();
    petExternalService = new PetExternalServiceImpl(restClient, RetryRegistry.ofDefaults());
  }

  @AfterEach
  void tearDown() {
    wireMockServer.stop();
  }

  @Test
  void findPetByIdSuccessfully() {
    wireMockServer.stubFor(
      get(urlEqualTo("/pet/1"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("""
              {
                "id": 1,
                "name": "PetName",
                "status": "available"
              }
            """)
        )
    );

    PetVO petVO = this.petExternalService.findById(1L).orElse(null);
    assertNotNull(petVO);
    assertEquals(PET_NAME, petVO.getName());
    assertEquals(STATUS_AVAILABLE, petVO.getStatus());
  }

  @Test
  void findByIdPetErrorConnectServerSecondConnectSuccessfully() {
    wireMockServer.stubFor(get(urlEqualTo("/pet/1"))
      .inScenario("retry")
      .whenScenarioStateIs(STARTED)
      .willReturn(serverError())
      .willSetStateTo("SECOND"));

    wireMockServer.stubFor(get(urlEqualTo("/pet/1"))
      .inScenario("retry")
      .whenScenarioStateIs("SECOND")
      .willReturn(okJson("""
          {
            "id": 1,
            "name": "PetName",
            "status": "available"
          }
        """)
      )
    );

    PetVO petVO = this.petExternalService.findById(1L).orElse(null);

    assertNotNull(petVO);
    wireMockServer.verify(2, getRequestedFor(urlEqualTo("/pet/1")));
  }

  @Test
  void findByIdPetErrorConnectServerShouldThrowPetException() {
    PetExternalService service = this.getBadPetExternalService();

    assertThrows(PetException.class, () -> service.findById(1L));
  }

  @Test
  void findPetByIdNotFoundThrowPetNotFoundException() {
    this.findPetByIdCheckThrowError(10L, 404, 1);
  }

  @Test
  void findByIdShouldRetryWhenOccursError4xx() {
    this.findPetByIdCheckThrowError(1L, 403, 1);
  }

  @Test
  void findByIdShouldRetryWhenOccursError5xx() {
    this.findPetByIdCheckThrowError(1L, 500, 3);
  }

  @Test
  void savePetSuccessfully() {
    wireMockServer.stubFor(
      post(urlEqualTo("/pet"))
        .withHeader("Content-Type", containing("application/json"))
        .withRequestBody(matchingJsonPath("$.name", equalTo("PetName")))
        .withRequestBody(matchingJsonPath("$.status", equalTo("available")))
        .willReturn(
          aResponse()
            .withStatus(201)
            .withHeader("Content-Type", "application/json")
            .withBody("""
              {
                "id": 1,
                "name": "PetName",
                "status": "available"
              }
            """)
        )
    );

    PetVO petVO = this.petExternalService.save(this.createPetVO());
    assertNotNull(petVO);
    assertEquals(PET_NAME, petVO.getName());
    assertEquals(STATUS_AVAILABLE, petVO.getStatus());
  }

  @Test
  void savePetErrorServerThrowPetException() {
    PetExternalService service = this.getBadPetExternalService();
    var petVO = this.createPetVO();

    PetException petException = assertThrows(PetException.class,
      () -> service.save(petVO));

    assertNotNull(petException);
  }

  @Test
  void savePetErrorConnectServerSecondConnectSuccessfully() {
    wireMockServer.stubFor(post(urlEqualTo("/pet"))
      .withHeader("Content-Type", containing("application/json"))
      .withRequestBody(matchingJsonPath("$.name", equalTo("PetName")))
      .withRequestBody(matchingJsonPath("$.status", equalTo("available")))
      .inScenario("retry")
      .whenScenarioStateIs(STARTED)
      .willReturn(serverError())
      .willSetStateTo("SECOND"));

    wireMockServer.stubFor(
      post(urlEqualTo("/pet"))
        .withHeader("Content-Type", containing("application/json"))
        .withRequestBody(matchingJsonPath("$.name", equalTo("PetName")))
        .withRequestBody(matchingJsonPath("$.status", equalTo("available")))
        .inScenario("retry")
        .whenScenarioStateIs("SECOND")
        .willReturn(
          aResponse()
            .withStatus(201)
            .withHeader("Content-Type", "application/json")
            .withBody("""
              {
                "id": 1,
                "name": "PetName",
                "status": "available"
              }
            """)
        )
    );

    PetVO petVO = this.petExternalService.save(this.createPetVO());

    assertNotNull(petVO);
    verify(2, postRequestedFor(urlEqualTo("/pet")));
  }

  @Test
  void saveShouldNotRetryWhenOccursError4xx() {
    this.saveByIdCheckThrowError(403, 1);
  }

  @Test
  void saveShouldRetryWhenOccursError5xx() {
    this.saveByIdCheckThrowError(500, 3);
  }

  private PetExternalService getBadPetExternalService() {
    RestClient restClient = RestClient.builder()
      .baseUrl("http://localhost:9999")
      .build();

    return new PetExternalServiceImpl(restClient, RetryRegistry.ofDefaults());
  }

  private void findPetByIdCheckThrowError(Long id, int statusCode, int callNumber) {
    wireMockServer.stubFor(get(urlEqualTo("/pet/" + id))
      .willReturn(
        aResponse().withStatus(statusCode)
      )
    );

    PetException petException = assertThrows(PetException.class, () ->
      this.petExternalService.findById(id));

    assertNotNull(petException);
    verify(callNumber, getRequestedFor(urlEqualTo("/pet/" + id)));
  }

  private PetVO createPetVO() {
    var petVO = new PetVO();
    petVO.setId(PetExternalServiceImplWireMockTest.ID);
    petVO.setName(PetExternalServiceImplWireMockTest.PET_NAME);
    petVO.setStatus(STATUS_AVAILABLE);
    return petVO;
  }

  private void saveByIdCheckThrowError(int statusCode, int callNumber) {
    wireMockServer.stubFor(post(urlEqualTo("/pet"))
      .willReturn(
        aResponse().withStatus(statusCode)
      )
    );
    var petVO = this.createPetVO();

    PetException petException = assertThrows(PetException.class, () ->
      this.petExternalService.save(petVO));

    assertNotNull(petException);
    verify(callNumber, postRequestedFor(urlEqualTo("/pet")));
  }


}
