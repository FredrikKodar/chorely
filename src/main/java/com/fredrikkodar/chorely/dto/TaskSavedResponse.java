package com.fredrikkodar.chorely.dto;

import org.springframework.web.util.HtmlUtils;

public record TaskSavedResponse(Integer id, String title, String description, Integer points) {

    public TaskSavedResponse(Integer id, String title, String description, Integer points) {
        this.id = id;
        this.title = HtmlUtils.htmlEscape(title);
        this.description = HtmlUtils.htmlEscape(description);
        this.points = points;
    }

}
