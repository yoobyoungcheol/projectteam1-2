package com.TFteamAI.team1AI.repository.board;

import com.TFteamAI.team1AI.entity.board.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 카테고리별 페이징 조회
    @Query("SELECT b FROM Board b WHERE b.category = :category ORDER BY b.createDate DESC")
    Page<Board> findByCategory(@Param("category") String category, Pageable pageable);

    // 카테고리별 일반 조회
    List<Board> findByCategory(String category);

    // 온도 설정 기록 조회
    @Query("SELECT b FROM Board b WHERE b.category = 'TEMPERATURE' ORDER BY b.createDate DESC")
    List<Board> findLatestTemperatureRecords(Pageable pageable);
}
