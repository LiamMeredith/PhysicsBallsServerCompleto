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
public class DbStopitemJpaController implements Serializable {

    public DbStopitemJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DbStopitem dbStopitem) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(dbStopitem);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDbStopitem(dbStopitem.getId()) != null) {
                throw new PreexistingEntityException("DbStopitem " + dbStopitem + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DbStopitem dbStopitem) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            dbStopitem = em.merge(dbStopitem);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = dbStopitem.getId();
                if (findDbStopitem(id) == null) {
                    throw new NonexistentEntityException("The dbStopitem with id " + id + " no longer exists.");
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
            DbStopitem dbStopitem;
            try {
                dbStopitem = em.getReference(DbStopitem.class, id);
                dbStopitem.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The dbStopitem with id " + id + " no longer exists.", enfe);
            }
            em.remove(dbStopitem);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DbStopitem> findDbStopitemEntities() {
        return findDbStopitemEntities(true, -1, -1);
    }

    public List<DbStopitem> findDbStopitemEntities(int maxResults, int firstResult) {
        return findDbStopitemEntities(false, maxResults, firstResult);
    }

    private List<DbStopitem> findDbStopitemEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DbStopitem.class));
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

    public DbStopitem findDbStopitem(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DbStopitem.class, id);
        } finally {
            em.close();
        }
    }

    public int getDbStopitemCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DbStopitem> rt = cq.from(DbStopitem.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
