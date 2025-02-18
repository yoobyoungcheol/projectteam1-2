package com.TFteamAI.team1AI.service.board;

import com.TFteamAI.team1AI.dto.fashion.FashionDataDTO;
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
import java.util.List;

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

    public Board createTemperatureRecord(Float temperature, FashionDataDTO fashionData) {
        Board board = new Board();

        // 기존 Board 엔티티 필드명에 맞게 설정
        board.setBoardTitle("온도 설정 기록");
        board.setBoardWrite("시스템");
        board.setCategory("TEMPERATURE");
        board.setSetTemperature(temperature);

        // 탐지된 의류 정보 생성
        StringBuilder clothesInfo = new StringBuilder();
        if (fashionData.getCoat() > 0) clothesInfo.append("코트(").append(fashionData.getCoat()).append(") ");
        if (fashionData.getJacket() > 0) clothesInfo.append("자켓(").append(fashionData.getJacket()).append(") ");
        if (fashionData.getJumper() > 0) clothesInfo.append("점퍼(").append(fashionData.getJumper()).append(") ");
        if (fashionData.getPadding() > 0) clothesInfo.append("패딩(").append(fashionData.getPadding()).append(") ");
        if (fashionData.getVest() > 0) clothesInfo.append("조끼(").append(fashionData.getVest()).append(") ");
        if (fashionData.getCardigan() > 0) clothesInfo.append("가디건(").append(fashionData.getCardigan()).append(") ");
        if (fashionData.getBlouse() > 0) clothesInfo.append("블라우스(").append(fashionData.getBlouse()).append(") ");
        if (fashionData.getShirt() > 0) clothesInfo.append("셔츠(").append(fashionData.getShirt()).append(") ");
        if (fashionData.getSweater() > 0) clothesInfo.append("스웨터(").append(fashionData.getSweater()).append(") ");

        board.setDetectedClothes(clothesInfo.toString());

        // 내용 생성
        String content = String.format(
                "탐지된 의류: %s\n" +
                        "설정 온도: %.1f°C\n" +
                        "아우터 비율: %d%%\n" +
                        "이너&아우터 비율: %d%%\n" +
                        "이너 비율: %d%%",
                clothesInfo.toString(),
                temperature,
                fashionData.getOuterRatio(),
                fashionData.getMixedRatio(),
                fashionData.getInnerRatio()
        );

        board.setBoardContent(content);
        board.setCreateDate(LocalDateTime.now());

        return boardRepository.save(board);
    }

    // 온도 설정 기록 조회
    public List<Board> getTemperatureRecords() {
        return boardRepository.findByCategory("TEMPERATURE");
    }
}