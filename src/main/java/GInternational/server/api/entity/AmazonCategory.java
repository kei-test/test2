package GInternational.server.api.entity;

import GInternational.server.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity(name = "amazon_category")
public class AmazonCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "amazon_category_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "category_role", nullable = false)
    private String categoryRole;

    @JsonIgnore
    @OneToMany(mappedBy = "amazonCategory", cascade = CascadeType.REMOVE)
    private List<AmazonCommunity> communities = new ArrayList<>();
}
