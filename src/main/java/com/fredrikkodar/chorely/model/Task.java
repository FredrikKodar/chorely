package com.fredrikkodar.chorely.model;

import com.fredrikkodar.chorely.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //    private Integer userId;
    @NotBlank
    @Size(min = 8, max = 140, message = "Title must be 8-40 characters.")
    private String title;
    @NotBlank
    @Size(min = 8, max = 255, message = "Description must be 8-255 characters.")
    private String description;
    @NotNull
    @Min(0)
    private Integer points;
    @CreationTimestamp
    private Date created;
    private Date updated;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    public Task() {
        this.points = 0;
        this.status = TaskStatus.ASSIGNED;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Date getCreated() {
        return created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

}
