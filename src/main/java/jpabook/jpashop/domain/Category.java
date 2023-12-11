package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;    // 카테고리명

    @ManyToMany
    @JoinTable(name = "category_item"  // 중간 테이블, 실전에선 사용하지 않음(필드 추가가 불가능하기 때문)
            , joinColumns = @JoinColumn(name = "category_id")
            ,inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> item = new ArrayList<>();


    /* 셀프 양방향 관계 만들기*/
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    //==연관관계 메서드==//
    public void addCildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }
}
