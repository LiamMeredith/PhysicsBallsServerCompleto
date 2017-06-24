/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Liam-Portatil
 */
public class DbObstacleJpaController implements Serializable {

    public DbObstacleJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DbObstacle dbObstacle) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(dbObstacle);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DbObstacle dbObstacle) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            dbObstacle = em.merge(dbObstacle);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = dbObstacle.getId();
                if (findDbObstacle(id) == null) {
                    throw new NonexistentEntityException("The dbObstacle with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DbObstacle dbObstacle;
            try {
                dbObstacle = em.getReference(DbObstacle.class, id);
                dbObstacle.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The dbObstacle with id " + id + " no longer exists.", enfe);
            }
            em.remove(dbObstacle);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DbObstacle> findDbObstacleEntities() {
        return findDbObstacleEntities(true, -1, -1);
    }

    public List<DbObstacle> findDbObstacleEntities(int maxResults, int firstResult) {
        return findDbObstacleEntities(false, maxResults, firstResult);
    }

    private List<DbObstacle> findDbObstacleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DbObstacle.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public DbObstacle findDbObstacle(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DbObstacle.class, id);
        } finally {
            em.close();
        }
    }

    public int getDbObstacleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DbObstacle> rt = cq.from(DbObstacle.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
