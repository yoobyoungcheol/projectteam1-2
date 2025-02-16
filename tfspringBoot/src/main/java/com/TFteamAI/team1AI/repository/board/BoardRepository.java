package com.TFteamAI.team1AI.repository.board;

import com.TFteamAI.team1AI.entity.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
}
