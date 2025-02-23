package com.TFteamAI.team1AI.controller.board;

import com.TFteamAI.team1AI.entity.board.Board;
import com.TFteamAI.team1AI.service.board.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;



@Controller
@RequestMapping("/tf/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false, defaultValue = "TEMPERATURE") String category) {
        Page<Board> boards = boardService.getBoardListByCategory(page, category);

        model.addAttribute("boards", boards);
        model.addAttribute("currentPage", page);
        model.addAttribute("category", category);

        return "board/list";
    }

    @GetMapping("/{id}")
    public String viewBoard(@PathVariable Long id, Model model) {
        Board board = boardService.getBoardById(id);
        model.addAttribute("board", board);
        return "board/view";
    }

    @GetMapping("/create")
    public String createForm() {
        return "board/create";
    }

    @PostMapping
    public String createBoard(@ModelAttribute Board board) {
        board.setCreateDate(LocalDateTime.now());
        boardService.createBoard(board);
        return "redirect:/tf/board/list";
    }

    @GetMapping("/{id}/edit")
    public String editBoardForm(@PathVariable Long id, Model model) {
        Board board = boardService.getBoardById(id);
        model.addAttribute("board", board);
        return "board/edit";
    }

    @PostMapping("/{id}")
    public String updateBoard(@PathVariable Long id, @ModelAttribute Board board) {
        board.setUpdateDate(LocalDateTime.now());
        boardService.updateBoard(id, board);
        return "redirect:/tf/board/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return "redirect:/tf/board/list";
    }

}
