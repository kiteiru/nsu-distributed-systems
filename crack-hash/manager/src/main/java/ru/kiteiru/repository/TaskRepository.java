package ru.kiteiru.repository;


import org.springframework.data.mongodb.repository.MongoRepository;

import ru.kiteiru.types.Task;

public interface TaskRepository extends MongoRepository<Task, String> {

}