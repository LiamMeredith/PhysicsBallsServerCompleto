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
 * @author pepe
 */
public class DbSpaceJpaController implements Serializable {

    public DbSpaceJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DbSpace dbSpace) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(dbSpace);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DbSpace dbSpace) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            dbSpace = em.merge(dbSpace);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = dbSpace.getId();
                if (findDbSpace(id) == null) {
                    throw new NonexistentEntityException("The dbSpace with id " + id + " no longer exists.");
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
            DbSpace dbSpace;
            try {
                dbSpace = em.getReference(DbSpace.class, id);
                dbSpace.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The dbSpace with id " + id + " no longer exists.", enfe);
            }
            em.remove(dbSpace);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DbSpace> findDbSpaceEntities() {
        return findDbSpaceEntities(true, -1, -1);
    }

    public List<DbSpace> findDbSpaceEntities(int maxResults, int firstResult) {
        return findDbSpaceEntities(false, maxResults, firstResult);
    }

    private List<DbSpace> findDbSpaceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DbSpace.class));
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

    public DbSpace findDbSpace(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DbSpace.class, id);
        } finally {
            em.close();
        }
    }

    public int getDbSpaceCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DbSpace> rt = cq.from(DbSpace.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
