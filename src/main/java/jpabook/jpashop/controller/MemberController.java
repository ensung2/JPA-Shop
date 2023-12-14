package jpabook.jpashop.controller;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /* GetMapping : 폼 화면을 열어봄 */
    @GetMapping("/members/new")
    public String createForm(Model model) {

        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    /* PostMapping : 해당 데이터를 실제등록 */
    @PostMapping("/members/new")
    public String create(@Valid MemberForm form,       // id에 NotEmpty를 걸어주었기 떄문에 @Valid 를 설정
                         BindingResult result){

        if (result.hasErrors()) {       // 해당 화면에서 에러가 나면 다시 해당 화면으로 돌아오게 한다.
            return "members/createMemberForm";
        }
        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";        // 성공시 첫번쨰(메인페이지)로 넘어감
    }

    // 회원 목록 조회
    @GetMapping("/members")
    public String list(Model model) {
        model.addAttribute("members", memberService.findMembers());

        return "members/memberList";
    }
}
