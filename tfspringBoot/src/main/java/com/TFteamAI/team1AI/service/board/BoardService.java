package com.TFteamAI.team1AI.service.board;

import com.TFteamAI.team1AI.entity.board.Board;
import com.TFteamAI.team1AI.repository.board.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    // 게시글 목록 조회 (페이징)
    public Page<Board> getBoardList(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createDate"));
        return boardRepository.findAll(pageable);
    }

    // 게시글 상세 조회
    public Board getBoardById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));
    }

    // 게시글 생성
    public Board createBoard(Board board) {
        board.setCreateDate(LocalDateTime.now());
        board.setViewCount(0);
        board.setLikeCount(0);
        return boardRepository.save(board);
    }

    // 게시글 수정
    public void updateBoard(Long id, Board updatedBoard) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));

        // 제목과 내용 업데이트
        board.setBoardTitle(updatedBoard.getBoardTitle());
        board.setBoardContent(updatedBoard.getBoardContent());
        board.setUpdateDate(LocalDateTime.now());

        boardRepository.save(board);
    }

    // 게시글 삭제
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }

    // 조회수 증가
    public void incrementViewCount(Long id) {
        Board board = getBoardById(id);
        board.setViewCount(board.getViewCount() + 1);
        boardRepository.save(board);
    }

    // 좋아요 증가
    public void incrementLikeCount(Long id) {
        Board board = getBoardById(id);
        board.setLikeCount(board.getLikeCount() + 1);
        boardRepository.save(board);
    }
}