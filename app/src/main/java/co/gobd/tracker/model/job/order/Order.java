package co.gobd.tracker.model.job.order;

import co.gobd.tracker.model.job.Location;

/**
 * Created by fahad on 5/16/16.
 */
public class Order {

    private Location From;
    private Location To;
    private String Description;
    private OrderCart OrderCart;
    private String NoteToDeliveryMan;
    private String Type;
    private String Variant;
    private String UserId;
    private Double RequiredChangeFor;
    private String PaymentMethod;

    public Order(Location from, Location to, String description, OrderCart orderCart,
                 String noteToDeliveryMan, String type, String variant,
                 String userId, Double RequiredChangeFor, String paymentMethod) {

        From = from;
        To = to;
        Description = description;
        OrderCart = orderCart;
        NoteToDeliveryMan = noteToDeliveryMan;
        Type = type;
        Variant = variant;
        UserId = userId;
        this.RequiredChangeFor = RequiredChangeFor;
        PaymentMethod = paymentMethod;
    }

    /**
     * @return The From
     */
    public Location getFrom() {
        return From;
    }

    /**
     * @param From The From
     */
    public void setFrom(Location From) {
        this.From = From;
    }

    /**
     * @return The To
     */
    public Location getTo() {
        return To;
    }

    /**
     * @param To The To
     */
    public void setTo(Location To) {
        this.To = To;
    }

    /**
     * @return The PackageDescription
     */
    public String getDescription() {
        return Description;
    }

    /**
     * @param Description The Description
     */
    public void setDescription(String Description) {
        this.Description = Description;
    }

    /**
     * @return The OrderCart
     */
    public OrderCart getOrderCart() {
        return OrderCart;
    }

    /**
     * @param OrderCart The OrderCart
     */
    public void setOrderCart(OrderCart OrderCart) {
        this.OrderCart = OrderCart;
    }

    /**
     * @return The NoteToDeliveryMan
     */
    public String getNoteToDeliveryMan() {
        return NoteToDeliveryMan;
    }

    /**
     * @param NoteToDeliveryMan The NoteToDeliveryMan
     */
    public void setNoteToDeliveryMan(String NoteToDeliveryMan) {
        this.NoteToDeliveryMan = NoteToDeliveryMan;
    }

    /**
     * @return The Type
     */
    public String getType() {
        return Type;
    }

    /**
     * @param Type The Type
     */
    public void setType(String Type) {
        this.Type = Type;
    }

    /**
     * @return The UserId
     */
    public String getUserId() {
        return UserId;
    }

    /**
     * @param UserId The UserId
     */
    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    /**
     * @return The PaymentMethod
     */
    public String getPaymentMethod() {
        return PaymentMethod;
    }

    /**
     * @param PaymentMethod The PaymentMethod
     */
    public void setPaymentMethod(String PaymentMethod) {
        this.PaymentMethod = PaymentMethod;
    }

    public Double getRequiredChangeFor() {
        return RequiredChangeFor;
    }

    public void setRequiredChangeFor(Double requiredChangeFor) {
        RequiredChangeFor = requiredChangeFor;
    }


}

