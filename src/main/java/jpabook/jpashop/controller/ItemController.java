package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    /* 상품 등록을 위한 컨트롤러*/

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form) {

        /*setter를 하나씩 쓰는것보다 order에서 사용했던것과 같이
        * 메서드를 사용하여 작업하는게 제일 좋다. (보안상, 유지보수상)
        * 해당 setter는 예제를 위해 간편하게 넘어가는걸로~*/

        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);

        return "redirect:/items";       // 상품등록 성공시 상품리스트로 이동
    }

    /* 상품 목록 조회 */
    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    /* 상품 수정 버튼을 눌렀을 때 */
    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
            Book item = (Book) itemService.findOne(itemId);     // book만 사용하기 위해 casting

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    /**
     * 2. merge 방식
     *
     * 1) 준영속 엔티티의 식별자 값으로 영속성 엔티티를 조회
     * 2) 영속성 엔티티의 값을 준영속 엔티티의 값으로 모두 병합(교체)
     * 3) 트랜잭션 커밋 시점에 변경 감지 기능이 동작해서 db에 update sql이 실행
     */
    /* 상품 수정 후 submit을 눌렀을 때 - merge 버전 */
    @PostMapping("/items/{itemId}/edit")
    public String updateItem(@ModelAttribute("form") BookForm form,
                             @PathVariable Long itemId) {
//        Book book = new Book();
//
//        book.setId(form.getId());
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());
//
//        itemService.saveItem(book);

        /* 병합감지를 사용한 상품 수정 */
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());
        return "redirect:/items";
    }
}
