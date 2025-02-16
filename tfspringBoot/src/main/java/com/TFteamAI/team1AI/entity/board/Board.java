package com.TFteamAI.team1AI.entity.board;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "board")
public class Board {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;   // board 테이블 고유 ID

    @Column(nullable = false)
    private String boardTitle;   // board 제목

    @Column(nullable = false)
    private String boardContent; // board 내용

    @Column(nullable = true)    // 현재 test를 위해 true 설정
    private String boardWrite;   // board 작성자

    private LocalDateTime createDate;   // board 게시글 생성일자

    private LocalDateTime updateDate;   // board 게시글 수정일자

    @Column(columnDefinition = "INT DEFAULT 0")
    private int viewCount;  // 조회수

    @Column(columnDefinition = "INT DEFAULT 0")
    private int likeCount; // 좋아요

    /*
    Hibernate:
    create table board (
        board_id bigint not null auto_increment,
        board_content varchar(255) not null,
        board_title varchar(255) not null,
        board_write varchar(255) not null,
        create_date datetime(6),
        like_count INT DEFAULT 0,
        update_date datetime(6),
        view_count INT DEFAULT 0,
        primary key (board_id)
    ) engine=InnoDB
     */
}
