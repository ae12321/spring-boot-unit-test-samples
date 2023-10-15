package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.model.Employee;

@DataJpaTest
// In-memoryデータベースを無効
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EmployeeRepositoryIntegrationTests {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    public void setup() {
        employeeRepository.deleteAll();

        employee1 = Employee.builder().firstName("first1").lastName("last1").email("first1@example.com").build();
        employee2 = Employee.builder().firstName("first2").lastName("last2").email("first2@example.com").build();
    }

    @DisplayName("save employee")
    @Test
    public void givenEmployeeObject_whenSave_thenReturnSavedEmployee() {
        Employee saved = employeeRepository.save(employee1);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isGreaterThan(0);
    }

    @DisplayName("get all employees")
    @Test
    public void givenEmployees_whenFindAll_thenEmployees() {
        employeeRepository.save(employee1);
        employeeRepository.save(employee2);

        List<Employee> employees = employeeRepository.findAll();

        assertThat(employees).isNotNull();
        assertThat(employees.size()).isEqualTo(2);
    }

    @DisplayName("get employee by id")
    @Test
    public void givenEmployee_whenFindById_thenReturnEmployee() {
        employeeRepository.save(employee1);

        Employee retrieved = employeeRepository.findById(employee1.getId()).get();

        assertThat(retrieved).isNotNull();
    }

    @DisplayName("get employee by email")
    @Test
    public void givenEmployee_whenFindByEmail_thenReturnEmployee() {
        employeeRepository.save(employee1);

        Employee retrieved = employeeRepository.findByEmail(employee1.getEmail()).get();

        assertThat(retrieved).isNotNull();
    }

    @Test
    public void givenEmployee_whenUpdateEmployee_thenReturnUpdatedEmployee() {
        employeeRepository.save(employee1);

        Employee retrieved = employeeRepository.findById(employee1.getId()).get();
        retrieved.setEmail("abc@example.com");
        retrieved.setFirstName("abc");
        Employee updated = employeeRepository.save(retrieved);

        assertThat(updated.getEmail()).isEqualTo("abc@example.com");
        assertThat(updated.getFirstName()).isEqualTo("abc");

        // run `Debug test`, stop breakpoint
    }

    @DisplayName("delete employee")
    @Test
    public void givenEmployee_whenDelete_thenRemoveEmployee() {
        employeeRepository.save(employee1);

        // when - action or the behaviour that we are going test
        employeeRepository.deleteById(employee1.getId());
        Optional<Employee> maybeEmployee = employeeRepository.findById(employee1.getId());

        // then - verify the output
        assertThat(maybeEmployee).isEmpty();
    }

    @DisplayName("custom query using JPQL")
    @Test
    public void givenFirstNameAndLastName_whenFindByFirstNameAndLastNameUsingJPQL_thenReturnEmployee() {
        Employee saved = employeeRepository.save(employee1);

        // custom query using JPQL with index
        Employee retrieved1 = employeeRepository.findUsingJPQL1(saved.getFirstName(), saved.getLastName());
        assertThat(retrieved1).isNotNull();

        // custom query using JPQL with named params
        Employee retrieved2 = employeeRepository.findUsingJPQL2(saved.getFirstName(), saved.getLastName());
        assertThat(retrieved2).isNotNull();
    }

    @Test
    public void findUsingNativeSQL() {
        Employee saved = employeeRepository.save(employee1);

        Employee retrieve1 = employeeRepository.findUsingNativeSQL1(saved.getFirstName(), saved.getLastName());
        assertThat(retrieve1).isNotNull();

        Employee retrieve2 = employeeRepository.findUsingNativeSQL2(saved.getLastName(), saved.getFirstName());
        assertThat(retrieve2).isNotNull();
    }
}
