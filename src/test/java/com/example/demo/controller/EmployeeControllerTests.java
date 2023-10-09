package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.internal.invocation.ArgumentMatcherAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.demo.model.Employee;
import com.example.demo.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest
public class EmployeeControllerTests {
    
    @Autowired
    private MockMvc mockMvc;

    // EmployeeServiceのmockインスタンスの作成、application contextに追加、controllerに注入
    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test1() throws Exception {
        Employee employee = Employee.builder().firstName("paul").lastName("smith").email("smith@example.com").build();

        BDDMockito.given(employeeService.saveEmployee(ArgumentMatchers.any(Employee.class))).willAnswer((invocation) -> invocation.getArgument(0));

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
    public void test2() throws Exception {
        List<Employee> employees = new ArrayList<>();
        employees.add(Employee.builder().firstName("paul").lastName("smith").email("smith@example.com").build());
        employees.add(Employee.builder().firstName("jane").lastName("doe").email("doe@example.com").build());

        BDDMockito.given(employeeService.getAllEmployees()).willReturn(employees);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees"));

        response.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(employees.size())));
    }

    @Test
    public void test3() throws Exception {
        long employeeId = 1L;
        Employee employee = Employee.builder()
            .id(employeeId)
            .firstName("paul")
            .lastName("smith")
            .email("smith@example.com")
            .build();

        BDDMockito.given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.of(employee));

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/{id}", employeeId));

        response.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(employee.getFirstName())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is(employee.getLastName())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(employee.getEmail())));
    }

    @Test
    public void test4() throws Exception {
        long employeeId = 1L;

        BDDMockito.given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.empty());

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/{id}", employeeId));

        response.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void test_updateEmployee_1() throws Exception {
        long employeeId = 1L;
        Employee employee = Employee.builder()
            .id(employeeId)
            .firstName("paul")
            .lastName("smith")
            .email("smith@example.com")
            .build();

        Employee updated = Employee.builder()
            .id(employeeId)
            .firstName("jane")
            .lastName("doe")
            .email("doe@example.com")
            .build();
        
        BDDMockito.given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.of(employee));
        BDDMockito.given(employeeService.updateEmployee(ArgumentMatchers.any(Employee.class))).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/employees/{id}", employeeId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updated)));

        response.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(updated.getFirstName())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is(updated.getLastName())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(updated.getEmail())));
    }
}
