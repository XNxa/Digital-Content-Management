package com.dcm.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "log-index")
@TypeAlias("Event")
public class Log {
    @Id
    private String id;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime date;

    @Field(type = FieldType.Keyword)
    private String user;

    @Field(type = FieldType.Keyword)
    private String action;

    @Field(type = FieldType.Match_Only_Text)
    private String before;

    @Field(type = FieldType.Match_Only_Text)
    private String after;
}
