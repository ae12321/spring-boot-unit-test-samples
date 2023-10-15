package com.example.demo.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

// fixing port number
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class EmployeeControllerIT {

    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        "postgres:16-alpine"
    )
        .withUsername("testuser")
        .withPassword("asdfasdf")
        .withDatabaseName("postgres");

    // postgresインスタンスをApplicationContextにリンクさせるために
    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        System.out.println(postgres.getUsername() + " : " + postgres.getPassword() + " : " + postgres.getDatabaseName());
    }

    // 起動と停止
    @BeforeAll
    public static void beforeAll() {
        postgres.start();
    }
    @AfterAll
    public static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    public void beforeEach() {
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        employeeRepository.deleteAll();
    }

    @Test
    public void post() throws Exception {
        Employee employee = Employee.builder().firstName("first1").lastName("last1").email("first1@example.com").build();   

        ResultActions response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(employee))
        );

        response.andDo(MockMvcResultHandlers.print())   
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(employee.getFirstName())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is(employee.getLastName())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(employee.getEmail())));
    }


    @Test
    public void getAll() throws Exception {
        List<Employee> employees = new ArrayList<>();
        employees.add(Employee.builder().firstName("paul").lastName("smith").email("smith@example.com").build());
        employees.add(Employee.builder().firstName("jane").lastName("doe").email("doe@example.com").build());

        employeeRepository.saveAll(employees);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees"));

        response.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(employees.size())));
    }



    @Test
    public void getById() throws Exception {
        Employee employee = Employee.builder()
            .firstName("paul")
            .lastName("smith")
            .email("smith@example.com")
            .build();

        employeeRepository.save(employee);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/{id}", employee.getId()));

        response.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(employee.getFirstName())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is(employee.getLastName())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(employee.getEmail())));
    }

    @Test
    public void getById_notFound() throws Exception {
        Employee employee = Employee.builder()
            .firstName("paul")
            .lastName("smith")
            .email("smith@example.com")
            .build();
        employeeRepository.save(employee);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/{id}", employee.getId() + 1));

        response.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }



    @Test
    public void updateEmployee() throws Exception {
        Employee employee = Employee.builder()
            .firstName("paul")
            .lastName("smith")
            .email("smith@example.com")
            .build();
        employeeRepository.save(employee);

        employee.setFirstName("jane");
        employee.setLastName("doe");
        employee.setEmail("doe@example.com");
        employeeRepository.save(employee);
        
        ResultActions response = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/employees/{id}", employee.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(employee)));

        response.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(employee.getFirstName())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is(employee.getLastName())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(employee.getEmail())));
    }

    @Test
    public void test_updateEmployee_2() throws Exception {
        Employee employee = Employee.builder()
            .firstName("jane")
            .lastName("doe")
            .email("doe@example.com")
            .build();
        employeeRepository.save(employee);

        ResultActions response = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/employees/{id}", employee.getId() + 1)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(employee)));

        response.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteEmployee() throws Exception {
        Employee employee = Employee.builder()
            .firstName("jane")
            .lastName("doe")
            .email("doe@example.com")
            .build();
        employeeRepository.save(employee);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/employees/{id}", employee.getId()));

        response.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
