package org.yes.cart.dao.impl;

import org.apache.lucene.search.SortField;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.util.ShopCodeContext;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class GenericDAOHibernateImpl<T, PK extends Serializable> extends HibernateDaoSupport
        implements GenericDAO<T, PK> {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    final private Class<T> persistentClass;
    final private EntityFactory entityFactory;


    /**
     * Default constructor.
     *
     * @param type          - entity type
     * @param entityFactory {@link EntityFactory} to create the entities
     */
    @SuppressWarnings("unchecked")
    public GenericDAOHibernateImpl(
            final Class<T> type,
            final EntityFactory entityFactory) {
        this.persistentClass = type;
        this.entityFactory = entityFactory;
    }

    /**
     * {@inheritDoc}
     */
    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    /**
     * {@inheritDoc}
     */
    public T findById(PK id) {
        return findById(id, false);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T findById(final PK id, final boolean lock) {
        T entity;
        if (lock) {
            entity = (T) getHibernateTemplate().get(getPersistentClass(), id, LockMode.UPGRADE);
        } else {
            entity = (T) getHibernateTemplate().get(getPersistentClass(), id);
        }
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByExample(final T exampleInstance, final String[] excludeProperty) {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        Example example = Example.create(exampleInstance);
        for (String exclude : excludeProperty) {
            example.excludeProperty(exclude);
        }
        crit.add(example);
        return crit.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> T findSingleByNamedQuery(final String namedQueryName, final Object... parameters) {
        List<T> rez = (List<T>) this.findByNamedQuery(namedQueryName, parameters);
        if (!rez.isEmpty()) {
            return rez.get(0);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Object getScalarResultByNamedQuery(final String namedQueryName, final Object... parameters) {
        Query query = getSession().getNamedQuery(namedQueryName);
        int idx = 1;
        for (Object param : parameters) {
            query.setParameter(String.valueOf(idx), param);
            idx++;
        }
        return query.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    public Object findSingleByQuery(final String hsqlQuery, final Object... parameters) {
        Query query = getSession().createQuery(hsqlQuery);
        int idx = 1;
        for (Object param : parameters) {
            query.setParameter(String.valueOf(idx), param);
            idx++;
        }
        final List rez = query.list();
        int size = rez.size();
        switch (size) {
            case 0: {
                return null;
            }
            case 1: {
                return rez.get(0);
            }
            default: {
                LOG.error("#findSingleByQuery has more than one result for " + hsqlQuery);
                return rez.get(0);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByNamedQuery(final String namedQueryName, final Object... parameters) {
        Query query = getSession().getNamedQuery(namedQueryName);
        if (parameters != null) {
            int idx = 1;
            for (Object param : parameters) {
                query.setParameter(String.valueOf(idx), param);
                idx++;
            }
        }
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Object> findQueryObjectByNamedQuery(final String namedQueryName, final Object... parameters) {
        Query query = getSession().getNamedQuery(namedQueryName);
        int idx = 1;
        if (parameters != null) {
            for (Object param : parameters) {
                query.setParameter(String.valueOf(idx), param);
                idx++;
            }
        }
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findQueryObjectsByNamedQuery(final String namedQueryName, final Object... parameters) {
        Query query = getSession().getNamedQuery(namedQueryName);
        int idx = 1;
        if (parameters != null) {
            for (Object param : parameters) {
                query.setParameter(String.valueOf(idx), param);
                idx++;
            }
        }
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findQueryObjectsByNamedQueryWithList(final String namedQueryName, final List parameter) {
        Query query = getSession().getNamedQuery(namedQueryName);
        query.setParameterList("list", parameter);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findQueryObjectsByNamedQueryWithList(
            final String namedQueryName, final Collection<Object> listParameter,
            final Object... parameters) {
        Query query = getSession().getNamedQuery(namedQueryName);
        query.setParameterList("list", listParameter);
        int idx = 1;
        if (parameters != null) {
            for (Object param : parameters) {
                query.setParameter(String.valueOf(idx), param);
                idx++;
            }
        }
        return query.list();
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findRangeByNamedQuery(final String namedQueryName,
                                         final int firtsResult,
                                         final int maxResults,
                                         final Object... parameters) {
        Query query = getSession().getNamedQuery(namedQueryName);
        query.setFirstResult(firtsResult);
        query.setMaxResults(maxResults);
        int idx = 1;
        for (Object param : parameters) {
            query.setParameter(String.valueOf(idx), param);
            idx++;
        }
        return query.list();
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        return findByCriteria();
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T saveOrUpdate(T entity) {
        getHibernateTemplate().saveOrUpdate(entity);
        return entity;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T create(T entity) {
        getHibernateTemplate().save(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T update(T entity) {
        getHibernateTemplate().update(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(T entity) {
        getHibernateTemplate().delete(entity);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByCriteria(Criterion... criterion) {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return crit.list();
    }

    /**
     * {@inheritDoc}
     */
    public T findSingleByCriteria(final Criterion... criterion) {
        return findSingleByCriteria(null, criterion);
    }

    /**
     * {@inheritDoc}
     */
    public T findSingleByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        if (criteriaTuner != null) {
            criteriaTuner.tune(crit);
        }
        return (T) crit.uniqueResult();
    }


    private Class<T> getPersistentClass() {
        return persistentClass;
    }


    /**
     * Check is entity need to be in index.
     *
     * @param entity entity to check
     * @return true if entity need to be in lucene index.
     */
    protected boolean isIncludeInLuceneIndex(T entity) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public int fullTextSearchReindex(final PK primaryKey) {
        int result = 0;
        if (null != getPersistentClass().getAnnotation(org.hibernate.search.annotations.Indexed.class)) {
            T entity = findById(primaryKey);
            if (entity != null) {
                FullTextSession fullTextSession = Search.getFullTextSession(getSession());
                fullTextSession.purge(getPersistentClass(), (Serializable) entity);
                Transaction tx = fullTextSession.beginTransaction();
                if (isIncludeInLuceneIndex(entity)) {
                    fullTextSession.index(entity);
                    fullTextSession.flushToIndexes();
                    result++;
                }
                tx.commit();
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public int fullTextSearchReindex() {
        final int BATCH_SIZE = 20;
        int index = 0;

        if (null != getPersistentClass().getAnnotation(org.hibernate.search.annotations.Indexed.class)) {
            FullTextSession fullTextSession = Search.getFullTextSession(getSession());
            fullTextSession.setFlushMode(FlushMode.MANUAL);
            fullTextSession.setCacheMode(CacheMode.IGNORE);
            fullTextSession.purgeAll(getPersistentClass());
            fullTextSession.getSearchFactory().optimize(getPersistentClass());
            Transaction tx = fullTextSession.beginTransaction();
            ScrollableResults results = fullTextSession.createCriteria(persistentClass)
                    .setFetchSize(BATCH_SIZE)
                    .scroll(ScrollMode.FORWARD_ONLY);

            while (results.next()) {
                index++;
                T entity = (T) results.get(0);
                if (isIncludeInLuceneIndex(entity)) {
                    fullTextSession.index(entity); //index each element
                    if (index % BATCH_SIZE == 0) {
                        fullTextSession.flushToIndexes(); //apply changes to indexes
                        fullTextSession.clear(); //clear since the queue is processed
                        if (LOG.isInfoEnabled()) {
                            LOG.info("Indexed " + index + " items of " + persistentClass + " class");
                        }
                    }
                }
            }
            fullTextSession.flushToIndexes(); //apply changes to indexes
            fullTextSession.clear(); //clear since the queue is processed
            tx.commit();
        }


        return index;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> fullTextSearch(final org.apache.lucene.search.Query query) {
        if (null != getPersistentClass().getAnnotation(org.hibernate.search.annotations.Indexed.class)) {
            FullTextSession fullTextSession = Search.getFullTextSession(getSession());
            Query fullTextQuery = fullTextSession.createFullTextQuery(query, getPersistentClass());
            List<T> list = fullTextQuery.list();
            if (list != null) {
                return list;
            }
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> fullTextSearch(final org.apache.lucene.search.Query query,
                                  final int firtsResult,
                                  final int maxResults) {
        return fullTextSearch(query, firtsResult, maxResults, null);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> fullTextSearch(final org.apache.lucene.search.Query query,
                                  final int firtsResult,
                                  final int maxResults,
                                  final String sortFieldName) {
        return fullTextSearch(query, firtsResult, maxResults, sortFieldName, false);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> fullTextSearch(final org.apache.lucene.search.Query query,
                                  final int firtsResult,
                                  final int maxResults,
                                  final String sortFieldName,
                                  final boolean reverse) {
        if (null != getPersistentClass().getAnnotation(org.hibernate.search.annotations.Indexed.class)) {
            FullTextSession fullTextSession = Search.getFullTextSession(getSession());
            FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(query, getPersistentClass());
            if (sortFieldName != null) {
                org.apache.lucene.search.Sort sort = new org.apache.lucene.search.Sort(
                        new org.apache.lucene.search.SortField(sortFieldName, SortField.STRING, reverse));
                fullTextQuery.setSort(sort);
            }
            fullTextQuery.setFirstResult(firtsResult);
            fullTextQuery.setMaxResults(maxResults);
            List<T> list = fullTextQuery.list();
            if (list != null) {
                return list;
            }
        }
        return Collections.EMPTY_LIST;
    }


    /**
     * {@inheritDoc}
     */
    public int getResultCount(final org.apache.lucene.search.Query query) {
        FullTextSession fullTextSession = Search.getFullTextSession(getSession());
        return fullTextSession.createFullTextQuery(query, getPersistentClass()).getResultSize();
    }

    /**
     * {@inheritDoc}
     */
    public int executeNativeUpdate(final String nativeQuery) {
        SQLQuery sqlQuery = getSession().createSQLQuery(nativeQuery);
        return sqlQuery.executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    public List executeNativeQuery(final String nativeQuery) {
        SQLQuery sqlQuery = getSession().createSQLQuery(nativeQuery);
        return sqlQuery.list();
    }

    /**
     * {@inheritDoc}
     */
    public List executeHsqlQuery(final String hsql) {
        Query query = getSession().createQuery(hsql);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    public int executeHsqlUpdate(final String hsql) {
        Query query = getSession().createQuery(hsql);
        return query.executeUpdate();
    }


    /**
     * {@inheritDoc}
     */
    public int executeNativeUpdate(final String nativeQuery, final Object... parameters) {
        SQLQuery sqlQuery = getSession().createSQLQuery(nativeQuery);
        int idx = 1;
        for (Object param : parameters) {
            sqlQuery.setParameter(String.valueOf(idx), param);
            idx++;
        }
        return sqlQuery.executeUpdate();
    }


    /**
     * {@inheritDoc}
     */
    public int executeUpdate(final String namedQueryName, final Object... parameters) {
        final Query query = getSession().getNamedQuery(namedQueryName);
        int idx = 1;
        if (parameters != null) {
            for (Object param : parameters) {
                query.setParameter(String.valueOf(idx), param);
                idx++;
            }
        }
        return query.executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    public void flushClear() {
        getSession().flush();
        getSession().clear();
    }


}