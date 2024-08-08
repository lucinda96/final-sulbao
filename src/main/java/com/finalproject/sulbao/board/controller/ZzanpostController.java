package com.finalproject.sulbao.board.controller;

import com.finalproject.sulbao.board.domain.Post;
import com.finalproject.sulbao.board.dto.CommentDto;
import com.finalproject.sulbao.board.dto.PostDto;
import com.finalproject.sulbao.board.dto.UserDto;
import com.finalproject.sulbao.board.repository.LikeRepository;
import com.finalproject.sulbao.board.repository.PostRepository;
import com.finalproject.sulbao.board.service.PostService;
import com.finalproject.sulbao.login.model.repository.LoginRepository;
import com.finalproject.sulbao.login.model.repository.MemberInfoRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/zzanposts")
@RequiredArgsConstructor
public class ZzanpostController {

    private final PostRepository postRepository;
    private final PostService postService;
    private final LoginRepository loginRepository;
    private final LikeRepository likeRepository;
    private final MemberInfoRepository memberInfoRepository;

    @GetMapping
    public String zzanpostList(Model model, @RequestParam(defaultValue = "0") int page, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, 6);
        Page<Post> postPage = postRepository.findAll(pageable);
        List<PostDto> postDtoList = postPage.getContent().stream().map(PostDto::toPostDto).toList();
        model.addAttribute("postDtoList", postDtoList);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "/board/zzanpost-list";
    }

    @GetMapping("/more")
    @ResponseBody
    public List<PostDto> loadMorePosts(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 6);
        return postRepository.findAll(pageable).getContent().stream().map(PostDto::toPostDto).toList();
    }

    @GetMapping("/new")
    public String newZzanpost() {
        return "/board/new-zzanpost";
    }

    @GetMapping("/{id}")
    public String zzanpost(@PathVariable Long id, Model model, HttpServletRequest request) {
//         임시로 로그인한 회원 세션에 저장
        UserDto userDto = UserDto.toUserDto(loginRepository.findById(1L).orElseThrow());
        request.getSession().setAttribute("userDto", userDto);

        postService.updateHit(id);
        Post post = postRepository.findById(id).orElseThrow();
        boolean isLiked = likeRepository.findByPostIdAndUserId(post.getId(), userDto.getId()).isPresent();
        model.addAttribute("userDto", request.getSession().getAttribute("userDto"));
        model.addAttribute("postDto", PostDto.toPostDto(post));
        model.addAttribute("commentDtoList", post.getComments().stream().map(CommentDto::toCommentDto).toList());
        model.addAttribute("isLiked", isLiked);
        return "/board/zzanpost";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.ok("삭제되었습니다.");
    }

}
