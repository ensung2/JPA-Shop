package jpabook.jpashop.repository;


import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    // 상품저장
    public void save(Item item) {
        if (item.getId() == null) {     // 아이디 값이 없다면? 새로 생기는 객체라는 뜻!
            em.persist(item);
        }else {
            em.merge(item);             // 업데이트의 개념
        }
    }

    // 상품 단일 조회
    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    // 상품 리스트 조회
    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
