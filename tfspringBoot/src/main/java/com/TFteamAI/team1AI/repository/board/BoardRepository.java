package com.TFteamAI.team1AI.repository.board;

import com.TFteamAI.team1AI.entity.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findByCategory(String category);

    // 최신 온도 설정 기록 조회
    @Query("SELECT b FROM Board b WHERE b.category = 'TEMPERATURE' ORDER BY b.createDate DESC")
    List<Board> findLatestTemperatureRecords(Pageable pageable);
}
