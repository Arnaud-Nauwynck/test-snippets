package fr.an.test.ambarijpa;

import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "widget_layout")
@TableGenerator(name = "widget_layout_id_generator",
        table = "ambari_sequences",
        pkColumnName = "sequence_name",
        valueColumnName = "sequence_value",
        pkColumnValue = "widget_layout_id_seq",
        initialValue = 0,
        uniqueConstraints=@UniqueConstraint(columnNames={"layout_name", "cluster_id"})
)
@NamedQueries({
    @NamedQuery(name = "WidgetLayoutEntity.findAll", query = "SELECT widgetLayout FROM WidgetLayoutEntity widgetLayout"),
    @NamedQuery(name = "WidgetLayoutEntity.findByCluster", query = "SELECT widgetLayout FROM WidgetLayoutEntity widgetLayout WHERE widgetLayout.clusterId = :clusterId"),
    @NamedQuery(name = "WidgetLayoutEntity.findBySectionName", query = "SELECT widgetLayout FROM WidgetLayoutEntity widgetLayout WHERE widgetLayout.sectionName = :sectionName"),
    @NamedQuery(name = "WidgetLayoutEntity.findByName", query = "SELECT widgetLayout FROM WidgetLayoutEntity widgetLayout WHERE widgetLayout.clusterId = :clusterId AND widgetLayout.layoutName = :layoutName AND widgetLayout.userName = :userName")
    })
public class WidgetLayoutEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "widget_layout_id_generator")
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "layout_name", nullable = false, length = 255)
  private String layoutName;

  @Column(name = "section_name", nullable = false, length = 255)
  private String sectionName;

  @Column(name = "cluster_id", nullable = false)
  private Long clusterId;

  @Column(name = "user_name", nullable = false)
  private String userName;

  @Column(name = "scope", nullable = false)
  private String scope;

  @Column(name = "display_name")
  private String displayName;

  
  @ManyToOne
  @JoinColumn(name = "cluster_id", referencedColumnName = "cluster_id", nullable = false, updatable = false, insertable = false)
  private ClusterEntity clusterEntity;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "widgetLayout", orphanRemoval = true)
  @OrderBy("widgetOrder")
  private List<WidgetLayoutUserWidgetEntity> listWidgetLayoutUserWidgetEntity;

}
