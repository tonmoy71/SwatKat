package co.gobd.tracker.model.job;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import co.gobd.tracker.model.job.order.Order;
import co.gobd.tracker.model.job.task.JobTask;

/**
 * Created by fahad on 4/25/16.
 */
public class JobModel{

    private String Name;
    private String State;
    private Order Order;
    private User User;
    private String JobServedBy;
    private List<JobTask> Tasks;
    private String CreateTime;
    private String ModifiedTime;
    private String PreferredDeliveryTime;
    private String InvoiceId;
    private String PaymentMethod;
    private Boolean Deleted;
    private String PaymentStatus;
    private String HRID;
    private String Id;

    public JobModel(String name, String state, Order order,
                    User user, String jobServedBy, List<JobTask> tasks,
                    String createTime, String modifiedTime, String preferredDeliveryTime,
                    String invoiceId, String paymentMethod, Boolean deleted, String paymentStatus,
                    String HRID, String id) {

        Name = name;
        State = state;
        Order = order;
        User = user;
        JobServedBy = jobServedBy;
        Tasks = tasks;
        CreateTime = createTime;
        ModifiedTime = modifiedTime;
        PreferredDeliveryTime = preferredDeliveryTime;
        InvoiceId = invoiceId;
        PaymentMethod = paymentMethod;
        Deleted = deleted;
        PaymentStatus = paymentStatus;
        this.HRID = HRID;
        Id = id;
    }


    public String getName(){
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getState(){
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getJobServedBy() {
        return JobServedBy;
    }

    public void setJobServedBy(String jobServedBy) {
        JobServedBy = jobServedBy;
    }

    public List<JobTask> getTasks(){
        return Tasks;
    }

    public void setTasks(List<JobTask> Tasks){
        this.Tasks = Tasks;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getHRID() {
        return HRID;
    }

    public void setHRID(String HRID) {
        this.HRID = HRID;
    }

    public String getPaymentStatus() {
        return PaymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        PaymentStatus = paymentStatus;
    }

    public Boolean getDeleted() {
        return Deleted;
    }

    public void setDeleted(Boolean deleted) {
        Deleted = deleted;
    }

    public String getPaymentMethod() {
        return PaymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        PaymentMethod = paymentMethod;
    }

    public String getInvoiceId() {
        return InvoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        InvoiceId = invoiceId;
    }

    public String getPreferredDeliveryTime() {
        return PreferredDeliveryTime;
    }

    public void setPreferredDeliveryTime(String preferredDeliveryTime) {
        PreferredDeliveryTime = preferredDeliveryTime;
    }

    public String getModifiedTime() {
        return ModifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        ModifiedTime = modifiedTime;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public co.gobd.tracker.model.job.User getUser() {
        return User;
    }

    public void setUser(co.gobd.tracker.model.job.User user) {
        User = user;
    }

    public co.gobd.tracker.model.job.order.Order getOrder() {
        return Order;
    }

    public void setOrder(co.gobd.tracker.model.job.order.Order order) {
        Order = order;
    }

    @Override
    public String toString() {
        return "JobModel{" +
                "Name='" + Name + '\'' +
                ", State='" + State + '\'' +
                ", Tasks=" + Tasks +
                '}';
    }

}
