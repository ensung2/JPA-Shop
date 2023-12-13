package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MemberRepository {

//    @PersistenceContext // -> 스프링부트가 지원해줘서 @Autowired로 바꿀 수 있음,
        @PersistenceContext EntityManager em;

    // 멤버 저장
    public void save(Member member) {
        em.persist(member); // 커밋 되는 시점에 db로 데이터가 전달됨
    }

    // 멤버 조회
    public Member findOne(Long id) {
        return em.find(Member.class, id);   // 단건 조회, class 다음에 pk를 넣어주면 됨
    }

    // 멤버 목록 조회 // 인라인 변수 단축키 : ctrl + alt + n
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
