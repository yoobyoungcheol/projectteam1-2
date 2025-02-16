package com.TFteamAI.team1AI.entity.fashion;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "fashion")
public class Fashion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fashionId; // fashion 테이블 고유 ID

    private String name;    // 이름

    private Float confidence;   // 확률

    private Integer trackId;    // 추적

    @Column(name = "receive_time")
    private LocalDateTime receiveTime;  // 데이터 수신 일자

    private Integer count;  //개수

    /*

    Hibernate:
    create table fashion (
        fashion_id bigint not null auto_increment,
        confidence float(23),
        count integer,
        name varchar(255),
        receive_time datetime(6),
        track_id integer,
        primary key (fashion_id)
    ) engine=InnoDB

     */
}
