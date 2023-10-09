package com.example.demo.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.impl.EmployeeServiceImpl;

// Mockitoのアノテーションを使用していることを伝えるアノテーション
@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTests {

    @Mock
    private EmployeeRepository employeeRepository;

    // @Mock設定されたクラスに対し注入するクラスを指定するためのアノテーション
    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee1;

    @BeforeEach
    public void setup() {
        // employeeRepository = Mockito.mock(EmployeeRepository.class);
        // employeeService = new EmployeeServiceImpl(employeeRepository);

        employee1 = Employee.builder().id(1L).firstName("poll").lastName("smith").email("smith@example.com").build();
    }

    @DisplayName("saveEmployee method")
    @Test
    public void givenEmployee_whenSaveEmployee_thenReturnEmployee() {
        
        // saveEmployee内で使用するmockすべきものの戻りを固定化
        given(employeeRepository.findByEmail(employee1.getEmail())).willReturn(Optional.empty());
        given(employeeRepository.save(employee1)).willReturn(employee1);
        System.out.println(employeeRepository);
        System.out.println(employeeService);

        Employee saved = employeeService.saveEmployee(employee1);
        System.out.println(saved);

        Assertions.assertThat(saved).isNotNull();
    }

    @DisplayName("saveEmployee method")
    @Test
    public void givenExistingEmployee_whenSaveEmployee_thenThrowsException() {
        
        given(employeeRepository.findByEmail(employee1.getEmail())).willReturn(Optional.of(employee1));

        System.out.println(employeeRepository);
        System.out.println(employeeService);

        org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException.class, ()->{
            employeeService.saveEmployee(employee1);
        });

        verify(employeeRepository, never()).save(any(Employee.class));
    }
}
