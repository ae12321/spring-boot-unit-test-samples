package com.example.demo.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.impl.EmployeeServiceImpl;

public class EmployeeServiceTests {

    private EmployeeRepository employeeRepository;
    private EmployeeService employeeService;

    @BeforeEach
    public void setup() {
        employeeRepository = Mockito.mock(EmployeeRepository.class);
        employeeService = new EmployeeServiceImpl(employeeRepository);
    }

    @DisplayName("saveEmployee method")
    @Test
    public void givenEmployee_whenSaveEmployee_thenReturnEmployee() {
        Employee employee = Employee.builder().id(1L).firstName("poll").lastName("smith").email("smith@example.com").build();
        
        // saveEmployee内で使用するmockすべきものの戻りを固定化
        BDDMockito.given(employeeRepository.findByEmail(employee.getEmail())).willReturn(Optional.empty());
        BDDMockito.given(employeeRepository.save(employee)).willReturn(employee);
        System.out.println(employeeRepository);
        System.out.println(employeeService);

        Employee saved = employeeService.saveEmployee(employee);
        System.out.println(saved);

        Assertions.assertThat(saved).isNotNull();
    }
}
