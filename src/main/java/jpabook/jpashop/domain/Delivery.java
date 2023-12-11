package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter @Setter
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    /* 엑세스가 많은 order가 주인이 됨 */
    @OneToOne(mappedBy = "delivery", fetch = LAZY)
    private Order order;

    @Embedded
    private Address address;

    /* enum 타입일땐 꼭 넣어주기! ORDINAL로 했을경우 추가됐을때 숫자가 밀림, string으로 해주기!!! */
    @Enumerated(EnumType.ORDINAL)
    private DeliveryStatus status;
}
