package com.example.demo.repository;

// import org.assertj.core.api.Assertions;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.model.Employee;


@DataJpaTest
public class EmployeeRepositoryTests {
    
    @Autowired
    private EmployeeRepository employeeRepository;

    @DisplayName("JUnit test for save employee operation")
    @Test
    public void givenEmployeeObject_whenSave_thenReturnSavedEmployee() {
        // given - precondition or setup
        Employee employee = Employee.builder().firstName("poll").lastName("smith").email("smith@example.com").build(); 

        // when - action or the behaviour that we are going test
        Employee saved = employeeRepository.save(employee);

        // then - verify the output
        // Assertions.assertThat(saved).isNotNull();
        // Assertions.assertThat(saved.getId()).isGreaterThan(0);
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isGreaterThan(0);
    }
}
