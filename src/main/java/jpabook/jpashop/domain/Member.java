package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded   // 내장객체를 포함했다고 표현
    private Address address;

    @OneToMany(mappedBy = "member") // order테이블에 있는 member필드에 의해 매핑 되었다는 의미 (읽기전용)
    private List<Order> orders = new ArrayList<>();
}
