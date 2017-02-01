package fr.an.tests.eclipselink.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.jpa.impl.JPAQuery;

import fr.an.tests.eclipselink.domain.City_;
import fr.an.tests.eclipselink.domain.Hotel;
import fr.an.tests.eclipselink.domain.Hotel_;
import fr.an.tests.eclipselink.domain.QHotel;

@Component
public class DynQueryService {

	@Autowired
	private EntityManager em;
	
	public static class HotelSpecification {
		public String nameLike;
		public String addressLike;
		public String cityNameLike;
		
		public HotelSpecification nameLike(String nameLike) {
			this.nameLike = nameLike;
			return this;
		}
		public HotelSpecification addressLike(String addressLike) {
			this.addressLike = addressLike;
			return this;
		}
		public HotelSpecification cityNameLike(String cityNameLike) {
			this.cityNameLike = cityNameLike;
			return this;
		}
	}
	
	public List<Hotel> findByQuery(HotelSpecification spec) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Hotel> cq = cb.createQuery(Hotel.class);
		Map<String,Object> params = new HashMap<>();
		Root<Hotel> root = cq.from(Hotel.class);
		cq.select(root);
		List<Predicate> predicates = new ArrayList<>();
		
		if (spec.nameLike != null) {
			predicates.add(cb.like(root.get(Hotel_.name), cb.parameter(String.class, "nameLike")));
			params.put("nameLike", spec.nameLike);
		}
		if (spec.addressLike != null) {
			predicates.add(cb.like(root.get(Hotel_.address), cb.parameter(String.class, "addressLike")));
			params.put("addressLike", spec.addressLike);
		}
		if (spec.cityNameLike != null) {
			predicates.add(cb.like(root.get(Hotel_.city).get(City_.name), cb.parameter(String.class, "cityNameLike")));
			params.put("cityNameLike", spec.cityNameLike);
		}
		
		Predicate andPredicates = cb.and(predicates.toArray(new Predicate[predicates.size()]));
		cq.where(andPredicates);
		TypedQuery<Hotel> q = em.createQuery(cq);
		for(Map.Entry<String,Object> e : params.entrySet()) {
			q.setParameter(e.getKey(), e.getValue());
		}
		List<Hotel> res = q.getResultList();
		return res;
	}

	public List<Hotel> findByQueryDsl(HotelSpecification spec) {
		JPAQuery<Hotel> query = new JPAQuery<>(em);
		JPAQuery<Hotel> q = query.select(QHotel.hotel).from(QHotel.hotel);
		
		if (spec.nameLike != null) {
			q.where(QHotel.hotel.name.like(spec.nameLike));
		}
		if (spec.addressLike != null) {
			q.where(QHotel.hotel.address.like(spec.addressLike));
		}
		if (spec.cityNameLike != null) {
			q.where(QHotel.hotel.city.name.like(spec.cityNameLike));
		}
		
		List<Hotel> res = q.fetch();
		return res;
	}	
}
