package com.pj.magic.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.AreaDao;
import com.pj.magic.model.Area;

@Repository
public class AreaDaoImpl implements AreaDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public void save(Area area) {
		if (area.getId() == null) {
			entityManager.persist(area);
		} else {
			entityManager.merge(area);
		}
	}

	@Override
	public Area get(long id) {
		return entityManager.find(Area.class, id);
	}

	@Override
	public List<Area> getAll() {
        return entityManager.createQuery("SELECT a FROM Area a ORDER BY a.name", Area.class).getResultList();
	}

	@Override
	public Area findByName(String name) {
        TypedQuery<Area> query = entityManager.createQuery("SELECT a FROM Area a WHERE a.name = :name", Area.class);
        query.setParameter("name", name);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}