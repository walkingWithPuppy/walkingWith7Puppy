package com.turkey.walkingwith7puppy.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.turkey.walkingwith7puppy.dto.BoardDto;
import com.turkey.walkingwith7puppy.dto.request.BoardRequest;
import com.turkey.walkingwith7puppy.dto.response.BoardListResponse;
import com.turkey.walkingwith7puppy.dto.response.BoardResponse;
import com.turkey.walkingwith7puppy.security.UserDetailsImpl;
import com.turkey.walkingwith7puppy.service.BoardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "boards", description = "게시물 API")
@RequiredArgsConstructor
@RestController
public class BoardController {

	private final BoardService boardService;

	@Operation(summary = "get boards", description = "게시물 전체를 가져오기")
	@GetMapping("/boards")
	public ResponseEntity<List<BoardListResponse>> getBoardList() {

		return ResponseEntity.status(HttpStatus.OK)
				.body(boardService.searchBoards());
	}

	@Operation(summary = "get boards/id", description = "선택된 게시물 가져오기")
	@GetMapping("/boards/{boardId}")
	public ResponseEntity<BoardResponse> getBoard(@PathVariable final Long boardId) {

		return ResponseEntity.status(HttpStatus.OK)
				.body(boardService.searchBoard(boardId));
	}

	@Operation(summary = "post boards", description = "게시물 작성하기")
	@Parameter(name = "ACCESS_KEY", description = "ACCESS_KEY", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
	@Parameter(name = "REFRESH_KEY", description = "REFRESH_KEY", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
	@PostMapping(value ="/boards", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> createBoard(

		@Parameter(hidden = true) @AuthenticationPrincipal final UserDetailsImpl userDetails,
		@RequestPart(value = "data") @Valid final BoardRequest boardRequest,
		@RequestPart(value = "img") final MultipartFile file) {

		boardService.createBoard(userDetails.getMember(), BoardDto.from(boardRequest), file);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@Operation(summary = "put boards/id", description = "게시물 수정하기")
	@Parameter(name = "엑세스키", description = "ACCESS_KEY", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
	@Parameter(name = "리프레쉬키", description = "REFRESH_KEY", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
	@PutMapping("/boards/{boardId}")
	public ResponseEntity<Void> updateBoard(
		@AuthenticationPrincipal final UserDetailsImpl userDetails,
		@PathVariable final Long boardId,
		@RequestPart(value = "data") @Valid final BoardRequest boardRequest,
		@RequestPart(value = "img", required = false) final MultipartFile file) {

		boardService.updateBoard(userDetails.getMember(), boardId, BoardDto.from(boardRequest), file);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@Operation(summary = "delete boards/id", description = "게시물 삭제하기")
	@Parameter(name = "엑세스키", description = "ACCESS_KEY", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
	@Parameter(name = "리프레쉬키", description = "REFRESH_KEY", in = ParameterIn.HEADER, schema = @Schema(type = "string"))
	@DeleteMapping("/boards/{boardId}")
	public ResponseEntity<Void> deleteArticle(
		@AuthenticationPrincipal final UserDetailsImpl userDetails,
		@PathVariable final Long boardId) {

		boardService.deleteBoard(userDetails.getMember(), boardId);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@Operation(summary = "get boards/지역구", description = "지역구에 따른 게시물 검색하기")
	@GetMapping("/boards/search")
	public ResponseEntity<List<BoardResponse>> searchBoardList(@RequestParam String address) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(boardService.searchBoards(address));
	}
}
