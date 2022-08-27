

import orm.anotation.Colum;
import orm.anotation.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Table("products ")
public class Products {

    @Colum("id")
    private Integer id;

    @Colum("name")
    private String name;

    @Colum("price")
    private Integer price;
    @Colum("created_at")

    private Date createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Date getCreateAt() {
        return createdAt;
    }

    public void setCreateAt(Date createAt) {
        this.createdAt = createAt;
    }

    @Override
    public String toString() {
        return "Products{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", createdAt=" + createdAt +
                '}';
    }
}
