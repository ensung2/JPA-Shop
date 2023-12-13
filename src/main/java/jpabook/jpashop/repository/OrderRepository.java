package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    /**
     * JPQL로 만들기
     */
    // 첫번째 방법
    public List<Order> findAllByString(OrderSearch orderSearch) {

        //==동적 쿼리 생성==//
        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        // 1. 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        // 2. 회원명 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        // ctrl + alt + v
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
//                .setFirstResult(100)    // 100부터 가져와서 조회한다는 뜻
                .setMaxResults(1000);      // 최대 1000건

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

         return query.getResultList();
    }

    /**
     * JPA Criteria
     */
    // 두번쨰 방법 (조금 더 업그레이드 된 방법) - Criteria 사용 (jpql을 자바코드로 작성할 수 있게 jpa에서 표준으로 제공)
    // 실무에서도 자주 사용하는 방법은 아니라구 함 (Querydsl을 사용하자), 유지보수에 좋지않다.
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName()
                            + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();

    }

//    /**
//     * Querydsl로 처리
//     * 강력추천하는 방법!!
//     */
//    public List<Order> findAllByQuerydsl(OrderSearch orderSearch) {
//        QOrder order = QOrder.order;
//        QMember member = QMember.member;
//
//        return
//
//    }
//
//    private BooleanExpression statusEq(OrderStatus orderStatus) {
//        if (statusCond == null) {
//            return null;
//        }return order.status.eq(statusCond);
//    }
}
