package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);

    // define custom query using JPQL with index params
    @Query("select e from Employee e where e.firstName = ?1 and e.lastName = ?2")
    Employee findUsingJPQL1(String firstName, String lastName);

    // custom query using JPQL with named params
    @Query("select e from Employee e where e.firstName = :firstName and e.lastName = :lastName")
    Employee findUsingJPQL2(@Param("firstName") String first, @Param("lastName") String last);

    // native sql with index
    @Query(value = "select * from employees e where e.first_name = ?1 and e.last_name = ?2", nativeQuery = true)
    Employee findUsingNativeSQL1(String firstName, String lastName);

    @Query(value = "select * from employees e where e.first_name = :firstName and e.last_name = :lastName", nativeQuery = true)
    Employee findUsingNativeSQL2(@Param("lastName") String last, @Param("firstName") String first);
}
