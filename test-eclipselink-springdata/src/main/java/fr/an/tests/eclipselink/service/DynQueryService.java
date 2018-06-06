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

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Component;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.jpa.impl.JPAQuery;

import fr.an.tests.eclipselink.domain.City_;
import fr.an.tests.eclipselink.domain.Hotel;
import fr.an.tests.eclipselink.domain.Hotel_;
import fr.an.tests.eclipselink.domain.QHotel;
import fr.an.tests.eclipselink.service.DynQueryService.HotelSpecification;

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
		
		private void foo() {
			// TODO Auto-generated method stub
			DynQueryService service = new DynQueryService();
			
			HotelSpecification crit1 = new HotelSpecification().
					nameLike("Hilton%");
			HotelSpecification crit2 = new HotelSpecification().
					nameLike("Hilton%").cityNameLike("Paris");
			HotelSpecification crit3 = new HotelSpecification().
					nameLike("Hilton%").cityNameLike("Paris").addressLike("rue Saint Lazare");
			
			service.findByQuery(crit1);
			service.findByQuery(new HotelSpecification().
					nameLike("Hilton%").cityNameLike("Paris"));
			
			Assert.assertNotNull(crit1);
			Assert.assertNotNull(crit2);
			Assert.assertNotNull(crit3);
		}
	}
	
	public List<Hotel> findByQuery_JPAStrings_noBindVars(String critNameLike, String critAddressLike, String critCityNameLike) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Hotel> cq = cb.createQuery(Hotel.class);
		Map<String,Object> params = new HashMap<>();
		Root<Hotel> root = cq.from(Hotel.class);
		cq.select(root);
		List<Predicate> predicates = new ArrayList<>();
		
		if (critNameLike != null) {
			predicates.add(cb.like(root.get("name"), critNameLike));
		}
		if (critAddressLike != null) {
			predicates.add(cb.like(root.get("address"), critAddressLike));
		}
		if (critCityNameLike != null) {
			predicates.add(cb.like(root.get("city").get("name"), critCityNameLike));
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
		
	
	
	public List<Hotel> findByQuery_JPAStrings(String critNameLike, String critAddressLike, String critCityNameLike) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Hotel> cq = cb.createQuery(Hotel.class);
		Map<String,Object> params = new HashMap<>();
		Root<Hotel> root = cq.from(Hotel.class);
		cq.select(root);
		List<Predicate> predicates = new ArrayList<>();
		
		if (critNameLike != null) {
			predicates.add(cb.like(root.get("name"), cb.parameter(String.class, "nameLike")));
			params.put("nameLike", critNameLike);
		}
		if (critAddressLike != null) {
			predicates.add(cb.like(root.get("address"), cb.parameter(String.class, "addressLike")));
			params.put("addressLike", critAddressLike);
		}
		if (critCityNameLike != null) {
			predicates.add(cb.like(root.get("city").get("name"), cb.parameter(String.class, "cityNameLike")));
			params.put("cityNameLike", critCityNameLike);
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

	public List<Hotel> findByQueryDslBindParams(HotelSpecification spec) {
		JPAQuery<Hotel> query = new JPAQuery<>(em);
		QueryMetadata qm = query.getMetadata();
		JPAQuery<Hotel> q = query.select(QHotel.hotel).from(QHotel.hotel);
		
		if (spec.nameLike != null) {
			q.where(QHotel.hotel.name.like(param(qm, spec.nameLike)));
		}
		if (spec.addressLike != null) {
			q.where(QHotel.hotel.address.like(param(qm, spec.addressLike)));
		}
		if (spec.cityNameLike != null) {
			q.where(QHotel.hotel.city.name.like(param(qm, spec.cityNameLike)));
		}
		
		List<Hotel> res = q.fetch();
		return res;
	}
	
	
	public com.querydsl.core.types.Predicate specificationToQueryDslPredicate(HotelSpecification spec) {
		BooleanBuilder res = new BooleanBuilder();
		
		if (spec.nameLike != null) {
			res.and(QHotel.hotel.name.like(spec.nameLike)); // NO bind-var!
		}
		if (spec.addressLike != null) {
			res.and(QHotel.hotel.address.like(spec.addressLike)); // NO bind-var!
		}
		if (spec.cityNameLike != null) {
			res.and(QHotel.hotel.city.name.like(spec.cityNameLike)); // NO bind-var!
		}
		return res.getValue();
	}
	
	public List<Hotel> findByQueryDslPredicate(com.querydsl.core.types.Predicate predicate) {
		JPAQuery<Hotel> query = new JPAQuery<>(em);
		JPAQuery<Hotel> q = query.select(QHotel.hotel).from(QHotel.hotel);
		q.where(predicate);
		List<Hotel> res = q.fetch();
		return res;
	}
	
	public static interface HotelRepository2 extends JpaRepository<Hotel,Integer>, 
													JpaSpecificationExecutor<Hotel> {
		
		@Override Hotel findOne(Specification<Hotel> spec);
		@Override List<Hotel> findAll(Specification<Hotel> spec);
		@Override Page<Hotel> findAll(Specification<Hotel> spec, Pageable pageable);
		@Override List<Hotel> findAll(Specification<Hotel> spec, Sort sort);
		@Override long count(Specification<Hotel> spec);
	}
	

	public interface HotelRepository extends JpaRepository<Hotel,Integer>, 
												// JpaSpecificationExecutor<Hotel>,
												QueryDslPredicateExecutor<Hotel> {
		
		// using querydsl.Predicate ...  (not java.persistence.Specification)
		@Override Hotel findOne(Predicate qryDslPredicate);
		@Override Iterable<Hotel> findAll(Predicate qryDslPredicate);
		@Override Iterable<Hotel> findAll(Predicate qryDslPredicate, Sort sort);
		@Override Iterable<Hotel> findAll(Predicate qryDslPredicate, OrderSpecifier<?>... orders);
		@Override Iterable<Hotel> findAll(OrderSpecifier<?>... orders);
		@Override Page<Hotel> findAll(Predicate qryDslPredicate, Pageable pageable);
		@Override long count(Predicate qryDslPredicate);
		@Override boolean exists(Predicate qryDslPredicate);
	}
	
	public void foo(HotelRepository hotelRepository, String nameLike, String cityLike) {
		QHotel.hotel h = QHotel.hotel;
		Predicate pred = h.name.like(nameLike).and(h.city.like(cityLike));
		List<Hotel> hotels1 = hotelRepository.findAll(pred);
		List<Hotel> hotels2 = hotelRepository.findAll(h.name.like(nameLike).and(h.city.like(cityLike)));
		
	}
	
	public static class HotelJPASpecification implements Specification<Hotel> {
		
		public String nameLike;
		public String addressLike;
		public String cityNameLike;
		// setters..
		
		@Override
		public Predicate toPredicate(Root<Hotel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
			List<Predicate> predicates = new ArrayList<>();
			
			if (nameLike != null) {
				predicates.add(cb.like(root.get("name"), nameLike)); // NO bind-var !!
			}
			if (addressLike != null) {
				predicates.add(cb.like(root.get("address"), addressLike)); // NO bind-var !!
			}
			if (cityNameLike != null) {
				predicates.add(cb.like(root.get("city").get("name"), cityNameLike));  // NO bind-var !!
			}
			Predicate andPredicates = cb.and(predicates.toArray(new Predicate[predicates.size()]));
			return andPredicates;
		}
		
	}
	
	public static Param<String> param(QueryMetadata qm, String value) {
		Param<String> param = new Param<>(String.class);
		qm.setParam(param, value);
		return param;
	}
}
