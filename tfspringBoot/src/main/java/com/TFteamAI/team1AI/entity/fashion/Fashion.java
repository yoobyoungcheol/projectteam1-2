package com.TFteamAI.team1AI.entity.fashion;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "fashion")
@AllArgsConstructor
@NoArgsConstructor
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

    public Fashion(String name, Float confidence, Integer trackId, LocalDateTime receiveTime, Integer count) {
        this.name = name;
        this.confidence = confidence;
        this.trackId = trackId;
        this.receiveTime = receiveTime;
        this.count = count;
    }
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
