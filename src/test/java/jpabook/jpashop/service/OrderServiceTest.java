package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    @DisplayName("상품 주문 테스트")
    public void 상품주문() throws Exception {
        //given (ctrl+alt+m을통해 기본 메서드로 지정)
        Member member = createMember();

        Item book = createBook("시골 jpa", 10000, 10);

        int orderCount = 2;
        // when

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);


        // assertEquals 양식 (message : 확인할 내용, expected : 기대하는 값, actual : 실제 값) = 동등한지 확인
        assertEquals("상품 주문시 상태는 order", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류수가 정확해야 한다", 1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다.", 10000*orderCount, getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다.", 8, book.getStockQuantity());
    }

    private Item createBook(String name, int price, int stockQuantity) {
        Item book = new Book();
        book.setName(name);     // 값 적은 후 ctrl+alt+p눌러서 파라미터 형태로 변경
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }


    @Test(expected = NotEnoughStockException.class)
    @DisplayName("재고 수량을 초과한 상품 주문 시 테스트")
    public void 수량초과() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("시골 jpa", 10000, 10);

        int orderCount=11;

        // when
        orderService.order(member.getId(), item.getId(), orderCount);

        //then
        fail("재고 수량 부족 예외가 발생해야 합니다.");
    }

    @Test
    @DisplayName("상품 주문 취소 테스트")
    public void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("시골 jpa", 10000, 10);

        int orderCount=2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // when
        orderService.cancleOrder(orderId);


        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문 취소시 상태는 CANCLE이다.", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야 한다.", 10, item.getStockQuantity());

    }

}