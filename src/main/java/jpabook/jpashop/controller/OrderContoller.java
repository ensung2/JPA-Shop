package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderContoller {

    /* 상품 주문 = 회원정보 + 아이템정보 */

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    /* 상품 주문 클릭 시 화면 */
    @GetMapping("/order")
    public String createForm(Model model){

        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }
    /* 상품 주문 폼 작성 후 submit 클릭 시 실행 */
    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId,    // @RequestParam : form-submit 방식
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count) {

        orderService.order(memberId, itemId, count);
        return "redirect:/orders";      // 주문 내역(목록)으로 이동

    }

    /* 주문 내역(목록) 페이지로 이동 */
    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch")OrderSearch orderSearch,  // 회원이름, 주문상태 확인
                            Model model){
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);
        return "order/orderList";
    }

    /* 주문 취소 */
    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancleOrder(orderId);
        return "redirect:/orders";
    }
}
