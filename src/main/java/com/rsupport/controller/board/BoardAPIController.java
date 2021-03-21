package com.rsupport.controller.board;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.rsupport.domain.board.Board;
import com.rsupport.domain.board.BoardDTO;
import com.rsupport.domain.member.Member;
import com.rsupport.service.board.BoardService;
import com.rsupport.service.file.FileService;
import com.rsupport.service.member.MemberService;
import com.rsupport.service.write.WriteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BoardAPIController {

	private final BoardService boardService;
	private final WriteService writeService;
	private final FileService fileService;
	private final MemberService memberService;
	
	@PostMapping("/board/create")
	public void makeBoard(MultipartHttpServletRequest request) throws Exception {
		
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		
		System.out.println(title + " " + content);
		
		// 공지사항 저장
		BoardDTO dto = BoardDTO
				.builder()
				.title(title)
				.content(content)
				.build();
		
		// 보드를 저장
		Board board = boardService.saveBoard(dto);
		
		// 멤버 가져오기
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Member member = memberService.findByMemberIDEntity(auth.getName());
		
		// write 저장
		writeService.saveWrite(member, board);
		
		if(request.getFileNames().hasNext()) {
			String files = request.getFileNames().next();
			List<MultipartFile> mpf = request.getFiles(files);
			fileService.saveFile(mpf);
		}
	}
	
}
