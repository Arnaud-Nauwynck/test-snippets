package fr.an.test.ambarijpa;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.an.test.ambarijpa.WidgetLayoutUserWidgetEntity.WidgetLayoutUserWidgetEntityPK;

@IdClass(WidgetLayoutUserWidgetEntityPK.class)
@Entity
@Table(name = "widget_layout_user_widget")
public class WidgetLayoutUserWidgetEntity {

	@Id
	@Column(name = "widget_layout_id", nullable = false, updatable = false, insertable = false)
	private Long widgetLayoutId;

	@Id
	@Column(name = "widget_id", nullable = false, updatable = false, insertable = false)
	private Long userWidgetId;

	public static class WidgetLayoutUserWidgetEntityPK implements Serializable {

		@Id
		@Column(name = "widget_layout_id", nullable = false, updatable = false)
		private Long widgetLayoutId;

		@Id
		@Column(name = "widget_id", nullable = false, updatable = false)
		private Long userWidgetId;

	}

	@ManyToOne
	@JoinColumn(name = "widget_layout_id", referencedColumnName = "id")
	private WidgetLayoutEntity widgetLayout;

	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "widget_id", referencedColumnName = "id")
	private WidgetEntity widget;

	@Column(name = "widget_order")
	private Integer widgetOrder;


}
