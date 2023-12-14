package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    /**
     * 1. 변경 감지 기능 사용
     * => 변경 감지 기능을 사용하면 원하는 속성만 선택해서 변경할 수 있지만,
     * 병합을 사용하면 모든 속성이 변경된다 (병합 시 값이 없다면 null로 업데이트 할 위험도 있다, 모든 필드를 교체하기 때문)
     * 그래서 엔티티를 변경할떄는 !! 변경 감지 기능을 사용하는것이 안전하다. (merge 되도록 사용x)
     */
    @Transactional
    public Item updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId);     // 영속상태
        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuantity);

        // 커밋됨 -> 플래쉬를 날림 (변경감지) 후 db에 업데이트

        return findItem;

    }


    // 상품 조회
    public List<Item> findItems() {
       return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
