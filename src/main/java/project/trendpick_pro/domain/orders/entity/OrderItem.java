package project.trendpick_pro.domain.orders.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.cart.entity.CartItem;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.coupon.entity.CouponCard;
import project.trendpick_pro.domain.product.entity.product.Product;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItem extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_card_id")
    private CouponCard couponCard;

    @Column(name = "order_price", nullable = false)
    private int orderPrice;

    private int orderItemByQuantity;

    @Column(name = "discount_price", nullable = false)
    private int discountPrice;

    @Column(name = "size", nullable = false)
    private String size;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "count", nullable = false)
    private int quantity;

    public static OrderItem of(Product product, int quantity, String size, String color) {
        return new OrderItem(product, quantity, size, color);
    }

    public static OrderItem of(Product product, CartItem cartItem) {
        return new OrderItem(product, cartItem.getQuantity(), cartItem.getSize(), cartItem.getColor());
    }

    public void cancel() {
        this.product.getProductOption().increaseStock(this.quantity);
        if (this.couponCard != null) {
            this.couponCard.cancel(this);
        }
    }

    public void connectOrder(Order order) {
        this.order = order;
    }

    public void applyCouponCard(CouponCard couponCard) {
        this.couponCard = couponCard;
    }

    public void cancelCouponCard(){
        this.couponCard = null;
        this.discountPrice = 0;
    }

    public void discount(int price){
        this.discountPrice += price;
    }

    private OrderItem(Product product, int quantity, String size, String color) {
        this.product = product;
        this.orderPrice = product.getProductOption().getPrice() - (int) (product.getProductOption().getPrice() * (product.getDiscountRate() / 100));
        this.quantity = quantity;
        this.size = size;
        this.color = color;
        this.discountPrice = 0;
        this.orderItemByQuantity = this.orderPrice * this.quantity;
        product.getProductOption().decreaseStock(quantity);
    }
}
