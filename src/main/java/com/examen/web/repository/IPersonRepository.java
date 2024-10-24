package com.examen.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.examen.web.model.Person;

public interface IPersonRepository extends JpaRepository<Person, Integer>{

}
