package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id") // pk이름
    private Member member;
                                   //
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    /* 엑세스가 많은곳에 pk를 둠*/
    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    //==연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem...orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem:orderItems) {
            order.addOrderItem(orderItem);
        }

        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비지니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel(){       // 주문을 취소하려는데 이미 상품이 배송됐다면?
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능 합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);     // 배송완료가 아니라면 주문 취소할것
        for (OrderItem orderItem:orderItems) {
            orderItem.cancle();                 // 만약에 고객이 상품을 1개이상 주문했을경우? 각각 캔슬 날려주기
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
//        return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum(); 로 줄여 사용할 수 있다.
        int totalPrice = 0;
        for (OrderItem orderItem:orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
