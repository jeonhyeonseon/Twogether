package com.think_different.think_different.board.service;

import com.think_different.think_different.board.dto.BoardDetailResponseDto;
import com.think_different.think_different.board.dto.BoardListResponseDto;
import com.think_different.think_different.board.dto.BoardRegisterRequestDto;
import com.think_different.think_different.board.dto.BoardUpdateRequestDto;
import com.think_different.think_different.board.entity.Board;
import com.think_different.think_different.board.repository.BoardRepository;
import com.think_different.think_different.member.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public Page<BoardListResponseDto> getBoardList(String keyword, Pageable pageable) {

        Page<Board> page = (keyword == null || keyword.isBlank())
                ? boardRepository.findAll(pageable)
                : boardRepository.findByTitleContainingIgnoreCase(keyword, pageable);

        return page.map(BoardListResponseDto::fromBoard);
    }

    public void registerBoard(BoardRegisterRequestDto boardRegisterRequestDto, Member member) {
        // DTO를 Entity로 변환하기
        Board board = boardRegisterRequestDto.toBoard(member);

        boardRepository.save(board);
    }

    public BoardDetailResponseDto getBoardDetail(Long id) {

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        return BoardDetailResponseDto.fromBoard(board);
    }

    public BoardUpdateRequestDto getUpdateBoard(Long id) {

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        return BoardUpdateRequestDto.fromBoard(board);
    }

    public void updateBoard(Long id, BoardUpdateRequestDto boardUpdateRequestDto, Member member) {

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        if (!board.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        board.updateBoard(boardUpdateRequestDto.getTitle(), boardUpdateRequestDto.getContents());
    }

    public void deleteBoard(Long id) {

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        boardRepository.delete(board);
    }
}
