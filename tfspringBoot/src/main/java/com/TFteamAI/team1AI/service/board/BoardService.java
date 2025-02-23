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
import java.time.format.DateTimeFormatter;

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

    // 게시글 목록 조회 (페이징, 카테고리별)
    public Page<Board> getBoardListByCategory(int page, String category) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createDate"));
        return boardRepository.findByCategory(category, pageable);
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

        board.setBoardTitle(updatedBoard.getBoardTitle());
        board.setBoardContent(updatedBoard.getBoardContent());
        board.setUpdateDate(LocalDateTime.now());

        boardRepository.save(board);
    }

    // 게시글 삭제
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }

    // 온도 설정 기록 생성
    public Board createTemperatureRecord(Float temperature, FashionDataDTO fashionData) {
        Board board = new Board();
        board.setCategory("TEMPERATURE");
        board.setSetTemperature(temperature);

        // 탐지된 의류 정보 생성
        StringBuilder clothesInfo = new StringBuilder();
        if (fashionData.getCoat() > 0)
            clothesInfo.append("코트(").append(fashionData.getCoat()).append(") ");
        if (fashionData.getJacket() > 0)
            clothesInfo.append("자켓(").append(fashionData.getJacket()).append(") ");
        if (fashionData.getJumper() > 0) clothesInfo.append("점퍼(").append(fashionData.getJumper()).append(") ");
        if (fashionData.getPadding() > 0) clothesInfo.append("패딩(").append(fashionData.getPadding()).append(") ");
        if (fashionData.getVest() > 0) clothesInfo.append("조끼(").append(fashionData.getVest()).append(") ");
        if (fashionData.getCardigan() > 0) clothesInfo.append("가디건(").append(fashionData.getCardigan()).append(") ");
        if (fashionData.getBlouse() > 0) clothesInfo.append("블라우스(").append(fashionData.getBlouse()).append(") ");
        if (fashionData.getShirt() > 0) clothesInfo.append("셔츠(").append(fashionData.getShirt()).append(") ");
        if (fashionData.getSweater() > 0) clothesInfo.append("스웨터(").append(fashionData.getSweater()).append(") ");

        board.setDetectedClothes(clothesInfo.toString());

        // 의류 비율 분석 결과 저장
        StringBuilder content = new StringBuilder();
        content.append("탐지된 의류: ").append(clothesInfo.toString()).append("\n");
        content.append("설정 온도: ").append(temperature).append("°C\n");
        content.append("의류 비율:\n")
                .append("- 아우터: ").append(fashionData.getOuterRatio()).append("%\n")
                .append("- 이너&아우터: ").append(fashionData.getMixedRatio()).append("%\n")
                .append("- 이너: ").append(fashionData.getInnerRatio()).append("%");

        board.setBoardContent(content.toString());
        return boardRepository.save(board);  // DB에 저장
    }

    // 온도 설정 기록 조회
    public List<Board> getTemperatureRecords() {
        return boardRepository.findByCategory("TEMPERATURE");
    }

    // 시스템 로그 자동 생성
    public Board createSystemLog(String title, String content) {
        Board board = new Board();
        board.setBoardTitle(title);
        board.setBoardContent(content);
        board.setBoardWrite("SYSTEM");  // 작성자는 항상 "SYSTEM"
        board.setCategory("SYSTEM_LOG");  // 카테고리
        board.setCreateDate(LocalDateTime.now());
        return boardRepository.save(board);
    }

    // 의류 탐지 로그 생성
    public Board createDetectionLog(FashionDataDTO fashionData) {
        StringBuilder content = new StringBuilder("의류 탐지 결과:\n");

        // 탐지된 의류 정보 기록
        if (fashionData.getCoat() > 0)
            content.append("- 코트 ").append(fashionData.getCoat()).append("개 탐지됨\n");
        if (fashionData.getJacket() > 0)
            content.append("- 자켓 ").append(fashionData.getJacket()).append("개 탐지됨\n");
        if (fashionData.getJumper() > 0) content.append("- 점퍼 ").append(fashionData.getJumper()).append("개 탐지됨\n");
        if (fashionData.getPadding() > 0) content.append("- 패딩 ").append(fashionData.getPadding()).append("개 탐지됨\n");
        if (fashionData.getVest() > 0) content.append("- 조끼 ").append(fashionData.getVest()).append("개 탐지됨\n");
        if (fashionData.getCardigan() > 0) content.append("- 가디건 ").append(fashionData.getCardigan()).append("개 탐지됨\n");
        if (fashionData.getBlouse() > 0) content.append("- 블라우스 ").append(fashionData.getBlouse()).append("개 탐지됨\n");
        if (fashionData.getShirt() > 0) content.append("- 셔츠 ").append(fashionData.getShirt()).append("개 탐지됨\n");
        if (fashionData.getSweater() > 0) content.append("- 스웨터 ").append(fashionData.getSweater()).append("개 탐지됨\n");

        // 의류 비율 정보 추가
        content.append("\n의류 비율:\n")
                .append("- 아우터: ").append(fashionData.getOuterRatio()).append("%\n")
                .append("- 이너&아우터: ").append(fashionData.getMixedRatio()).append("%\n")
                .append("- 이너: ").append(fashionData.getInnerRatio()).append("%");

        return createSystemLog("의류 탐지 결과", content.toString());
    }

    // MQTT 연결 상태 로그 생성
    public Board createMqttConnectionLog(boolean isConnected) {
        String title = isConnected ? "MQTT 서버 연결 성공" : "MQTT 서버 연결 실패";
        String content = isConnected ?
                "MQTT 서버와의 연결이 성공적으로 수립되었습니다.\n연결 시각: " + LocalDateTime.now() :
                "MQTT 서버와의 연결이 실패했습니다.\n실패 시각: " + LocalDateTime.now() + "\n재연결을 시도합니다.";

        return createSystemLog(title, content);  // 시스템 로그로 저장
    }

    // 온도 설정 로그 생성
    public Board createTemperatureLog(float temperature, String reason) {
        String content = String.format(
                "실내 온도가 %.1f°C로 설정되었습니다.\n" +
                        "설정 사유: %s\n" +
                        "설정 시각: %s",
                temperature,
                reason,
                LocalDateTime.now()
        );

        return createSystemLog("온도 설정 변경", content);
    }

}