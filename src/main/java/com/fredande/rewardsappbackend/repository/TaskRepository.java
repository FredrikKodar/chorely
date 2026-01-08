package com.fredande.rewardsappbackend.repository;

import com.fredande.rewardsappbackend.model.Task;
import com.fredande.rewardsappbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByUser(User user);

    @Query(value = "SELECT t FROM Task t WHERE t.id = :taskId AND t.user = :userId")
    Optional<Task> findByIdAndUser(@Param("taskId") Integer taskId, @Param("userId") User user);

    Optional<Task> findByIdAndCreatedBy(Integer id, User user);

}
