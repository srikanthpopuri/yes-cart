/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.dao;

import org.hibernate.criterion.Criterion;
import org.yes.cart.domain.entityindexer.IndexFilter;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.queryobject.FilteredNavigationRecordRequest;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * Generic DAO service.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface GenericDAO<T, PK extends Serializable> {

    /**
     * Get the entity factory to create entities.
     *
     * @return configured instance of {@link EntityFactory}
     */
    EntityFactory getEntityFactory();

    /**
     * Get value of PK from an object.
     *
     * @param entity entity object
     * @param <I> type
     *
     * @return id value
     */
    <I> I getEntityIdentifier(Object entity);

    /**
     * Find entity by Id.
     *
     * @param id   primary key
     * @param lock true if need lock for update.
     *
     * @return instance of T or null if not found
     */
    T findById(PK id, boolean lock);

    /**
     * Find entity by Id.
     *
     * @param id primary key
     *
     * @return instance of T or null if not found
     */
    T findById(PK id);

    /**
     * Get all entities.
     *
     * @return list of all entities
     */
    List<T> findAll();

    /**
     * Get all entities iterator (scroll results as opposed to load all)
     *
     * @return scrollable results iterator
     */
    ResultsIterator<T> findAllIterator();

    /**
     * Find entities, that mach given example.
     *
     * @param exampleInstance pattern
     * @param excludeProperty property to exclude
     *
     * @return list of found entities
     */
    List<T> findByExample(T exampleInstance, String[] excludeProperty);

    /**
     * Find single entity, that returned by named query.
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     *
     * @return single entity   or null if not found
     */
    <T> T findSingleByNamedQuery(String namedQueryName, Object... parameters);

    /**
     * Find single entity, that returned by named query.
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     *
     * @return single entity   or null if not found
     */
    <T> T findSingleByNamedQueryCached(String namedQueryName, Object... parameters);

    /**
     * Find by hsql query.
     *
     * @param hsqlQuery  query
     * @param parameters parameters
     *
     * @return list of objects.
     */
    List<Object> findByQuery(String hsqlQuery, Object... parameters);


    /**
     * Find single entity, that returned by named query.
     *
     * @param hsqlQuery  HSQL query string
     * @param parameters optional parameters for named query
     *
     * @return single entity
     */
    Object findSingleByQuery(String hsqlQuery, Object... parameters);


    /**
     * Executes aggregate named query, that return single scalar value.
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     *
     * @return single entity
     */
    Object getScalarResultByNamedQuery(String namedQueryName, Object... parameters);

    /**
     * Executes aggregate named query, that return single scalar value. And force load collections .
     *
     * @param namedQueryName      name of query
     * @param parameters          optional parameters for named query
     *
     * @return single entity
     */
    Object getScalarResultByNamedQueryWithInit(String namedQueryName,  Object... parameters);


    /**
     * Find entities within named query .
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     *
     * @return list of found entities
     */
    List<T> findByNamedQuery(String namedQueryName, Object... parameters);

    /**
     * Find entities within named query .
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     *
     * @return list of found entities
     */
    ResultsIterator<T> findByNamedQueryIterator(String namedQueryName, Object... parameters);

    /**
     * Find entities within named query .
     *
     * @param namedQueryName name of query
     * @param timeout timeout to lock object for
     * @param parameters     optional parameters for named query
     *
     * @return list of found entities
     */
    List<T> findByNamedQueryForUpdate(String namedQueryName, int timeout, Object... parameters);

    /**
     * Find entities within named query .
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     *
     * @return list of found entities
     */
    List<T> findByNamedQueryCached(String namedQueryName, Object... parameters);

    /**
     * Find "query objects" within named query .
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     *
     * @return list of found objects
     */
    List<Object> findQueryObjectByNamedQuery(String namedQueryName, Object... parameters);

    /**
     * Find "query objects" within named query .
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     *
     * @return list of found objects
     */
    ResultsIterator<Object> findQueryObjectByNamedQueryIterator(String namedQueryName, Object... parameters);

    /**
     * Find "query objects" within named query .
     *
     * @param namedQueryName name of query
     * @param firstResult    first row of result
     * @param maxResults     size of result set
     * @param parameters     optional parameters for named query
     *
     * @return list of found objects
     */
    List<Object> findQueryObjectRangeByNamedQuery(String namedQueryName,
                                                  int firstResult,
                                                  int maxResults,
                                                  Object... parameters);

    /**
     * Find "query objects" within named query .
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     *
     * @return list of found objects
     */
    List<Object[]> findQueryObjectsByNamedQuery(String namedQueryName, Object... parameters);

    /**
     * Find "query objects" within named query .
     *
     * @param namedQueryName name of query
     * @param firstResult    first row of result
     * @param maxResults     size of result set
     * @param parameters     optional parameters for named query
     *
     * @return list of found objects
     */
    List<Object[]> findQueryObjectsRangeByNamedQuery(String namedQueryName,
                                                     int firstResult,
                                                     int maxResults,
                                                     Object... parameters);


    /**
     * Find entities within named query .
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     * @param firstResult    first row of result
     * @param maxResults     size of result set
     *
     * @return list of found entities
     */
    List<T> findRangeByNamedQuery(String namedQueryName,
                                  int firstResult,
                                  int maxResults,
                                  Object... parameters);

    /**
     * Find entities by criteria.
     *
     * @param criterion given criteria
     *
     * @return list of found entities.
     */
    List<T> findByCriteria(Criterion... criterion);

    /**
     * Find entities by criteria.
     *
     * @param criteriaTuner optional criteria tuner.
     * @param criterion     given criteria
     *
     * @return list of found entities.
     */
    List<T> findByCriteria(CriteriaTuner criteriaTuner, Criterion... criterion);

    /**
     * Find single entity by criteria.
     *
     * @param criterion given criteria
     *
     * @return single entity or null if not found.
     */
    T findSingleByCriteria(Criterion... criterion);

    /**
     * Find single entity by criteria.
     *
     * @param criteriaTuner optional criteria tuner.
     * @param criterion     given criteria
     *
     * @return single entity or null if not found.
     */
    T findSingleByCriteria(CriteriaTuner criteriaTuner, Criterion... criterion);

    /**
     * Find entities by criteria.
     *
     * @param firstResult scroll to first result.
     * @param criterion   given criteria
     *
     * @return list of found entities.
     */
    T findUniqueByCriteria(int firstResult, Criterion... criterion);


    /**
     * Persist the new entity in DB.
     *
     * @param entity entity to persist
     *
     * @return persisted entity.
     */
    T create(T entity);

    /**
     * Update the entity in DB.
     *
     * @param entity entity to update
     *
     * @return updated entity.
     */
    T update(T entity);

    /**
     * Save or update the entity. Please, use #create or #update instead of this method.
     *
     * @param entity entity to update
     *
     * @return updated entity.
     */
    T saveOrUpdate(T entity);

    /**
     * Delete the given entity.
     *
     * @param entity to delete
     */
    void delete(Object entity);

    /**
     * Re-read given entity to get latest state from DB.
     *
     * @param entity to refresh
     */
    void refresh(Object entity);

    /**
     * Evict given entity from the first and second level cache.
     *
     * @param entity to evict
     */
    void evict(Object entity);

    /**
     * Force reindex the all entities.
     *
     * @param async true if async required
     *
     * @return document quantity in index
     */
    int fullTextSearchReindex(boolean async);

    /**
     * Force reindex the all entities.
     *
     * @param async true if async required
     * @param filter indexing filter
     *
     * @return document quantity in index
     */
    int fullTextSearchReindex(boolean async, IndexFilter<T> filter);

    /**
     * Force reindex given entity.
     *
     * @param primaryKey to reindex.
     *
     * @return document quantity in index
     */
    int fullTextSearchReindex(PK primaryKey);

    /**
     * Force reindex given entity.
     *
     * @param primaryKey to reindex.
     * @param purgeOnly true in case if need purge without reindexing from search index
     *
     * @return document quantity in index
     */
    int fullTextSearchReindex(PK primaryKey, boolean purgeOnly);


    /**
     * Get the full text search result.
     *
     * @param query lucene search query
     *
     * @return list of found entities
     */
    List<T> fullTextSearch(org.apache.lucene.search.Query query);

    /**
     * Get the full text search result.
     *
     * @param query         lucene search query
     * @param firstResult   first row of result
     * @param maxResults    size of result set
     * @param sortFieldName optional  sort field name
     * @param reverse       reverse the search result
     *
     * @return list of found entities
     */
    List<T> fullTextSearch(final org.apache.lucene.search.Query query,
                           int firstResult,
                           int maxResults,
                           String sortFieldName,
                           boolean reverse);

    /**
     * Get the full text search result.
     *
     * @param query         lucene search query
     * @param firstResult   first row of result
     * @param maxResults    size of result set
     * @param sortFieldName optional  sort field name
     * @param reverse       reverse the search result
     * @param fields        list of fields for projections
     *
     * @return list of found entities
     */
    Pair<List<Object[]>, Integer> fullTextSearch(org.apache.lucene.search.Query query,
                                                 int firstResult,
                                                 int maxResults,
                                                 String sortFieldName,
                                                 boolean reverse,
                                                 String ... fields);

    /**
     * Get the full text search result. The map returned by this method should be a single use only.
     * i.e. DO NOT CACHE this method. There are no benefits to this as final FilterNavigationRecord's are already
     * cached and it will make it harder to work with this map as some entries must be thrown away (e.g. zero counts
     * for multi value), sorted (e.g. multivalue).
     *
     * @param query lucene search query
     * @param facetingRequest faceting request context
     *
     * @return list of facets with values and their counts
     */
    Map<String, List<Pair<String, Integer>>> fullTextSearchNavigation(org.apache.lucene.search.Query query,
                                                                      List<FilteredNavigationRecordRequest> facetingRequest);


    /**
     * Get the full text search result.
     *
     * @param query lucene search query
     *
     * @return count items in result
     */
    int fullTextSearchCount(org.apache.lucene.search.Query query);

    /**
     * Execute native delete / update sql.
     *
     * @param nativeQuery native sql
     *
     * @return quantity of updated / deleted rows
     */
    int executeNativeUpdate(String nativeQuery);

    /**
     * Execute native sql.
     *
     * @param nativeQuery native sql
     *
     * @return result of select.
     */
    List executeNativeQuery(String nativeQuery);

    /**
     * Execute hsql.
     *
     * @param hsql hibernate sql
     *
     * @return result of select.
     */
    List executeHsqlQuery(String hsql);

    /**
     * Execute update.
     *
     * @param hsql       hibernate sql
     * @param parameters hsql query parameters.
     *
     * @return quantity of updated records.
     */
    int executeHsqlUpdate(String hsql, Object... parameters);

    /**
     * Execute update.
     *
     * @param namedQueryName named query name
     * @param parameters     parameters
     *
     * @return quantity of updated records.
     */
    int executeUpdate(String namedQueryName, Object... parameters);

    /**
     * Execute native delete / update sql.
     *
     * @param nativeQuery native sql
     * @param parameters  sql query parameters.
     *
     * @return quantity of updated / deleted rows
     */
    int executeNativeUpdate(String nativeQuery, Object... parameters);

    /**
     * Flush clear session.
     */
    void flushClear();

    /**
     * Flush session.
     */
    void flush();

    /**
     * Clear session.
     */
    void clear();
}
