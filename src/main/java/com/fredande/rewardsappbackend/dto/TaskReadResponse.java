package com.fredande.rewardsappbackend.dto;

import com.fredande.rewardsappbackend.enums.TaskStatus;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;

public record TaskReadResponse(Integer id,
                               String title,
                               String description,
                               Integer points,
                               Date created,
                               Date updated,
                               TaskStatus status,
                               Integer assignedTo) {

    public TaskReadResponse(Integer id, String title, String description, Integer points, Date created, Date updated, TaskStatus status, Integer assignedTo) {
        this.id = id;
        this.title = HtmlUtils.htmlEscape(title);
        this.description = HtmlUtils.htmlEscape(description);
        this.points = points;
        this.created = created;
        this.updated = updated;
        this.status = status;
        this.assignedTo = assignedTo;
    }

}
