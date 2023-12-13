package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// 트랜잭션 안에서 변경이 되는 경우 필수로 적어주기 (spring이 제공하는것으로 사용)
@Transactional(readOnly = true)     // jpa 조회 시 성능 최적화 (단순 읽기(조회)용, 데이터 변경x)
@RequiredArgsConstructor            // final이 붙어있는 필드만 가지고 생성자를 자동 생성해줌 (직접 쓸 필요 없음)
public class MemberService {

    // 회원 레파지토리 사용을 위해 연결
    // @Autowired를 원래 붙여주나, 생성자가 1개라면 생략 가능하다.
    // final로 쓰는것을 권장, 이유는? 컴파일 시점에 체크를 해줄 수 있기 때문!
    private final MemberRepository memberRepository;

    // 실무에서는 검증 로직이 있어도 멀티 쓰레드 상황을 고려해서 회원 테이블의 회원명 컬럼에 유니크 제약 조건을 추가하는 것이 안전하다.
    // 스프링 필드 주입 대신에 생성자 주입을 사용하자
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /**
     * 회원 가입
     */
    @Transactional
    public Long join(Member member) {

        validateDuplicateMember(member);    // 1. 중복 회원 검증
        memberRepository.save(member);      // 2. 검증에 문제가 없다면 회원 저장
        return member.getId();              // 3. id 반환
    }

    // 1-2. 중복 회원일 경우 예외 실행하기
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());

        // 예외 발생시(없는 회원이라면) 에러메세지 실행
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 회원 단일 조회
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
