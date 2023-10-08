package com.example.demo.repository;

// import org.assertj.core.api.Assertions;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

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

    @DisplayName("JUnit test for get all employees operation")
    @Test
    public void givenEmployees_whenFindAll_thenEmployees() {
        // given - precondition or setup
        Employee employee1 = Employee.builder().firstName("poll").lastName("smith").email("smith@example.com").build();
        Employee employee2 = Employee.builder().firstName("john").lastName("due").email("john@example.com").build();
        employeeRepository.save(employee1);
        employeeRepository.save(employee2);

        // when - action or the behaviour that we are going test
        List<Employee> employees = employeeRepository.findAll();

        // then - verify the output
        assertThat(employees).isNotNull();
        assertThat(employees.size()).isEqualTo(2);
    }

    @DisplayName("get employee by id")
    @Test
    public void givenEmployee_whenFindById_thenReturnEmployee() {
        // given - precondition or setup
        Employee employee = Employee.builder().firstName("poll").lastName("smith").email("smith@example.com").build();
        employeeRepository.save(employee);

        // when - action or the behaviour that we are going test
        Employee retrieved = employeeRepository.findById(employee.getId()).get();

        // then - verify the output
        assertThat(retrieved).isNotNull();
    }

    @DisplayName("get employee by email")
    @Test
    public void givenEmployee_whenFindByEmail_thenReturnEmployee() {
        // given - precondition or setup
        String email = "smith@example.com";
        Employee employee = Employee.builder().firstName("poll").lastName("smith").email(email).build();
        employeeRepository.save(employee);

        // when - action or the behaviour that we are going test
        Employee retrieved = employeeRepository.findByEmail(employee.getEmail()).get();

        // then - verify the output
        assertThat(retrieved).isNotNull();
    }

    @Test
    public void givenEmployee_whenUpdateEmployee_thenReturnUpdatedEmployee() {
        // given - precondition or setup
        Employee employee = Employee.builder().firstName("poll").lastName("smith").email("smith@example.com").build();
        employeeRepository.save(employee);

        // when - action or the behaviour that we are going test
        Employee retrieved = employeeRepository.findById(employee.getId()).get();
        retrieved.setEmail("abc@example.com");
        retrieved.setFirstName("abc");
        Employee updated = employeeRepository.save(retrieved);

        // then - verify the output
        assertThat(updated.getEmail()).isEqualTo("abc@example.com");
        assertThat(updated.getFirstName()).isEqualTo("abc");

        // run `Debug test`, stop breakpoint
    }

    @DisplayName("delete employee")
    @Test
    public void givenEmployee_whenDelete_thenRemoveEmployee() {
        // given - precondition or setup
        Employee employee = Employee.builder().firstName("poll").lastName("smith").email("smith@example.com").build();
        employeeRepository.save(employee);

        // when - action or the behaviour that we are going test
        employeeRepository.deleteById(employee.getId());
        Optional<Employee> maybeEmployee = employeeRepository.findById(employee.getId());

        // then - verify the output
        assertThat(maybeEmployee).isEmpty();
    }

    @DisplayName("custom query using JPQL")
    @Test
    public void givenFirstNameAndLastName_whenFindByFirstNameAndLastNameUsingJPQL_thenReturnEmployee() {
        String firstName = "poll";
        String lastName = "smith";

        Employee employee = Employee.builder().firstName(firstName).lastName(lastName).email("smith@example.com").build();
        employeeRepository.save(employee);

        // custom query using JPQL with index
        Employee retrieved1 = employeeRepository.findUsingJPQL1(firstName, lastName);
        assertThat(retrieved1).isNotNull();

        // custom query using JPQL with named params
        Employee retrieved2 = employeeRepository.findUsingJPQL2(firstName, lastName);
        assertThat(retrieved2).isNotNull();
    }

    @Test
    public void findUsingNativeSQL() {
        Employee employee = Employee.builder().firstName( "poll").lastName("smith").email("smith@example.com").build();
        employeeRepository.save(employee);

        Employee retrieve1 = employeeRepository.findUsingNativeSQL1(employee.getFirstName(), employee.getLastName());
        assertThat(retrieve1).isNotNull();

        Employee retrieve2 = employeeRepository.findUsingNativeSQL2(employee.getLastName(), employee.getFirstName());
        assertThat(retrieve2).isNotNull();
    }
}
